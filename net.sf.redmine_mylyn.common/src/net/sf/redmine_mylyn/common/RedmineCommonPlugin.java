package net.sf.redmine_mylyn.common;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

public class RedmineCommonPlugin extends Plugin {

	public final static String PLUGIN_ID = "net.sf.redmine_mylyn.common.RedmineCommonPlugin"; //$NON-NLS-1$
	
	private static RedmineCommonPlugin plugin;
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		
		plugin = this;

		Bundle logBundle = Platform.getBundle("org.eclipse.equinox.log"); //$NON-NLS-1$
		if (logBundle != null && logBundle.getState()==Bundle.RESOLVED) {
			logBundle.start();
		}

	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		
		super.stop(context);
	}
	
	public static RedmineCommonPlugin getDefault() {
		return plugin;
	}
}

