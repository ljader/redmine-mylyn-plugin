package net.sf.redmine_mylyn.core;

import static net.sf.redmine_mylyn.core.IRedmineConstants.TASK_ATTRIBUTE_PARENT;
import static net.sf.redmine_mylyn.core.IRedmineConstants.TASK_ATTRIBUTE_SUBTASKS;
import static net.sf.redmine_mylyn.core.IRedmineConstants.TASK_ATTRIBUTE_STATUS_CHANGE;
import static net.sf.redmine_mylyn.core.IRedmineConstants.TASK_ATTRIBUTE_TIMEENTRY_ACTIVITY;
import static net.sf.redmine_mylyn.core.IRedmineConstants.TASK_ATTRIBUTE_TIMEENTRY_COMMENTS;
import static net.sf.redmine_mylyn.core.IRedmineConstants.TASK_ATTRIBUTE_TIMEENTRY_HOURS;
import static net.sf.redmine_mylyn.core.IRedmineConstants.TASK_ATTRIBUTE_TIMEENTRY_TOTAL;

import java.lang.reflect.Field;
import java.util.EnumSet;

import net.sf.redmine_mylyn.api.client.RedmineApiIssueProperty;
import net.sf.redmine_mylyn.api.model.Issue;
import net.sf.redmine_mylyn.internal.core.Messages;
import net.sf.redmine_mylyn.internal.core.PropertyAccessor;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;


public enum RedmineAttribute {

	ID("<used by search engine>", null, null, TaskAttribute.TYPE_INTEGER, Flag.HIDDEN, Flag.READ_ONLY), //$NON-NLS-1$
	
