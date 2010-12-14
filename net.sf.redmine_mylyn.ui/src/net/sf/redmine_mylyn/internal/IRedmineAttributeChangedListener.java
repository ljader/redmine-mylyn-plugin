package net.sf.redmine_mylyn.internal;

import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;

public interface IRedmineAttributeChangedListener {

	public void attributeChanged(ITask task, TaskAttribute attribute);
	
}