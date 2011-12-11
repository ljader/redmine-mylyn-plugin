package net.sf.redmine_mylyn.internal.core.client;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import net.sf.redmine_mylyn.api.client.IRedmineApiWebHelper;
import net.sf.redmine_mylyn.api.exception.RedmineApiAuthenticationException;
import net.sf.redmine_mylyn.core.IRedmineConstants;
import net.sf.redmine_mylyn.core.RedmineCorePlugin;
import net.sf.redmine_mylyn.internal.core.Messages;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NTCredentials;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.net.AbstractWebLocation;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.commons.net.UnsupportedRequestException;
import org.eclipse.mylyn.commons.net.WebUtil;
import org.eclipse.mylyn.tasks.core.TaskRepository;

public class ApiWebHelper implements IRedmineApiWebHelper {

	private final AbstractWebLocation location;
	private final TaskRepository repository;
	
	public ApiWebHelper(AbstractWebLocation location, TaskRepository repository) {
		this.location = location;
		this.repository = repository;
	}
	
	@Override
	public String getBasePath() {
		try {
			return new URL(location.getUrl()).getPath();
		} catch (MalformedURLException e) {
			//TODO ExceptionHandling
			e.printStackTrace();
		}
		return "/"; //$NON-NLS-1$
	}
	@Override
	public boolean useApiKey() {
		return repository.getProperty(IRedmineConstants.REPOSITORY_SETTING_API_KEY)!=null;
	}

	@Override
	public String getApiKey() {
		return repository.getProperty(IRedmineConstants.REPOSITORY_SETTING_API_KEY);
	}

	@Override
	public Credentials getRepositoryCredentials() {
		AuthenticationCredentials credentials = location.getCredentials(AuthenticationType.REPOSITORY);
		
		
		if(credentials!=null && !credentials.getUserName().isEmpty()) {
			try {
				URL url = new URL(location.getUrl());

				String host = url.getHost();
				String username = credentials.getUserName();
				String password = credentials.getPassword();
				
				Credentials httpCredentials = new UsernamePasswordCredentials(username, password);
				int i = username.indexOf("\\"); //$NON-NLS-1$
				if (i > 0 && i < username.length() - 1 && host != null) {
					httpCredentials = new NTCredentials(username.substring(i + 1), password, host, username.substring(0, i));
				}
				
				return httpCredentials;
			} catch (MalformedURLException e) {
				IStatus status = RedmineCorePlugin.toStatus(e, Messages.ERRMSG_MALFORMED_URL);
				StatusHandler.fail(status);
			}
			
		}
		
		return null;
	}

	@Override
	public HostConfiguration createHostConfiguration(HttpClient httpClient, IProgressMonitor monitor) {
		return WebUtil.createHostConfiguration(httpClient, location, monitor);
	}
	
	@Override
	public int execute(HttpClient httpClient, HostConfiguration hostConfiguration, HttpMethod httpMethod, IProgressMonitor monitor) throws IOException {
		return WebUtil.execute(httpClient, hostConfiguration, httpMethod, monitor);
	}
	
	@Override
	public void refreshRepostitoryCredentials(String message, IProgressMonitor monitor) throws RedmineApiAuthenticationException {
		try {
			location.requestCredentials(AuthenticationType.REPOSITORY, message, monitor);
		} catch (UnsupportedRequestException e) {
			throw new RedmineApiAuthenticationException(Messages.ERRMSG_CANT_REQUEST_CREDENTIALS, e);
		}
	}
	@Override
	public void refreshHttpAuthCredentials(String message, IProgressMonitor monitor) throws RedmineApiAuthenticationException {
		try {
			location.requestCredentials(AuthenticationType.HTTP, message, monitor);
		} catch (UnsupportedRequestException e) {
			throw new RedmineApiAuthenticationException(Messages.ERRMSG_CANT_REQUEST_CREDENTIALS, e);
		}
	}
	
	@Override
	public void refreshProxyCredentials(String message, IProgressMonitor monitor) throws RedmineApiAuthenticationException {
		try {
			location.requestCredentials(AuthenticationType.PROXY, message, monitor);
		} catch (UnsupportedRequestException e) {
			throw new RedmineApiAuthenticationException(Messages.ERRMSG_CANT_REQUEST_CREDENTIALS, e);
		}
	}
}
