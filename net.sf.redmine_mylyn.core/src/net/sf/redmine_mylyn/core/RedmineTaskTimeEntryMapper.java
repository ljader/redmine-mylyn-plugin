package net.sf.redmine_mylyn.core;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import net.sf.redmine_mylyn.api.model.Configuration;
import net.sf.redmine_mylyn.api.model.CustomField;
import net.sf.redmine_mylyn.api.model.CustomValue;
import net.sf.redmine_mylyn.api.model.Project;
import net.sf.redmine_mylyn.api.model.TimeEntryActivity;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.tasks.core.IRepositoryPerson;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;

public class RedmineTaskTimeEntryMapper {

	private int id;

	private float hours;
	
	private int activityId;

	private IRepositoryPerson user;
	
	private Date spentOn;
	
	private String comments;
	
	private List<CustomValue> customValues;
	
	public static RedmineTaskTimeEntryMapper fromTimeEntryAttribute(TaskAttribute attribute) {
		RedmineTaskTimeEntryMapper mapper = new RedmineTaskTimeEntryMapper();
		mapper.readTaskAttribute(attribute);
		return mapper;
	}
	
	public void applyTo(TaskAttribute taskAttribute, Configuration configuration) {
		Assert.isNotNull(taskAttribute);
		
		TaskData taskData = taskAttribute.getTaskData();
		TaskAttributeMapper mapper = taskData.getAttributeMapper();
		
		String projectVal = taskData.getRoot().getAttribute(RedmineAttribute.PROJECT.getTaskKey()).getValue();
		Project project = configuration.getProjects().getById(RedmineUtil.parseIntegerId(projectVal));
		
		taskAttribute.getMetaData().defaults().setType(IRedmineConstants.TASK_ATTRIBUTE_TIMEENTRY);
		if (getTimeEntryId() > 0) {
			mapper.setIntegerValue(taskAttribute, getTimeEntryId());
		}
		if (getHours() > 0f) {
			TaskAttribute child = taskAttribute.createMappedAttribute(IRedmineConstants.TASK_ATTRIBUTE_TIMEENTRY_HOURS);
			child.getMetaData().defaults().setType(RedmineAttribute.TIME_ENTRY_HOURS.getType());
			child.getMetaData().setLabel(RedmineAttribute.TIME_ENTRY_HOURS.getLabel());
			mapper.setValue(child, ""+getHours());
		}
		if (getActivityId()>0) {
			TaskAttribute child = taskAttribute.createMappedAttribute(IRedmineConstants.TASK_ATTRIBUTE_TIMEENTRY_ACTIVITY);
			child.getMetaData().defaults().setType(RedmineAttribute.TIME_ENTRY_ACTIVITY.getType());
			child.getMetaData().setLabel(RedmineAttribute.TIME_ENTRY_ACTIVITY.getLabel());
			mapper.setIntegerValue(child, getActivityId());

			//Option for ActivityId
			if(project!=null) {
				TimeEntryActivity activity = project.getTimeEntryActivities().getById(getActivityId());
				if (activity!=null) {
					child.putOption(""+activity.getId(), activity.getName());
				}
			}
		}
		if (getUser() != null) {
			TaskAttribute child = taskAttribute.createMappedAttribute(IRedmineConstants.TASK_ATTRIBUTE_TIMEENTRY_AUTHOR);
			mapper.setRepositoryPerson(child, getUser());
		}
		if (getSpentOn()!=null) {
			TaskAttribute child = taskAttribute.createMappedAttribute(IRedmineConstants.TASK_ATTRIBUTE_TIMEENTRY_SPENTON);
			child.getMetaData().defaults().setType(TaskAttribute.TYPE_DATE);
			mapper.setDateValue(child, getSpentOn());
		}
		if (getComments()!=null) {
			TaskAttribute child = taskAttribute.createMappedAttribute(IRedmineConstants.TASK_ATTRIBUTE_TIMEENTRY_COMMENTS);
			child.getMetaData().defaults().setType(RedmineAttribute.TIME_ENTRY_COMMENTS.getType());
			child.getMetaData().setLabel(RedmineAttribute.TIME_ENTRY_COMMENTS.getLabel());
			mapper.setValue(child, getComments());
		}
		if (getCustomValues()!=null) {
			for (CustomValue customValue : getCustomValues()) {
				TaskAttribute child = taskAttribute.createMappedAttribute(IRedmineConstants.TASK_KEY_PREFIX_TIMEENTRY_CF + customValue.getCustomFieldId());
				
				CustomField customField = configuration.getCustomFields().getById(customValue.getCustomFieldId());
				if(customField!=null) {
					child.getMetaData().defaults().setType(RedmineUtil.getTaskAttributeType(customField));
					child.setValue(customValue.getValue());
					child.getMetaData().setLabel(customField.getName());
				}
			}
		}
	}
	
