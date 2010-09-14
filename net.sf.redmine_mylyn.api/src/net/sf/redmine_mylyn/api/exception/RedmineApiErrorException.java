package net.sf.redmine_mylyn.api.exception;

import java.text.MessageFormat;

public class RedmineApiErrorException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public RedmineApiErrorException(String message, Object... params) {
		super(MessageFormat.format(message, params));
	}

	public RedmineApiErrorException(String message, Throwable cause, Object... params) {
		super(MessageFormat.format(message, params), cause);
	}
	
}
