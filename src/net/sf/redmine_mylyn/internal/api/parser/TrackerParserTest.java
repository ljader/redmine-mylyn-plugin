package net.sf.redmine_mylyn.internal.api.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;

import net.sf.redmine_mylyn.api.model.container.Trackers;
import net.sf.redmine_mylyn.internal.api.TrackerValidator;

import org.apache.commons.httpclient.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TrackerParserTest {

	InputStream input;
	AttributeParser<Trackers> testee;
	
	@Before
	public void setUp() throws Exception {
		input = getClass().getResourceAsStream(TrackerValidator.RESOURCE_FILE);
		testee = new  AttributeParser<Trackers>(Trackers.class);
	}
	
	@After
	public void tearDown() throws Exception {
		input.close();
	}

	@Test
	public void testParseResponse() throws Exception {
		Trackers ct = testee.parseResponse(input, HttpStatus.SC_OK);
		
		assertNotNull(ct);
		assertEquals(TrackerValidator.COUNT, ct.getAll().size());
		
		TrackerValidator.validate2(ct.getById(2));
		TrackerValidator.validateOrder(ct);
	}

}
