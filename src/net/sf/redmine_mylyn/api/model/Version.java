package net.sf.redmine_mylyn.api.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="version")
public class Version extends Property {

	private static final long serialVersionUID = 1L;

	public enum Status {
		@XmlEnumValue("closed")
		CLOSED, 
		@XmlEnumValue("locked")
		LOCKED, 
		@XmlEnumValue("open")
		OPEN
	};
	
	private Status status;

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}
	
}
