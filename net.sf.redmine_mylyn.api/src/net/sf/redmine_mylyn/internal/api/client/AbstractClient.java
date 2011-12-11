package net.sf.redmine_mylyn.internal.api.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Arrays;

import net.sf.redmine_mylyn.api.RedmineApiPlugin;
import net.sf.redmine_mylyn.api.client.IRedmineApiClient;
import net.sf.redmine_mylyn.api.client.IRedmineApiWebHelper;
import net.sf.redmine_mylyn.api.exception.RedmineApiAuthenticationException;
import net.sf.redmine_mylyn.api.exception.RedmineApiErrorException;
import net.sf.redmine_mylyn.api.exception.RedmineApiHttpStatusException;
import net.sf.redmine_mylyn.common.logging.ILogService;
import net.sf.redmine_mylyn.internal.api.Messages;
import net.sf.redmine_mylyn.internal.api.parser.IModelParser;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HeaderElement;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.mylyn.commons.net.Policy;
import org.eclipse.mylyn.commons.net.WebUtil;

public abstract class AbstractClient implements IRedmineApiClient {

	protected final static String HEADER_STATUS = "status"; //$NON-NLS-1$

	protected final static String HEADER_REDIRECT = "location"; //$NON-NLS-1$

	protected final static String HEADER_WWW_AUTHENTICATE = "WWW-Authenticate"; //$NON-NLS-1$

	protected final static String HEADER_WWW_AUTHENTICATE_REALM = "realm"; //$NON-NLS-1$
	
	protected final static String REDMINE_REALM = "Redmine API"; //$NON-NLS-1$

	public final static String REDMINE_URL_LOGIN = "/login"; //$NON-NLS-1$

	public final static String REDMINE_URL_LOGIN_CALLBACK = "/login?back_url="; //$NON-NLS-1$

	public final static String REDMINE_URL_LOGIN = "/login";

	public final static String REDMINE_URL_LOGIN_CALLBACK = "/login?back_url=";

	protected final HttpClient httpClient;
	
	protected final IRedmineApiWebHelper webHelper;
	
	protected URL url;
	
	protected String characterEncoding = "UTF-8"; //$NON-NLS-1$
	
	private ILogService log;
	
	public AbstractClient(IRedmineApiWebHelper webHelper) {
		log = RedmineApiPlugin.getLogService(AbstractClient.class);
		
		this.webHelper = webHelper;
		
		MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
		httpClient = new HttpClient(connectionManager);
		httpClient.getParams().setCookiePolicy(CookiePolicy.RFC_2109);
		httpClient.getParams().setAuthenticationPreemptive(true);
	}

	protected <T extends Object> T executeMethod(HttpMethodBase method, IModelParser<T> parser, IProgressMonitor monitor) throws RedmineApiErrorException {
		return executeMethod(method, parser, monitor, HttpStatus.SC_OK);
	}
	
