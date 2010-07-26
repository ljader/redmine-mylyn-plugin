package net.sf.redmine_mylyn.internal.api.parser.adapter.type;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.sf.redmine_mylyn.api.model.Issue;
import net.sf.redmine_mylyn.internal.api.parser.PartialIssueParser;
import net.sf.redmine_mylyn.internal.api.parser.adapter.PartialIssueAdapter;

@XmlRootElement(name="issues", namespace=PartialIssueParser.FAKE_NS)
@XmlAccessorType(XmlAccessType.NONE)
public class PartialIssues {

	@XmlElement(name="issue")
	@XmlJavaTypeAdapter(PartialIssueAdapter.class)
	public Issue[] issues;

}
