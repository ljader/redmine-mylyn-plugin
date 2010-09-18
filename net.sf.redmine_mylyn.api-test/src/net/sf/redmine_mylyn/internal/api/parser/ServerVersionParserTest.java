package net.sf.redmine_mylyn.internal.api.parser;

import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import net.sf.redmine_mylyn.api.client.RedmineServerVersion;
import net.sf.redmine_mylyn.api.exception.RedmineApiRemoteException;
import net.sf.redmine_mylyn.internal.api.ServerVersionValidator;

import org.apache.commons.httpclient.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ServerVersionParserTest {

	InputStream input;
	TypedParser<RedmineServerVersion> testee;
	
	@Before
	public void setUp() throws Exception {
		input = getClass().getResourceAsStream(ServerVersionValidator.RESOURCE_FILE);
		testee = new TypedParser<RedmineServerVersion>(RedmineServerVersion.class);
	}
	
	@After
	public void tearDown() throws Exception {
		input.close();
	}

	@Test
	public void testParseResponse() throws Exception {
		RedmineServerVersion version = testee.parseResponse(input, HttpStatus.SC_OK);
		
		assertNotNull(version);
		ServerVersionValidator.validate(version);
	}

	@Test(expected=RedmineApiRemoteException.class)
	public void testParseOldApiResponse() throws Exception {
		String old = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<version xmlns=\"http://redmin-mylyncon.sf.net/schemas/WS-API-2.6\"><plugin major=\"2\" minor=\"7\" tiny=\"0\">2.7.0.stable.RC2</plugin><redmine>1.0.1.stable</redmine><rails>2.3.5</rails></version>";
		input = new ByteArrayInputStream(old.getBytes());
		
		testee.parseResponse(input, HttpStatus.SC_OK);
	}
	
}
