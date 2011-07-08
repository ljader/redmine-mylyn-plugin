package net.sf.redmine_mylyn.core;

import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import net.sf.redmine_mylyn.api.model.Configuration;
import net.sf.redmine_mylyn.api.model.CustomField;
import net.sf.redmine_mylyn.api.model.CustomField.Format;
import net.sf.redmine_mylyn.api.model.Issue;
import net.sf.redmine_mylyn.api.model.IssuePriority;
import net.sf.redmine_mylyn.api.model.IssueStatus;
import net.sf.redmine_mylyn.api.model.Project;
import net.sf.redmine_mylyn.api.model.Property;
import net.sf.redmine_mylyn.api.model.TimeEntry;
import net.sf.redmine_mylyn.core.client.IClient;
import net.sf.redmine_mylyn.internal.core.IssueMapper;
import net.sf.redmine_mylyn.internal.core.Messages;
import net.sf.redmine_mylyn.internal.core.ProgressValues;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.RepositoryResponse;
import org.eclipse.mylyn.tasks.core.RepositoryResponse.ResponseKind;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataCollector;
import org.eclipse.mylyn.tasks.core.data.TaskOperation;


public class RedmineTaskDataHandler extends AbstractTaskDataHandler {

	private RedmineRepositoryConnector connector;
	
	public RedmineTaskDataHandler(RedmineRepositoryConnector connector) {
		this.connector = connector;
	}

	@Override
	public boolean canGetMultiTaskData(TaskRepository taskRepository) {
		return true;
	}

	@Override
	public void getMultiTaskData(TaskRepository repository, Set<String> taskIds, TaskDataCollector collector, IProgressMonitor monitor) throws CoreException {
		TaskData[] taskData = connector.getTaskData(repository, taskIds, monitor);
		for (TaskData data : taskData) {
			if (data!=null) {
				collector.accept(data);
			}
		}
	}
	
	@Override
	public boolean canInitializeSubTaskData(TaskRepository taskRepository, ITask task) {
		return true;
	}
	
	@Override
	public TaskAttributeMapper getAttributeMapper(TaskRepository repository) {
		return new RedmineTaskAttributeMapper(repository, connector.getRepositoryConfiguration(repository));
	}
	
	@Override
	public boolean initializeSubTaskData(TaskRepository repository, TaskData taskData, TaskData parentTaskData, IProgressMonitor monitor) throws CoreException {
		System.out.println("Parent-Project: " + parentTaskData.getRoot().getAttribute(RedmineAttribute.PROJECT.getTaskKey()).getValue()); //$NON-NLS-1$
		System.out.println("Parent-Tracker: " + parentTaskData.getRoot().getAttribute(RedmineAttribute.TRACKER.getTaskKey()).getValue()); //$NON-NLS-1$
		
		Issue issue = new Issue();
		
		TaskAttribute parentRoot = parentTaskData.getRoot();
		issue.setProjectId(RedmineUtil.parseIntegerId(parentRoot.getAttribute(RedmineAttribute.PROJECT.getTaskKey()).getValue()));
		issue.setTrackerId(RedmineUtil.parseIntegerId(parentRoot.getAttribute(RedmineAttribute.TRACKER.getTaskKey()).getValue()));
		
		
		if(initializeNewTaskData(issue, repository, taskData, monitor)) {
			TaskAttribute childRoot = taskData.getRoot();
			childRoot.getAttribute(RedmineAttribute.PARENT.getTaskKey()).setValue(parentTaskData.getTaskId());
			childRoot.getAttribute(RedmineAttribute.CATEGORY.getTaskKey()).setValue(parentRoot.getAttribute(RedmineAttribute.CATEGORY.getTaskKey()).getValue());
			childRoot.getAttribute(RedmineAttribute.VERSION.getTaskKey()).setValue(parentRoot.getAttribute(RedmineAttribute.VERSION.getTaskKey()).getValue());
			childRoot.getAttribute(RedmineAttribute.PRIORITY.getTaskKey()).setValue(parentRoot.getAttribute(RedmineAttribute.PRIORITY.getTaskKey()).getValue());
			
			return true;
		}
		return false;
	}

	@Override
	public boolean initializeTaskData(TaskRepository repository, TaskData taskData, ITaskMapping taskMapping, IProgressMonitor monitor) throws CoreException {
		Configuration conf = connector.getRepositoryConfiguration(repository);
		Issue issue = new Issue();
		
		try {
			Project project = conf.getProjects().getAll().get(0);
			issue.setProjectId(project.getId());
			issue.setTrackerId(conf.getTrackers().getById(project.getTrackerIds()).get(0).getId());
			
			return initializeNewTaskData(issue, repository, taskData, monitor);
		} catch (RuntimeException e) {
			IStatus status = new Status(IStatus.ERROR, RedmineCorePlugin.PLUGIN_ID, Messages.ERRMSG_TASK_INITIALIZATION_FALED_INSUFFICENT_DATA, e);
			StatusHandler.log(status);
			throw new CoreException(status);
		}
	}

