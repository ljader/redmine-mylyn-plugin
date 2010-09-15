package net.sf.redmine_mylyn.api.exception;

public class RedmineApiRemoteException extends RedmineApiErrorException {
	
	private static final long serialVersionUID = 1L;

	public RedmineApiRemoteException(String message, Object... params) {
		super(message, params);
	}

	public RedmineApiRemoteException(String message, Throwable cause, Object... params) {
		super(message, cause, params);
	}
}
