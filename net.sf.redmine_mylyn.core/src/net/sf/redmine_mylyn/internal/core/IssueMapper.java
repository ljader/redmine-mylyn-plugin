package net.sf.redmine_mylyn.internal.core;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import net.sf.redmine_mylyn.api.model.Attachment;
import net.sf.redmine_mylyn.api.model.Configuration;
import net.sf.redmine_mylyn.api.model.CustomField;
import net.sf.redmine_mylyn.api.model.CustomValue;
import net.sf.redmine_mylyn.api.model.Issue;
import net.sf.redmine_mylyn.api.model.Journal;
import net.sf.redmine_mylyn.api.model.TimeEntry;
import net.sf.redmine_mylyn.api.model.container.CustomValues;
import net.sf.redmine_mylyn.common.logging.ILogService;
import net.sf.redmine_mylyn.core.IRedmineConstants;
import net.sf.redmine_mylyn.core.IRedmineExtensionField;
import net.sf.redmine_mylyn.core.RedmineAttribute;
import net.sf.redmine_mylyn.core.RedmineCorePlugin;
import net.sf.redmine_mylyn.core.RedmineOperation;
import net.sf.redmine_mylyn.core.RedmineRepositoryConnector;
import net.sf.redmine_mylyn.core.RedmineTaskTimeEntryMapper;
import net.sf.redmine_mylyn.core.RedmineUtil;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttachmentMapper;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskCommentMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;

public class IssueMapper {