	@PropertyAccessor(value="subject")
	SUMMARY(Messages.SUMMARY, TaskAttribute.SUMMARY, RedmineApiIssueProperty.SUBJECT, TaskAttribute.TYPE_SHORT_TEXT, Flag.HIDDEN, Flag.REQUIRED),
	@PropertyAccessor("authorId")
	REPORTER(Messages.REPORTER, TaskAttribute.USER_REPORTER, null, TaskAttribute.TYPE_PERSON, Flag.READ_ONLY),
	@PropertyAccessor
	DESCRIPTION(Messages.DESCRIPTION, TaskAttribute.DESCRIPTION, RedmineApiIssueProperty.DESCRIPTION, TaskAttribute.TYPE_LONG_RICH_TEXT, Flag.HIDDEN, Flag.REQUIRED),
	@PropertyAccessor("assignedToId")
	ASSIGNED_TO(Messages.ASSIGNED_TO, TaskAttribute.USER_ASSIGNED, RedmineApiIssueProperty.ASSIGNED_TO,  IRedmineConstants.EDITOR_TYPE_PERSON),
	@PropertyAccessor("createdOn")
	DATE_SUBMITTED(Messages.SUBMITTED, TaskAttribute.DATE_CREATION, null, TaskAttribute.TYPE_DATE, Flag.HIDDEN, Flag.READ_ONLY),
	@PropertyAccessor("updatedOn")
	DATE_UPDATED(Messages.LAST_MODIFICATION, TaskAttribute.DATE_MODIFICATION, null, TaskAttribute.TYPE_DATE, Flag.HIDDEN, Flag.READ_ONLY),
	@PropertyAccessor("startDate")
	DATE_START(Messages.START_DATE,  RedmineAttribute.TASK_KEY_STARTDATE, RedmineApiIssueProperty.START_DATE, TaskAttribute.TYPE_DATE, Flag.HIDDEN),
	@PropertyAccessor("dueDate")
	DATE_DUE(Messages.DUE_DATE, TaskAttribute.DATE_DUE, RedmineApiIssueProperty.DUE_DATE, TaskAttribute.TYPE_DATE, Flag.HIDDEN),
	@PropertyAccessor
	PROJECT(Messages.PROJECT, TaskAttribute.PRODUCT, RedmineApiIssueProperty.PROJECT, TaskAttribute.TYPE_SINGLE_SELECT, Flag.REQUIRED),
	@PropertyAccessor
	PRIORITY(Messages.PRIORITY, TaskAttribute.PRIORITY, RedmineApiIssueProperty.PRIORITY, TaskAttribute.TYPE_SINGLE_SELECT, Flag.HIDDEN, Flag.REQUIRED),
	@PropertyAccessor
	CATEGORY(Messages.CATEGORY, RedmineAttribute.TASK_KEY_CATEGORY, RedmineApiIssueProperty.CATEGORY, TaskAttribute.TYPE_SINGLE_SELECT),
	@PropertyAccessor("fixedVersionId")
	VERSION(Messages.TARGET_VERSION, TaskAttribute.VERSION, RedmineApiIssueProperty.FIXED_VERSION, TaskAttribute.TYPE_SINGLE_SELECT),
	@PropertyAccessor
	TRACKER(Messages.TRACKER, RedmineAttribute.TASK_KEY_TRACKER, RedmineApiIssueProperty.TRACKER, TaskAttribute.TYPE_SINGLE_SELECT, Flag.REQUIRED),
	@PropertyAccessor
	STATUS(Messages.STATUS, TaskAttribute.STATUS, null, TaskAttribute.TYPE_SINGLE_SELECT, Flag.REQUIRED, Flag.HIDDEN),
	@PropertyAccessor("statusId")
	STATUS_CHG(Messages.STATUS,  TASK_ATTRIBUTE_STATUS_CHANGE, RedmineApiIssueProperty.STATUS, TaskAttribute.TYPE_SINGLE_SELECT, Flag.OPERATION),
	@PropertyAccessor("parentId")
	PARENT(Messages.PARENT,  TASK_ATTRIBUTE_PARENT, RedmineApiIssueProperty.PARENT, IRedmineConstants.EDITOR_TYPE_PARENTTASK),
	@PropertyAccessor
	SUBTASKS("Subtasks:",  TASK_ATTRIBUTE_SUBTASKS, null, TaskAttribute.TYPE_TASK_DEPENDENCY, Flag.READ_ONLY),
	COMMENT(Messages.COMMENT, TaskAttribute.COMMENT_NEW, null, TaskAttribute.TYPE_LONG_RICH_TEXT, Flag.HIDDEN),
	@PropertyAccessor("doneRatio")
	PROGRESS(Messages.DONE_RATIO, RedmineAttribute.TASK_KEY_PROGRESS, RedmineApiIssueProperty.DONE_RATIO, TaskAttribute.TYPE_SINGLE_SELECT),
	@PropertyAccessor("estimatedHours")
	ESTIMATED(Messages.ESTIMATED_HOURS, RedmineAttribute.TASK_KEY_ESTIMATE, RedmineApiIssueProperty.ESTIMATED_HOURS, IRedmineConstants.EDITOR_TYPE_ESTIMATED, Flag.HIDDEN),
	WATCHERS("Watchers ", RedmineAttribute.TASK_KEY_WATCHERS, null, IRedmineConstants.EDITOR_TYPE_WATCHERS, Flag.HIDDEN),

	WATCHERS_ADD("Add watcher", RedmineAttribute.TASK_KEY_WATCHERS_ADD, null, IRedmineConstants.EDITOR_TYPE_PERSON, Flag.HIDDEN),
	WATCHERS_REMOVE(null, RedmineAttribute.TASK_KEY_WATCHERS_REMOVE, null, null, Flag.HIDDEN),
	
	TIME_ENTRY_TOTAL(Messages.TOTAL_HOURS, TASK_ATTRIBUTE_TIMEENTRY_TOTAL, null, IRedmineConstants.EDITOR_TYPE_DURATION, Flag.HIDDEN, Flag.READ_ONLY),
	TIME_ENTRY_HOURS(Messages.SPENT_TIME, TASK_ATTRIBUTE_TIMEENTRY_HOURS, null, IRedmineConstants.EDITOR_TYPE_DURATION, Flag.HIDDEN),
	TIME_ENTRY_ACTIVITY(Messages.ACTIVITY, TASK_ATTRIBUTE_TIMEENTRY_ACTIVITY, null, TaskAttribute.TYPE_SINGLE_SELECT, Flag.HIDDEN),
	TIME_ENTRY_COMMENTS(Messages.COMMENT, TASK_ATTRIBUTE_TIMEENTRY_COMMENTS, null, TaskAttribute.TYPE_LONG_TEXT, Flag.HIDDEN)
	; 


	
	public final static String TASK_KEY_CATEGORY = "task.redmine.category"; //$NON-NLS-1$
	public final static String TASK_KEY_TRACKER = "task.redmine.tracker"; //$NON-NLS-1$
	public final static String TASK_KEY_PROGRESS = "task.redmine.progress"; //$NON-NLS-1$
	public final static String TASK_KEY_ESTIMATE = "task.redmine.estimate"; //$NON-NLS-1$
	public final static String TASK_KEY_STARTDATE = "task.redmine.startdate"; //$NON-NLS-1$
	public final static String TASK_KEY_WATCHERS = "task.redmine.watchers"; //$NON-NLS-1$
	public final static String TASK_KEY_WATCHERS_ADD = "task.redmine.watchers.add"; //$NON-NLS-1$
	public final static String TASK_KEY_WATCHERS_REMOVE = "task.redmine.watchers.remove"; //$NON-NLS-1$
	
