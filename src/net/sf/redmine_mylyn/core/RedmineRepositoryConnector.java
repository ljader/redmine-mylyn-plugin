package net.sf.redmine_mylyn.core;

import java.util.Date;

import net.sf.redmine_mylyn.api.model.Configuration;
import net.sf.redmine_mylyn.api.model.Issue;
import net.sf.redmine_mylyn.api.model.IssueStatus;
import net.sf.redmine_mylyn.core.client.IClient;
import net.sf.redmine_mylyn.internal.core.RedmineTaskMapper;
import net.sf.redmine_mylyn.internal.core.Util;
import net.sf.redmine_mylyn.internal.core.client.ClientManager;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.net.Policy;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.TaskRepositoryLocationFactory;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataCollector;
import org.eclipse.mylyn.tasks.core.data.TaskMapper;
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
	public boolean canCreateTaskFromKey(TaskRepository repository) {
		try {
			return getClientManager().getClient(repository) != null;
		} catch (RedmineStatusException e) {
			return false;
		}
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
		monitor = Policy.monitorFor(monitor);
		monitor.beginTask("Task Download", IProgressMonitor.UNKNOWN);
		
		TaskData taskData = null;
		
		try {
			int id = Integer.parseInt(taskId);

			IClient client = getClientManager().getClient(repository);
			Issue issue = client.getIssue(id, monitor);

			if(issue==null) {
				IStatus status = new Status(IStatus.INFO, RedmineCorePlugin.PLUGIN_ID, "Can't find Issue #"+taskId);
				throw new CoreException(status);
			}
			taskData = taskDataHandler.createTaskDataFromTicket(repository, issue, monitor);
		} catch (OperationCanceledException e) {
			throw new CoreException(new Status(IStatus.CANCEL, RedmineCorePlugin.PLUGIN_ID, "Operation canceled"));
		} catch(NumberFormatException e) {
			throw new CoreException(RedmineCorePlugin.toStatus(e, "Invalid TaskId {0}", taskId));
		} catch (RedmineStatusException e) {
			throw new CoreException(e.getStatus());
		} finally {
			monitor.done();
		}

		return taskData;
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
	public boolean hasTaskChanged(TaskRepository taskRepository, ITask task, TaskData taskData) {
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
	public void updateTaskFromTaskData(TaskRepository taskRepository, ITask task, TaskData taskData) {
		
		TaskMapper mapper = getTaskMapping(taskData);
		mapper.applyTo(task);

		task.setUrl(getTaskUrl(taskRepository.getUrl(), task.getTaskId()));
		
		Configuration configuration = getRepositoryConfiguration(taskRepository);
		Assert.isNotNull(configuration);
		
		//Set CompletionDate, if Closed-Status
		TaskAttribute attribute = taskData.getRoot().getMappedAttribute(RedmineAttribute.STATUS.getTaskKey());
		IssueStatus issueStatus = configuration.getIssueStatuses().getById(Util.parseIntegerId(attribute.getValue()));
		if(issueStatus==null) {
			IStatus status = new Status(IStatus.ERROR, RedmineCorePlugin.PLUGIN_ID, "Missing IssueStatus #"+attribute.getValue());
			StatusHandler.log(status);
		} else {
			if(issueStatus.isClosed()) {
				Date date = task.getCompletionDate();
				attribute =  taskData.getRoot().getMappedAttribute(RedmineAttribute.DATE_UPDATED.getTaskKey());
				try {
					date = new Date(Long.parseLong(attribute.getValue()));
				} catch(NumberFormatException e) {
					IStatus status = RedmineCorePlugin.toStatus(e, "Invalid Timestamp {0}", attribute.getValue());
					StatusHandler.log(status);
					date = new Date(0);
				}
				task.setCompletionDate(date);
			} else {
				task.setCompletionDate(null);
			}
		}

	}

	@Override
	public AbstractTaskDataHandler getTaskDataHandler() {
		return taskDataHandler;
	}

	@Override
	public TaskMapper getTaskMapping(TaskData taskData) {
		TaskRepository repository = taskData.getAttributeMapper().getTaskRepository();
		return new RedmineTaskMapper(taskData, getRepositoryConfiguration(repository));
	}
}