	public static void updateTaskData(final TaskRepository repository,
			final TaskData taskData, final Configuration cfg, final Issue issue)
					throws CoreException {

		final TaskAttribute root = taskData.getRoot();
		TaskAttribute taskAttribute = null;

		/* Default Attributes */
		for (final RedmineAttribute redmineAttribute : RedmineAttribute
				.values()) {
			final String taskKey = redmineAttribute.getTaskKey();
			if (taskKey == null) {
				continue;
			}

			taskAttribute = root.getAttribute(taskKey);
			if (taskAttribute != null) {
				final Field field = redmineAttribute.getAttributeField();

				if (field != null) {
					try {
						setValue(taskAttribute, field.get(issue));
					} catch (final Exception e) {
						final IStatus status = RedmineCorePlugin.toStatus(e,
								Messages.ERRMSG_CANT_READ_PROPERTY_X,
								redmineAttribute.name());
						final ILogService log = RedmineCorePlugin
								.getLogService(IssueMapper.class);
						log.error(e, status.getMessage());
						throw new CoreException(status);
					}
				} else {
					switch (redmineAttribute) {
					case WATCHERS:
						for (final int watcherId : issue.getWatcherIds()) {
							taskAttribute.addValue(Integer.toString(watcherId));
						}
						break;
					default:
						break;
					}

				}
			}
		}

		/* Custom Attributes */
		if (issue.getCustomValues() != null) {
			for (final CustomValue customValue : issue.getCustomValues()
					.getAll()) {
				taskAttribute = taskData.getRoot().getAttribute(
						IRedmineConstants.TASK_KEY_PREFIX_ISSUE_CF
						+ customValue.getCustomFieldId());
				if (taskAttribute != null) {
					setValue(taskAttribute, customValue.getValue());
				}
			}
		}

		/* Journals */
		if (issue.getJournals() != null) {
			int jrnlCount = 1;
			for (final Journal journal : issue.getJournals().getAll()) {
				final TaskCommentMapper mapper = new TaskCommentMapper();
				mapper.setAuthor(repository
						.createPerson("" + journal.getUserId())); //$NON-NLS-1$
				mapper.setCreationDate(journal.getCreatedOn());
				mapper.setText(journal.getNotes());
				final String issueUrl = RedmineRepositoryConnector.getTaskUrl(
						repository.getUrl(), issue.getId());
				mapper.setUrl(issueUrl
						+ String.format(
								IRedmineConstants.REDMINE_URL_PART_COMMENT,
								journal.getId()));
				mapper.setNumber(jrnlCount++);
				mapper.setCommentId(String.valueOf(journal.getId()));

				taskAttribute = taskData.getRoot().createAttribute(
						TaskAttribute.PREFIX_COMMENT + mapper.getCommentId());
				mapper.applyTo(taskAttribute);
			}
		}

		/* Attachments */
		if (issue.getAttachments() != null) {
			for (final Attachment attachment : issue.getAttachments().getAll()) {
				final TaskAttachmentMapper mapper = new TaskAttachmentMapper();
				mapper.setAttachmentId("" + attachment.getId()); //$NON-NLS-1$
				mapper.setAuthor(repository
						.createPerson("" + attachment.getAuthorId())); //$NON-NLS-1$
				mapper.setDescription(attachment.getDescription());
				mapper.setCreationDate(attachment.getCreatedOn());
				mapper.setContentType(attachment.getContentType());
				mapper.setFileName(attachment.getFilename());
				mapper.setLength(attachment.getFilesize());
				mapper.setUrl(String.format(
						IRedmineConstants.REDMINE_URL_ATTACHMENT_DOWNLOAD,
						repository.getUrl(), attachment.getId()));

				taskAttribute = taskData.getRoot().createAttribute(
						TaskAttribute.PREFIX_ATTACHMENT
						+ mapper.getAttachmentId());
				mapper.applyTo(taskAttribute);
			}
		}

		if (issue.getTimeEntries() != null
				&& issue.getTimeEntries().isViewAllowed()) {
			// TODO kind/label
			taskAttribute = taskData.getRoot().createAttribute(
					IRedmineConstants.TASK_ATTRIBUTE_TIMEENTRY_TOTAL);

			if (issue.getTimeEntries() != null) {
				taskAttribute.setValue("" + issue.getTimeEntries().getSum()); //$NON-NLS-1$

				for (final TimeEntry timeEntry : issue.getTimeEntries()
						.getAll()) {
					final RedmineTaskTimeEntryMapper mapper = new RedmineTaskTimeEntryMapper();
					mapper.setTimeEntryId(timeEntry.getId());
					mapper.setUser(repository
							.createPerson("" + timeEntry.getUserId())); //$NON-NLS-1$
					mapper.setActivityId(timeEntry.getActivityId());
					mapper.setHours(timeEntry.getHours());
					mapper.setSpentOn(timeEntry.getSpentOn());
					mapper.setComments(timeEntry.getComments());
					mapper.setCustomValues(timeEntry.getCustomValues().getAll());

					taskAttribute = taskData.getRoot().createAttribute(
							IRedmineConstants.TASK_ATTRIBUTE_TIMEENTRY_PREFIX
							+ mapper.getTimeEntryId());
					mapper.applyTo(taskAttribute, cfg);
				}
			}

		}

		setTaskKind(issue, cfg, root);

	}

