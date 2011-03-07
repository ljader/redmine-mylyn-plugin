package net.sf.redmine_mylyn.internal.core.client;

import java.io.InputStream;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import net.sf.redmine_mylyn.api.client.IRedmineApiClient;
import net.sf.redmine_mylyn.api.client.RedmineApiIssueProperty;
import net.sf.redmine_mylyn.api.client.RedmineServerVersion;
import net.sf.redmine_mylyn.api.exception.RedmineApiErrorException;
import net.sf.redmine_mylyn.api.exception.RedmineApiInvalidDataException;
import net.sf.redmine_mylyn.api.model.Attachment;
import net.sf.redmine_mylyn.api.model.Configuration;
import net.sf.redmine_mylyn.api.model.Issue;
import net.sf.redmine_mylyn.api.model.TimeEntry;
import net.sf.redmine_mylyn.api.query.Query;
import net.sf.redmine_mylyn.core.RedmineCorePlugin;
import net.sf.redmine_mylyn.core.RedmineStatusException;
import net.sf.redmine_mylyn.core.RedmineUtil;
import net.sf.redmine_mylyn.core.client.IClient;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskAttachmentSource;

public class Client implements IClient {
	
	private final IRedmineApiClient apiClient;

	public Client(IRedmineApiClient apiClient) {
		this.apiClient = apiClient; 
	}
	
	@Override
	public Configuration getConfiguration(){
		return apiClient.getConfiguration();
	}
	
	@Override
	public void updateConfiguration(IProgressMonitor monitor) throws RedmineStatusException {
		try {
			apiClient.updateConfiguration(monitor);
		} catch (RedmineApiErrorException e) {
			throw new RedmineStatusException(e, "Update of configuration failed - {0}", e.getMessage());
		}
	}
	
	@Override
	public RedmineServerVersion checkClientConnection(IProgressMonitor monitor) throws RedmineStatusException {
		
		RedmineServerVersion version;
		try {
			version = apiClient.detectServerVersion(monitor);
		} catch (RedmineApiErrorException e) {
			throw new RedmineStatusException(e);
		}
		
		return version;
	}
	
	@Override
	public Issue getIssue(int id, IProgressMonitor monitor) throws RedmineStatusException {
		try {
			return apiClient.getIssue(id, monitor);
		} catch (RedmineApiErrorException e) {
			throw new RedmineStatusException(e);
		}
	}
	
	@Override
	public Issue[] getIssues(Set<String> taskIds, IProgressMonitor monitor) throws RedmineStatusException {
		int[] ids = new int[taskIds.size()];
		int lv=0;
		for(String taskId : taskIds) {
			int id = RedmineUtil.parseIntegerId(taskId);
			if(id>0) {
				ids[lv++] = id;
			}
		}
		
		try {
			return apiClient.getIssues(monitor, ids);
		} catch (RedmineApiErrorException e) {
			throw new RedmineStatusException(e);
		}
	}

	@Override
	public int[] getUpdatedIssueIds(Set<ITask> tasks, Date updatedSince, IProgressMonitor monitor) throws RedmineStatusException {
		int[] ids = new int[tasks.size()];
		int lv=0;
		for(ITask task : tasks) {
			ids[lv++] = RedmineUtil.parseIntegerId(task.getTaskId());
		}
		
		try {
			return apiClient.getUpdatedIssueIds(ids, updatedSince, monitor);
		} catch (RedmineApiErrorException e) {
			throw new RedmineStatusException(e);
		}
	}
	
	@Override
	public Issue[] query(Query query, IProgressMonitor monitor) throws RedmineStatusException {
		try {
			return apiClient.query(query, monitor);
		} catch (RedmineApiErrorException e) {
			throw new RedmineStatusException(e);
		}
	}

	@Override
	public int createIssue(Issue issue, IProgressMonitor monitor) throws RedmineStatusException {
		ErrrorCollector errorCollector = new ErrrorCollector();
		try {
			return apiClient.createIssue(issue, errorCollector, monitor).getId();
		} catch (RedmineApiErrorException e) {
			throw new RedmineStatusException(e);
		} catch (RedmineApiInvalidDataException e) {
			IStatus status = new Status(IStatus.ERROR, RedmineCorePlugin.PLUGIN_ID, errorCollector.getErrorString(), e);
			throw new RedmineStatusException(status);
		}
	}
	
	@Override
	public void updateIssue(Issue issue, String comment, TimeEntry timeEntry, IProgressMonitor monitor) throws RedmineStatusException {
		ErrrorCollector errorCollector = new ErrrorCollector();
		try {
			apiClient.updateIssue(issue, comment, timeEntry, errorCollector, monitor);
		} catch (RedmineApiErrorException e) {
			throw new RedmineStatusException(e);
		} catch (RedmineApiInvalidDataException e) {
			IStatus status = new Status(IStatus.ERROR, RedmineCorePlugin.PLUGIN_ID, errorCollector.getErrorString(), e);
			throw new RedmineStatusException(status);
		}
	}
	
	@Override
	public void updateIssue(int issueId, Map<RedmineApiIssueProperty, String> issueValues, String comment, TimeEntry timeEntry, IProgressMonitor monitor) throws RedmineStatusException {
		ErrrorCollector errorCollector = new ErrrorCollector();
		try {
			apiClient.updateIssue(issueId, issueValues, comment, timeEntry, errorCollector, monitor);
		} catch (RedmineApiErrorException e) {
			throw new RedmineStatusException(e);
		} catch (RedmineApiInvalidDataException e) {
			IStatus status = new Status(IStatus.ERROR, RedmineCorePlugin.PLUGIN_ID, errorCollector.getErrorString(), e);
			throw new RedmineStatusException(status);
		}
	}
	
	@Override
	public void uploadAttachment(int issueId, String fileName, String description, AbstractTaskAttachmentSource source, String comment, IProgressMonitor monitor) throws RedmineStatusException {
		ErrrorCollector errorCollector = new ErrrorCollector();
		try {
			Attachment attachment = new Attachment();
			attachment.setFilename(fileName);
			attachment.setDescription(description);
			attachment.setFilesize(source.getLength());
			attachment.setContentType(source.getContentType());

			apiClient.uploadAttachment(issueId, attachment, source.createInputStream(monitor), comment, errorCollector, monitor);
		} catch (CoreException e) {
			new RedmineStatusException(e.getStatus());
		} catch (RedmineApiErrorException e) {
			throw new RedmineStatusException(e);
		} catch (RedmineApiInvalidDataException e) {
			IStatus status = new Status(IStatus.ERROR, RedmineCorePlugin.PLUGIN_ID, errorCollector.getErrorString(), e);
			throw new RedmineStatusException(status);
		}
	}
	
	@Override
	public InputStream getAttachmentContent(int attachmentId, String fileName, IProgressMonitor monitor) throws RedmineStatusException {
		try {
			return apiClient.getAttachmentContent(attachmentId, fileName, monitor);
		} catch (RedmineApiErrorException e) {
			throw new RedmineStatusException(e);
		}
	}
}
