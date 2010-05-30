package net.sf.redmine_mylyn.api.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import net.sf.redmine_mylyn.internal.api.model.SortedProperty;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="issueStatus")
public class IssueStatus extends SortedProperty {
	
	private static final long serialVersionUID = 1L;

	protected boolean isClosed;

	protected boolean isDefault;

	public boolean isClosed() {
		return isClosed;
	}

	public void setClosed(boolean closed) {
		this.isClosed = closed;
	}

	public boolean isDefault() {
		return isDefault;
	}

	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}

	
}
