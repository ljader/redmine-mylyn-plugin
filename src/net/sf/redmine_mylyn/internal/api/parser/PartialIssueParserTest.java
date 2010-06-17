package net.sf.redmine_mylyn.internal.api.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;

import net.sf.redmine_mylyn.internal.api.PartialIssueValidator;
import net.sf.redmine_mylyn.internal.api.parser.adapter.type.PartialIssues;

import org.apache.commons.httpclient.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PartialIssueParserTest {

	InputStream input;
	PartialIssueParser testee;
	
	@Before
	public void setUp() throws Exception {
		input = getClass().getResourceAsStream(PartialIssueValidator.RESOURCE_FILE);
		testee = new PartialIssueParser();
	}

	@After
	public void tearDown() throws Exception {
		input.close();
	}

	@Test
	public void testParseResponse() throws Exception {
		PartialIssues ct = testee.parseResponse(input, HttpStatus.SC_OK);
		
		assertNotNull(ct);
		assertNotNull(ct.issues);
		assertEquals(PartialIssueValidator.COUNT, ct.issues.length);
		
		PartialIssueValidator.validate1(ct.issues[5]);
	}
	
}
