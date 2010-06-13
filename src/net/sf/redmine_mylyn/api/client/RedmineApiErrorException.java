package net.sf.redmine_mylyn.api.client;

public class RedmineApiErrorException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public RedmineApiErrorException(Throwable cause) {
		super(cause.getMessage(), cause);
	}

}
