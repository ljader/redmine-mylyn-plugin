package net.sf.redmine_mylyn.common;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

public class RedmineCommonPlugin extends Plugin {

	public final static String PLUGIN_ID = "net.sf.redmine_mylyn.common.RedmineCommonPlugin";
	
	private static RedmineCommonPlugin plugin;
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
	
	public static RedmineCommonPlugin getDefault() {
		return plugin;
	}

	public IPath getLogFilePath() {
		return getStateLocation().append("redmine_connector.log");
	}
}