	public static Issue createIssue(final TaskRepository repository,
			final TaskData taskData, final Set<TaskAttribute> oldAttributes,
			final Configuration cfg) throws CoreException {
		final Issue issue = taskData.getTaskId().isEmpty() ? new Issue()
		: new Issue(RedmineUtil.parseIntegerId(taskData.getTaskId()));

		final TaskAttribute root = taskData.getRoot();
		TaskAttribute taskAttribute = null;

		/* Default Attributes */
		for (final RedmineAttribute redmineAttribute : RedmineAttribute
				.values()) {
			if (!redmineAttribute.isOperationValue()
					&& !redmineAttribute.isReadOnly()) {
				setProperty(redmineAttribute, root, issue);
			}
		}

		/* Watcher */
		final TaskAttribute watchersAttribute = root
				.getAttribute(RedmineAttribute.WATCHERS.getTaskKey());
		if (watchersAttribute != null) {
			final LinkedHashSet<String> watchers = new LinkedHashSet<String>(
					watchersAttribute.getValues());

			final TaskAttribute newWatcherAttribute = watchersAttribute
					.getAttribute(RedmineAttribute.WATCHERS_ADD.getTaskKey());
			if (newWatcherAttribute != null
					&& !newWatcherAttribute.getMetaData().isReadOnly()) {
				issue.setWatchersAddAllowed(true);
				for (final String newWatcher : newWatcherAttribute.getValues()) {
					watchers.add(newWatcher);
				}
			}

			final TaskAttribute oldWatcherAttribute = watchersAttribute
					.getAttribute(RedmineAttribute.WATCHERS_REMOVE.getTaskKey());
			if (oldWatcherAttribute != null
					&& !oldWatcherAttribute.getMetaData().isReadOnly()) {
				issue.setWatchersDeleteAllowed(true);
				for (final String oldWatcher : oldWatcherAttribute.getValues()) {
					watchers.remove(oldWatcher);
				}
			}

			if (watchers.size() > 0) {
				final int[] watcherIds = new int[watchers.size()];
				int lv = 0;
				for (final String idVal : watchers) {
					watcherIds[lv++] = Integer.parseInt(idVal);
				}
				issue.setWatcherIds(watcherIds);
			}

		}

		/* Custom Attributes */
		final int[] customFieldIds = cfg.getProjects()
				.getById(issue.getProjectId())
				.getCustomFieldIdsByTrackerId(issue.getTrackerId());
		if (customFieldIds != null && customFieldIds.length > 0) {
			final CustomValues customValues = new CustomValues();
			issue.setCustomValues(customValues);
			for (final int customFieldId : customFieldIds) {
				taskAttribute = root
						.getAttribute(IRedmineConstants.TASK_KEY_PREFIX_ISSUE_CF
								+ customFieldId);
				if (taskAttribute != null) {
					if (TaskAttribute.TYPE_MULTI_SELECT.equals(taskAttribute
							.getMetaData().getType())) {
						final List<String> values = taskAttribute.getValues();
						for (final String value : values) {
							customValues
							.setCustomValue(
									customFieldId,
									formatCustomValue(value,
											customFieldId, cfg));

						}
					} else {
						customValues.setCustomValue(
								customFieldId,
								formatCustomValue(taskAttribute.getValue(),
										customFieldId, cfg));
					}
				}
			}
		}

		/* Operations */
		taskAttribute = root.getMappedAttribute(TaskAttribute.OPERATION);
		if (taskAttribute != null) {
			final RedmineOperation redmineOperation = RedmineOperation
					.fromTaskKey(taskAttribute.getValue());
			taskAttribute = root.getAttribute(TaskAttribute.PREFIX_OPERATION
					+ taskAttribute.getValue());

			if (redmineOperation != null && taskAttribute != null) {
				String value = null;

				if (redmineOperation.isAssociated()) {
					taskAttribute = root.getAttribute(redmineOperation
							.getInputId());
					if (taskAttribute != null) {
						value = taskAttribute.getValue();
					}
				} else if (redmineOperation.needsRestoreValue()) {
					value = taskAttribute.getMetaData().getValue(
							IRedmineConstants.TASK_ATTRIBUTE_OPERATION_RESTORE);
				}

				if (value != null) {
					final RedmineAttribute redmineAttribute = RedmineAttribute
							.fromTaskKey(redmineOperation.getInputId());
					setProperty(redmineAttribute, root, issue);
				}
			}
		}

		setTaskKind(issue, cfg, root);

		return issue;
	}

	/**
	 * Sets the ${task.kind} mylyn property to the redmine tracker name.
	 *
	 * @param issue
	 *            the redmine issue
	 * @param cfg
	 *            the redmine configuration
	 * @param task
	 *            the mylyn task
	 */
	private static void setTaskKind(final Issue issue, final Configuration cfg,
			final TaskAttribute task) {
		if (issue.getTrackerId() != 0) {
			TaskAttribute kind = task.getAttribute(TaskAttribute.TASK_KIND);
			if (kind == null) {
				kind = task.createAttribute(TaskAttribute.TASK_KIND);
			}
			kind.setValue(cfg.getTrackers().getById(issue.getTrackerId())
					.getName());
		}
	}

