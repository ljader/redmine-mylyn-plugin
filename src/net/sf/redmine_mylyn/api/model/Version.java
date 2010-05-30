package net.sf.redmine_mylyn.api.model;

public class Version extends Property {

	enum Status {CLOSED, LOCKED, OPEN};
	
	private String description;
	
	private Status status;
}
