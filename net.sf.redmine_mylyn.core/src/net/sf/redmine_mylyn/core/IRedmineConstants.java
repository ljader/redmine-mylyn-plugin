package net.sf.redmine_mylyn.core;

public interface IRedmineConstants {

	public final static String REDMINE_URL_TICKET = "/issues/show/";
	public final static String REDMINE_URL_PART_COMMENT = "#note-%d";
	public final static String REDMINE_URL_ATTACHMENT_DOWNLOAD = "%s/attachments/download/%d";

	public final static String EDITOR_TYPE_ESTIMATED = "estimated";
	public final static String EDITOR_TYPE_PARENTTASK = "parenttask";
	
	public final static String TASK_ATTRIBUTE_TIMEENTRY = "task.redmine.timeentry";
	public final static String TASK_ATTRIBUTE_TIMEENTRY_PREFIX = "task.redmine.timeentry.";
	public final static String TASK_ATTRIBUTE_TIMEENTRY_AUTHOR = "task.redmine.timeentry.author";
	public final static String TASK_ATTRIBUTE_TIMEENTRY_ACTIVITY = "task.redmine.timeentry.activity";
	public final static String TASK_ATTRIBUTE_TIMEENTRY_HOURS = "task.redmine.timeentry.hours";
	public final static String TASK_ATTRIBUTE_TIMEENTRY_SPENTON = "task.redmine.timeentry.spenton";
	public final static String TASK_ATTRIBUTE_TIMEENTRY_COMMENTS = "task.redmine.timeentry.comments";
//	public final static String TASK_ATTRIBUTE_TIMEENTRY_CUSTOMVALUE = "task.redmine.timeentry.customvalue.";
//	public final static String TASK_ATTRIBUTE_TIMEENTRY_CUSTOMVALUES = "task.redmine.timeentry.customvalues";
	public final static String TASK_ATTRIBUTE_TIMEENTRY_TOTAL = "task.redmine.timeentry.total";
	public final static String TASK_ATTRIBUTE_TIMEENTRY_NEW = "task.redmine.timeentry.new";

	public final static String TASK_ATTRIBUTE_STATUS_CHANGE = "task.redmine.status.change";
	public final static String TASK_ATTRIBUTE_PARENT = "task.redmine.parent";
	public final static String TASK_ATTRIBUTE_OPERATION_RESTORE = "task.redmine.operation.restorevalue";

	public final static String TASK_KEY_PREFIX_ISSUE_CF = "task.redmine.custom.";
	public final static String TASK_KEY_PREFIX_TIMEENTRY_CF = "task.redmine.timeentry.custom.";

}