	public static TimeEntry createTimeEntry(final TaskRepository repository,
			final TaskData taskData, final Set<TaskAttribute> oldAttributes,
			final Configuration cfg) throws CoreException {
		TimeEntry timeEntry = null;

		try {
			final String val = getValue(taskData,
					RedmineAttribute.TIME_ENTRY_HOURS);
			if (!val.isEmpty()) {
				timeEntry = new TimeEntry();

				/* Default Attributes */
				final long milisec = Long.parseLong(val);
				final long minutes = milisec / 60000;
				float hours = minutes / 60;
				hours += minutes % 60 / 60.0;

				timeEntry.setHours(hours);
				timeEntry.setActivityId(Integer.parseInt(getValue(taskData,
						RedmineAttribute.TIME_ENTRY_ACTIVITY)));
				timeEntry.setComments(getValue(taskData,
						RedmineAttribute.TIME_ENTRY_COMMENTS));

				/* Custom Attributes */
				final List<CustomField> customFields = cfg.getCustomFields()
						.getTimeEntryCustomFields();
				if (customFields != null && customFields.size() > 0) {
					final CustomValues customValues = new CustomValues();
					timeEntry.setCustomValues(customValues);
					for (final CustomField customField : customFields) {
						final String value = getValue(taskData,
								IRedmineConstants.TASK_KEY_PREFIX_TIMEENTRY_CF
								+ customField.getId());
						customValues.setCustomValue(
								customField.getId(),
								formatCustomValue(value, customField.getId(),
										cfg));
					}
				}

				/* Extension/Additional Attributes */
				final IRedmineExtensionField additionalFields[] = RedmineCorePlugin
						.getDefault().getExtensionManager()
						.getAdditionalTimeEntryFields(repository);
				for (final IRedmineExtensionField additionalField : additionalFields) {
					final String value = getValue(taskData,
							IRedmineConstants.TASK_KEY_PREFIX_TIMEENTRY_EX
							+ additionalField.getTaskKey());
					timeEntry.addExtensionValue(additionalField.getSubmitKey(),
							value);
				}

			}
		} catch (final NumberFormatException e) {
			timeEntry = null;
			final IStatus status = RedmineCorePlugin.toStatus(e,
					Messages.ERRMSG_ILLEGAL_ATTRIBUTE_VALUE);
			StatusHandler.log(status);
		}

		return timeEntry;
	}

	private static String formatCustomValue(String value,
			final int customFieldId, final Configuration cfg) {
		final CustomField field = cfg.getCustomFields().getById(customFieldId);
		if (field != null) {
			switch (field.getFieldFormat()) {
			case DATE:
				value = value.isEmpty() ? "" : RedmineUtil.formatDate(RedmineUtil.parseDate(value));break; //$NON-NLS-1$
			case BOOL:
				value = Boolean.parseBoolean(value) ? IRedmineConstants.BOOLEAN_TRUE_SUBMIT_VALUE
						: IRedmineConstants.BOOLEAN_FALSE_SUBMIT_VALUE;
			default:
				return value;
			}
		}
		return value;
	}

	private static String getValue(final TaskData taskData,
			final RedmineAttribute redmineAttribute) {
		return getValue(taskData, redmineAttribute.getTaskKey());
	}

	private static String getValue(final TaskData taskData, final String taskKey) {
		final TaskAttribute attribute = taskData.getRoot()
				.getAttribute(taskKey);
		return attribute == null ? "" : attribute.getValue(); //$NON-NLS-1$
	}

	private static void setValue(final TaskAttribute attribute,
			final Object value) {
		if (value == null) {
			attribute.clearValues();
		} else if (value instanceof String) {
			setValue(attribute, (String) value);
		} else if (value instanceof Date) {
			setValue(attribute, (Date) value);
		} else if (value instanceof Integer) {
			setValue(attribute, ((Integer) value).intValue());
		} else if (value instanceof int[]) {
			for (final int intVal : (int[]) value) {
				attribute.addValue(Integer.toString(intVal));
			}
		} else {
			setValue(attribute, value.toString());
		}
	}

