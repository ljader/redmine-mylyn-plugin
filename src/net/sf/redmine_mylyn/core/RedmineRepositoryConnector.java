package net.sf.redmine_mylyn.core;

import net.sf.redmine_mylyn.api.model.Configuration;
import net.sf.redmine_mylyn.internal.core.client.ClientManager;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.TaskRepositoryLocationFactory;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataCollector;
import org.eclipse.mylyn.tasks.core.sync.ISynchronizationSession;


public class RedmineRepositoryConnector extends AbstractRepositoryConnector {

	private TaskRepositoryLocationFactory locationFactory;
	
	private RedmineTaskDataHandler taskDataHandler;
	
	private ClientManager clientManager;
	
	public RedmineRepositoryConnector() {
		taskDataHandler = new RedmineTaskDataHandler(this);
	}

	public synchronized ClientManager getClientManager() {
		if (clientManager == null) {
			IPath path = RedmineCorePlugin.getDefault().getRepostioryAttributeCachePath();
			clientManager = new ClientManager(locationFactory, path.toFile());
		}
		return clientManager;
	}
	
	public Configuration getRepositoryConfiguration(TaskRepository repository) {
		try {
			return getClientManager().getClient(repository).getConfiguration();
		} catch (RedmineStatusException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new Configuration();
	}
	
	public void setTaskRepositoryLocationFactory(TaskRepositoryLocationFactory repositoryLocationFactory) {
		this.locationFactory = repositoryLocationFactory;
	}

	@Override
	public boolean canCreateNewTask(TaskRepository repository) {
		try {
			return getClientManager().getClient(repository) != null;
		} catch (RedmineStatusException e) {
			return false;
		}
	}

	@Override
	public boolean canCreateTaskFromKey(TaskRepository arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getConnectorKind() {
		return RedmineCorePlugin.REPOSITORY_KIND;
	}

	@Override
	public String getLabel() {
		return "Redmine (supports Redmine 1.0 with enabled REST-API and Mylyn-Pugin)";
	}

	@Override
	public String getRepositoryUrlFromTaskUrl(String arg0) {
		int index=arg0.indexOf(IRedmineConstants.REDMINE_URL_TICKET);
		return (index>0) ? arg0.substring(0, index) : null;
	}

	@Override
	public TaskData getTaskData(TaskRepository repository, String taskId, IProgressMonitor monitor) throws CoreException {
		return taskDataHandler.getTaskData(repository, taskId, monitor);
	}

	@Override
	public String getTaskIdFromTaskUrl(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTaskUrl(String repositoryUrl, String taskId) {
		return getTaskUrl(repositoryUrl, Integer.parseInt(taskId));
	}
	
	public static String getTaskUrl(String repositoryUrl, int taskId) {
		return repositoryUrl + IRedmineConstants.REDMINE_URL_TICKET + taskId;
	}

	@Override
	public boolean hasTaskChanged(TaskRepository arg0, ITask arg1, TaskData arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IStatus performQuery(TaskRepository arg0, IRepositoryQuery arg1, TaskDataCollector arg2, ISynchronizationSession arg3, IProgressMonitor arg4) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateRepositoryConfiguration(TaskRepository repository, IProgressMonitor monitor) throws CoreException {
		try {
			getClientManager().getClient(repository).updateConfiguration(monitor);
		} catch (RedmineStatusException e) {
			IStatus status = RedmineCorePlugin.toStatus(e, "Update of configuration failed");
			throw new CoreException(status);
		}
	}

	@Override
	public void updateTaskFromTaskData(TaskRepository arg0, ITask arg1, TaskData arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public AbstractTaskDataHandler getTaskDataHandler() {
		return taskDataHandler;
	}
	
	
}
