package net.sf.redmine_mylyn.core;

import net.sf.redmine_mylyn.api.client.RedmineApiStatusException;

import org.eclipse.core.runtime.IStatus;

public class RedmineStatusException extends Exception {

	private static final long serialVersionUID = 1L;
	
	private IStatus status;
	
	public RedmineStatusException(IStatus status) {
		super(status.getException());
		this.status = status;
	}

	public RedmineStatusException(RedmineApiStatusException apiException) {
		super(apiException.getStatus().getException());
		this.status = apiException.getStatus();
	}
	
	public IStatus getStatus() {
		return status;
	}
	
}
