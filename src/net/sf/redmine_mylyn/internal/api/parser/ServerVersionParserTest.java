package net.sf.redmine_mylyn.internal.api.parser;

import static org.junit.Assert.assertNotNull;

import java.io.InputStream;

import net.sf.redmine_mylyn.api.client.RedmineServerVersion;
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

}
