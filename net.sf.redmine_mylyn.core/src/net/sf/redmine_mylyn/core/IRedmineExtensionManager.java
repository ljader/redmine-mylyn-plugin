package net.sf.redmine_mylyn.core;

import java.util.Map;

import org.eclipse.mylyn.tasks.core.IRepositoryListener;
import org.eclipse.mylyn.tasks.core.TaskRepository;

public interface IRedmineExtensionManager extends IRepositoryListener {

	public abstract Map<String, String> getExtensions();

	public abstract IRedmineExtensionField[] getAdditionalTimeEntryFields(
			TaskRepository repository);

}
