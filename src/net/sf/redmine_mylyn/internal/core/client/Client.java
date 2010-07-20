package net.sf.redmine_mylyn.internal.core.client;

import java.util.Date;
import java.util.Set;

import net.sf.redmine_mylyn.api.client.IRedmineApiClient;
import net.sf.redmine_mylyn.api.client.RedmineApiStatusException;
import net.sf.redmine_mylyn.api.client.RedmineServerVersion;
import net.sf.redmine_mylyn.api.model.Configuration;
import net.sf.redmine_mylyn.api.model.Issue;
import net.sf.redmine_mylyn.api.model.PartialIssue;
import net.sf.redmine_mylyn.api.query.Query;
import net.sf.redmine_mylyn.core.RedmineStatusException;
import net.sf.redmine_mylyn.core.RedmineUtil;
import net.sf.redmine_mylyn.core.client.IClient;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.tasks.core.ITask;

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
		} catch (RedmineApiStatusException e) {
			throw new RedmineStatusException(e);
		}
	}
	
	@Override
	public RedmineServerVersion checkClientConnection(IProgressMonitor monitor) throws RedmineStatusException {
		
		RedmineServerVersion version;
		try {
			version = apiClient.detectServerVersion(monitor);
		} catch (RedmineApiStatusException e) {
			throw new RedmineStatusException(e);
		}
		
		return version;
	}
	
	@Override
	public Issue getIssue(int id, IProgressMonitor monitor) throws RedmineStatusException {
		try {
			return apiClient.getIssue(id, monitor);
		} catch (RedmineApiStatusException e) {
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
		} catch (RedmineApiStatusException e) {
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
			return apiClient.getUpdatedIssueIds(ids, updatedSince.getTime(), monitor);
		} catch (RedmineApiStatusException e) {
			throw new RedmineStatusException(e);
		}
	}
	
	@Override
	public PartialIssue[] query(Query query, IProgressMonitor monitor) throws RedmineStatusException {
		try {
			return apiClient.query(query, monitor);
		} catch (RedmineApiStatusException e) {
			throw new RedmineStatusException(e);
		}
	}

}
