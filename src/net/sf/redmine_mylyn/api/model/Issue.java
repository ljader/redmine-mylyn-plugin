package net.sf.redmine_mylyn.api.model;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import net.sf.redmine_mylyn.api.model.container.Attachments;
import net.sf.redmine_mylyn.api.model.container.CustomValues;
import net.sf.redmine_mylyn.api.model.container.Journals;
import net.sf.redmine_mylyn.api.model.container.TimeEntries;

@XmlRootElement
@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public class Issue implements IModel {

	private static final long serialVersionUID = 1L;

	@XmlAttribute
	private int id;
	
	private String subject;
	
	private String description;

	private Date createdOn;
	
	private Date updatedOn;
	
	private int trackerId;
	
	private int projectId;

	private int statusId;

	private int priorityId;
	
	private boolean watched;
	
	@XmlElement(name="watchers")
	@XmlList
	private int[] watcherIds;
	
	private Date startDate;
	
	private Date dueDate;

	private int doneRatio;
	
	private float estimatedHours;

	private int authorId;
	
	private int categoryId;

	private int assignedToId;

	private int fixedVersionId;
	
	private int parentId;

	@XmlElement(name="availableStatus")
	@XmlList
	private int [] availableStatusId;

	private CustomValues customValues;

	private Journals journals;

	private Attachments attachments;

	//TODO
	//IssueRelations

	private TimeEntries timeEntries;

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
		return watcherIds;
	}

	public void setWatcherIds(int[] watcherIds) {
		this.watcherIds = watcherIds;
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

	
}