	private void readTaskAttribute(TaskAttribute taskAttribute) {
		TaskData taskData = taskAttribute.getTaskData();
		TaskAttributeMapper mapper = taskData.getAttributeMapper();
		
		id = mapper.getIntegerValue(taskAttribute);
		try {
			hours = Float.parseFloat(mapper.getValue(taskAttribute.getMappedAttribute(IRedmineConstants.TASK_ATTRIBUTE_TIMEENTRY_HOURS)));
		} catch (NumberFormatException e) {
			IStatus status = RedmineCorePlugin.toStatus(e, null, "INVALID_HOURS_FORMAT_{0}", mapper.getValue(taskAttribute.getMappedAttribute(IRedmineConstants.TASK_ATTRIBUTE_TIMEENTRY_HOURS)));
			StatusHandler.log(status);
		}
		activityId = mapper.getIntegerValue(taskAttribute.getMappedAttribute(IRedmineConstants.TASK_ATTRIBUTE_TIMEENTRY_ACTIVITY));
		user = mapper.getRepositoryPerson(taskAttribute.getMappedAttribute(IRedmineConstants.TASK_ATTRIBUTE_TIMEENTRY_AUTHOR));
		spentOn = mapper.getDateValue(taskAttribute.getMappedAttribute(IRedmineConstants.TASK_ATTRIBUTE_TIMEENTRY_SPENTON));
		comments = mapper.getValue(taskAttribute.getMappedAttribute(IRedmineConstants.TASK_ATTRIBUTE_TIMEENTRY_COMMENTS));
		//TODO customs
	}

	public int getTimeEntryId() {
		return id;
	}

	public void setTimeEntryId(int id) {
		this.id = id;
	}

	public float getHours() {
		return hours;
	}

	public void setHours(float hours) {
		this.hours = hours;
	}

	public int getActivityId() {
		return activityId;
	}

	public void setActivityId(int activityId) {
		this.activityId = activityId;
	}

	public IRepositoryPerson getUser() {
		return user;
	}

	public void setUser(IRepositoryPerson user) {
		this.user = user;
	}

	public Date getSpentOn() {
		return spentOn;
	}

	public void setSpentOn(Date spentOn) {
		this.spentOn = spentOn;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public List<CustomValue> getCustomValues() {
		return customValues;
	}

	public void setCustomValues(List<CustomValue> customValues) {
		this.customValues = customValues;
	}

	
	public static TaskAttribute getAuthorAttribute(TaskAttribute timeEntryAttribute) {
		return timeEntryAttribute.getMappedAttribute(IRedmineConstants.TASK_ATTRIBUTE_TIMEENTRY_AUTHOR);
	}

	public static TaskAttribute getHoursAttribute(TaskAttribute timeEntryAttribute) {
		return timeEntryAttribute.getMappedAttribute(IRedmineConstants.TASK_ATTRIBUTE_TIMEENTRY_HOURS);
	}

	public static TaskAttribute getActivityAttribute(TaskAttribute timeEntryAttribute) {
		return timeEntryAttribute.getMappedAttribute(IRedmineConstants.TASK_ATTRIBUTE_TIMEENTRY_ACTIVITY);
	}
	
	public static TaskAttribute getCommentsAttribute(TaskAttribute timeEntryAttribute) {
		return timeEntryAttribute.getMappedAttribute(IRedmineConstants.TASK_ATTRIBUTE_TIMEENTRY_COMMENTS);
	}
	
	public static List<TaskAttribute> getCustomAttributes(TaskAttribute timeEntryAttribute) {
		ArrayList<TaskAttribute> customAttributes = new ArrayList<TaskAttribute>();
		for(Entry<String, TaskAttribute> entry : timeEntryAttribute.getAttributes().entrySet()) {
			if(entry.getKey().startsWith(IRedmineConstants.TASK_KEY_PREFIX_TIMEENTRY_CF)) {
				customAttributes.add(entry.getValue());
			}
		}
		return customAttributes;
	}

}
