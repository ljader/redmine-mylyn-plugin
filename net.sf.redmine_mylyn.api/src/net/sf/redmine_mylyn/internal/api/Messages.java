package net.sf.redmine_mylyn.internal.api;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "net.sf.redmine_mylyn.internal.api.messages"; //$NON-NLS-1$
	public static String AUTHENTICATION_CANCELED;
	public static String AUTHENTICATION_REQUIRED;
	public static String ERRMSG_ADDITIONAL_HTTPAUTH_NOT_SUPPORTED;
	public static String ERRMSG_AUTH_TOKEN_REQUEST_FAILED;
	public static String ERRMSG_CANT_PICK_SUBMIT_VALUE;
	public static String ERRMSG_CANT_RESTORE_QUERYFILTER_INVALID_FIELDID_X;
	public static String ERRMSG_CONFIGURATION_SERIALIZATION_FAILED;
	public static String ERRMSG_CREATION_OF_SUBMIT_DATA_FAILED;
	public static String ERRMSG_INPUTSTREAM_PARSING_FAILED;
	public static String ERRMSG_INPUTSTREAM_PARSING_FAILED_CONFIG_ERROR_X;
	public static String ERRMSG_INPUTSTREAM_PARSING_FAILED_X;
	public static String ERRMSG_INVALID_ENCODING_X;
	public static String ERRMSG_INVALID_REDMINE_URL;
	public static String ERRMSG_METHOD_EXECUTION_FAILED;
	public static String ERRMSG_METHOD_EXECUTION_FAILED_INVALID_ENCODING;
	public static String ERRMSG_METHOD_EXECUTION_FAILED_X;
	public static String ERRMSG_MISSING_CREDENTIALS_SYNCHRONIZATION_FAILED;
	public static String ERRMSG_PARAMETER_X_INVALID_INTEGER;
	public static String ERRMSG_PARAMETER_X_INVALID_MILISEC;
	public static String ERRMSG_QUERY_FIELD_INVALID_INTEGER_X_X;
	public static String ERRMSG_REST_SERVICE_NOT_ENABLED_OR_INVALID_CGI;
	public static String ERRMSG_SERVER_ERROR;
	public static String ERRMSG_UNEXCPECTED_EXCEPTION_METHOD_EXECUTION_FAILED;
	public static String ERRMSG_UNEXCPECTED_EXCEPTION_METHOD_EXECUTION_FAILED_X;
	public static String ERRMSG_UNEXPECTED_HTTP_STATUS_X;
	public static String ERRMSG_UNSUPPORTED_REDMINE_VERSION;
	public static String ERRMSG_UPDATING_ATTRIBUTES_FAILED;
	public static String LOG_HTTP_METHOD_X_X;
	public static String LOG_HTTP_METHOD_X_X_X;
	public static String PROGRESS_DETECT_REDMINE_VERSION;
	public static String PROGRESS_DOWNLOAD_ATTACHMENT;
	public static String PROGRESS_EXECUTE_QUERY;
	public static String PROGRESS_FETCH_ISSUE;
	public static String PROGRESS_FETCH_ISSUES;
	public static String PROGRESS_REQUEST_AUTHTOKEN;
	public static String PROGRESS_SEARCH_UPDATED_ISSUES;
	public static String PROGRESS_UPDATING_ATTRIBUTES;
	public static String PROGRESS_UPLOAD_ATTACHMENT;
	public static String PROGRESS_UPLOAD_TASK;
	public static String Q_ALL;
	public static String Q_CLOSED;
	public static String Q_CONTAINS;
	public static String Q_CONTAINS_NOT;
	public static String Q_CURRENT_WEEK;
	public static String Q_DAY_AGO;
	public static String Q_DAYS_LATER;
	public static String Q_GREATER_THEN;
	public static String Q_IS;
	public static String Q_IS_NOT;
	public static String Q_LESS_THE_DAYS_AGO;
	public static String Q_LESS_THEN;
	public static String Q_LESS_THEN_DAYS_LATER;
	public static String Q_MORE_THEN_DAYS_AGOU;
	public static String Q_MORE_THEN_DAYS_LATER;
	public static String Q_NONE;
	public static String Q_OPEN;
	public static String Q_TODAY;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
