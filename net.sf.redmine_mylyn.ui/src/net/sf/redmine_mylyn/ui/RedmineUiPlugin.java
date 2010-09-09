package net.sf.redmine_mylyn.ui;

import net.sf.redmine_mylyn.core.RedmineCorePlugin;
import net.sf.redmine_mylyn.core.RedmineRepositoryConnector;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.ui.TaskRepositoryLocationUiFactory;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.statushandlers.StatusManager;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogListener;
import org.osgi.service.log.LogReaderService;
import org.osgi.service.log.LogService;

public class RedmineUiPlugin extends AbstractUIPlugin implements LogListener {

	public static final String PLUGIN_ID = "net.sf.redmine_mylyn.ui";

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}
	
	private ServiceReference logReaderServiceReference;
	
	private LogReaderService logReaderService;

	@Override
	public void start(BundleContext bundleContext) throws Exception {
		super.start(bundleContext);
		
		RedmineUiPlugin.context = bundleContext;
		
		AbstractRepositoryConnector connector = TasksUi.getRepositoryConnector(RedmineCorePlugin.REPOSITORY_KIND);
		if(connector instanceof RedmineRepositoryConnector) {
			RedmineRepositoryConnector redmineConnector = (RedmineRepositoryConnector)connector;
			
			redmineConnector.setTaskRepositoryLocationFactory(new TaskRepositoryLocationUiFactory());
			TasksUi.getRepositoryManager().addListener(redmineConnector.getClientManager());
			
			RedmineCorePlugin.getDefault().setConnector(redmineConnector);
		}
		
		
		logReaderServiceReference = bundleContext.getServiceReference(LogReaderService.class.getName());
		if(logReaderServiceReference!=null) {
			logReaderService = (LogReaderService)bundleContext.getService(logReaderServiceReference);
			logReaderService.addLogListener(this);
		}
	}

	
	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		if(logReaderService!=null) {
			logReaderService.removeLogListener(this);
			bundleContext.ungetService(logReaderServiceReference);
		}
		
		RedmineUiPlugin.context = null;
		
		super.stop(bundleContext);
	}

	@Override
	public void logged(LogEntry entry) {
		if (entry.getBundle().getSymbolicName().startsWith("net.sf.redmine_mylyn.")) {
			IStatus status = buildStatus(entry);

			getLog().log(buildStatus(entry));

			if(status.getSeverity()==IStatus.ERROR) {
				StatusManager.getManager().handle(status, StatusManager.SHOW);
			}
		}
	}
	
	private IStatus buildStatus(LogEntry entry) {
		int severity = entry.getLevel()==LogService.LOG_ERROR ? IStatus.ERROR : IStatus.INFO;
		String pluginId = entry.getBundle().getSymbolicName();
		
		return new Status(severity, pluginId, entry.getMessage(), entry.getException());
	}

	
}