	protected <T extends Object> T executeMethod(HttpMethodBase method, IModelParser<T> parser, IProgressMonitor monitor, int... expectedSC) throws RedmineApiErrorException {
		monitor = Policy.monitorFor(monitor);
		method.setFollowRedirects(false);

		T response = null;
		try {
			int sc = performExecuteMethod(method, monitor);

			//HTTP-Status 500
			if (sc==HttpStatus.SC_INTERNAL_SERVER_ERROR) {
				Header statusHeader = method.getResponseHeader(HEADER_STATUS);
				String msg = Messages.ERRMSG_SERVER_ERROR;
				if (statusHeader != null) {
					msg += " : " + statusHeader.getValue().replace(""+HttpStatus.SC_INTERNAL_SERVER_ERROR, "").trim(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				}
				
				log.error(msg);
				throw new RedmineApiHttpStatusException(sc, msg);
			}
			
			//HTTP-Status 302 => login page
			if(sc==HttpStatus.SC_MOVED_TEMPORARILY) {
				Header respHeader = method.getResponseHeader(HEADER_REDIRECT);
				if (respHeader != null && (respHeader.getValue().endsWith(REDMINE_URL_LOGIN) || respHeader.getValue().contains(REDMINE_URL_LOGIN_CALLBACK))) {

					log.error(Messages.ERRMSG_REST_SERVICE_NOT_ENABLED_OR_INVALID_CGI);
					throw new RedmineApiHttpStatusException(sc, Messages.ERRMSG_REST_SERVICE_NOT_ENABLED_OR_INVALID_CGI);
				}		
			}

			if (parser!=null && expectedSC != null) {
				Arrays.sort(expectedSC);
				if(Arrays.binarySearch(expectedSC, sc)>=0) {
					InputStream input = WebUtil.getResponseBodyAsStream(method, monitor);
					try {
						response = parser.parseResponse(input, sc);
					} finally {
						input.close();
					}
				} else {
					Header statusHeader = method.getResponseHeader(HEADER_STATUS);
					String value = statusHeader==null ? ""+sc : statusHeader.getValue(); //$NON-NLS-1$

					log.error(Messages.ERRMSG_UNEXPECTED_HTTP_STATUS_X, value);
					throw new RedmineApiHttpStatusException(sc, Messages.ERRMSG_UNEXPECTED_HTTP_STATUS_X, value);
				}
			}
		} catch (IOException e) {
			//TODO
			log.error(e, Messages.ERRMSG_METHOD_EXECUTION_FAILED_X, e.getMessage());
			throw new RedmineApiErrorException(Messages.ERRMSG_METHOD_EXECUTION_FAILED, e);
		} finally {
			method.releaseConnection();
		}
		
		return response;
	}
	
	synchronized protected int performExecuteMethod(HttpMethod method, IProgressMonitor monitor) throws RedmineApiErrorException {
		try {
			HostConfiguration hostConfiguration = webHelper.createHostConfiguration(httpClient, monitor);

			//complete URL
			String basePath = webHelper.getBasePath();
			if (!method.getPath().startsWith(basePath)) {
				method.setPath(basePath + method.getPath());
			}
			
			//Authentication
			if (webHelper.useApiKey()) {
				//Api-Key
				StringBuilder queryString = new StringBuilder();
				queryString.append("key=").append(webHelper.getApiKey()); //$NON-NLS-1$
				if(method.getQueryString()!=null) {
					queryString.append('&');
					queryString.append(method.getQueryString());
				}
				method.setQueryString(queryString.toString());
			} else {
				//Redmine Credentials
				Credentials credentials = webHelper.getRepositoryCredentials();
				if(credentials!=null) {
					
					AuthScope authScope = new AuthScope(hostConfiguration.getHost(), hostConfiguration.getPort(), REDMINE_REALM);
					httpClient.getState().setCredentials(authScope, credentials);
				}
			}
			
			//Perform Method
			String query = method.getQueryString();
			if(query==null) {
				log.debug(Messages.LOG_HTTP_METHOD_X_X, method.getName(), method.getPath());
			} else {
				log.debug(Messages.LOG_HTTP_METHOD_X_X_X, method.getName(), method.getPath(), URLDecoder.decode(query, "UTF-8")); //$NON-NLS-2$ //$NON-NLS-1$ //$NON-NLS-1$ //$NON-NLS-1$
			}
			
			int sc = webHelper.execute(httpClient, hostConfiguration, method, monitor);
			
			//Update incorrect credentials
			if(sc==HttpStatus.SC_UNAUTHORIZED || sc==HttpStatus.SC_PROXY_AUTHENTICATION_REQUIRED) {
				refreshCredentials(sc, method, monitor);
				performExecuteMethod(method, monitor);
			}
			
			return sc;
		} catch (RuntimeException e) {
			log.error(e, Messages.ERRMSG_UNEXCPECTED_EXCEPTION_METHOD_EXECUTION_FAILED_X, e.getMessage());
			throw new RedmineApiErrorException(Messages.ERRMSG_UNEXCPECTED_EXCEPTION_METHOD_EXECUTION_FAILED, e);
		} catch (ConnectException e) {
			log.info(Messages.ERRMSG_METHOD_EXECUTION_FAILED_X, e.getMessage());
			throw new RedmineApiErrorException(e.getMessage(), e);
		} catch (IOException e) {
			//TODO
			log.error(e, Messages.ERRMSG_METHOD_EXECUTION_FAILED_X, e.getMessage());
			throw new RedmineApiErrorException(Messages.ERRMSG_METHOD_EXECUTION_FAILED, e);
		}
	}
	
	protected void refreshCredentials(int statusCode, HttpMethod method, IProgressMonitor monitor) throws RedmineApiErrorException {
		if (Policy.isBackgroundMonitor(monitor)) {
			throw new RedmineApiAuthenticationException(Messages.ERRMSG_MISSING_CREDENTIALS_SYNCHRONIZATION_FAILED);
		}
		
		try {
			String message = Messages.AUTHENTICATION_REQUIRED;
			switch (statusCode) {
			case HttpStatus.SC_UNAUTHORIZED:

				Header authHeader = method.getResponseHeader(HEADER_WWW_AUTHENTICATE);
				if(authHeader!=null) {
					for (HeaderElement headerElem : authHeader.getElements()) {
						if (headerElem.getName().contains(HEADER_WWW_AUTHENTICATE_REALM)) {
							if(headerElem.getValue().equals(REDMINE_REALM)) {
								webHelper.refreshRepostitoryCredentials(message, monitor);
							} else {
								if (webHelper.useApiKey()) {
									webHelper.refreshHttpAuthCredentials(message + ": " + headerElem.getValue(), monitor); //$NON-NLS-1$
								} else {
									throw new RedmineApiErrorException(Messages.ERRMSG_ADDITIONAL_HTTPAUTH_NOT_SUPPORTED);
								}
							}
							break;
						}
					}
				}
				break;
				
			case HttpStatus.SC_PROXY_AUTHENTICATION_REQUIRED:
				
				webHelper.refreshProxyCredentials(message, monitor);
				break;
				
			}
			
		} catch (OperationCanceledException e) {
			monitor.setCanceled(true);
			throw new RedmineApiAuthenticationException(Messages.AUTHENTICATION_CANCELED);
		}
	}

}
