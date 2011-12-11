package net.sf.redmine_mylyn.core;

import java.text.MessageFormat;

import net.sf.redmine_mylyn.common.logging.ILogService;
import net.sf.redmine_mylyn.common.logging.LogServiceImpl;
import net.sf.redmine_mylyn.internal.core.RedmineExtensionManager;
import net.sf.redmine_mylyn.internal.core.RedmineSpentTimeManager;
import net.sf.redmine_mylyn.internal.core.client.ClientManager;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.tasks.core.ITaskActivityManager;
import org.osgi.framework.BundleContext;

public class RedmineCorePlugin extends Plugin /*implements BundleActivator*/ {

	private static BundleContext context;

	public static final String PLUGIN_ID = "net.sf.redmine_mylyn.core"; //$NON-NLS-1$

	public final static String REPOSITORY_KIND = "redmineV2"; //$NON-NLS-1$
	
	private static RedmineCorePlugin plugin;
	
	private RedmineRepositoryConnector connector;
	
	private RedmineSpentTimeManager spentTimeManager;
	
	private RedmineExtensionManager extensionManager;
	
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

		if(spentTimeManager!=null) {
			spentTimeManager.start();
		}
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		if(spentTimeManager!=null) {
			spentTimeManager.stop();
		}

		if(connector!=null) {
			((ClientManager)connector.getClientManager()).writeCache();
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
		return stateLocation.append("repositoryClientDataCache"); //$NON-NLS-1$
	}

	public IPath getRepostioryAttributeCachePath2() {
		IPath stateLocation = Platform.getStateLocation(getBundle());
		return stateLocation.append("repositoryClientDataCache.zip"); //$NON-NLS-1$
	}
	
	public IRedmineSpentTimeManager getSpentTimeManager(ITaskActivityManager taskActivityManager) {
		if(spentTimeManager==null) {
			spentTimeManager = new RedmineSpentTimeManager(taskActivityManager);

			if(context!=null) {
				spentTimeManager.start();
			}
		}
		
		return spentTimeManager;
	}
	
	public IRedmineExtensionManager getExtensionManager() {
		if(extensionManager==null) {
			extensionManager = RedmineExtensionManager.getInstance();
		}
		return extensionManager;
	}
	
	public static ILogService getLogService(Class<?> clazz) {
		return LogServiceImpl.getInstance((plugin==null ? null : plugin.getBundle()), clazz);
	}
	
	public static IStatus toStatus(Throwable e, String message) {
		return new Status(IStatus.ERROR, PLUGIN_ID, message, e);
	}

	public static IStatus toStatus(Throwable e, String message, Object... params) {
		message = MessageFormat.format(message, params);
		return toStatus(e, message);
	}

}
