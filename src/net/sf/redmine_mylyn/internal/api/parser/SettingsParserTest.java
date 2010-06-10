package net.sf.redmine_mylyn.internal.api.parser;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertFalse;

import java.io.InputStream;

import net.sf.redmine_mylyn.api.model.Settings;

import org.apache.commons.httpclient.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SettingsParserTest {

	public final static String RESOURCE_FILE = "/xmldata/settings.xml";
	
	InputStream input;
	SettingsParser testee;
	
	@Before
	public void setUp() throws Exception {
		input = getClass().getResourceAsStream(RESOURCE_FILE);
		testee = new SettingsParser();
	}
	
	@After
	public void tearDown() throws Exception {
		input.close();
	}

	@Test
	public void testParseResponse() throws Exception {
		Settings settings = testee.parseResponse(input, HttpStatus.SC_OK);
		
		assertNotNull(settings);
		assertFalse(settings.isUseIssueDoneRatio());
	}
	
}
