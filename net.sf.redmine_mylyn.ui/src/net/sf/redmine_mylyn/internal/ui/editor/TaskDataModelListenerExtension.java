package net.sf.redmine_mylyn.internal.ui.editor;

import java.util.ArrayList;
import java.util.List;

import net.sf.redmine_mylyn.common.logging.ILogService;
import net.sf.redmine_mylyn.ui.RedmineUiPlugin;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.mylyn.tasks.core.data.TaskDataModelEvent;
import org.eclipse.mylyn.tasks.core.data.TaskDataModelListener;

public class TaskDataModelListenerExtension extends TaskDataModelListener {

	private static final ILogService LOG_SERVICE = RedmineUiPlugin
			.getLogService(TaskDataModelListenerExtension.class);

	private List<TaskDataModelListener> extensionPoints;

	@Override
	public void attributeChanged(final TaskDataModelEvent event) {
		checkExtensionPoints();
		for (final TaskDataModelListener taskDataModelListener : extensionPoints) {
			executeExtension(taskDataModelListener, event);
		}
	}

	private void checkExtensionPoints() {
		if (extensionPoints == null) {
			final List<TaskDataModelListener> handlers = new ArrayList<TaskDataModelListener>();
			final IConfigurationElement[] config = Platform
					.getExtensionRegistry().getConfigurationElementsFor(
							"net.sf.redmine_mylyn.editor.modelchanger");
			try {
				for (final IConfigurationElement configElement : config) {
					final Object o = configElement.createExecutableExtension("class");
					if (o instanceof TaskDataModelListener) {
						handlers.add((TaskDataModelListener) o);
					}
				}
			} catch (final CoreException e) {
				LOG_SERVICE
						.error(e,
								"Error configuring extension point for model change events.");
			}
			extensionPoints = handlers;
		}
	}

	private void executeExtension(final TaskDataModelListener o,
			final TaskDataModelEvent event) {
		final ISafeRunnable runnable = new ISafeRunnable() {
			@Override
			public void handleException(final Throwable e) {
				LOG_SERVICE
						.error(e,
								"Exception in client implementation of model change event handler.");
			}

			@Override
			public void run() throws Exception {
				o.attributeChanged(event);
			}
		};
		SafeRunner.run(runnable);
	}

}
