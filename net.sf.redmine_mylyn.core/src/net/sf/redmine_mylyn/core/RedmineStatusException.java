package net.sf.redmine_mylyn.core;

import net.sf.redmine_mylyn.api.exception.RedmineApiErrorException;

import org.eclipse.core.runtime.IStatus;

public class RedmineStatusException extends Exception {

	private static final long serialVersionUID = 1L;
	
	private IStatus status;
	
	public RedmineStatusException(IStatus status) {
		super(status.getException());
		this.status = status;
	}

	public RedmineStatusException(RedmineApiErrorException apiException) {
		super(apiException);
		this.status = RedmineCorePlugin.toStatus(apiException, apiException.getMessage());
	}

	public RedmineStatusException(RedmineApiErrorException apiException, String message) {
		super(apiException);
		this.status = RedmineCorePlugin.toStatus(apiException, message);
	}
	
	public IStatus getStatus() {
		return status;
	}
	
}
