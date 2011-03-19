package net.sf.redmine_mylyn.internal.core;

import java.util.ArrayList;

import net.sf.redmine_mylyn.core.IRedmineSpentTimeManager;
import net.sf.redmine_mylyn.core.IRedmineSpentTimeManagerListener;
import net.sf.redmine_mylyn.core.RedmineCorePlugin;

import org.eclipse.core.runtime.Assert;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskActivationListener;
import org.eclipse.mylyn.tasks.core.ITaskActivityListener;
import org.eclipse.mylyn.tasks.core.ITaskActivityManager;
import org.eclipse.mylyn.tasks.core.TaskActivationAdapter;
import org.eclipse.mylyn.tasks.core.TaskActivityAdapter;

public class RedmineSpentTimeManager implements IRedmineSpentTimeManager {

	private final static String ELAPSED_TIME = "RedmineSpentTimeManager.elapsedTime"; //$NON-NLS-1$

	private final static String UNCAPTURED_SPENT_TIME = "RedmineSpentTimeManager.uncapturedSpentTime"; //$NON-NLS-1$
	
	private final ITaskActivityManager taskActivityManager;
	
	private final ITaskActivityListener taskActivityListener;
	
	private final ITaskActivationListener taskActivationListener;
	
	private final ArrayList<IRedmineSpentTimeManagerListener> listeners = new ArrayList<IRedmineSpentTimeManagerListener>();
	
	public RedmineSpentTimeManager(ITaskActivityManager taskActivityManager) {
		this.taskActivityManager = taskActivityManager;
		
		taskActivityListener = new TaskActivityAdapter() {
			@Override
			public void elapsedTimeUpdated(ITask task, long newElapsedTime) {
				if(isUsableTask(task)) {
					if(RedmineSpentTimeManager.this.taskActivityManager.isActive(task)) {
						setActiveTask(task);
					}
				}
			}
		};
		
		taskActivationListener = new TaskActivationAdapter() {
			@Override
			public void taskActivated(ITask task) {
			}
			
			@Override
			public void taskDeactivated(ITask task) {
			}
		};
	}
	
	public void start() {

		taskActivityManager.addActivityListener(taskActivityListener);
		taskActivityManager.addActivationListener(taskActivationListener);
		
	}

	public void stop() {
		taskActivityManager.removeActivityListener(taskActivityListener);
		taskActivityManager.removeActivationListener(taskActivationListener);
	}

	@Override
	public long getUncapturedSpentTime(ITask task) {
		Assert.isNotNull(task);
		Assert.isTrue(isUsableTask(task));
		return readLongAttribute(task, UNCAPTURED_SPENT_TIME);
	}
	
	@Override
	public long getAndClearUncapturedSpentTime(ITask task) {
		Assert.isNotNull(task);
		Assert.isTrue(isUsableTask(task));
		long uncaptured = getUncapturedSpentTime(task);
		setUncapturedSpentTime(task, 0);
		return uncaptured;
	}
	
	@Override
	public void resetUncapturedSpentTime(ITask task) {
		Assert.isNotNull(task);
		Assert.isTrue(isUsableTask(task));
		setUncapturedSpentTime(task, 0);
	}
	
	@Override
	public void addRedmineSpentTimeManagerListener(IRedmineSpentTimeManagerListener listener) {
		synchronized (listeners) {
			if(listener!=null && !listeners.contains(listener)) {
				listeners.add(listener);
			}
		}
	}
	
	@Override
	public void removeRedmineSpentTimeManagerListener(IRedmineSpentTimeManagerListener listener) {
		synchronized (listeners) {
			if(listener!=null) {
				listeners.remove(listener);
			}
		}
	}
	
	private boolean isUsableTask(ITask task) {
		if(!task.getConnectorKind().equals(RedmineCorePlugin.REPOSITORY_KIND))
			return false;
		
		if(task.getRepositoryUrl()==null || task.getRepositoryUrl().isEmpty())
			return false;
		
		if(task.getTaskId()==null || task.getTaskId().isEmpty())
			return false;
		
		return true;
	}
	
	private void setElapsedTime(ITask task, long newElapsedTime) {
		long oldElapsedTime = readLongAttribute(task, ELAPSED_TIME);
		long uncapturedSpentTime  = readLongAttribute(task, UNCAPTURED_SPENT_TIME);
		
		if(newElapsedTime < oldElapsedTime) {
			uncapturedSpentTime = newElapsedTime;
		} else {
			uncapturedSpentTime += newElapsedTime-oldElapsedTime;
		}
		
		writeLongAttribute(task, ELAPSED_TIME, newElapsedTime);
		setUncapturedSpentTime(task, uncapturedSpentTime);
	}
	
	private void setUncapturedSpentTime(ITask task, long newUncapturedSpentTime) {
		writeLongAttribute(task, UNCAPTURED_SPENT_TIME, newUncapturedSpentTime);
		
		synchronized (listeners) {
			for (IRedmineSpentTimeManagerListener listener : listeners) {
				listener.uncapturedElapsedTimeUpdated(task, newUncapturedSpentTime);
			}
		}
	}
	
	private void setActiveTask(ITask task) {
		setElapsedTime(task, taskActivityManager.getElapsedTime(task));
	}
	
	private long readLongAttribute(ITask task, String attribute) {
		long val = 0;
		String stringVal = task.getAttribute(attribute);
		if(stringVal!=null) {
			try {
				val = Long.parseLong(stringVal);
			} catch(NumberFormatException e) {
				RedmineCorePlugin.getDefault().getLogService(this.getClass()).error(e, Messages.ERRMSG_INVALID_LONG, stringVal);
			}
		}
		return val;
	}
	
	private void writeLongAttribute(ITask task, String attribute, long newValue) {
		task.setAttribute(attribute, Long.toString(newValue));
	}
}
