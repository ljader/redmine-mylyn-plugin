package net.sf.redmine_mylyn.api.model;

import java.io.Serializable;

public class CustomField implements Serializable {
	
	enum Type {IssueCustomField, TimeEntryCustomField};

	enum Format {STRING, TEXT, INT, FLOAT, LIST, DATE, BOOL};

	private static final long serialVersionUID = 1L;

	private int id;
	
	private Type type;
	
	private String name;
	
	private Format fieldFormat;
	
	private String[] possibleValues; 
	
	private String regexp;
	
	private int minLength;

	private int maxLength;
	
	private boolean isRequired;
	
	private boolean isForAll;

	private boolean isFilter;
	
	private String defaultValue;
	
}
