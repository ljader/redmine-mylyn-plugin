package net.sf.redmine_mylyn.api;

import net.sf.redmine_mylyn.common.logging.ILogService;
import net.sf.redmine_mylyn.common.logging.LogServiceImpl;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;


public class RedmineApiPlugin extends Plugin {

	public final static String PLUGIN_ID = "net.sf.redmine_mylyn.api.client.RedmineApiPlugin"; //$NON-NLS-1$

	private static RedmineApiPlugin plugin;
	
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		
		plugin = this;
	}
	
	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		
		super.stop(context);
	}

	public static RedmineApiPlugin getDefault() {
		return plugin;
	}

	public static ILogService getLogService(Class<?> clazz) {
		return plugin==null ? LogServiceImpl.getInstance() : LogServiceImpl.getInstance(plugin.getBundle(), clazz);
	}

}
