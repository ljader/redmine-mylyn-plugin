package net.sf.redmine_mylyn.api.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name="property")
@XmlAccessorType(XmlAccessType.FIELD)
public class Property implements Serializable {

	private static final long serialVersionUID = 1L;

	@XmlAttribute(required=true)
	protected int id;
	
	@XmlElement(required=true)
	protected String name;

	public int getId() {
		return id;
	}

	public String getName() {
		return name==null ? "" : name; //$NON-NLS-1$
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean equals(Object obj) {
		return obj.getClass().equals(getClass()) && ((Property)obj).getId()==getId();
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 31 * hash + id;
		//TODO hash over class
		return hash;
	}

}