	private boolean initializeNewTaskData(Issue issue, TaskRepository repository, TaskData taskData, IProgressMonitor monitor) throws CoreException {
		Configuration conf = connector.getRepositoryConfiguration(repository);
		
		try {
			createAttributes(repository, taskData, issue, conf);
			createOperations(taskData, issue, conf);
			
			/* Default-Values */
			TaskAttribute root = taskData.getRoot();
			root.getAttribute(RedmineAttribute.PROJECT.getTaskKey()).setValue(""+issue.getProjectId()); //$NON-NLS-1$
			root.getAttribute(RedmineAttribute.TRACKER.getTaskKey()).setValue(""+issue.getTrackerId()); //$NON-NLS-1$
			
			IssuePriority priority = conf.getIssuePriorities().getDefault();
			if(priority!=null) {
				root.getAttribute(RedmineAttribute.PRIORITY.getTaskKey()).setValue(""+priority.getId()); //$NON-NLS-1$
			} else if(conf.getIssuePriorities().getAll().size()>0){
				root.getAttribute(RedmineAttribute.PRIORITY.getTaskKey()).setValue(""+conf.getIssuePriorities().getAll().get(0)); //$NON-NLS-1$
			}
			
			IssueStatus status = conf.getIssueStatuses().getDefault();
			if(status!=null) {
				root.getAttribute(RedmineAttribute.STATUS.getTaskKey()).setValue(""+status.getId()); //$NON-NLS-1$
				root.getAttribute(RedmineAttribute.STATUS_CHG.getTaskKey()).setValue(""+status.getId()); //$NON-NLS-1$
			} else if(conf.getIssueStatuses().getAll().size()>0){
				root.getAttribute(RedmineAttribute.STATUS.getTaskKey()).setValue(""+conf.getIssueStatuses().getAll().get(0)); //$NON-NLS-1$
				root.getAttribute(RedmineAttribute.STATUS_CHG.getTaskKey()).setValue(""+conf.getIssueStatuses().getAll().get(0)); //$NON-NLS-1$
			}
			
		} catch (RedmineStatusException e) {
			throw new CoreException(RedmineCorePlugin.toStatus(e, e.getMessage()));
		}
		
		return true;
	}
	
	@Override
	public RepositoryResponse postTaskData(TaskRepository repository, TaskData taskData, Set<TaskAttribute> oldAttributes, IProgressMonitor monitor) throws CoreException {
		String taskId = taskData.getTaskId();
		try {
			IClient client = connector.getClientManager().getClient(repository);
			Configuration cfg = connector.getRepositoryConfiguration(repository);

			if(taskData.isNew() || taskId.isEmpty()) {
				Issue issue = IssueMapper.createIssue(repository, taskData, oldAttributes, cfg);
				taskId += client.createIssue(issue, monitor);
			} else {
				Issue issue = IssueMapper.createIssue(repository, taskData, oldAttributes, cfg);
				TimeEntry timeEntry = IssueMapper.createTimeEntry(repository, taskData, oldAttributes, cfg);
				TaskAttribute commentAttribute = taskData.getRoot().getAttribute(RedmineAttribute.COMMENT.getTaskKey());
				String comment = commentAttribute==null ? null : commentAttribute.getValue();
				client.updateIssue(issue, comment, timeEntry, monitor);
			}
		} catch (RedmineStatusException e) {
			throw new CoreException(e.getStatus());
		}
		
		return new RepositoryResponse(ResponseKind.TASK_CREATED, "" + taskId); //$NON-NLS-1$
	}
	
	public TaskData createTaskDataFromIssue(TaskRepository repository, Issue issue, IProgressMonitor monitor) throws CoreException {

		Configuration configuration = connector.getRepositoryConfiguration(repository);
		try {
			TaskData taskData = new TaskData(getAttributeMapper(repository), RedmineCorePlugin.REPOSITORY_KIND, repository.getRepositoryUrl(), issue.getId() + ""); //$NON-NLS-1$
			createAttributes(repository, taskData, issue, configuration);
			createOperations(taskData, issue, configuration);

			IssueMapper.updateTaskData(repository, taskData, configuration, issue);
			return taskData;
		} catch (RedmineStatusException e) {
			IStatus status = RedmineCorePlugin.toStatus(e, e.getMessage());
			throw new CoreException(status);
		}
	}

