package net.sf.redmine_mylyn.api.client;

import net.sf.redmine_mylyn.api.model.Configuration;
import net.sf.redmine_mylyn.internal.api.client.Api_2_7_ClientImpl;

public class RedmineApiClientFactory {

	public static IRedmineApiClient createClient(IRedmineApiWebHelper webHelper, RedmineServerVersion.SubVersion redmineVersion, RedmineServerVersion.SubVersion pluginVersion, Configuration initialConfiguration) {
		//TODO
		return new Api_2_7_ClientImpl(webHelper, initialConfiguration);
	}
	
}
