package net.sf.redmine_mylyn.core;

import java.util.List;
import java.util.Set;

import net.sf.redmine_mylyn.api.model.Configuration;
import net.sf.redmine_mylyn.api.model.CustomField;
import net.sf.redmine_mylyn.api.model.CustomField.Format;
import net.sf.redmine_mylyn.api.model.Issue;
import net.sf.redmine_mylyn.api.model.IssuePriority;
import net.sf.redmine_mylyn.api.model.IssueStatus;
import net.sf.redmine_mylyn.api.model.Project;
import net.sf.redmine_mylyn.api.model.Property;
import net.sf.redmine_mylyn.core.client.IClient;
import net.sf.redmine_mylyn.internal.core.ProgressValues;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.net.Policy;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.RepositoryResponse;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataCollector;


public class RedmineTaskDataHandler extends AbstractTaskDataHandler {

	private RedmineRepositoryConnector connector;
	
	public RedmineTaskDataHandler(RedmineRepositoryConnector connector) {
		this.connector = connector;
	}

	@Override
	public boolean canGetMultiTaskData(TaskRepository taskRepository) {
		//TODO
		return false;
	}

	@Override
	public void getMultiTaskData(TaskRepository repository, Set<String> taskIds, TaskDataCollector collector, IProgressMonitor monitor) throws CoreException {
		// TODO Auto-generated method stub
		super.getMultiTaskData(repository, taskIds, collector, monitor);
	}
	
	@Override
	public boolean canInitializeSubTaskData(TaskRepository taskRepository, ITask task) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean initializeSubTaskData(TaskRepository repository, TaskData taskData, TaskData parentTaskData, IProgressMonitor monitor) throws CoreException {
		// TODO Auto-generated method stub
		return super.initializeSubTaskData(repository, taskData, parentTaskData, monitor);
	}
	
	@Override
	public TaskAttributeMapper getAttributeMapper(TaskRepository repository) {
		return new RedmineTaskAttributeMapper(repository, connector.getRepositoryConfiguration(repository));
	}

	@Override
	public boolean initializeTaskData(TaskRepository repository, TaskData taskData, ITaskMapping taskMapping, IProgressMonitor monitor) throws CoreException {
		// TODO Auto-generated method stub
		Configuration conf = connector.getRepositoryConfiguration(repository);
		Issue issue = new Issue();
		
		try {
			Project project = conf.getProjects().getAll().get(0);
			issue.setProjectId(project.getId());
			issue.setTrackerId(conf.getTrackers().getById(project.getTrackerIds()).get(0).getId());
		} catch (RuntimeException e) {
			IStatus status = new Status(IStatus.ERROR, RedmineCorePlugin.PLUGIN_ID, "Initialization of task failed. The provided data are insufficient.");
			StatusHandler.log(status);
			throw new CoreException(status);
		}
		
		try {
			createAttributes(taskData, issue, conf);
			//Operations

			/* Default-Values */
			TaskAttribute root = taskData.getRoot();
			root.getAttribute(RedmineAttribute.PROJECT.getTaskKey()).setValue(""+issue.getProjectId());
			root.getAttribute(RedmineAttribute.TRACKER.getTaskKey()).setValue(""+issue.getTrackerId());
			
			IssuePriority priority = conf.getIssuePriorities().getDefault();
			if(priority!=null) {
				root.getAttribute(RedmineAttribute.PRIORITY.getTaskKey()).setValue(""+priority.getId());
			} else if(conf.getIssuePriorities().getAll().size()>0){
				root.getAttribute(RedmineAttribute.PRIORITY.getTaskKey()).setValue(""+conf.getIssuePriorities().getAll().get(0));
			}
			
			IssueStatus status = conf.getIssueStatuses().getDefault();
			if(status!=null) {
				root.getAttribute(RedmineAttribute.STATUS.getTaskKey()).setValue(""+status.getId());
				root.getAttribute(RedmineAttribute.STATUS_CHG.getTaskKey()).setValue(""+status.getId());
			} else if(conf.getIssueStatuses().getAll().size()>0){
				root.getAttribute(RedmineAttribute.STATUS.getTaskKey()).setValue(""+conf.getIssueStatuses().getAll().get(0));
				root.getAttribute(RedmineAttribute.STATUS_CHG.getTaskKey()).setValue(""+conf.getIssueStatuses().getAll().get(0));
			}
			
		} catch (RedmineStatusException e) {
			throw new CoreException(RedmineCorePlugin.toStatus(e, e.getMessage()));
		}
		
		return true;
	}

