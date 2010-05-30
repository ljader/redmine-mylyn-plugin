package net.sf.redmine_mylyn.api.client;

import org.eclipse.core.runtime.IStatus;

public class RedmineApiStatusException extends RedmineApiErrorException {

	private static final long serialVersionUID = 1L;
	
	private IStatus status;
	
	public RedmineApiStatusException(IStatus status) {
		super(status.getException());
		this.status = status;
	}

	public IStatus getStatus() {
		return status;
	}
}
