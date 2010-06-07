package net.sf.redmine_mylyn.api.model.container;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import net.sf.redmine_mylyn.api.model.TimeEntryActivity;

@XmlRootElement(name="timeEntryActivities")
@XmlAccessorType(XmlAccessType.NONE)
public class TimeEntryActivities extends AbstractSortedPropertyContainer<TimeEntryActivity> {

	protected List<TimeEntryActivity> activities;
	
	protected TimeEntryActivity defaultActivity;
	
	@Override
	@XmlElement(name="timeEntryActivity")
	protected List<TimeEntryActivity> getModifiableList() {
		if(activities==null) {
			activities = new ArrayList<TimeEntryActivity>() {

				private static final long serialVersionUID = -690772074652255323L;

				@Override
				public boolean add(TimeEntryActivity e) {
					if(defaultActivity==null || e.isDefault()) {
						defaultActivity = e;
					}
					return super.add(e);
				}
			};
		}
		return activities;
	}

	public TimeEntryActivity getDefault() {
		return defaultActivity;
	}
}
