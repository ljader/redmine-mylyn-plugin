package net.sf.redmine_mylyn.core;

import net.sf.redmine_mylyn.core.client.IClient;

import org.eclipse.mylyn.tasks.core.IRepositoryListener;
import org.eclipse.mylyn.tasks.core.TaskRepository;

public interface IRedmineClientManager extends IRepositoryListener {

	public IClient getClient(TaskRepository repository) throws RedmineStatusException;
	
}
