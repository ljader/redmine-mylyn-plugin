package net.sf.redmine_mylyn.api.exception;

public class RedmineApiAuthenticationException extends RedmineApiErrorException {
	
	private static final long serialVersionUID = 1L;

	public RedmineApiAuthenticationException(String message, Object... params) {
		super(message, params);
	}

	public RedmineApiAuthenticationException(String message, Throwable cause, Object... params) {
		super(message, cause, params);
	}
}
