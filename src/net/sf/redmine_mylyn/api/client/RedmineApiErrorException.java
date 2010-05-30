package net.sf.redmine_mylyn.api.client;

public class RedmineApiErrorException extends Exception {
	
	public RedmineApiErrorException(Throwable cause) {
		super(cause.getMessage(), cause);
	}

}
