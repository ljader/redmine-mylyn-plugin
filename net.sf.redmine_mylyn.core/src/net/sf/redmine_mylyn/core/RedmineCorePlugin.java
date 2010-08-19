package net.sf.redmine_mylyn.core;


import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

public class RedmineCorePlugin extends Plugin /*implements BundleActivator*/ {

	private static BundleContext context;

	public static final String PLUGIN_ID = "net.sf.redmine_mylyn.core";

	public final static String REPOSITORY_KIND = "redmineV2";
	
	private static RedmineCorePlugin plugin;
	
	private RedmineRepositoryConnector connector;
	
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
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		if(connector!=null) {
			connector.getClientManager().writeCache();
		}
		
		RedmineCorePlugin.context = null;
		super.stop(bundleContext);
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
