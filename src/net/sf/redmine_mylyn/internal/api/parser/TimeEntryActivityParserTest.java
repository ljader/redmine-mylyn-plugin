package net.sf.redmine_mylyn.internal.api.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;

import net.sf.redmine_mylyn.api.model.container.TimeEntryActivities;
import net.sf.redmine_mylyn.internal.api.TimeEntryActivityValidator;

import org.apache.commons.httpclient.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TimeEntryActivityParserTest {

	InputStream input;
	AttributeParser<TimeEntryActivities> testee;
	
	@Before
	public void setUp() throws Exception {
		input = getClass().getResourceAsStream(TimeEntryActivityValidator.RESOURCE_FILE);
		testee = new  AttributeParser<TimeEntryActivities>(TimeEntryActivities.class);
	}
	
	@After
	public void tearDown() throws Exception {
		input.close();
	}

	@Test
	public void testParseResponse() throws Exception {
		TimeEntryActivities ct = testee.parseResponse(input, HttpStatus.SC_OK);
		
		assertNotNull(ct);
		assertEquals(TimeEntryActivityValidator.COUNT, ct.getAll().size());
		
		TimeEntryActivityValidator.validate11(ct.getById(11));
		TimeEntryActivityValidator.validateOrder(ct);
		TimeEntryActivityValidator.validateDefault(ct.getDefault());
	}

}
