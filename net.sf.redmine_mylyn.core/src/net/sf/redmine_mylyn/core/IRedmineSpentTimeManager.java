package net.sf.redmine_mylyn.core;

import org.eclipse.mylyn.tasks.core.ITask;

public interface IRedmineSpentTimeManager {

	public long getUncapturedSpentTime(ITask task);

	public long getAndClearUncapturedSpentTime(ITask task);
	
	public void resetUncapturedSpentTime(ITask task);
	
	public long getLastActivationTimestamp(ITask task);

	public long getLastDeactivationTimestamp(ITask task);

	public void addRedmineSpentTimeManagerListener(IRedmineSpentTimeManagerListener listener);

	public void removeRedmineSpentTimeManagerListener(IRedmineSpentTimeManagerListener listener);
	
}