	private void createAttributes(TaskRepository repository, TaskData data, Issue issue,  Configuration configuration) throws RedmineStatusException {
		createDefaultAttributes(repository, data, issue, configuration);
		createCustomAttributes(data, issue, configuration.getCustomFields().getIssueCustomFields(), IRedmineConstants.TASK_KEY_PREFIX_ISSUE_CF, false);
	}
	
	private static void createDefaultAttributes(TaskRepository repository, TaskData data, Issue issue , Configuration cfg) throws RedmineStatusException {
		boolean existingTask = issue.getId()>0;
		Project project = cfg.getProjects().getById(issue.getProjectId());

		if (project==null || cfg.getSettings()==null) {
			IStatus status = new Status(IStatus.ERROR, RedmineCorePlugin.PLUGIN_ID, Messages.ERRMSG_TASK_INITIALIZATION_FALED_INSUFFICENT_DATA);
			StatusHandler.log(status);
			throw new RedmineStatusException(status);
		}

		TaskAttribute attribute;
		
		createAttribute(data, RedmineAttribute.SUMMARY);
		createAttribute(data, RedmineAttribute.DESCRIPTION);
		createAttribute(data, RedmineAttribute.PRIORITY, cfg.getIssuePriorities().getAll());
		
		if(existingTask) {
			attribute = createAttribute(data, RedmineAttribute.PROJECT, cfg.getProjects().getMoveAllowed(project));
			attribute.getMetaData().setReadOnly(true);
		} else {
			createAttribute(data, RedmineAttribute.PROJECT, cfg.getProjects().getNewAllowed());
		}
		
		createAttribute(data, RedmineAttribute.PARENT);
		createAttribute(data, RedmineAttribute.TRACKER, cfg.getTrackers().getById(project.getTrackerIds()));

		if (existingTask) {
			createAttribute(data, RedmineAttribute.REPORTER);
			createAttribute(data, RedmineAttribute.DATE_SUBMITTED);
			createAttribute(data, RedmineAttribute.DATE_UPDATED);
			
			createAttribute(data, RedmineAttribute.COMMENT);
			
			createAttribute(data, RedmineAttribute.STATUS, cfg.getIssueStatuses().getById(issue.getAvailableStatusId()));
			createAttribute(data, RedmineAttribute.STATUS_CHG, cfg.getIssueStatuses().getById(issue.getAvailableStatusId()));

////			createAttribute(data, RedmineAttribute.RELATION, ticket.getRelations(), false);
//			
		} else {
			createAttribute(data, RedmineAttribute.STATUS, cfg.getIssueStatuses().getAll());
			createAttribute(data, RedmineAttribute.STATUS_CHG, cfg.getIssueStatuses().getAll());

		}
		
		createAttribute(data, RedmineAttribute.CATEGORY, cfg.getIssueCategories().getById(project.getIssueCategoryIds()), true);
		createAttribute(data, RedmineAttribute.VERSION, cfg.getVersions().getOpenById(project.getVersionIds()), true);
		
		attribute = createAttribute(data, RedmineAttribute.PROGRESS, ProgressValues.availableValues());
		if (!cfg.getSettings().isUseIssueDoneRatio()) {
			attribute.getMetaData().setReadOnly(true);
			attribute.getMetaData().setType(null);
		}
		
		//Planning
		createAttribute(data, RedmineAttribute.ESTIMATED);
		createAttribute(data, RedmineAttribute.DATE_DUE);
		createAttribute(data, RedmineAttribute.DATE_START);

		createAttribute(data, RedmineAttribute.ASSIGNED_TO, cfg.getUsers().getById(project.getAssignableMemberIds()), !existingTask);

		//Attributes for a new TimeEntry
		if(existingTask) {
			if (issue.getTimeEntries()!=null && issue.getTimeEntries().isNewAllowed()) {
				createAttribute(data, RedmineAttribute.TIME_ENTRY_HOURS);
				createAttribute(data, RedmineAttribute.TIME_ENTRY_ACTIVITY, project.getTimeEntryActivities().getAll());
				createAttribute(data, RedmineAttribute.TIME_ENTRY_COMMENTS);
				
				for (IRedmineExtensionField additionalField : RedmineCorePlugin.getDefault().getExtensionManager().getAdditionalTimeEntryFields(repository)) {
					createAttribute(data, additionalField, IRedmineConstants.TASK_KEY_PREFIX_TIMEENTRY_EX);
				}
				
				createCustomAttributes(data, issue, cfg.getCustomFields().getTimeEntryCustomFields(), IRedmineConstants.TASK_KEY_PREFIX_TIMEENTRY_CF, true);
			}
		}

		//Watchers
		if(existingTask) {
			if(issue.isWatchersViewAllowed()) {
				attribute = createAttribute(data, RedmineAttribute.WATCHERS, cfg.getUsers().getAll());
				
				if(issue.isWatchersAddAllowed()) {
					TaskAttribute addWatcherAttribute = attribute.createAttribute(RedmineAttribute.WATCHERS_ADD.getTaskKey());
					addWatcherAttribute.getMetaData().setLabel(RedmineAttribute.WATCHERS_ADD.getLabel());
					addOptions(addWatcherAttribute, cfg.getUsers().getById(project.getMemberIds()));
				}

				if(issue.isWatchersDeleteAllowed()) {
					attribute.createAttribute(RedmineAttribute.WATCHERS_REMOVE.getTaskKey());
				}
			}
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
			addOptions(attr, properties);
		}
		return attr;
	}
	
