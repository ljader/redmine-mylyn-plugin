package net.sf.redmine_mylyn.core.client;


import net.sf.redmine_mylyn.api.client.IRedmineApiClient;
import net.sf.redmine_mylyn.api.client.RedmineApiClientFactory;
import net.sf.redmine_mylyn.api.model.Configuration;
import net.sf.redmine_mylyn.core.RedmineCorePlugin;
import net.sf.redmine_mylyn.core.RedmineStatusException;
import net.sf.redmine_mylyn.internal.core.client.ApiWebHelper;
import net.sf.redmine_mylyn.internal.core.client.Client;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.net.AbstractWebLocation;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.TaskRepositoryLocationFactory;

public class ClientFactory {

	protected static TaskRepositoryLocationFactory repositoryLocationFactory = new TaskRepositoryLocationFactory();
	
	public static IClient createClient(TaskRepository repository) throws RedmineStatusException {
		AbstractWebLocation location = repositoryLocationFactory.createWebLocation(repository);
		return createClient(repository, location, new Configuration());
	}

	public static IClient createClient(TaskRepository repository, AbstractWebLocation location, Configuration initialConfiguration) throws RedmineStatusException {
		//TODO
		IRedmineApiClient apiClient = RedmineApiClientFactory.createClient(new ApiWebHelper(location, repository), null, null, initialConfiguration);
		
		if(apiClient==null) {
			IStatus status = new Status(IStatus.ERROR, RedmineCorePlugin.PLUGIN_ID, "No client available for this version of Redmine");
			throw new RedmineStatusException(status);
		}
		
		IClient client = new Client(apiClient);
		return client;
	}

}
