package net.sf.redmine_mylyn.api.client;

import net.sf.redmine_mylyn.internal.api.client.Api_2_7_ClientImpl;

import org.eclipse.mylyn.commons.net.AbstractWebLocation;

public class RedmineApiClientFactory {

	public static IRedmineApiClient createClient(AbstractWebLocation location, RedmineServerVersion.SubVersion redmineVersion, RedmineServerVersion.SubVersion pluginVersion) {
		//TODO
		return new Api_2_7_ClientImpl(location);
	}
	
}
