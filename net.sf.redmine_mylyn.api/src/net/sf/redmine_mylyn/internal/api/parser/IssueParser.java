package net.sf.redmine_mylyn.internal.api.parser;

import java.io.InputStream;

import net.sf.redmine_mylyn.api.exception.RedmineApiErrorException;
import net.sf.redmine_mylyn.api.model.Configuration;
import net.sf.redmine_mylyn.api.model.Issue;
import net.sf.redmine_mylyn.api.model.IssueStatus;

public class IssueParser extends TypedParser<Issue> {

	private Configuration configuration;
	
	public IssueParser(Configuration configuration) {
		super(Issue.class);
		this.configuration = configuration;
	}
	
	@Override
	public Issue parseResponse(InputStream input, int sc) throws RedmineApiErrorException {
		Issue issue = super.parseResponse(input, sc);
		if(issue!=null) {
			IssueStatus status = configuration.getIssueStatuses().getById(issue.getStatusId());
			issue.setClosed(status==null || status.isClosed());
		}
		return issue;
	}
}