	private static TaskAttribute addOptions(TaskAttribute attribute, List<? extends Property> properties) {
		for (Property property : properties) {
			attribute.putOption(String.valueOf(property.getId()), property.getName());
		}
		return attribute;
	}

	private static void createCustomAttributes(TaskData taskData, Issue issue , List<CustomField> customFields, String prefix, boolean hidden) throws RedmineStatusException {
		for (CustomField customField : customFields) {
			TaskAttribute taskAttribute = createAttribute(taskData, customField, prefix);
			if(hidden) {
				taskAttribute.getMetaData().setKind(null);
			}
			if (customField.getFieldFormat()==Format.LIST) {
				if (!customField.isRequired()) {
					taskAttribute.putOption("", ""); //$NON-NLS-1$ //$NON-NLS-2$
				}
				for (String option : customField.getPossibleValues()) {
					taskAttribute.putOption(option, option);
				}
			}
			
		}
	}

	private static TaskAttribute createAttribute(TaskData taskData, CustomField customField, String prefix) {
		TaskAttribute attr = taskData.getRoot().createAttribute(prefix + customField.getId());
		attr.getMetaData().setType(RedmineUtil.getTaskAttributeType(customField));
		attr.getMetaData().setKind(TaskAttribute.KIND_DEFAULT);
		attr.getMetaData().setLabel(customField.getName());
		attr.getMetaData().setReadOnly(false);
		return attr;
	}
	
	private static TaskAttribute createAttribute(TaskData taskData, IRedmineExtensionField  additionalField, String prefix) {
		TaskAttribute attr = taskData.getRoot().createAttribute(prefix + additionalField.getTaskKey());
		attr.getMetaData().setType(additionalField.getEditorType());
		attr.getMetaData().setLabel(additionalField.getLabel());
		attr.getMetaData().setReadOnly(false);
		
		if (additionalField.getOptions()!=null) {
			if (!additionalField.isRequired()) {
				attr.putOption("", ""); //$NON-NLS-1$ //$NON-NLS-2$
			}
			for (Entry<String, String> entry : additionalField.getOptions().entrySet()) {
				attr.putOption(entry.getKey(), entry.getValue());
			}
			
		}
		return attr;
	}
	
	private void createOperations(TaskData taskData, Issue issue, Configuration configuration) {
		IssueStatus currentStatus = null;
		if(issue!=null) {
			currentStatus = configuration.getIssueStatuses().getById(issue.getStatusId());
		}
		
		if(currentStatus!=null) {
			createOperation(taskData, RedmineOperation.none, ""+currentStatus.getId(), currentStatus.getName()); //$NON-NLS-1$
		}
		
		createOperation(taskData, RedmineOperation.markas, null);
	}

	private static TaskAttribute createOperation(TaskData taskData, RedmineOperation operation, String defaultValue, Object... labelArgs) {
		TaskAttribute operationAttrib = taskData.getRoot().getAttribute(TaskAttribute.OPERATION);
		if(operationAttrib==null) {
			operationAttrib = taskData.getRoot().createAttribute(TaskAttribute.OPERATION);
			TaskOperation.applyTo(operationAttrib, operation.toString(), null);
		}

		TaskAttribute attribute = taskData.getRoot().createAttribute(TaskAttribute.PREFIX_OPERATION + operation.getTaskKey());
		TaskOperation.applyTo(attribute, operation.getTaskKey(), operation.getLabel(labelArgs));
		
		if(operation.isAssociated()) {
			attribute.getMetaData().putValue(TaskAttribute.META_ASSOCIATED_ATTRIBUTE_ID, operation.getInputId());
		} else if(operation.needsRestoreValue() && defaultValue!=null && defaultValue!=""){ //$NON-NLS-1$
			attribute.getMetaData().putValue(IRedmineConstants.TASK_ATTRIBUTE_OPERATION_RESTORE, defaultValue);
		}

		return attribute;
	}

}
