package net.sf.redmine_mylyn.internal.api.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;

import net.sf.redmine_mylyn.api.model.container.IssueStatuses;
import net.sf.redmine_mylyn.internal.api.IssueStatusValidator;

import org.apache.commons.httpclient.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class IssueStatusParserTest {

	InputStream input;
	AttributeParser<IssueStatuses> testee;
	
	@Before
	public void setUp() throws Exception {
		input = getClass().getResourceAsStream("/xmldata/issuestatus.xml");
		testee = new  AttributeParser<IssueStatuses>(IssueStatuses.class);
	}
	
	@After
	public void tearDown() throws Exception {
		input.close();
	}

	@Test
	public void testParseResponse() throws Exception {
		IssueStatuses statuses = testee.parseResponse(input, HttpStatus.SC_OK);
		
		assertNotNull(statuses);
		assertEquals(6, statuses.getAll().size());
		
		IssueStatusValidator.validateIssueStatus5(statuses.get(5));
		IssueStatusValidator.validateOrder(statuses);
	}

}