	private final String prettyName;

	private final String taskKey;
	
	private final RedmineApiIssueProperty apiIssueProperty;
	
	private final String type;
	
	private final EnumSet<Flag> flags;
	
	private Field attributeField;
	
	public static RedmineAttribute getByTaskKey(String taskKey) {
		for (RedmineAttribute attribute : values()) {
			if (taskKey.equals(attribute.getTaskKey())) {
				return attribute;
			}
		}
		return null;
	}

	RedmineAttribute(String prettyName, String taskKey, RedmineApiIssueProperty issueProperty, String type, Flag... flags) {
		this.taskKey = taskKey;
		this.prettyName = prettyName;
		this.type = type;
		this.apiIssueProperty = issueProperty;
		
		this.flags = flags.length==0 || flags[0]==null ? EnumSet.noneOf(Flag.class) : EnumSet.of(flags [0], flags);
		
		try {
			Field field = getClass().getField(name());
			PropertyAccessor accessor = field.getAnnotation(PropertyAccessor.class);
			if(accessor!=null) {
				String fieldName = accessor.value();
				if(fieldName.isEmpty()) {
					fieldName = name().toLowerCase();
					if(type.equals(TaskAttribute.TYPE_SINGLE_SELECT)) {
						fieldName += "Id"; //$NON-NLS-1$
					}
				}
				attributeField = Issue.class.getDeclaredField(fieldName);
				attributeField.setAccessible(true);
				
				if(attributeField==null) {
					IStatus status = new Status(IStatus.ERROR, RedmineCorePlugin.PLUGIN_ID, Messages.ERRMSG_SHOULD_NEVER_HAPPENS);
					StatusHandler.fail(status);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			IStatus status = RedmineCorePlugin.toStatus(e, Messages.ERRMSG_SHOULD_NEVER_HAPPENS);
			StatusHandler.fail(status);
		}
		
		
	}

	public RedmineApiIssueProperty getApiIssueProperty() {
		return apiIssueProperty;
	}

	public String getTaskKey() {
		return taskKey;
	}

	public static RedmineAttribute fromTaskKey(String taskKey) {
		for (RedmineAttribute attr : RedmineAttribute.values()) {
			if (attr.getTaskKey()!=null && attr.getTaskKey().equals(taskKey)) {
				return attr;
			}
		}
		return null;
	}
	
	public String getKind() {
		if (isHidden()) {
			return null;
		}

		switch (this) {
		case REPORTER :
		case ASSIGNED_TO :
			return TaskAttribute.KIND_PEOPLE;
		
		default:
			return TaskAttribute.KIND_DEFAULT;
		}
	}
	
	public String getType() {
		return type;
	}
	
	public boolean isReadOnly() {
		return flags.contains(Flag.READ_ONLY);
	}
	
	public boolean isHidden() {
		return flags.contains(Flag.HIDDEN);
	}
	
	public boolean isRequired() {
		return flags.contains(Flag.REQUIRED);
	}
	
	public boolean isOperationValue() {
		return flags.contains(Flag.OPERATION);
	}
	
	public Field getAttributeField() {
		return attributeField;
	}

	public String getLabel() {
		return prettyName;
	}
	
	@Override
	public String toString() {
		return prettyName;
	}

	public boolean match(TaskAttribute attribute) {
		return attribute!=null && taskKey.equals(attribute.getId());
	}
	
	private static enum Flag {
		READ_ONLY, HIDDEN, CUSTOM_FIELD, REQUIRED, OPERATION
	};

}
