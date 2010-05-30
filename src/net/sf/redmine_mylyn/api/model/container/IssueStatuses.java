package net.sf.redmine_mylyn.api.model.container;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import net.sf.redmine_mylyn.api.model.IssueStatus;

@XmlRootElement(name="issueStatuses")
@XmlAccessorType(XmlAccessType.NONE)
public class IssueStatuses extends AbstractSortedPropertyContainer<IssueStatus> {

	protected List<IssueStatus> issueStatus;
	
	@Override
	@XmlElement(name="issueStatus")
	protected List<IssueStatus> getModifiableList() {
		if(issueStatus==null) {
			issueStatus = new ArrayList<IssueStatus>();
		}
		return issueStatus;
	}

}
