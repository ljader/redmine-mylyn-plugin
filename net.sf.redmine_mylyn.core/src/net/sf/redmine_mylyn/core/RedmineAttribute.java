package net.sf.redmine_mylyn.core;

import static net.sf.redmine_mylyn.core.IRedmineConstants.TASK_ATTRIBUTE_PARENT;
import static net.sf.redmine_mylyn.core.IRedmineConstants.TASK_ATTRIBUTE_STATUS_CHANGE;
import static net.sf.redmine_mylyn.core.IRedmineConstants.TASK_ATTRIBUTE_TIMEENTRY_ACTIVITY;
import static net.sf.redmine_mylyn.core.IRedmineConstants.TASK_ATTRIBUTE_TIMEENTRY_COMMENTS;
import static net.sf.redmine_mylyn.core.IRedmineConstants.TASK_ATTRIBUTE_TIMEENTRY_HOURS;
import static net.sf.redmine_mylyn.core.IRedmineConstants.TASK_ATTRIBUTE_TIMEENTRY_TOTAL;

import java.lang.reflect.Field;
import java.util.EnumSet;

import net.sf.redmine_mylyn.api.model.Issue;
import net.sf.redmine_mylyn.internal.core.PropertyAccessor;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;


public enum RedmineAttribute {

	ID("<used by search engine>", null, TaskAttribute.TYPE_INTEGER, Flag.HIDDEN, Flag.READ_ONLY),
	
	@PropertyAccessor(value="subject")
	SUMMARY("Summary:", TaskAttribute.SUMMARY, TaskAttribute.TYPE_SHORT_TEXT, Flag.HIDDEN, Flag.REQUIRED),
	@PropertyAccessor("authorId")
	REPORTER("Reporter:", TaskAttribute.USER_REPORTER, TaskAttribute.TYPE_PERSON, Flag.READ_ONLY),
	@PropertyAccessor
	DESCRIPTION("Description:", TaskAttribute.DESCRIPTION, TaskAttribute.TYPE_LONG_RICH_TEXT, Flag.HIDDEN, Flag.REQUIRED),
	@PropertyAccessor("assignedToId")
	ASSIGNED_TO("Assigned To:", TaskAttribute.USER_ASSIGNED, TaskAttribute.TYPE_SINGLE_SELECT),
	@PropertyAccessor("createdOn")
	DATE_SUBMITTED("Submitted:", TaskAttribute.DATE_CREATION, TaskAttribute.TYPE_DATE, Flag.HIDDEN, Flag.READ_ONLY),
	@PropertyAccessor("updatedOn")
	DATE_UPDATED("Last Modification:", TaskAttribute.DATE_MODIFICATION, TaskAttribute.TYPE_DATE, Flag.HIDDEN, Flag.READ_ONLY),
	@PropertyAccessor("startDate")
	DATE_START("Start Date:",  RedmineAttribute.TASK_KEY_STARTDATE, TaskAttribute.TYPE_DATE, Flag.HIDDEN),
	@PropertyAccessor("dueDate")
	DATE_DUE("Due Date:", TaskAttribute.DATE_DUE, TaskAttribute.TYPE_DATE, Flag.HIDDEN),
	@PropertyAccessor
	PROJECT("Project:", TaskAttribute.PRODUCT, TaskAttribute.TYPE_SINGLE_SELECT, Flag.REQUIRED),
	@PropertyAccessor
	PRIORITY("Priority:", TaskAttribute.PRIORITY, TaskAttribute.TYPE_SINGLE_SELECT, Flag.HIDDEN, Flag.REQUIRED),
	@PropertyAccessor
	CATEGORY("Category:", RedmineAttribute.TASK_KEY_CATEGORY, TaskAttribute.TYPE_SINGLE_SELECT),
	@PropertyAccessor("fixedVersionId")
	VERSION("Target version:", TaskAttribute.VERSION, TaskAttribute.TYPE_SINGLE_SELECT),
	@PropertyAccessor
	TRACKER("Tracker:", RedmineAttribute.TASK_KEY_TRACKER, TaskAttribute.TYPE_SINGLE_SELECT, Flag.REQUIRED),
	@PropertyAccessor
	STATUS("Status:", TaskAttribute.STATUS, TaskAttribute.TYPE_SINGLE_SELECT, Flag.REQUIRED, Flag.HIDDEN),
	@PropertyAccessor("statusId")
	STATUS_CHG("Status:",  TASK_ATTRIBUTE_STATUS_CHANGE, TaskAttribute.TYPE_SINGLE_SELECT, Flag.OPERATION),
	@PropertyAccessor("parentId")
	PARENT("Parent:",  TASK_ATTRIBUTE_PARENT, IRedmineConstants.EDITOR_TYPE_PARENTTASK),
	COMMENT("Comment: ", TaskAttribute.COMMENT_NEW, TaskAttribute.TYPE_LONG_RICH_TEXT, Flag.HIDDEN),
	@PropertyAccessor("doneRatio")
	PROGRESS("Done ratio: ", RedmineAttribute.TASK_KEY_PROGRESS, TaskAttribute.TYPE_SINGLE_SELECT),
	@PropertyAccessor("estimatedHours")
	ESTIMATED("Estimated hours: ", RedmineAttribute.TASK_KEY_ESTIMATE, IRedmineConstants.EDITOR_TYPE_ESTIMATED, Flag.HIDDEN),
	
