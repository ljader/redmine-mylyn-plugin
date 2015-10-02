package net.sf.redmine_mylyn.core;

import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import net.sf.redmine_mylyn.api.model.Configuration;
import net.sf.redmine_mylyn.api.model.CustomField;
import net.sf.redmine_mylyn.api.model.Issue;
import net.sf.redmine_mylyn.api.model.IssuePriority;
import net.sf.redmine_mylyn.api.model.IssueStatus;
import net.sf.redmine_mylyn.api.model.Member;
import net.sf.redmine_mylyn.api.model.Project;
import net.sf.redmine_mylyn.api.model.Property;
import net.sf.redmine_mylyn.api.model.TimeEntry;
import net.sf.redmine_mylyn.api.model.User;
import net.sf.redmine_mylyn.api.model.Version;
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

	private final RedmineRepositoryConnector connector;

	public RedmineTaskDataHandler(final RedmineRepositoryConnector connector) {
		this.connector = connector;
	}

	@Override
	public boolean canGetMultiTaskData(final TaskRepository taskRepository) {
		return true;
	}

	@Override
	public void getMultiTaskData(final TaskRepository repository, final Set<String> taskIds, final TaskDataCollector collector, final IProgressMonitor monitor) throws CoreException {
		final TaskData[] taskData = connector.getTaskData(repository, taskIds, monitor);
		for (final TaskData data : taskData) {
			if (data!=null) {
				collector.accept(data);
			}
		}
	}

	@Override
	public boolean canInitializeSubTaskData(final TaskRepository taskRepository, final ITask task) {
		return true;
	}

	@Override
	public TaskAttributeMapper getAttributeMapper(final TaskRepository repository) {
		return new RedmineTaskAttributeMapper(repository, connector.getRepositoryConfiguration(repository));
	}

	@Override
	public boolean initializeSubTaskData(final TaskRepository repository, final TaskData taskData, final TaskData parentTaskData, final IProgressMonitor monitor) throws CoreException {
		final Issue issue = new Issue();

		final TaskAttribute parentRoot = parentTaskData.getRoot();
		issue.setProjectId(RedmineUtil.parseIntegerId(parentRoot.getAttribute(RedmineAttribute.PROJECT.getTaskKey()).getValue()));
		issue.setTrackerId(RedmineUtil.parseIntegerId(parentRoot.getAttribute(RedmineAttribute.TRACKER.getTaskKey()).getValue()));


		if(initializeNewTaskData(issue, repository, taskData, monitor)) {
			final TaskAttribute childRoot = taskData.getRoot();
			childRoot.getAttribute(RedmineAttribute.PARENT.getTaskKey()).setValue(parentTaskData.getTaskId());
			childRoot.getAttribute(RedmineAttribute.CATEGORY.getTaskKey()).setValue(parentRoot.getAttribute(RedmineAttribute.CATEGORY.getTaskKey()).getValue());
			childRoot.getAttribute(RedmineAttribute.VERSION.getTaskKey()).setValue(parentRoot.getAttribute(RedmineAttribute.VERSION.getTaskKey()).getValue());
			childRoot.getAttribute(RedmineAttribute.PRIORITY.getTaskKey()).setValue(parentRoot.getAttribute(RedmineAttribute.PRIORITY.getTaskKey()).getValue());

			return true;
		}
		return false;
	}

	@Override
	public boolean initializeTaskData(final TaskRepository repository, final TaskData taskData, final ITaskMapping taskMapping, final IProgressMonitor monitor) throws CoreException {
		final Configuration conf = connector.getRepositoryConfiguration(repository);
		final Issue issue = new Issue();

		try {
			final Project project = conf.getProjects().getAll().get(0);
			issue.setProjectId(project.getId());
			issue.setTrackerId(conf.getTrackers().getById(project.getTrackerIds()).get(0).getId());

			return initializeNewTaskData(issue, repository, taskData, monitor);
		} catch (final RuntimeException e) {
			final IStatus status = new Status(IStatus.ERROR, RedmineCorePlugin.PLUGIN_ID, Messages.ERRMSG_TASK_INITIALIZATION_FALED_INSUFFICENT_DATA, e);
			StatusHandler.log(status);
			throw new CoreException(status);
		}
	}

	private boolean initializeNewTaskData(final Issue issue, final TaskRepository repository, final TaskData taskData, final IProgressMonitor monitor) throws CoreException {
		final Configuration conf = connector.getRepositoryConfiguration(repository);

		try {
			createAttributes(repository, taskData, issue, conf);
			createOperations(taskData, issue, conf);

			/* Default-Values */
			final TaskAttribute root = taskData.getRoot();
			root.getAttribute(RedmineAttribute.PROJECT.getTaskKey()).setValue(""+issue.getProjectId()); //$NON-NLS-1$
			root.getAttribute(RedmineAttribute.TRACKER.getTaskKey()).setValue(""+issue.getTrackerId()); //$NON-NLS-1$

			final IssuePriority priority = conf.getIssuePriorities().getDefault();
			if(priority!=null) {
				root.getAttribute(RedmineAttribute.PRIORITY.getTaskKey()).setValue(""+priority.getId()); //$NON-NLS-1$
			} else if(conf.getIssuePriorities().getAll().size()>0){
				root.getAttribute(RedmineAttribute.PRIORITY.getTaskKey()).setValue(""+conf.getIssuePriorities().getAll().get(0)); //$NON-NLS-1$
			}

			final IssueStatus status = conf.getIssueStatuses().getDefault();
			if(status!=null) {
				root.getAttribute(RedmineAttribute.STATUS.getTaskKey()).setValue(""+status.getId()); //$NON-NLS-1$
				root.getAttribute(RedmineAttribute.STATUS_CHG.getTaskKey()).setValue(""+status.getId()); //$NON-NLS-1$
			} else if(conf.getIssueStatuses().getAll().size()>0){
				root.getAttribute(RedmineAttribute.STATUS.getTaskKey()).setValue(""+conf.getIssueStatuses().getAll().get(0)); //$NON-NLS-1$
				root.getAttribute(RedmineAttribute.STATUS_CHG.getTaskKey()).setValue(""+conf.getIssueStatuses().getAll().get(0)); //$NON-NLS-1$
			}

		} catch (final RedmineStatusException e) {
			throw new CoreException(RedmineCorePlugin.toStatus(e, e.getMessage()));
		}

		return true;
	}

	@Override
	public RepositoryResponse postTaskData(final TaskRepository repository, final TaskData taskData, final Set<TaskAttribute> oldAttributes, final IProgressMonitor monitor) throws CoreException {
		String taskId = taskData.getTaskId();
		try {
			final IClient client = connector.getClientManager().getClient(repository);
			final Configuration cfg = connector.getRepositoryConfiguration(repository);

			if(taskData.isNew() || taskId.isEmpty()) {
				final Issue issue = IssueMapper.createIssue(repository, taskData, oldAttributes, cfg);
				taskId += client.createIssue(issue, monitor);
			} else {
				final Issue issue = IssueMapper.createIssue(repository, taskData, oldAttributes, cfg);
				final TimeEntry timeEntry = IssueMapper.createTimeEntry(repository, taskData, oldAttributes, cfg);
				final TaskAttribute commentAttribute = taskData.getRoot().getAttribute(RedmineAttribute.COMMENT.getTaskKey());
				final String comment = commentAttribute==null ? null : commentAttribute.getValue();
				final TaskAttribute attribute = taskData.getRoot().getAttribute(RedmineAttribute.DATE_UPDATED.getTaskKey());
				final Date lastModified = RedmineUtil.parseDate(attribute.getValue());
				client.updateIssue(issue, comment, lastModified, timeEntry, monitor);
			}
		} catch (final RedmineStatusException e) {
			throw new CoreException(e.getStatus());
		}

		return new RepositoryResponse(ResponseKind.TASK_CREATED, "" + taskId); //$NON-NLS-1$
	}

	public TaskData createTaskDataFromIssue(final TaskRepository repository, final Issue issue, final IProgressMonitor monitor) throws CoreException {

		final Configuration configuration = connector.getRepositoryConfiguration(repository);
		try {
			final TaskData taskData = new TaskData(getAttributeMapper(repository), RedmineCorePlugin.REPOSITORY_KIND, repository.getRepositoryUrl(), issue.getId() + ""); //$NON-NLS-1$
			createAttributes(repository, taskData, issue, configuration);
			createOperations(taskData, issue, configuration);

			IssueMapper.updateTaskData(repository, taskData, configuration, issue);
			return taskData;
		} catch (final RedmineStatusException e) {
			final IStatus status = RedmineCorePlugin.toStatus(e, e.getMessage());
			throw new CoreException(status);
		}
	}

	private void createAttributes(final TaskRepository repository, final TaskData data, final Issue issue,  final Configuration configuration) throws RedmineStatusException {
		createDefaultAttributes(repository, data, issue, configuration);
		createCustomAttributes(data, configuration, issue, configuration.getCustomFields().getIssueCustomFields(), IRedmineConstants.TASK_KEY_PREFIX_ISSUE_CF, false);
	}

	private static void createDefaultAttributes(final TaskRepository repository, final TaskData data, final Issue issue , final Configuration cfg) throws RedmineStatusException {
		final boolean existingTask = issue.getId()>0;
		final Project project = cfg.getProjects().getById(issue.getProjectId());

		if (project==null) {
			//https://sourceforge.net/tracker/index.php?func=detail&aid=3441198&group_id=228995&atid=1075435#
			final IStatus status = RedmineCorePlugin.toStatus(IStatus.ERROR, Messages.ERRMSG_TASK_INITIALIZATION_FALED_INSUFFICENT_DATA_X_X, issue.getId(), "Project" + " " + issue.getProjectId() );
			StatusHandler.log(status);
			throw new RedmineStatusException(status);
		}

		if (cfg.getSettings()==null) {
			//https://sourceforge.net/tracker/index.php?func=detail&aid=3441198&group_id=228995&atid=1075435#
			final IStatus status = RedmineCorePlugin.toStatus(IStatus.ERROR, Messages.ERRMSG_TASK_INITIALIZATION_FALED_INSUFFICENT_DATA_X_X, issue.getId(), "Settings" );
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
		createAttribute(data, RedmineAttribute.SUBTASKS);
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

				for (final IRedmineExtensionField additionalField : RedmineCorePlugin.getDefault().getExtensionManager().getAdditionalTimeEntryFields(repository)) {
					createAttribute(data, additionalField, IRedmineConstants.TASK_KEY_PREFIX_TIMEENTRY_EX);
				}

				createCustomAttributes(data, cfg, issue, cfg.getCustomFields().getTimeEntryCustomFields(), IRedmineConstants.TASK_KEY_PREFIX_TIMEENTRY_CF, true);
			}
		}

		//Watchers
		if(existingTask) {
			if(issue.isWatchersViewAllowed()) {
				attribute = createAttribute(data, RedmineAttribute.WATCHERS, cfg.getUsers().getAll());

				if(issue.isWatchersAddAllowed()) {
					final TaskAttribute addWatcherAttribute = attribute.createAttribute(RedmineAttribute.WATCHERS_ADD.getTaskKey());
					addWatcherAttribute.getMetaData().setLabel(RedmineAttribute.WATCHERS_ADD.getLabel());
					addOptions(addWatcherAttribute, cfg.getUsers().getById(project.getMemberIds()));
				}

				if(issue.isWatchersDeleteAllowed()) {
					attribute.createAttribute(RedmineAttribute.WATCHERS_REMOVE.getTaskKey());
				}
			}
		}
	}

	private static TaskAttribute createAttribute(final TaskData data, final RedmineAttribute redmineAttribute) {
		final TaskAttribute attr = data.getRoot().createAttribute(redmineAttribute.getTaskKey());
		attr.getMetaData().setType(redmineAttribute.getType());
		attr.getMetaData().setKind(redmineAttribute.getKind());
		attr.getMetaData().setLabel(redmineAttribute.toString());
		attr.getMetaData().setReadOnly(redmineAttribute.isReadOnly());
		return attr;
	}

	private static TaskAttribute createAttribute(final TaskData data, final RedmineAttribute redmineAttribute, final List<? extends Property> values) {
		return createAttribute(data, redmineAttribute, values, false);
	}

	private static TaskAttribute createAttribute(final TaskData data, final RedmineAttribute redmineAttribute, final List<? extends Property> properties, final boolean allowEmtpy) {
		final TaskAttribute attr = createAttribute(data, redmineAttribute);

		if (properties != null && properties.size() > 0) {
			if (allowEmtpy) {
				attr.putOption("", ""); //$NON-NLS-1$ //$NON-NLS-2$
			}
			addOptions(attr, properties);
		}
		return attr;
	}

	private static TaskAttribute addOptions(final TaskAttribute attribute, final List<? extends Property> properties) {
		for (final Property property : properties) {
			attribute.putOption(String.valueOf(property.getId()), property.getName());
		}
		return attribute;
	}

	private static void createCustomAttributes(final TaskData taskData, final Configuration configuration, final Issue issue , final List<CustomField> customFields, final String prefix, final boolean hidden) throws RedmineStatusException {
		final Project project = configuration.getProjects().getById(issue.getProjectId());

		for (final CustomField customField : customFields) {
			final TaskAttribute taskAttribute = createAttribute(taskData, customField, prefix);
			if(hidden) {
				taskAttribute.getMetaData().setKind(null);
			}
			if (customField.getFieldFormat().isListType()) {
				if (!customField.isRequired()) {
					taskAttribute.putOption("", ""); //$NON-NLS-1$ //$NON-NLS-2$
				}

				switch (customField.getFieldFormat()) {
				case VERSION:
					for( final Version version : configuration.getVersions().getById(project.getVersionIds()) ) {
						taskAttribute.putOption(""+version.getId(), version.getName()); //$NON-NLS-1$
					}
					break;
				case USER:
					for( final Member member : project.getMembers() ) {
						final User user = configuration.getUsers().getById(member.getUserId());
						if (user != null) {
							taskAttribute.putOption(""+user.getId(), user.getName()); //$NON-NLS-1$
						}
					}
					break;
				default:
					for (final String option : customField.getPossibleValues()) {
						taskAttribute.putOption(option, option);
					}
				}
			}

		}
	}

	private static TaskAttribute createAttribute(final TaskData taskData, final CustomField customField, final String prefix) {
		final TaskAttribute attr = taskData.getRoot().createAttribute(prefix + customField.getId());
		attr.getMetaData().setType(RedmineUtil.getTaskAttributeType(customField));
		attr.getMetaData().setKind(TaskAttribute.KIND_DEFAULT);
		attr.getMetaData().setLabel(customField.getName());
		attr.getMetaData().setReadOnly(false);
		return attr;
	}

	private static TaskAttribute createAttribute(final TaskData taskData, final IRedmineExtensionField  additionalField, final String prefix) {
		final TaskAttribute attr = taskData.getRoot().createAttribute(prefix + additionalField.getTaskKey());
		attr.getMetaData().setType(additionalField.getEditorType());
		attr.getMetaData().setLabel(additionalField.getLabel());
		attr.getMetaData().setReadOnly(false);

		if (additionalField.getOptions()!=null) {
			if (!additionalField.isRequired()) {
				attr.putOption("", ""); //$NON-NLS-1$ //$NON-NLS-2$
			}
			for (final Entry<String, String> entry : additionalField.getOptions().entrySet()) {
				attr.putOption(entry.getKey(), entry.getValue());
			}

		}
		return attr;
	}

	private void createOperations(final TaskData taskData, final Issue issue, final Configuration configuration) {
		IssueStatus currentStatus = null;
		if(issue!=null) {
			currentStatus = configuration.getIssueStatuses().getById(issue.getStatusId());
		}

		if(currentStatus!=null) {
			createOperation(taskData, RedmineOperation.none, ""+currentStatus.getId(), currentStatus.getName()); //$NON-NLS-1$
		}

		createOperation(taskData, RedmineOperation.markas, null);
	}

	private static TaskAttribute createOperation(final TaskData taskData, final RedmineOperation operation, final String defaultValue, final Object... labelArgs) {
		TaskAttribute operationAttrib = taskData.getRoot().getAttribute(TaskAttribute.OPERATION);
		if(operationAttrib==null) {
			operationAttrib = taskData.getRoot().createAttribute(TaskAttribute.OPERATION);
			TaskOperation.applyTo(operationAttrib, operation.toString(), null);
		}

		final TaskAttribute attribute = taskData.getRoot().createAttribute(TaskAttribute.PREFIX_OPERATION + operation.getTaskKey());
		TaskOperation.applyTo(attribute, operation.getTaskKey(), operation.getLabel(labelArgs));

		if(operation.isAssociated()) {
			attribute.getMetaData().putValue(TaskAttribute.META_ASSOCIATED_ATTRIBUTE_ID, operation.getInputId());
		} else if(operation.needsRestoreValue() && defaultValue!=null && defaultValue!=""){ //$NON-NLS-1$
			attribute.getMetaData().putValue(IRedmineConstants.TASK_ATTRIBUTE_OPERATION_RESTORE, defaultValue);
		}

		return attribute;
	}

}
