package net.sf.redmine_mylyn.internal.core;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "net.sf.redmine_mylyn.internal.core.messages"; //$NON-NLS-1$
	public static String ACTIVITY;
	public static String ASSIGNED_TO;
	public static String CATEGORY;
	public static String COMMENT;
	public static String DESCRIPTION;
	public static String DONE_RATIO;
	public static String DUE_DATE;
	public static String ERRMSG_CANT_FIND_ISSUE;
	public static String ERRMSG_CANT_READ_CACHEDATA;
	public static String ERRMSG_CANT_READ_PROPERTY_X;
	public static String ERRMSG_CANT_WRITE_CACHEDATA;
	public static String ERRMSG_CONFIGURATION_UPDATE_FAILED;
	public static String ERRMSG_CANT_REQUEST_CREDENTIALS;
	public static String ERRMSG_ILLEGAL_ATTRIBUTE_VALUE;
	public static String ERRMSG_INVALID_LONG;
	public static String ERRMSG_INVALID_TASKID_X;
	public static String ERRMSG_INVALID_TIMESTAMP_X;
	public static String ERRMSG_MALFORMED_URL;
	public static String ERRMSG_MISSING_ISSUE_STATUS;
	public static String ERRMSG_MISSING_UPDATEDON;
	public static String ERRMSG_NO_MATCHING_CLIENT_VERSION;
	public static String ERRMSG_SHOULD_NEVER_HAPPENS;
	public static String ERRMSG_SYNCRONIZATION_FAILED;
	public static String ERRMSG_TASK_INITIALIZATION_FALED_INSUFFICENT_DATA;
	public static String ERRMSG_X_VALID_INTEGER;
	public static String ERRMSG_X_VALID_UNIXTIME_DATE;
	public static String ESTIMATED_HOURS;
	public static String LAST_MODIFICATION;
	public static String OPERATION_CANCELED;
	public static String PARENT;
	public static String PRIORITY;
	public static String PROGRESS_CHECKING_CHANGED_TASKS;
	public static String PROGRESS_TASK_DOWNLOAD;
	public static String PROJECT;
	public static String REDMINE_CONNECTOR_LABEL;
	public static String REPORTER;
	public static String SPENT_TIME;
	public static String START_DATE;
	public static String STATUS_LEAVE_AS_X;
	public static String STATUS;
	public static String STATUS_MARK_AS_X;
	public static String SUBMITTED;
	public static String SUMMARY;
	public static String TARGET_VERSION;
	public static String TOTAL_HOURS;
	public static String TRACKER;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
