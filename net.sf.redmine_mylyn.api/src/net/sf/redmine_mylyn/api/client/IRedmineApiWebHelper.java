package net.sf.redmine_mylyn.api.client;

import java.io.IOException;

import net.sf.redmine_mylyn.api.exception.RedmineApiAuthenticationException;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.eclipse.core.runtime.IProgressMonitor;

public interface IRedmineApiWebHelper {

	public String getBasePath();
	
	public boolean useApiKey();
	
	public String getApiKey();
	
	public Credentials getRepositoryCredentials();
	
	public HostConfiguration createHostConfiguration(HttpClient httpClient, IProgressMonitor monitor);
	
	public int execute(HttpClient httpClient, HostConfiguration hostConfiguration, HttpMethod httpMethod, IProgressMonitor monitor) throws IOException;
	
	public void refreshRepostitoryCredentials(String message, IProgressMonitor monitor) throws RedmineApiAuthenticationException;

	public void refreshHttpAuthCredentials(String message, IProgressMonitor monitor) throws RedmineApiAuthenticationException;

	public void refreshProxyCredentials(String message, IProgressMonitor monitor) throws RedmineApiAuthenticationException;
	
}
