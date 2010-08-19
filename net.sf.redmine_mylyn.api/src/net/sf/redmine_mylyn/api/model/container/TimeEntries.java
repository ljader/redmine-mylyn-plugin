package net.sf.redmine_mylyn.api.model.container;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import net.sf.redmine_mylyn.api.model.TimeEntry;

@XmlType(name="timeEntries")
@XmlAccessorType(XmlAccessType.NONE)
public class TimeEntries extends AbstractTypedContainer<TimeEntry> {

	@XmlElement
	private float sum;
	
	@XmlAttribute(required=true)
	private boolean viewAllowed;

	@XmlAttribute(required=true)
	private boolean newAllowed;
	
	private List<TimeEntry> timeEntries;
	
	@Override
	@XmlElement(name="timeEntry")
	protected List<TimeEntry> getModifiableList() {
		if(timeEntries==null) {
			timeEntries = new ArrayList<TimeEntry>();
		}
		return timeEntries;
	}

	
	public boolean isViewAllowed() {
		return viewAllowed;
	}

	public void setViewAllowed(boolean viewAllowed) {
		this.viewAllowed = viewAllowed;
	}

	public boolean isNewAllowed() {
		return newAllowed;
	}


	public void setNewAllowed(boolean newAllowed) {
		this.newAllowed = newAllowed;
	}


	public float getSum() {
		return sum;
	}

}
