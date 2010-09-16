package net.sf.redmine_mylyn.internal.api.client;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.redmine_mylyn.api.TestData;
import net.sf.redmine_mylyn.api.client.IRedmineApiErrorCollector;
import net.sf.redmine_mylyn.api.client.IRedmineApiWebHelper;
import net.sf.redmine_mylyn.api.client.RedmineServerVersion;
import net.sf.redmine_mylyn.api.exception.RedmineApiAuthenticationException;
import net.sf.redmine_mylyn.api.exception.RedmineApiInvalidDataException;
import net.sf.redmine_mylyn.api.model.Configuration;
import net.sf.redmine_mylyn.api.model.Issue;
import net.sf.redmine_mylyn.api.query.Query;
import net.sf.redmine_mylyn.internal.api.CustomFieldValidator;
import net.sf.redmine_mylyn.internal.api.IssueCategoryValidator;
import net.sf.redmine_mylyn.internal.api.IssuePriorityValidator;
import net.sf.redmine_mylyn.internal.api.IssueStatusValidator;
import net.sf.redmine_mylyn.internal.api.IssueValidator;
import net.sf.redmine_mylyn.internal.api.PartialIssueValidator;
import net.sf.redmine_mylyn.internal.api.ProjectValidator;
import net.sf.redmine_mylyn.internal.api.QueryValidator;
import net.sf.redmine_mylyn.internal.api.ServerVersionValidator;
import net.sf.redmine_mylyn.internal.api.TimeEntryActivityValidator;
import net.sf.redmine_mylyn.internal.api.TrackerValidator;
import net.sf.redmine_mylyn.internal.api.UserValidator;
import net.sf.redmine_mylyn.internal.api.VersionValidator;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.methods.GetMethod;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class Api_2_7_ClientImplTest {

	private final static String RESPONSE_HEADER_OK = "HTTP/1.0 200 OK\n\n";

//	private final static String RESPONSE_HEADER_NOT_FOUND = "HTTP/1.0 404 NOT FOUND\n\n";

	private final static String RESPONSE_HEADER_CREATED = "HTTP/1.0 201 CREATED\n\n";

	private final static String RESPONSE_HEADER_FAILED = "HTTP/1.0 422 Unprocessable Entity\n\n";

	private final static String RESOURCE_FILE_SUBMIT_ERRORS = "/xmldata/issues/submit_errors.xml"; 
	
	private final static String RESOURCE_FILE_SUBMIT_NEW = "/xmldata/issues/created_issue.xml"; 

	private final static String RESOURCE_FILE_TOKEN_PAGE = "/html/token"; 

	private IProgressMonitor monitor;

	
	private static TestServer server; 

	private static ErrorCollector errorCollector;
	//	private TaskRepository repository;

	
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
				hostConfiguration.setHost("localhost", 1234);
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
	public void testGetConfiguration() {
		assertNotNull(testee.getConfiguration());
	}

	@Test
	public void testDetectServerVersion() throws Exception {
		RedmineServerVersion version = testee.detectServerVersion(monitor);
		assertNotNull(version);
	}
	
	@Test
	public void testUpdateConfiguration() throws Exception {
		Configuration configuration = testee.getConfiguration();
		
		assertNotNull(configuration.getIssueStatuses());
		assertEquals(0, configuration.getIssueStatuses().getAll().size());
		
		assertNotNull(configuration.getIssueCategories());
		assertEquals(0, configuration.getIssueCategories().getAll().size());
		
		assertNotNull(configuration.getIssuePriorities());
		assertEquals(0, configuration.getIssuePriorities().getAll().size());

		assertNotNull(configuration.getTrackers());
		assertEquals(0, configuration.getTrackers().getAll().size());
		
		assertNotNull(configuration.getCustomFields());
		assertEquals(0, configuration.getCustomFields().getAll().size());
		
		assertNotNull(configuration.getUsers());
		assertEquals(0, configuration.getUsers().getAll().size());
		
		assertNotNull(configuration.getTimeEntryActivities());
		assertEquals(0, configuration.getTimeEntryActivities().getAll().size());
		
		assertNotNull(configuration.getQueries());
		assertEquals(0, configuration.getQueries().getAll().size());
		
		assertNotNull(configuration.getProjects());
		assertEquals(0, configuration.getProjects().getAll().size());
		
		assertNotNull(configuration.getVersions());
		assertEquals(0, configuration.getVersions().getAll().size());
		
		assertNull(configuration.getSettings());
		
		testee.updateConfiguration(null);

		assertNotNull(configuration.getIssueStatuses());
		assertEquals(IssueStatusValidator.COUNT, configuration.getIssueStatuses().getAll().size());

		assertNotNull(configuration.getIssueCategories());
		assertEquals(IssueCategoryValidator.COUNT, configuration.getIssueCategories().getAll().size());

		assertNotNull(configuration.getIssuePriorities());
		assertEquals(IssuePriorityValidator.COUNT, configuration.getIssuePriorities().getAll().size());

		assertNotNull(configuration.getTrackers());
		assertEquals(TrackerValidator.COUNT, configuration.getTrackers().getAll().size());

		assertNotNull(configuration.getCustomFields());
		assertEquals(CustomFieldValidator.COUNT, configuration.getCustomFields().getAll().size());

		assertNotNull(configuration.getUsers());
		assertEquals(UserValidator.COUNT, configuration.getUsers().getAll().size());

		assertNotNull(configuration.getTimeEntryActivities());
		assertEquals(TimeEntryActivityValidator.COUNT, configuration.getTimeEntryActivities().getAll().size());

		assertNotNull(configuration.getQueries());
		assertEquals(QueryValidator.COUNT, configuration.getQueries().getAll().size());

		assertNotNull(configuration.getProjects());
		assertEquals(ProjectValidator.COUNT, configuration.getProjects().getAll().size());

		assertNotNull(configuration.getVersions());
		assertEquals(VersionValidator.COUNT, configuration.getVersions().getAll().size());

		assertNotNull(configuration.getSettings());
	}
	
	@Test
	public void testUpdatedIssues() throws Exception {
		int[] ids = testee.getUpdatedIssueIds(new int[]{1,6,7,8}, new Date(123456789000l), monitor);
		assertNotNull(ids);
		assertEquals("[1, 7, 8]", Arrays.toString(ids));
	}
	
	@Test
	public void testGetIssue() throws Exception {
		Issue issue = testee.getIssue(1, monitor);
		assertNotNull(issue);
		assertEquals(1, issue.getId());
	} 

	@Test
	public void testGetIssues() throws Exception {
		Issue[] issues = testee.getIssues(monitor, 1,7,8);
		assertNotNull(issues);
		assertEquals(3, issues.length);
		assertEquals(1, issues[0].getId());
		assertEquals(7, issues[1].getId());
		assertEquals(8, issues[2].getId());
	} 
	
	@Test
	public void testQuery() throws Exception {
		Query query = new Query();
		Issue[] issues =testee.query(query, monitor);
		assertNotNull(issues);
		assertEquals(PartialIssueValidator.COUNT, issues.length);
	}
	
	@Test
	public void testStoredQuery() throws Exception {
		fail("not implemented");
	}
	
	@Test
	public void testEmptyQuery() throws Exception {
		fail("not implemented");
	}

	@Test
	public void testCreateIssue() throws Exception {
		server.responseHeader = RESPONSE_HEADER_CREATED;
		server.responseResourcePath = RESOURCE_FILE_SUBMIT_NEW;
		
		Issue issue = testee.createIssue(TestData.issue2, errorCollector, monitor);
		assertNotNull(issue);
		assertEquals(14, issue.getId());
		assertEquals(0, errorCollector.lst.size());
	}

	@Test
	public void testUpdateIssue() throws Exception {
		server.responseHeader = RESPONSE_HEADER_OK;
		server.responseResourcePath = RESOURCE_FILE_SUBMIT_NEW;
		
		testee.updateIssue(TestData.issue2, "noContent", TestData.issue2.getTimeEntries().getAll().get(0), errorCollector, monitor);
		assertEquals(0, errorCollector.lst.size());
	}
	
	@Test(expected=RedmineApiInvalidDataException.class)
	public void testCreateIssue_failed() throws Exception {
		server.responseHeader = RESPONSE_HEADER_FAILED;
		server.responseResourcePath = RESOURCE_FILE_SUBMIT_ERRORS;
		
		try {
			testee.createIssue(TestData.issue2, errorCollector, monitor);
		} finally {
			assertEquals(2, errorCollector.lst.size());
			assertEquals("Zielversion ist kein gültiger Wert", errorCollector.lst.get(0));
			assertEquals("FooBar", errorCollector.lst.get(1));
		}
	}

	@Test(expected=RedmineApiInvalidDataException.class)
	public void testUpdateIssue_failed() throws Exception {
		server.responseHeader = RESPONSE_HEADER_FAILED;
		server.responseResourcePath = RESOURCE_FILE_SUBMIT_ERRORS;
		
		try {
			testee.updateIssue(TestData.issue2, "noContent", TestData.issue2.getTimeEntries().getAll().get(0), errorCollector, monitor);
		} finally {
			assertEquals(2, errorCollector.lst.size());
			assertEquals("Zielversion ist kein gültiger Wert", errorCollector.lst.get(0));
			assertEquals("FooBar", errorCollector.lst.get(1));
		}
	}
	
	@Test
	public void testGetAuthenticityToken() throws Exception {
		server.responseResourcePath = RESOURCE_FILE_TOKEN_PAGE;
		Method m = testee.getClass().getDeclaredMethod("getAuthenticityToken", IProgressMonitor.class);
		m.setAccessible(true);
		Object token = m.invoke(testee, monitor);
		assertNotNull(token);
		assertEquals("TRGcXOcqpj92D9ip7X44NaLZhaGJEdLOU7TCNFgqmZk=", (String)token);
	}
	
	@Test
	public void concurrencyRequests() throws Exception {
		Class<AbstractClient> clazz = AbstractClient.class;

		Method executeMethod = clazz.getDeclaredMethod("performExecuteMethod", HttpMethod.class, IProgressMonitor.class);
		executeMethod.setAccessible(true);
		
		HttpMethod firstMethod = new GetMethod("/mylyn/issuestatus");
		HttpMethod secondMethod = new GetMethod("/mylyn/issuestatus");

		InputStream stream = getClass().getResourceAsStream(IssueStatusValidator.RESOURCE_FILE);
		int len = stream.available();
		int partialLen = len/2;
		
		byte[] excpected = new byte[len];
		stream.read(excpected, 0, len);
		stream.close();
		
		byte[] firstBuffer = new byte[len];
		byte[] secondBuffer = new byte[len];
		
		
		try {
			executeMethod.invoke(testee, firstMethod, monitor);
			InputStream firstStream = firstMethod.getResponseBodyAsStream();
			firstStream.read(firstBuffer, 0, partialLen);
			
			executeMethod.invoke(testee, secondMethod, monitor);
			InputStream secondStream = secondMethod.getResponseBodyAsStream();
			secondStream.read(secondBuffer, 0, len);
			secondStream.close();
			
			firstStream.read(firstBuffer, partialLen, len-partialLen);
			firstStream.close();
			
		} finally {
			assertArrayEquals(excpected, firstBuffer);
			assertArrayEquals(excpected, secondBuffer);
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
				
				ServerSocket server = new ServerSocket(1234);
				
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
