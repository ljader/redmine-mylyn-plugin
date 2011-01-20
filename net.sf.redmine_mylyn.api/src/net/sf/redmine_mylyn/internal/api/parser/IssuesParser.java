package net.sf.redmine_mylyn.internal.api.parser;

import java.io.InputStream;

import net.sf.redmine_mylyn.api.exception.RedmineApiErrorException;
import net.sf.redmine_mylyn.api.model.Configuration;
import net.sf.redmine_mylyn.api.model.Issue;
import net.sf.redmine_mylyn.api.model.IssueStatus;
import net.sf.redmine_mylyn.internal.api.parser.adapter.type.Issues;

public class IssuesParser extends TypedParser<Issues> {

	private Configuration configuration;
	
	public IssuesParser(Configuration configuration) {
		super(Issues.class);
		this.configuration = configuration;
	}
	
	@Override
	public Issues parseResponse(InputStream input, int sc) throws RedmineApiErrorException {
		Issues issues = super.parseResponse(input, sc);
		if(issues!=null) {
			for(Issue issue : issues.getAll()) {
				IssueStatus status = configuration.getIssueStatuses().getById(issue.getStatusId());
				issue.setClosed(status==null || status.isClosed());
			}
		}
		return issues;
	}
}
