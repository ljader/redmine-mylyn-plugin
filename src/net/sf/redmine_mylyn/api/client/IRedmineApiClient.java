package net.sf.redmine_mylyn.api.client;


import net.sf.redmine_mylyn.api.model.Configuration;
import net.sf.redmine_mylyn.api.model.Issue;
import net.sf.redmine_mylyn.api.query.Query;

import org.eclipse.core.runtime.IProgressMonitor;

public interface IRedmineApiClient {

	public Configuration getConfiguration();

	public RedmineServerVersion detectServerVersion(IProgressMonitor monitor) throws RedmineApiErrorException;
	
	public void updateConfiguration(IProgressMonitor monitor) throws RedmineApiErrorException;
	
	public int[] getUpdatedIssueIds(int[] issues, long updatedSince, IProgressMonitor monitor) throws RedmineApiErrorException;
	
	public Issue getIssue(int id, IProgressMonitor monitor) throws RedmineApiErrorException;
	
	public Issue[] getIssues(IProgressMonitor monitor, int... issueIds) throws RedmineApiErrorException;
	
	public Issue[] query(Query query, IProgressMonitor monitor) throws RedmineApiErrorException;
	
	public Issue createIssue(Issue issue, IRedmineApiErrorCollector errorCollector, IProgressMonitor monitor) throws RedmineApiInvalidDataException, RedmineApiErrorException;
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
//		public void refreshRepositorySettings(TaskRepository repository, AbstractWebLocation location);

}
