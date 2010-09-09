package net.sf.redmine_mylyn.core;

import net.sf.redmine_mylyn.common.logging.ILogService;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogListener;
import org.osgi.service.log.LogReaderService;
import org.osgi.service.log.LogService;

public class RedmineCorePlugin extends Plugin implements LogListener /*implements BundleActivator*/ {

	private static BundleContext context;

	public static final String PLUGIN_ID = "net.sf.redmine_mylyn.core";

	public final static String REPOSITORY_KIND = "redmineV2";
	
	private static RedmineCorePlugin plugin;
	
	private RedmineRepositoryConnector connector;
	
	private ServiceReference logServiceReference;
	private LogService logService;
	
	private ServiceReference logReaderServiceReference;
	private LogReaderService logReaderService;
	
	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext bundleContext) throws Exception {
		super.start(bundleContext);
		
		RedmineCorePlugin.context = bundleContext;
		plugin = this;
		
		logServiceReference = context.getServiceReference(LogService.class.getName());
		if(logServiceReference!=null) {
			logService = (LogService)context.getService(logServiceReference);
		}

		logReaderServiceReference = context.getServiceReference(LogReaderService.class.getName());
		if(logReaderServiceReference!=null) {
			logReaderService = (LogReaderService)context.getService(logReaderServiceReference);
			logReaderService.addLogListener(this);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		if (logReaderServiceReference!=null) {
			logReaderService.removeLogListener(this);
			context.ungetService(logReaderServiceReference);
		}

		if (logServiceReference!=null) {
			context.ungetService(logServiceReference);
		}
		
		if(connector!=null) {
			connector.getClientManager().writeCache();
		}
		
		RedmineCorePlugin.context = null;
		super.stop(bundleContext);
	}

	@Override
	public void logged(LogEntry entry) {
		int severity = entry.getLevel()==LogService.LOG_ERROR ? IStatus.ERROR : IStatus.INFO;
		
		IStatus status = new Status(severity, entry.getBundle().getSymbolicName(), entry.getMessage(), entry.getException());
		if (entry.getLevel()==LogService.LOG_ERROR) {
			StatusHandler.fail(status);
		} else {
			StatusHandler.log(status);
		}
		
		//TODO logfile
	}
	
	public static RedmineCorePlugin getDefault() {
		return plugin;
	}

	public void setConnector(RedmineRepositoryConnector connector) {
		this.connector = connector;
	}
	
	public IPath getRepostioryAttributeCachePath() {
		IPath stateLocation = Platform.getStateLocation(getBundle());
		return stateLocation.append("repositoryClientDataCache");
	}

	public ILogService getLogService(Class<?> clazz) {
		return null;
	}
	
	public static IStatus toStatus(Throwable e, String message) {
		return new Status(IStatus.ERROR, PLUGIN_ID, message, e);
	}

	public static IStatus toStatus(Throwable e, String message, String... params) {
		for (int i = 0; i < params.length; i++) {
			message.replace("{"+i+"}", params[i]==null ? "<NULL>" : params[i]);
		}
		
		//unused placeholders
		message = message.replaceAll("\\{\\d+\\}", "");
		
		return toStatus(e, message);
	}

}