	@Override
	public RepositoryResponse postTaskData(TaskRepository arg0, TaskData arg1, Set<TaskAttribute> arg2, IProgressMonitor arg3) throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}

	public TaskData getTaskData(TaskRepository repository, String taskId, IProgressMonitor monitor) throws CoreException {
		monitor = Policy.monitorFor(monitor);
		try {
			int id = Integer.parseInt(taskId);
			monitor.beginTask("Task Download", IProgressMonitor.UNKNOWN);
			
			
			Issue issue;
			IClient client;
			
			client = connector.getClientManager().getClient(repository);
			issue = client.getIssue(id, monitor);
			//TODO not found
			
			return createTaskDataFromTicket(client, repository, issue, monitor);
			
		
		} catch (OperationCanceledException e) {
			throw new CoreException(new Status(IStatus.CANCEL, RedmineCorePlugin.PLUGIN_ID, "Operation canceled"));
		} catch(NumberFormatException e) {
			throw new CoreException(RedmineCorePlugin.toStatus(e, "Invalid TaskId {0}", taskId));
		} catch (RedmineStatusException e) {
			throw new CoreException(e.getStatus());
		} finally {
			monitor.done();
		}
	}

	public TaskData createTaskDataFromTicket(IClient client, TaskRepository repository, Issue issue, IProgressMonitor monitor) throws CoreException {

		try {
			TaskData taskData = new TaskData(getAttributeMapper(repository), RedmineCorePlugin.REPOSITORY_KIND, repository.getRepositoryUrl(), issue.getId() + ""); //$NON-NLS-1$
			createAttributes(taskData, issue, connector.getRepositoryConfiguration(repository));
//			createOperations(taskData, client.getClientData(), ticket);
//			updateTaskData(repository, taskData, client, ticket);
			return taskData;
		} catch (RedmineStatusException e) {
			IStatus status = RedmineCorePlugin.toStatus(e, e.getMessage());
			throw new CoreException(status);
		}
	}

	private void createAttributes(TaskData data, Issue issue,  Configuration configuration) throws RedmineStatusException {
		createDefaultAttributes(data, issue, configuration);
		createCustomAttributes(data, issue, configuration.getCustomFields().getIssueCustomFields(), IRedmineConstants.TASK_KEY_PREFIX_ISSUE_CF, false);
	}
	
	private static void createDefaultAttributes(TaskData data, Issue issue , Configuration cfg) throws RedmineStatusException {
		boolean existingTask = issue.getId()>0;
		Project project = cfg.getProjects().getById(issue.getProjectId());

		if (project==null || cfg.getSettings()==null) {
			IStatus status = new Status(IStatus.ERROR, RedmineCorePlugin.PLUGIN_ID, "Initialization of task failed. The provided data are insufficient.");
			StatusHandler.log(status);
			throw new RedmineStatusException(status);
		}

		TaskAttribute attribute;
		
		createAttribute(data, RedmineAttribute.SUMMARY);
		createAttribute(data, RedmineAttribute.DESCRIPTION);
		createAttribute(data, RedmineAttribute.PROJECT, cfg.getProjects().getAll());

		createAttribute(data, RedmineAttribute.ESTIMATED);
		createAttribute(data, RedmineAttribute.DATE_DUE);
		createAttribute(data, RedmineAttribute.DATE_START);

		if (existingTask) {
			createAttribute(data, RedmineAttribute.REPORTER);
			createAttribute(data, RedmineAttribute.DATE_SUBMITTED);
			createAttribute(data, RedmineAttribute.DATE_UPDATED);
			
			createAttribute(data, RedmineAttribute.COMMENT);
			
			createAttribute(data, RedmineAttribute.STATUS, cfg.getIssueStatuses().getById(issue.getAvailableStatusId()));
			createAttribute(data, RedmineAttribute.STATUS_CHG, cfg.getIssueStatuses().getById(issue.getAvailableStatusId()));
////			createAttribute(data, RedmineAttribute.RELATION, ticket.getRelations(), false);
//			
//			createAttribute(data, RedmineAttribute.TIME_ENTRY_TOTAL);
		} else {
			createAttribute(data, RedmineAttribute.STATUS, cfg.getIssueStatuses().getAll());
			createAttribute(data, RedmineAttribute.STATUS_CHG, cfg.getIssueStatuses().getAll());
		}
		
		createAttribute(data, RedmineAttribute.PRIORITY, cfg.getIssuePriorities().getAll());
		createAttribute(data, RedmineAttribute.CATEGORY, cfg.getIssueCategories().getById(project.getIssueCategoryIds()), true);
		
		//TODO prüfen alt: createAttribute nur, wenn:  existingTask RedmineUtil.parseInteger(ticket.getValue(RedmineAttribute.VERSION.getTicketKey()))!=null
		createAttribute(data, RedmineAttribute.VERSION, cfg.getVersions().getOpenById(project.getVersionIds()), true);

		createAttribute(data, RedmineAttribute.ASSIGNED_TO, cfg.getUsers().getById(project.getAssignableMemberIds()), !existingTask);
		createAttribute(data, RedmineAttribute.TRACKER, cfg.getTrackers().getById(project.getTrackerIds()));

		attribute = createAttribute(data, RedmineAttribute.PROGRESS, ProgressValues.availableValues());
		if (!cfg.getSettings().isUseIssueDoneRatio()) {
			attribute.getMetaData().setReadOnly(true);
			attribute.getMetaData().setType(null);
		}

		//Attributes for a new TimeEntry
		//TODO
		if (true /*client.supportTimeEntries() && ticket.getRight(RedmineAcl.TIMEENTRY_NEW)*/) {
			createAttribute(data, RedmineAttribute.TIME_ENTRY_HOURS);
			createAttribute(data, RedmineAttribute.TIME_ENTRY_ACTIVITY, cfg.getTimeEntryActivities().getAll());
			createAttribute(data, RedmineAttribute.TIME_ENTRY_COMMENTS);
			createCustomAttributes(data, issue, cfg.getCustomFields().getTimeEntryActivityCustomFields(), IRedmineConstants.TASK_KEY_PREFIX_TIMEENTRY_CF, true);
		}

	}
	
	private static TaskAttribute createAttribute(TaskData data, RedmineAttribute redmineAttribute) {
		TaskAttribute attr = data.getRoot().createAttribute(redmineAttribute.getTaskKey());
		attr.getMetaData().setType(redmineAttribute.getType());
		attr.getMetaData().setKind(redmineAttribute.getKind());
		attr.getMetaData().setLabel(redmineAttribute.toString());
		attr.getMetaData().setReadOnly(redmineAttribute.isReadOnly());
		return attr;
	}
	
	private static TaskAttribute createAttribute(TaskData data, RedmineAttribute redmineAttribute, List<? extends Property> values) {
		return createAttribute(data, redmineAttribute, values, false);
	}

	private static TaskAttribute createAttribute(TaskData data, RedmineAttribute redmineAttribute, List<? extends Property> properties, boolean allowEmtpy) {
		TaskAttribute attr = createAttribute(data, redmineAttribute);

		if (properties != null && properties.size() > 0) {
			if (allowEmtpy) {
				attr.putOption("", ""); //$NON-NLS-1$ //$NON-NLS-2$
			}
			for (Property property : properties) {
				attr.putOption(String.valueOf(property.getId()), property.getName());
			}
		} else {
			attr.getMetaData().setReadOnly(true);
		}
		return attr;
	}

	private static void createCustomAttributes(TaskData taskData, Issue issue , List<CustomField> customFields, String prefix, boolean hidden) throws RedmineStatusException {
		for (CustomField customField : customFields) {
			TaskAttribute taskAttribute = createAttribute(taskData, customField, prefix);
			if(hidden) {
				taskAttribute.getMetaData().setKind(null);
			}
			if (customField.getFieldFormat()==Format.LIST) {
				if (!customField.isRequired()) {
					taskAttribute.putOption("", "");
				}
				for (String option : customField.getPossibleValues()) {
					taskAttribute.putOption(option, option);
				}
			}
			
		}
	}

	private static TaskAttribute createAttribute(TaskData taskData, CustomField customField, String prefix) {
		TaskAttribute attr = taskData.getRoot().createAttribute(prefix + customField.getId());
		attr.getMetaData().setType(getTaskAttributeType(customField));
		attr.getMetaData().setKind(TaskAttribute.KIND_DEFAULT);
		attr.getMetaData().setLabel(customField.getName());
		attr.getMetaData().setReadOnly(false);
		return attr;
	}
	
	private static String getTaskAttributeType(CustomField customField) {
		String type = TaskAttribute.TYPE_SHORT_TEXT;
		switch (customField.getFieldFormat()) {
		case TEXT:
			type = TaskAttribute.TYPE_LONG_TEXT;
			break;
		case LIST:
			type = TaskAttribute.TYPE_SINGLE_SELECT;
			break;
		case DATE:
			type = TaskAttribute.TYPE_DATE;
			break;
		case BOOL:
			type = TaskAttribute.TYPE_BOOLEAN;
			break;
		default:
			type = TaskAttribute.TYPE_SHORT_TEXT;
		}
		return type;
	}
