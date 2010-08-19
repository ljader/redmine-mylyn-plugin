package net.sf.redmine_mylyn.internal.api.parser.adapter.type;

import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement(name="updatedIssues")
public class UpdatedIssuesType {
	
	@XmlList
	@XmlValue
	public int[] updatedIssueIds;

}
