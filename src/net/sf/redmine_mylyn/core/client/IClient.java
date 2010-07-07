package net.sf.redmine_mylyn.core.client;

import net.sf.redmine_mylyn.api.client.RedmineServerVersion;
import net.sf.redmine_mylyn.api.model.Configuration;
import net.sf.redmine_mylyn.api.model.Issue;
import net.sf.redmine_mylyn.core.RedmineStatusException;

import org.eclipse.core.runtime.IProgressMonitor;

public interface IClient {

	public Configuration getConfiguration();

	public void updateConfiguration(IProgressMonitor monitor) throws RedmineStatusException;
	
	public RedmineServerVersion checkClientConnection(IProgressMonitor monitor) throws RedmineStatusException;
	
	public Issue getIssue(int id, IProgressMonitor monitor) throws RedmineStatusException;
	
//	public Set<Integer> getChangedTicketId(Set<Integer> ids, Date changedSince, IProgressMonitor monitor) throws RedmineStatusException
//	
//	public void query(String searchParam, String projectId, String storedQueryId, List<RedmineTicket> tickets, IProgressMonitor monitor) throws RedmineException;
	
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
