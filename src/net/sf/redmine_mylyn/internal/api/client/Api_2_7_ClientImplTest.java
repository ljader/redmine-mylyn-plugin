package net.sf.redmine_mylyn.internal.api.client;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.redmine_mylyn.api.model.Configuration;
import net.sf.redmine_mylyn.internal.api.CustomFieldValidator;
import net.sf.redmine_mylyn.internal.api.IssueCategoryValidator;
import net.sf.redmine_mylyn.internal.api.IssuePriorityValidator;
import net.sf.redmine_mylyn.internal.api.IssueStatusValidator;
import net.sf.redmine_mylyn.internal.api.QueryValidator;
import net.sf.redmine_mylyn.internal.api.TimeEntryActivityValidator;
import net.sf.redmine_mylyn.internal.api.TrackerValidator;
import net.sf.redmine_mylyn.internal.api.UserValidator;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.commons.net.AbstractWebLocation;
import org.eclipse.mylyn.commons.net.WebLocation;
import org.eclipse.mylyn.commons.net.WebUtil;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class Api_2_7_ClientImplTest {

	private final static String RESPONSE_HEADER_OK = "HTTP/1.0 200 OK\n\n";

	private final static String RESPONSE_HEADER_NOT_FOUND = "HTTP/1.0 404 NOT FOUND\n\n";

	static IProgressMonitor monitor;

	static AbstractWebLocation location;
	
	static Thread server; 

	//	private TaskRepository repository;

	
	Api_2_7_ClientImpl testee;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		monitor = new NullProgressMonitor();
		location = new WebLocation("http://localhost:1234", "jsmith", "jsmith");

		server = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					ServerSocket server = new ServerSocket(1234);
					Pattern p = Pattern.compile("^(GET|POST)\\s+/mylyn/(\\S+).*$", Pattern.CASE_INSENSITIVE);
					
					while(!Thread.interrupted()) {
						OutputStream respStream = null;
						BufferedReader reqReader = null;
						Socket socket = server.accept();
						
						try {
							respStream = socket.getOutputStream();
							reqReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
							
							String request = reqReader.readLine();
							while(reqReader.ready()) {
								reqReader.readLine();
							}

							boolean flag = true;
							
							Matcher m = p.matcher(request);
							if(m.find()) {
								if(m.group(1).toUpperCase().equals("GET")) {
									InputStream responseStream = getClass().getResourceAsStream("/xmldata/" + m.group(2) + ".xml");
									if (responseStream!=null) {
										try {
											flag = false;
											respStream.write(RESPONSE_HEADER_OK.getBytes());
											
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
								
								if (flag) {
									respStream.write(RESPONSE_HEADER_NOT_FOUND.getBytes());
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
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		});
		server.start();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		server.interrupt();
	}

	@Before
	public void setUp() throws Exception {
		testee = new Api_2_7_ClientImpl(location);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetConfiguration() {
		assertNotNull(testee.getConfiguration());
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
		
		testee.updateConfiguration(null, true);

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

	}

	@Test
	public void concurrencyRequests() throws Exception {
		Class<AbstractClient> clazz = AbstractClient.class;
		
		Field httpClientField = clazz.getDeclaredField("httpClient");
		httpClientField.setAccessible(true);
		HttpClient httpClient = (HttpClient)httpClientField.get(testee);

		Method executeMethod = clazz.getDeclaredMethod("performExecuteMethod", HttpMethod.class, HostConfiguration.class, IProgressMonitor.class);
		executeMethod.setAccessible(true);
		
		HttpMethod firstMethod = new GetMethod("/mylyn/issuestatus");
		HttpMethod secondMethod = new GetMethod("/mylyn/issuestatus");
		
		HostConfiguration hostConfiguration = WebUtil.createHostConfiguration(httpClient, location, monitor);
		
		InputStream stream = getClass().getResourceAsStream(IssueStatusValidator.RESOURCE_FILE);
		int len = stream.available();
		int partialLen = len/2;
		
		byte[] excpected = new byte[len];
		stream.read(excpected, 0, len);
		stream.close();
		
		byte[] firstBuffer = new byte[len];
		byte[] secondBuffer = new byte[len];
		
		
		try {
			executeMethod.invoke(testee, firstMethod, hostConfiguration, monitor);
			InputStream firstStream = firstMethod.getResponseBodyAsStream();
			firstStream.read(firstBuffer, 0, partialLen);
			
			executeMethod.invoke(testee, secondMethod, hostConfiguration, monitor);
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
}
