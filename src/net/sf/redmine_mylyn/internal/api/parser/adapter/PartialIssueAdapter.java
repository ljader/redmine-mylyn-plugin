package net.sf.redmine_mylyn.internal.api.parser.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import net.sf.redmine_mylyn.api.model.Issue;
import net.sf.redmine_mylyn.internal.api.parser.adapter.type.PartialIssueType;

public class PartialIssueAdapter extends XmlAdapter<PartialIssueType, Issue> {

	@Override
	public PartialIssueType marshal(Issue arg0) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Issue unmarshal(PartialIssueType arg0) throws Exception {
		return arg0.toIssue();
	}
}
