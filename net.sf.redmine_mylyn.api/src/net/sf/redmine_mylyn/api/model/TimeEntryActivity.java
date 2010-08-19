package net.sf.redmine_mylyn.api.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="timeEntryActivity")
public class TimeEntryActivity extends SortedProperty {

	private static final long serialVersionUID = 1L;

	protected boolean isDefault;

	public boolean isDefault() {
		return isDefault;
	}

	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}

}
