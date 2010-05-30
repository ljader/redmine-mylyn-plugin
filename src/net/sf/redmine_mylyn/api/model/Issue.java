package net.sf.redmine_mylyn.api.model;

import java.util.Date;

public class Issue {

	private int id;
	
	private Tracker tracker;
	
	private Project project;
	
	private String subject;
	
	private String description;
	
	private Date dueDate;
	
	private IssueCategory category;
	
	private IssueStatus status;
	
	private User assignedTo;
	
	private IssuePriority priority;
	
	private Version fixedVersion;
	
	private User author;
	
	private Date createdOn;
	
	private Date updatedOn;
	
	private Date startDate;
	
	private int doneRatio;
	
	private float estimatedHours;
	
	private Issue parent;
	
//	private CustomValueContainer customValues;

}
