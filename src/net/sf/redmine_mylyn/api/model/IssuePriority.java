package net.sf.redmine_mylyn.api.model;

import net.sf.redmine_mylyn.internal.api.model.SortedProperty;

public class IssuePriority extends SortedProperty {

	private static final long serialVersionUID = 1L;

	protected boolean isDefault;

	public boolean isDefault() {
		return isDefault;
	}

	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}

	
}
