package net.sf.redmine_mylyn.api.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="tracker")
public class Tracker extends SortedProperty {

	private static final long serialVersionUID = 1L;
	
	@XmlList
	@XmlElement(name="issueCustomFields")
	private int []issueCustomFields;

	public int[] getIssueCustomFields() {
		if(issueCustomFields==null) {
			issueCustomFields = new int[0];
		}
		return issueCustomFields;
	}
	
	

}