	private static void setValue(final TaskAttribute attribute,
			final String value) {
		if (value == null) {
			attribute.setValue(""); //$NON-NLS-1$
		} else if (attribute.getMetaData().getType() == null) {
			attribute.setValue(value);
		} else if (attribute.getMetaData().getType()
				.equals(TaskAttribute.TYPE_MULTI_SELECT)) {
			attribute.addValue(value);
		} else if (attribute.getMetaData().getType()
				.equals(TaskAttribute.TYPE_BOOLEAN)) {
			attribute.setValue(RedmineUtil.parseBoolean(value).toString());
		} else if (attribute.getMetaData().getType()
				.equals(TaskAttribute.TYPE_DATE)
				|| attribute.getMetaData().getType()
				.equals(TaskAttribute.TYPE_DATETIME)) {
			setValue(attribute, RedmineUtil.parseRedmineDate(value));
		} else {
			attribute.setValue(value);
		}

	}

	private static void setValue(final TaskAttribute attribute, final Date value) {
		if (value == null) {
			attribute.setValue(""); //$NON-NLS-1$
		} else {
			attribute.setValue("" + value.getTime()); //$NON-NLS-1$
		}
	}

	private static void setValue(final TaskAttribute attribute, final int value) {
		if (attribute.getId().equals(RedmineAttribute.PROGRESS.getTaskKey())
				&& attribute.getMetaData().getType() == null) {
			attribute.setValue(""); //$NON-NLS-1$
		} else if ((attribute.getMetaData().getType() != null
				&& attribute.getMetaData().getType()
				.equals(TaskAttribute.TYPE_SINGLE_SELECT)
				|| attribute.getMetaData().getType()
				.equals(TaskAttribute.TYPE_PERSON)
				|| attribute.getMetaData().getType()
				.equals(IRedmineConstants.EDITOR_TYPE_PERSON) || attribute
				.getId().equals(RedmineAttribute.PARENT.getTaskKey()))
				&& value < 1) {
			attribute.setValue(""); //$NON-NLS-1$
		} else {
			attribute.setValue("" + value); //$NON-NLS-1$
		}
	}

	private static void setProperty(final RedmineAttribute redmineAttribute,
			final TaskAttribute root, final Issue issue) throws CoreException {
		final Field field = redmineAttribute.getAttributeField();
		if (!redmineAttribute.isReadOnly() && field != null) {
			final TaskAttribute taskAttribute = root
					.getAttribute(redmineAttribute.getTaskKey());
			if (taskAttribute != null) {
				try {
					switch (redmineAttribute) {
					case SUMMARY:
					case DESCRIPTION:
					case COMMENT:
						field.set(issue, taskAttribute.getValue());
						break;
					case PROGRESS:
						field.setInt(
								issue,
								taskAttribute.getValue().isEmpty() ? 0
										: Integer.parseInt(taskAttribute
												.getValue()));
						break;
					case ESTIMATED:
						if (!taskAttribute.getValue().isEmpty()) {
							field.set(issue,
									new Float(taskAttribute.getValue()));
						}
						break;
					default:
						if (redmineAttribute.getType().equals(
								TaskAttribute.TYPE_SINGLE_SELECT)
								|| redmineAttribute.getType().equals(
										TaskAttribute.TYPE_PERSON)
										|| redmineAttribute.getType().equals(
												IRedmineConstants.EDITOR_TYPE_PERSON)
												|| redmineAttribute == RedmineAttribute.PARENT) {
							final int idVal = RedmineUtil
									.parseIntegerId(taskAttribute.getValue());
							if (idVal > 0) {
								field.setInt(issue, idVal);
							}
						} else

							if (redmineAttribute.getType().equals(
									TaskAttribute.TYPE_DATE)) {
								if (!taskAttribute.getValue().isEmpty()) {
									field.set(issue, RedmineUtil
											.parseDate(taskAttribute.getValue()));
								}
							}
					}
				} catch (final Exception e) {
					final IStatus status = RedmineCorePlugin.toStatus(e,
							Messages.ERRMSG_CANT_READ_PROPERTY_X,
							redmineAttribute.name());
					final ILogService log = RedmineCorePlugin
							.getLogService(IssueMapper.class);
					log.error(e, status.getMessage());
					throw new CoreException(status);
				}
			}
		}
	}
}
