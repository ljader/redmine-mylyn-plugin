package net.sf.redmine_mylyn.api.model.container;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import net.sf.redmine_mylyn.api.model.TimeEntry;

@XmlType(name="timeEntries")
@XmlAccessorType(XmlAccessType.NONE)
public class TimeEntries extends AbstractTypedContainer<TimeEntry> {

	@XmlElement
	private float sum;
	
	private List<TimeEntry> timeEntries;
	
	@Override
	@XmlElement(name="timeEntry")
	protected List<TimeEntry> getModifiableList() {
		if(timeEntries==null) {
			timeEntries = new ArrayList<TimeEntry>();
		}
		return timeEntries;
	}

	public float getSum() {
		return sum;
	}

}
