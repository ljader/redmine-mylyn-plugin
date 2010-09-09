package net.sf.redmine_mylyn.api;

import net.sf.redmine_mylyn.common.logging.ILogService;
import net.sf.redmine_mylyn.common.logging.LogServiceOsgiImpl;
import net.sf.redmine_mylyn.common.logging.NullLogService;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;


public class RedmineApiPlugin extends Plugin {

	public final static String PLUGIN_ID = "net.sf.redmine_mylyn.api.client.RedmineApiPlugin";

	private static RedmineApiPlugin plugin;
	
	private ServiceReference logServiceReference;
	
	private LogService logService;

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		
		plugin = this;

		logServiceReference = context.getServiceReference(LogService.class.getName());
		if(logServiceReference!=null) {
			logService = (LogService)context.getService(logServiceReference);
		}

	}
	
	@Override
	public void stop(BundleContext context) throws Exception {
		if (logServiceReference!=null) {
			context.ungetService(logServiceReference);
		}
		
		plugin = null;
		
		super.stop(context);
	}

	public static RedmineApiPlugin getDefault() {
		return plugin;
	}

	public ILogService getLogService(Class<?> clazz) {
		if (logService!=null) {
			return new LogServiceOsgiImpl(logService);
		}
		return new NullLogService();
	}

}
