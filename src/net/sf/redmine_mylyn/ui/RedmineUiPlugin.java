package net.sf.redmine_mylyn.ui;

import net.sf.redmine_mylyn.core.RedmineCorePlugin;
import net.sf.redmine_mylyn.core.RedmineRepositoryConnector;

import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.ui.TaskRepositoryLocationUiFactory;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class RedmineUiPlugin extends AbstractUIPlugin {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	@Override
	public void start(BundleContext bundleContext) throws Exception {
		RedmineUiPlugin.context = bundleContext;
		
		AbstractRepositoryConnector connector = TasksUi.getRepositoryConnector(RedmineCorePlugin.REPOSITORY_KIND);
		if(connector instanceof RedmineRepositoryConnector) {
			RedmineRepositoryConnector redmineConnector = (RedmineRepositoryConnector)connector;
			
			redmineConnector.setTaskRepositoryLocationFactory(new TaskRepositoryLocationUiFactory());
			TasksUi.getRepositoryManager().addListener(redmineConnector.getClientManager());
			
			RedmineCorePlugin.getDefault().setConnector(redmineConnector);
		}
		
}

	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		RedmineUiPlugin.context = null;
	}

}
