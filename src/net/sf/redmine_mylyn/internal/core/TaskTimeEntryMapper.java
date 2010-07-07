package net.sf.redmine_mylyn.internal.core;

import java.util.Date;
import java.util.List;

import net.sf.redmine_mylyn.api.model.Configuration;
import net.sf.redmine_mylyn.api.model.CustomField;
import net.sf.redmine_mylyn.api.model.CustomValue;
import net.sf.redmine_mylyn.api.model.TimeEntryActivity;
import net.sf.redmine_mylyn.core.IRedmineConstants;
import net.sf.redmine_mylyn.core.RedmineAttribute;

import org.eclipse.core.runtime.Assert;
import org.eclipse.mylyn.tasks.core.IRepositoryPerson;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;

public class TaskTimeEntryMapper {

	private int id;

	private float hours;
	
	private int activityId;

	private IRepositoryPerson user;
	
	private Date spentOn;
	
	private String comments;
	
	private List<CustomValue> customValues;
	
	private final Configuration cfg;
	
	public TaskTimeEntryMapper(Configuration cfg) {
		this.cfg = cfg;
	}

//	public static TaskTimeEntryMapper createFrom(TaskAttribute taskAttribute) {
//		Assert.isNotNull(taskAttribute);
//		
//		TaskTimeEntryMapper mapper = new TaskTimeEntryMapper();
//		mapper.readTaskAttribute(taskAttribute);
//		
//		return mapper;
//	}

	public void applyTo(TaskAttribute taskAttribute) {
		Assert.isNotNull(taskAttribute);
		
		TaskData taskData = taskAttribute.getTaskData();
		TaskAttributeMapper mapper = taskData.getAttributeMapper();
		//TODO prüfen, solle ohne setType gehen
//		taskAttribute.getMetaData().defaults().setType(TASK_ATTRIBUTE_TIMEENTRY);
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
			TimeEntryActivity activity = cfg.getTimeEntryActivities().getById(getActivityId());
			if (activity!=null) {
				child.putOption(""+activity.getId(), activity.getName());
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
				child.getMetaData().defaults().setType(TaskAttribute.TYPE_SHORT_TEXT);
				child.setValue(customValue.getValue());

				//Labels of CustomFields
				CustomField customField = cfg.getCustomFields().getById(customValue.getCustomFieldId());
				if(customField!=null) {
					child.getMetaData().setLabel(customField.getName());
				}
			}
		}
	}
	
//	private void readTaskAttribute(TaskAttribute taskAttribute) {
//		TaskData taskData = taskAttribute.getTaskData();
//		TaskAttributeMapper mapper = taskData.getAttributeMapper();
//		
//		id = mapper.getIntegerValue(taskAttribute);
//		try {
//			hours = Float.parseFloat(mapper.getValue(taskAttribute.getMappedAttribute(TASK_ATTRIBUTE_TIMEENTRY_HOURS)));
//		} catch (NumberFormatException e) {
//			IStatus status = RedmineCorePlugin.toStatus(e, null, "INVALID_HOURS_FORMAT_{0}", 
//					mapper.getValue(taskAttribute.getMappedAttribute(TASK_ATTRIBUTE_TIMEENTRY_HOURS)));
//			StatusHandler.log(status);
//		}
//		activityId = mapper.getIntegerValue(taskAttribute.getMappedAttribute(TASK_ATTRIBUTE_TIMEENTRY_ACTIVITY));
//		userId = mapper.getIntegerValue(taskAttribute.getMappedAttribute(TASK_ATTRIBUTE_TIMEENTRY_AUTHOR));
//		spentOn = mapper.getDateValue(taskAttribute.getMappedAttribute(TASK_ATTRIBUTE_TIMEENTRY_SPENTON));
//		comments = mapper.getValue(taskAttribute.getMappedAttribute(TASK_ATTRIBUTE_TIMEENTRY_COMMENTS));
//		//TODO customs
//	}

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

	
//	public static TaskAttribute getAuthorAttribute(TaskAttribute timeEntryAttribute) {
//		return timeEntryAttribute.getMappedAttribute(TASK_ATTRIBUTE_TIMEENTRY_AUTHOR);
//	}
//
//	public static TaskAttribute getHoursAttribute(TaskAttribute timeEntryAttribute) {
//		return timeEntryAttribute.getMappedAttribute(TASK_ATTRIBUTE_TIMEENTRY_HOURS);
//	}
//
//	public static TaskAttribute getActivityAttribute(TaskAttribute timeEntryAttribute) {
//		return timeEntryAttribute.getMappedAttribute(TASK_ATTRIBUTE_TIMEENTRY_ACTIVITY);
//	}
//	
//	public static TaskAttribute getCommentsAttribute(TaskAttribute timeEntryAttribute) {
//		return timeEntryAttribute.getMappedAttribute(TASK_ATTRIBUTE_TIMEENTRY_COMMENTS);
//	}
//	
//	public static Collection<TaskAttribute> getCustomAttributes(TaskAttribute timeEntryAttribute) {
//		TaskAttribute customs = timeEntryAttribute.getMappedAttribute(TASK_ATTRIBUTE_TIMEENTRY_CUSTOMVALUES);
//		if (customs!=null) {
//			return customs.getAttributes().values();
//		}
//		return null;
//	}
//
//	public static TaskAttribute getCustomAttribute(TaskAttribute timeEntryAttribute, int customFieldId) {
//		String[] path = new String[]{TASK_ATTRIBUTE_TIMEENTRY_CUSTOMVALUES, TASK_ATTRIBUTE_TIMEENTRY_CUSTOMVALUE + customFieldId};
//		return timeEntryAttribute.getMappedAttribute(path);
//	}
}
