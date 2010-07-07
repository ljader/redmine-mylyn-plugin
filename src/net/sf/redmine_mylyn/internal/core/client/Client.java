package net.sf.redmine_mylyn.internal.core.client;

import net.sf.redmine_mylyn.api.client.IRedmineApiClient;
import net.sf.redmine_mylyn.api.client.RedmineApiStatusException;
import net.sf.redmine_mylyn.api.client.RedmineServerVersion;
import net.sf.redmine_mylyn.api.model.Configuration;
import net.sf.redmine_mylyn.api.model.Issue;
import net.sf.redmine_mylyn.core.RedmineStatusException;
import net.sf.redmine_mylyn.core.client.IClient;

import org.eclipse.core.runtime.IProgressMonitor;

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
		// TODO Auto-generated method stub
		return null;
	}


}
