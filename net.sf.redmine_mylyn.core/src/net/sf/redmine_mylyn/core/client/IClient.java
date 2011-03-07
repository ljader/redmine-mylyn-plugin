package net.sf.redmine_mylyn.core.client;

import java.io.InputStream;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import net.sf.redmine_mylyn.api.client.RedmineApiIssueProperty;
import net.sf.redmine_mylyn.api.client.RedmineServerVersion;
import net.sf.redmine_mylyn.api.model.Configuration;
import net.sf.redmine_mylyn.api.model.Issue;
import net.sf.redmine_mylyn.api.model.TimeEntry;
import net.sf.redmine_mylyn.api.query.Query;
import net.sf.redmine_mylyn.core.RedmineStatusException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskAttachmentSource;

public interface IClient {

	public Configuration getConfiguration();

	public void updateConfiguration(IProgressMonitor monitor) throws RedmineStatusException;
	
	public RedmineServerVersion checkClientConnection(IProgressMonitor monitor) throws RedmineStatusException;
	
	public Issue getIssue(int id, IProgressMonitor monitor) throws RedmineStatusException;
	
	public Issue[] getIssues(Set<String> ids, IProgressMonitor monitor) throws RedmineStatusException;
	
	public int[] getUpdatedIssueIds(Set<ITask> tasks, Date updatedSince, IProgressMonitor monitor) throws RedmineStatusException;

	public Issue[] query(Query query, IProgressMonitor monitor) throws RedmineStatusException;
	
	public int createIssue(Issue issue, IProgressMonitor monitor) throws RedmineStatusException;

	public void updateIssue(Issue issue, String comment, TimeEntry timeEntry, IProgressMonitor monitor) throws RedmineStatusException;

	public void updateIssue(int issueId, Map<RedmineApiIssueProperty, String> issueValues, String comment, TimeEntry timeEntry, IProgressMonitor monitor) throws RedmineStatusException;

	public InputStream getAttachmentContent(int attachmentId, String fileName, IProgressMonitor monitor) throws RedmineStatusException;
	
	public void uploadAttachment(int issueId, String fileName, String description, AbstractTaskAttachmentSource source, String comment, IProgressMonitor monitor) throws RedmineStatusException;

}
