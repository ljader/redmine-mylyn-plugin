package net.sf.redmine_mylyn.internal.api.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.redmine_mylyn.api.client.IRedmineApiErrorCollector;
import net.sf.redmine_mylyn.api.client.IRedmineApiWebHelper;
import net.sf.redmine_mylyn.api.exception.RedmineApiAuthenticationException;
import net.sf.redmine_mylyn.api.model.Attachment;
import net.sf.redmine_mylyn.api.model.CustomValue;
import net.sf.redmine_mylyn.api.model.Issue;
import net.sf.redmine_mylyn.api.model.Journal;
import net.sf.redmine_mylyn.internal.api.IssueValidator;
import net.sf.redmine_mylyn.internal.api.PartialIssueValidator;
import net.sf.redmine_mylyn.internal.api.ServerVersionValidator;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class Api_2_7_ClientImplDynamicTest {

	private final static String RESPONSE_HEADER_OK = "HTTP/1.0 200 OK\n\n";

	private IProgressMonitor monitor;

	private static TestServer server; 

	private static ErrorCollector errorCollector;
	
	private Api_2_7_ClientImpl testee;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		errorCollector = new ErrorCollector();
		server = new TestServer();
		server.start();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		server.interrupt();
	}

	@Before
	public void setUp() throws Exception {
		monitor = new NullProgressMonitor();
		testee = new Api_2_7_ClientImpl(new IRedmineApiWebHelper() {
			@Override
			public String getBasePath() {
				return "/";
			}
			@Override
			public boolean useApiKey() {
				return false;
			}
			@Override
			public String getApiKey() {
				return null;
			}
			@Override
			public Credentials getRepositoryCredentials() {
				return new UsernamePasswordCredentials("jsmith", "jsmith");
			}
			@Override
			public HostConfiguration createHostConfiguration(HttpClient httpClient, IProgressMonitor monitor) {
				HostConfiguration hostConfiguration = new HostConfiguration();
				hostConfiguration.setHost("localhost", 1235);
				return hostConfiguration;
			}
			@Override
			public int execute(HttpClient httpClient, HostConfiguration hostConfiguration, HttpMethod httpMethod, IProgressMonitor monitor) throws IOException {
				return httpClient.executeMethod(hostConfiguration, httpMethod);
			}
			@Override
			public void refreshRepostitoryCredentials(String message, IProgressMonitor monitor) throws RedmineApiAuthenticationException {}
			@Override
			public void refreshHttpAuthCredentials(String message, IProgressMonitor monitor) throws RedmineApiAuthenticationException {}
			@Override
			public void refreshProxyCredentials(String message,IProgressMonitor monitor) throws RedmineApiAuthenticationException {}
			
		});
		errorCollector.lst.clear();

		server.responseHeader = RESPONSE_HEADER_OK;
		server.responseResourcePath = null;

	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetIssues() throws Exception {
		String path = "/dynamicxmldata/getIssues/";
		File responseFiles = new File(getClass().getResource(path).getFile());
		
		for (String responseFile : responseFiles.list() ) {
			server.responseResourcePath = path + responseFile; 
			Issue[] issues = testee.getIssues(monitor, 0);
			
			for (Issue issue : issues) {
				
				for (Object journal : issue.getJournals().getAll() ) {
					Assert.assertTrue(journal instanceof Journal);
				}

				for (Object journal : issue.getAttachments().getAll() ) {
					Assert.assertTrue(journal instanceof Attachment);
				}
				
				for (Object journal : issue.getCustomValues().getAll() ) {
					Assert.assertTrue(journal instanceof CustomValue);
				}
				
			}
			
		}
		
	} 
	

	private static class ErrorCollector implements IRedmineApiErrorCollector {
		public ArrayList<String> lst = new ArrayList<String>();
		
		@Override
		public void accept(String errorMessage) {
			lst.add(errorMessage);
		}
	}

	private static class TestServer extends Thread {
		final Map <String, String> requestMap;
		final Pattern p = Pattern.compile("^(?:GET|POST)\\s+(?:/mylyn)?/(\\S+).*$", Pattern.CASE_INSENSITIVE);
		
		public String responseHeader;
		public String responseResourcePath;
		
		public TestServer() {
			requestMap = new HashMap<String, String>();
			requestMap.put("version", ServerVersionValidator.RESOURCE_FILE);
			requestMap.put("issues/updatedsince?issues=1,6,7,8&unixtime=123456789", IssueValidator.RESOURCE_FILE_UPDATED);
			requestMap.put("issue/1", IssueValidator.RESOURCE_FILE_ISSUE_1);
			requestMap.put("issues/list?issues=1,7,8", IssueValidator.RESOURCE_FILE_LIST);
			requestMap.put("issues", PartialIssueValidator.RESOURCE_FILE);
		}

		@Override
		public void run() {
			try {
				
				ServerSocket server = new ServerSocket(1235);
				
				while(!Thread.interrupted()) {
					OutputStream respStream = null;
					BufferedReader reqReader = null;
					Socket socket = server.accept();
					
					try {
						respStream = socket.getOutputStream();
						reqReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
						
						String request = reqReader.readLine();
						reqReader.skip(socket.getInputStream().available());

						//find ResponseBody
						String responseResourcePath = this.responseResourcePath;
						if (responseResourcePath==null) {
							
							Matcher m = p.matcher(request);
							if(m.find()) {
								String uri = m.group(1);
								responseResourcePath = requestMap.containsKey(uri) ? requestMap.get(uri) : "/xmldata/" + uri + ".xml";
							}
						}

						//Repsonse Header
						if(request.contains("/mylyn/token")) {
							respStream.write(RESPONSE_HEADER_OK.getBytes());
						} else {
							respStream.write(responseHeader.getBytes());
						}

						//Repsonse Body
						if(responseResourcePath!=null) {
							InputStream responseStream = getClass().getResourceAsStream(responseResourcePath);
							if(responseStream!=null) {
								try {
									int read = -1;
									byte[] buffer = new byte[4096];
									while((read=responseStream.read(buffer, 0, 4096))>-1) {
										respStream.write(buffer, 0, read);
									}
								} finally {
									responseStream.close();
								}
							}
						}
						

					} finally {
						if(respStream!=null) {
							respStream.close();
						}
						if(reqReader!=null) {
							reqReader.close();
						}
						socket.close();
					}
					
				}
				
				server.close();
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
	}
}
