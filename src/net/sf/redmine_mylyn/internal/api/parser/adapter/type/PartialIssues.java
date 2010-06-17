package net.sf.redmine_mylyn.internal.api.parser.adapter.type;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import net.sf.redmine_mylyn.api.model.PartialIssue;
import net.sf.redmine_mylyn.internal.api.parser.PartialIssueParser;

@XmlRootElement(name="issues", namespace=PartialIssueParser.FAKE_NS)
@XmlAccessorType(XmlAccessType.NONE)
public class PartialIssues {

	@XmlElement(name="issue")
	public PartialIssue[] issues;

}
