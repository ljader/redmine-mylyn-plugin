package net.sf.redmine_mylyn.core.client;

import java.util.Date;
import java.util.Set;

import net.sf.redmine_mylyn.api.client.RedmineServerVersion;
import net.sf.redmine_mylyn.api.model.Configuration;
import net.sf.redmine_mylyn.api.model.Issue;
import net.sf.redmine_mylyn.api.query.Query;
import net.sf.redmine_mylyn.core.RedmineStatusException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.tasks.core.ITask;

public interface IClient {

	public Configuration getConfiguration();

	public void updateConfiguration(IProgressMonitor monitor) throws RedmineStatusException;
	
	public RedmineServerVersion checkClientConnection(IProgressMonitor monitor) throws RedmineStatusException;
	
	public Issue getIssue(int id, IProgressMonitor monitor) throws RedmineStatusException;
	
	public Issue[] getIssues(Set<String> ids, IProgressMonitor monitor) throws RedmineStatusException;
	
	public int[] getUpdatedIssueIds(Set<ITask> tasks, Date updatedSince, IProgressMonitor monitor) throws RedmineStatusException;

	public Issue[] query(Query query, IProgressMonitor monitor) throws RedmineStatusException;
	
//	public void updateAttributes(boolean force, IProgressMonitor monitor) throws RedmineException;
//	
//	public int createTicket(String project, Map<String, String> postValues, IProgressMonitor monitor) throws RedmineException;
//	
//	public void updateTicket(int ticketId, Map<String, String> postValues, String comment, IProgressMonitor monitor) throws RedmineException;
//
//	public InputStream getAttachmentContent(int attachmentId, IProgressMonitor monitor) throws RedmineException;
//
//	public void uploadAttachment(int ticketId, String fileName, String comment, String description, AbstractTaskAttachmentSource source, IProgressMonitor monitor) throws RedmineException;
	
	
//	public void refreshRepositorySettings(TaskRepository repository, AbstractWebLocation location);

}
