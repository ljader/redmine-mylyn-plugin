package net.sf.redmine_mylyn.api.model;

import net.sf.redmine_mylyn.internal.api.model.Property;

public class Version extends Property {

	enum Status {CLOSED, LOCKED, OPEN};
	
	private String description;
	
	private Status status;
}
