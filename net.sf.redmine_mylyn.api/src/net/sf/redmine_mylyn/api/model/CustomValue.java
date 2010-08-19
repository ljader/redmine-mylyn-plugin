package net.sf.redmine_mylyn.api.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="customValue")
public class CustomValue implements IModel {
	
	private static final long serialVersionUID = 1L;

	@XmlAttribute
	private int id;
	
	@XmlAttribute
	private int customFieldId;
	
	@XmlValue
	private String value;

	public int getCustomFieldId() {
		return customFieldId;
	}

	public void setCustomFieldId(int customFieldId) {
		this.customFieldId = customFieldId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int getId() {
		return id;
	}
	
}