	TIME_ENTRY_TOTAL("Total (hours):", TASK_ATTRIBUTE_TIMEENTRY_TOTAL, TaskAttribute.TYPE_SHORT_TEXT, Flag.HIDDEN, Flag.READ_ONLY),
	TIME_ENTRY_HOURS("Spent time (hours):", TASK_ATTRIBUTE_TIMEENTRY_HOURS, IRedmineConstants.EDITOR_TYPE_DURATION, Flag.HIDDEN),
	TIME_ENTRY_ACTIVITY("Activity:", TASK_ATTRIBUTE_TIMEENTRY_ACTIVITY, TaskAttribute.TYPE_SINGLE_SELECT, Flag.HIDDEN),
	TIME_ENTRY_COMMENTS("Comment:", TASK_ATTRIBUTE_TIMEENTRY_COMMENTS, TaskAttribute.TYPE_LONG_TEXT, Flag.HIDDEN)
	; 


	
	public final static String TASK_KEY_CATEGORY = "task.redmine.category";
	public final static String TASK_KEY_TRACKER = "task.redmine.tracker";
	public final static String TASK_KEY_PROGRESS = "task.redmine.progress";
	public final static String TASK_KEY_ESTIMATE = "task.redmine.estimate";
	public final static String TASK_KEY_STARTDATE = "task.redmine.startdate";
	
	private final String prettyName;

	private final String taskKey;
	
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

	RedmineAttribute(String prettyName, String taskKey, String type, Flag... flags) {
		this.taskKey = taskKey;
		this.prettyName = prettyName;
		this.type = type;
		
		this.flags = flags.length==0 || flags[0]==null ? EnumSet.noneOf(Flag.class) : EnumSet.of(flags [0], flags);
		
		try {
			Field field = getClass().getField(name());
			PropertyAccessor accessor = field.getAnnotation(PropertyAccessor.class);
			if(accessor!=null) {
				String fieldName = accessor.value();
				if(fieldName.equals("")) {
					fieldName = name().toLowerCase();
					if(type.equals(TaskAttribute.TYPE_SINGLE_SELECT)) {
						fieldName += "Id";
					}
				}
				attributeField = Issue.class.getDeclaredField(fieldName);
				attributeField.setAccessible(true);
				
				if(attributeField==null) {
					IStatus status = new Status(IStatus.ERROR, RedmineCorePlugin.PLUGIN_ID, "Should never happens");
					StatusHandler.fail(status);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			IStatus status = RedmineCorePlugin.toStatus(e, "Should never happens");
			StatusHandler.fail(status);
		}
		
		
	}
	
	RedmineAttribute(String prettyName, String taskKey, String type) {
		this(prettyName, taskKey, type, (Flag)null);
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
		}
		return TaskAttribute.KIND_DEFAULT;
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
