package net.sf.redmine_mylyn.api.client;

public class RedmineApiRemoteException extends RedmineApiErrorException {
	
	private static final long serialVersionUID = 1L;

	public RedmineApiRemoteException(String message, String... params) {
		super(message, params);
	}

	public RedmineApiRemoteException(String message, Throwable cause, String... params) {
		super(message, cause, params);
	}
}
