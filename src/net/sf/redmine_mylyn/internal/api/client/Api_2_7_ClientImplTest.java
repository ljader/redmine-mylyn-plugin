package net.sf.redmine_mylyn.internal.api.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.redmine_mylyn.api.model.Configuration;
import net.sf.redmine_mylyn.internal.api.IssueStatusValidator;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.commons.net.AbstractWebLocation;
import org.eclipse.mylyn.commons.net.WebLocation;
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
		
		testee.updateConfiguration(null, true);

		assertNotNull(configuration.getIssueStatuses());
		assertEquals(6, configuration.getIssueStatuses().getAll().size());
		IssueStatusValidator.validateIssueStatus5(configuration.getIssueStatuses().get(5));
	}
}