//
//	private static void createOperations(TaskData taskData, RedmineClientData clientData, RedmineTicket ticket) {
//		RedmineTicketStatus currentStatus = null;
//		if(ticket!=null) {
//			String statusVal = ticket.getValue(RedmineAttribute.STATUS.getTicketKey());
//			if(statusVal!=null && statusVal.matches(IRedmineConstants.REGEX_INTEGER)) {
//				currentStatus = clientData.getStatus(Integer.parseInt(statusVal));
//			}
//		}
//		
//		if(currentStatus!=null) {
//			createOperation(taskData, RedmineOperation.none, ""+currentStatus.getValue(), currentStatus.getName()); //$NON-NLS-1$
//		}
//		
//		createOperation(taskData, RedmineOperation.markas, null);
//	}
//
//	private static TaskAttribute createOperation(TaskData taskData, RedmineOperation operation, String defaultValue, Object... labelArgs) {
//		TaskAttribute operationAttrib = taskData.getRoot().getAttribute(TaskAttribute.OPERATION);
//		if(operationAttrib==null) {
//			operationAttrib = taskData.getRoot().createAttribute(TaskAttribute.OPERATION);
//			TaskOperation.applyTo(operationAttrib, operation.toString(), null);
//		}
//
//		TaskAttribute attribute = taskData.getRoot().createAttribute(TaskAttribute.PREFIX_OPERATION + operation.toString());
//		TaskOperation.applyTo(attribute, operation.toString(), operation.getLabel(labelArgs));
//		
//		if(operation.isAssociated()) {
//			attribute.getMetaData().putValue(TaskAttribute.META_ASSOCIATED_ATTRIBUTE_ID, operation.getInputId());
//		} else if(operation.needsRestoreValue() && defaultValue!=null && defaultValue!=""){ //$NON-NLS-1$
//			attribute.getMetaData().putValue(IRedmineConstants.TASK_ATTRIBUTE_OPERATION_RESTORE, defaultValue);
//		}
//
//
//		return attribute;
//	}

}
