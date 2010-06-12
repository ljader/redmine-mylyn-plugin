package net.sf.redmine_mylyn.api.client;


import net.sf.redmine_mylyn.api.model.Configuration;

import org.eclipse.core.runtime.IProgressMonitor;

public interface IApiClient {

//	public Issue getIssue(IProgressMonitor monitor, int id);
//
//	public Issue[] getIssues(IProgressMonitor monitor, int... id);
	
	public Configuration getConfiguration();
	
	public void updateConfiguration(IProgressMonitor monitor, boolean force) throws RedmineApiStatusException;
	
	public int[] getUpdatedIssueIds(int[] issues, long updatedSince, IProgressMonitor monitor) throws RedmineApiStatusException;
	
//		public List<Integer> getChangedTicketId(Integer projectId, Date changedSince, IProgressMonitor monitor) throws RedmineException;
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
