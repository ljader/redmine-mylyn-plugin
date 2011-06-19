package net.sf.redmine_mylyn.api.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import net.sf.redmine_mylyn.api.model.container.CustomValues;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="timeEntry")
public class TimeEntry implements IModel {

	private static final long serialVersionUID = 1L;

	@XmlAttribute
	private int id;
	
	private float hours;

	private int activityId;

	private int userId;
	
	private Date spentOn;
	
	private String comments;
	
	private CustomValues customValues;
	
	private Map<String, String> extensionValues;

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

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
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

	public CustomValues getCustomValues() {
		return customValues;
	}

	public void setCustomValues(CustomValues customValues) {
		this.customValues = customValues;
	}

	public int getId() {
		return id;
	}
	
	public void addExtensionValue(String submitKey, String value) {
		if (extensionValues==null) {
			extensionValues = new HashMap<String, String>();
		}
		extensionValues.put(submitKey, value);
	}
	
	public Map<String, String> getExtensionValues() {
		if (extensionValues==null) {
			extensionValues = new HashMap<String, String>();
		}
		return extensionValues;
	}
	
}
