package net.sf.redmine_mylyn.core;

public interface IRedmineConstants {

	public final static String REDMINE_URL_TICKET = "/issues/show/"; //$NON-NLS-1$
	public final static String REDMINE_URL_PART_COMMENT = "#note-%d"; //$NON-NLS-1$
	public final static String REDMINE_URL_ATTACHMENT_DOWNLOAD = "%s/attachments/download/%d"; //$NON-NLS-1$

	public final static String EDITOR_TYPE_ESTIMATED = "estimated"; //$NON-NLS-1$
	public final static String EDITOR_TYPE_PARENTTASK = "parenttask"; //$NON-NLS-1$
	public final static String EDITOR_TYPE_DURATION = "duration"; //$NON-NLS-1$
	public final static String EDITOR_TYPE_WATCHERS = "watchers"; //$NON-NLS-1$
	public final static String EDITOR_TYPE_PERSON = "redmine_person"; //$NON-NLS-1$
	
	public final static String TASK_ATTRIBUTE_TIMEENTRY = "task.redmine.timeentry"; //$NON-NLS-1$
	public final static String TASK_ATTRIBUTE_TIMEENTRY_PREFIX = "task.redmine.timeentry."; //$NON-NLS-1$
	public final static String TASK_ATTRIBUTE_TIMEENTRY_AUTHOR = "task.redmine.timeentry.author"; //$NON-NLS-1$
	public final static String TASK_ATTRIBUTE_TIMEENTRY_ACTIVITY = "task.redmine.timeentry.activity"; //$NON-NLS-1$
	public final static String TASK_ATTRIBUTE_TIMEENTRY_HOURS = "task.redmine.timeentry.hours"; //$NON-NLS-1$
	public final static String TASK_ATTRIBUTE_TIMEENTRY_SPENTON = "task.redmine.timeentry.spenton"; //$NON-NLS-1$
	public final static String TASK_ATTRIBUTE_TIMEENTRY_COMMENTS = "task.redmine.timeentry.comments"; //$NON-NLS-1$
//	public final static String TASK_ATTRIBUTE_TIMEENTRY_CUSTOMVALUE = "task.redmine.timeentry.customvalue.";
//	public final static String TASK_ATTRIBUTE_TIMEENTRY_CUSTOMVALUES = "task.redmine.timeentry.customvalues";
	public final static String TASK_ATTRIBUTE_TIMEENTRY_TOTAL = "task.redmine.timeentry.total"; //$NON-NLS-1$
	public final static String TASK_ATTRIBUTE_TIMEENTRY_NEW = "task.redmine.timeentry.new"; //$NON-NLS-1$

	public final static String TASK_ATTRIBUTE_STATUS_CHANGE = "task.redmine.status.change"; //$NON-NLS-1$
	public final static String TASK_ATTRIBUTE_PARENT = "task.redmine.parent"; //$NON-NLS-1$
	public final static String TASK_ATTRIBUTE_SUBTASKS = "task.redmine.subtasks"; //$NON-NLS-1$
	public final static String TASK_ATTRIBUTE_OPERATION_RESTORE = "task.redmine.operation.restorevalue"; //$NON-NLS-1$

	public final static String TASK_KEY_PREFIX_ISSUE_CF = "task.redmine.custom."; //$NON-NLS-1$
	public final static String TASK_KEY_PREFIX_TIMEENTRY_CF = "task.redmine.timeentry.custom."; //$NON-NLS-1$
	public final static String TASK_KEY_PREFIX_ISSUE_EX = "task.redmine.extension.field."; //$NON-NLS-1$
	public final static String TASK_KEY_PREFIX_TIMEENTRY_EX = "task.redmine.timeentry.extension.field."; //$NON-NLS-1$
	
	public final static String REPOSITORY_SETTING_API_KEY = "API_KEY"; //$NON-NLS-1$
	
	public final static String BOOLEAN_TRUE_SUBMIT_VALUE  = "1"; //$NON-NLS-1$
	public final static String BOOLEAN_FALSE_SUBMIT_VALUE  = "0"; //$NON-NLS-1$
	
	public final static String EMPTY_DURATION_VALUE = "00:00"; //$NON-NLS-1$
	
	public final static  String REGEX_INTEGER = "^\\d+$"; //$NON-NLS-1$

	public final static  String DATE_FORMAT = "yyyy-MM-dd"; //$NON-NLS-1$
	
	public final static String REDMINE_URL_REVISION = "/repositories/revision/"; //$NON-NLS-N$

}
