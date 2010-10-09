package net.sf.redmine_mylyn.core;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;

import net.sf.redmine_mylyn.api.exception.RedmineApiErrorException;
import net.sf.redmine_mylyn.api.model.Configuration;
import net.sf.redmine_mylyn.api.model.Issue;
import net.sf.redmine_mylyn.api.model.IssueStatus;
import net.sf.redmine_mylyn.api.query.Query;
import net.sf.redmine_mylyn.common.logging.ILogService;
import net.sf.redmine_mylyn.core.client.IClient;
import net.sf.redmine_mylyn.internal.core.RedmineAttachmentHandler;
import net.sf.redmine_mylyn.internal.core.RedmineTaskMapper;
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
import org.eclipse.mylyn.internal.tasks.core.TaskTask;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.TaskRepositoryLocationFactory;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskAttachmentHandler;
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
	
	private ILogService log;
	
	public RedmineRepositoryConnector() {
		taskDataHandler = new RedmineTaskDataHandler(this);
		log = RedmineCorePlugin.getDefault().getLogService(getClass());
		
	}

	public synchronized ClientManager getClientManager() {
		if (clientManager == null) {
			IPath path = RedmineCorePlugin.getDefault().getRepostioryAttributeCachePath();
			IPath path2 = RedmineCorePlugin.getDefault().getRepostioryAttributeCachePath2();
			clientManager = new ClientManager(locationFactory, path.toFile(), path2.toFile());
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
			taskData = taskDataHandler.createTaskDataFromIssue(repository, issue, monitor);
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

	public TaskData[] getTaskData(TaskRepository repository, Set<String> taskIds, IProgressMonitor monitor) throws CoreException {
		monitor = Policy.monitorFor(monitor);
		monitor.beginTask("Task Download", IProgressMonitor.UNKNOWN);

		TaskData[] taskData = new TaskData[taskIds.size()];
		
		try {
			IClient client = getClientManager().getClient(repository);
			Issue[] issues = client.getIssues(taskIds, monitor);
			
			if(issues!=null) {
				for (int i=issues.length-1; i>=0; i--) {
					taskData[i] = taskDataHandler.createTaskDataFromIssue(repository, issues[i], monitor);
				}
			}
		} catch (OperationCanceledException e) {
			throw new CoreException(new Status(IStatus.CANCEL, RedmineCorePlugin.PLUGIN_ID, "Operation canceled"));
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
		TaskAttribute attribute = taskData.getRoot().getMappedAttribute(RedmineAttribute.DATE_UPDATED.getTaskKey());
		String repositoryDate = attribute.getValue();
		Date localeDate = task.getModificationDate();
		if (localeDate!=null) {
			return RedmineUtil.parseDate(repositoryDate).compareTo(localeDate)<0;
		}

		return true;
	}
	
	@Override
	public boolean canSynchronizeTask(TaskRepository taskRepository, ITask task) {
		//WORKAROUND: http://sourceforge.net/tracker/index.php?func=detail&aid=3069723&group_id=228995&atid=1075435
		if (!task.getConnectorKind().equals("redmine")) {
			if(task instanceof TaskTask) {
				try {
					Field f = TaskTask.class.getDeclaredField("connectorKind");
					f.setAccessible(true);
					f.set(task, RedmineCorePlugin.REPOSITORY_KIND);
				} catch (Exception e) {
					return false;
				}
			}
		}
		return true;
	}
	
	@Override
	public IStatus performQuery(TaskRepository repository, IRepositoryQuery repositoryQuery, TaskDataCollector collector, ISynchronizationSession session, IProgressMonitor monitor) {
		
		try {
			Query query = Query.fromUrl(repositoryQuery.getUrl(), repository.getCharacterEncoding(), getRepositoryConfiguration(repository));

			IClient client = getClientManager().getClient(repository);
			Issue[] partialIssues = client.query(query, monitor);
			
			for(Issue partialIssue : partialIssues) {
				Date updated = partialIssue.getUpdatedOn();
				
				// UpdatedOn should never be null
				if(updated==null) {
					IStatus status = new Status(IStatus.ERROR, RedmineCorePlugin.PLUGIN_ID, "Datum für updatedon fehlt");
					collector.failed(""+partialIssue.getId(), status);
					continue;
				}

				TaskData taskData = taskDataHandler.createTaskDataFromIssue(repository, partialIssue, monitor);

				//TODO mark only new or changed taks partial
				taskData.setPartial(true);
				
				collector.accept(taskData);
			}
			
		} catch (RedmineStatusException e) {
			IStatus status = e.getStatus();
			StatusHandler.log(status);
			return status;
		} catch (RedmineApiErrorException e) {
			IStatus status = RedmineCorePlugin.toStatus(e, "Syncronization failed");
			StatusHandler.log(status);
			return status;
		} catch (CoreException e) {
			e.getStatus();
		}
		
		return Status.OK_STATUS;
	}
	
	@Override
	public void preSynchronization(ISynchronizationSession session, IProgressMonitor monitor) throws CoreException {
		if (session.getTasks().isEmpty()) {
			return;
		}

		monitor = Policy.monitorFor(monitor);
		monitor.beginTask("Checking for changed tasks", 1);
		
		TaskRepository repository = session.getTaskRepository();
		if(repository.getSynchronizationTimeStamp()==null || repository.getSynchronizationTimeStamp().isEmpty()) {
			for (ITask task : session.getTasks()) {
				session.markStale(task);
			}
			return;
		}
		
		try {
			Date updatedSince = RedmineUtil.parseDate(repository.getSynchronizationTimeStamp());
			Set<ITask> tasks = session.getTasks();

			IClient client = getClientManager().getClient(repository);
			int[] changedIds = client.getUpdatedIssueIds(tasks, updatedSince, monitor);

			if(changedIds!=null && changedIds.length>0) {
				Arrays.sort(changedIds);
				for(ITask task : tasks) {
					if(Arrays.binarySearch(changedIds, RedmineUtil.parseIntegerId(task.getTaskId()))>=0) {
						session.markStale(task);
					}
				}
			}
		} catch (RedmineStatusException e) {
			throw new CoreException(e.getStatus());
		}
	}

	@Override
	public void postSynchronization(ISynchronizationSession event, IProgressMonitor monitor) throws CoreException {
		monitor = Policy.monitorFor(monitor);
		try {
			monitor.beginTask("", 1);
			if (event.isFullSynchronization() && event.getStatus() == null) {
				event.getTaskRepository().setSynchronizationTimeStamp(""+getSynchronizationTimestamp(event));
			} else {
				
			}
		} finally {
			monitor.done();
		}
	}

	@Override
	public void updateRepositoryConfiguration(TaskRepository repository, IProgressMonitor monitor) throws CoreException {
		try {
			getClientManager().getClient(repository).updateConfiguration(monitor);
		} catch (RedmineStatusException e) {
			throw new CoreException(e.getStatus());
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
		IssueStatus issueStatus = configuration.getIssueStatuses().getById(RedmineUtil.parseIntegerId(attribute.getValue()));
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

	@Override
	public AbstractTaskAttachmentHandler getTaskAttachmentHandler() {
		return new RedmineAttachmentHandler(this);
	}
	
	private long getSynchronizationTimestamp(ISynchronizationSession event) {
		Date mostRecent = new Date(0);
		String mostRecentTimeStamp = event.getTaskRepository().getSynchronizationTimeStamp();
		if (mostRecentTimeStamp != null) {
			mostRecent = RedmineUtil.parseDate(mostRecentTimeStamp);
		}
		
		for (ITask task : event.getChangedTasks()) {
			Date taskModifiedDate = task.getModificationDate();
			if (taskModifiedDate != null && taskModifiedDate.after(mostRecent)) {
				mostRecent = taskModifiedDate;
			}
		}
		return mostRecent.getTime();
	}
}
