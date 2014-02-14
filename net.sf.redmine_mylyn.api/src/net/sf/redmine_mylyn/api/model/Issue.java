
package net.sf.redmine_mylyn.api.model;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import net.sf.redmine_mylyn.api.client.RedmineApiIssueProperty;
import net.sf.redmine_mylyn.api.model.container.Attachments;
import net.sf.redmine_mylyn.api.model.container.CustomValues;
import net.sf.redmine_mylyn.api.model.container.Journals;
import net.sf.redmine_mylyn.api.model.container.TimeEntries;
import net.sf.redmine_mylyn.internal.api.client.IssuePropertyMapping;
import net.sf.redmine_mylyn.internal.api.parser.adapter.type.WatchersType;

@XmlRootElement
@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public class Issue implements IModel {

	private static final long serialVersionUID = 1L;

	@XmlAttribute(required=true)
	private int id;
	
	@XmlAttribute(required=true)
	private boolean editAllowed;
	
	private boolean watchersViewAllowed;
	
	private boolean watchersAddAllowed;
	
	private boolean watchersDeleteAllowed;
	
	@IssuePropertyMapping(RedmineApiIssueProperty.SUBJECT)
	private String subject;
	
	
	@IssuePropertyMapping(RedmineApiIssueProperty.DESCRIPTION)
	private String description;

	private Date createdOn;
	
	private Date updatedOn;
	
	@IssuePropertyMapping(RedmineApiIssueProperty.TRACKER)
	private int trackerId;
	
	@IssuePropertyMapping(RedmineApiIssueProperty.PROJECT)
	private int projectId;

	@IssuePropertyMapping(RedmineApiIssueProperty.STATUS)
	private int statusId;

	@IssuePropertyMapping(RedmineApiIssueProperty.PRIORITY)
	private int priorityId;
	
	private boolean watched;
	
	private int[] watcherIds;
	
	@IssuePropertyMapping(RedmineApiIssueProperty.START_DATE)
	private Date startDate;
	
	@IssuePropertyMapping(RedmineApiIssueProperty.DUE_DATE)
	private Date dueDate;

	@IssuePropertyMapping(RedmineApiIssueProperty.DONE_RATIO)
	private int doneRatio;
	
	@IssuePropertyMapping(RedmineApiIssueProperty.ESTIMATED_HOURS)
	private Float estimatedHours;

	private int authorId;
	
	@IssuePropertyMapping(RedmineApiIssueProperty.CATEGORY)
	private int categoryId;

	@IssuePropertyMapping(RedmineApiIssueProperty.ASSIGNED_TO)
	private int assignedToId;

	@IssuePropertyMapping(RedmineApiIssueProperty.FIXED_VERSION)
	private int fixedVersionId;
	
	@IssuePropertyMapping(RedmineApiIssueProperty.PARENT)
	private int parentId;

	@IssuePropertyMapping(RedmineApiIssueProperty.NOTES)
	private String notes;
	
	@XmlList
	private int [] subtasks;

	private boolean closed;
	
	public Issue(){
		
	}
	
	public Issue(int id){
		this.id = id;
	}
	
	@XmlElement(name="availableStatus")
	@XmlList
	private int [] availableStatusId;

	private CustomValues customValues;

	private Journals journals;

	private Attachments attachments;

	//TODO not implemented yet
	//IssueRelations

	private TimeEntries timeEntries;

	@XmlElement(name="watchers")
	void setWatchers(WatchersType watchers) {
		watcherIds = watchers.watchers;
		watchersViewAllowed = watchers.viewAllowed;
		watchersAddAllowed = watchers.addAllowed;
		watchersDeleteAllowed = watchers.deleteAllowed;
	}

	public boolean isEditAllowed() {
		return editAllowed;
	}

	public void setEditAllowed(boolean editAllowed) {
		this.editAllowed = editAllowed;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Date getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(Date updatedOn) {
		this.updatedOn = updatedOn;
	}

	public int getTrackerId() {
		return trackerId;
	}

	public void setTrackerId(int trackerId) {
		this.trackerId = trackerId;
	}

	public int getProjectId() {
		return projectId;
	}

	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}

	public int getStatusId() {
		return statusId;
	}

	public void setStatusId(int statusId) {
		this.statusId = statusId;
	}

	public int getPriorityId() {
		return priorityId;
	}

	public void setPriorityId(int priorityId) {
		this.priorityId = priorityId;
	}

	public boolean isWatched() {
		return watched;
	}

	public void setWatched(boolean watched) {
		this.watched = watched;
	}

	public int[] getWatcherIds() {
		if(watcherIds==null) {
			watcherIds = new int[0];
		}
		return watcherIds;
	}

	public void setWatcherIds(int[] watcherIds) {
		this.watcherIds = watcherIds;
	}

	public boolean isWatchersViewAllowed() {
		return watchersViewAllowed;
	}

	public void setWatchersViewAllowed(boolean watchersViewAllowed) {
		this.watchersViewAllowed = watchersViewAllowed;
	}

	public boolean isWatchersAddAllowed() {
		return watchersAddAllowed;
	}

	public void setWatchersAddAllowed(boolean watchersAddAllowed) {
		this.watchersAddAllowed = watchersAddAllowed;
	}

	public boolean isWatchersDeleteAllowed() {
		return watchersDeleteAllowed;
	}

	public void setWatchersDeleteAllowed(boolean watchersDeleteAllowed) {
		this.watchersDeleteAllowed = watchersDeleteAllowed;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public int getDoneRatio() {
		return doneRatio;
	}

	public void setDoneRatio(int doneRatio) {
		this.doneRatio = doneRatio;
	}

	public float getEstimatedHours() {
		return estimatedHours;
	}

	public void setEstimatedHours(float estimatedHours) {
		this.estimatedHours = estimatedHours;
		if (this.estimatedHours == 0) {
			this.estimatedHours = null;
		}
	}

	public int getAuthorId() {
		return authorId;
	}

	public void setAuthorId(int authorId) {
		this.authorId = authorId;
	}

	public int getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}

	public int getAssignedToId() {
		return assignedToId;
	}

	public void setAssignedToId(int assignedToId) {
		this.assignedToId = assignedToId;
	}

	public int getFixedVersionId() {
		return fixedVersionId;
	}

	public void setFixedVersionId(int fixedVersionId) {
		this.fixedVersionId = fixedVersionId;
	}

	public int getParentId() {
		return parentId;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

	public int[] getSubtasks() {
		return subtasks;
	}

	public void setSubtasks(int[] subtasks) {
		this.subtasks = subtasks;
	}

	public int[] getAvailableStatusId() {
		return availableStatusId;
	}

	public void setAvailableStatusId(int[] availableStatusId) {
		this.availableStatusId = availableStatusId;
	}

	public CustomValues getCustomValues() {
		return customValues;
	}

	public void setCustomValues(CustomValues customValues) {
		this.customValues = customValues;
	}

	public Journals getJournals() {
		return journals;
	}

	public void setJournals(Journals journals) {
		this.journals = journals;
	}

	public Attachments getAttachments() {
		return attachments;
	}

	public void setAttachments(Attachments attachments) {
		this.attachments = attachments;
	}

	public TimeEntries getTimeEntries() {
		return timeEntries;
	}

	public void setTimeEntries(TimeEntries timeEntries) {
		this.timeEntries = timeEntries;
	}

	public int getId() {
		return id;
	}

	public boolean isClosed() {
		return closed;
	}
	
	public void setClosed(boolean closed) {
		this.closed = closed;
	}
	
	public String getNotes() {
		return notes;
	}
	
	public void setNotes(String notes) {
		this.notes = notes;
	}
}
