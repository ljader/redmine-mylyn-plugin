package net.sf.redmine_mylyn.api.client;


import net.sf.redmine_mylyn.api.model.Configuration;
import net.sf.redmine_mylyn.api.model.Issue;
import net.sf.redmine_mylyn.api.model.PartialIssue;
import net.sf.redmine_mylyn.api.query.Query;

import org.eclipse.core.runtime.IProgressMonitor;

public interface IApiClient {

	public Configuration getConfiguration();
	
	public void updateConfiguration(IProgressMonitor monitor, boolean force) throws RedmineApiStatusException;
	
	public int[] getUpdatedIssueIds(int[] issues, long updatedSince, IProgressMonitor monitor) throws RedmineApiStatusException;
	
	public Issue getIssue(int id, IProgressMonitor monitor) throws RedmineApiStatusException;
	
	public Issue[] getIssues(IProgressMonitor monitor, int... issueIds) throws RedmineApiStatusException;
	
	public PartialIssue[] query(Query query, IProgressMonitor monitor) throws RedmineApiStatusException;
	
//		public void search(String searchParam, String projectId, String storedQueryId, List<RedmineTicket> tickets, IProgressMonitor monitor) throws RedmineException;
//		
//		public int createTicket(String project, Map<String, String> postValues, IProgressMonitor monitor) throws RedmineException;
//		
//		public void updateTicket(int ticketId, Map<String, String> postValues, String comment, IProgressMonitor monitor) throws RedmineException;
//	
//		public InputStream getAttachmentContent(int attachmentId, IProgressMonitor monitor) throws RedmineException;
//	
//		public void uploadAttachment(int ticketId, String fileName, String comment, String description, AbstractTaskAttachmentSource source, IProgressMonitor monitor) throws RedmineException;
//		
//		public Version checkClientConnection(IProgressMonitor monitor) throws RedmineException;
//		
//		public void refreshRepositorySettings(TaskRepository repository, AbstractWebLocation location);

}
