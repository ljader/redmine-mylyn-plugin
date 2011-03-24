package net.sf.redmine_mylyn.core;

import org.eclipse.mylyn.tasks.core.ITask;

public interface IRedmineSpentTimeManagerListener {

	public void uncapturedElapsedTimeUpdated(ITask task, long newUncapturedElapsedTimeUpdated);
	
}
