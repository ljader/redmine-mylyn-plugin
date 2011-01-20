package net.sf.redmine_mylyn.internal.api.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;

import net.sf.redmine_mylyn.api.model.Configuration;
import net.sf.redmine_mylyn.internal.api.PartialIssueValidator;
import net.sf.redmine_mylyn.internal.api.parser.adapter.type.Issues;
import net.sf.redmine_mylyn.internal.api.parser.mock.ConfigurationMock;

import org.apache.commons.httpclient.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PartialIssueParserTest {

	InputStream input;
	IssuesParser testee;
	
	@Before
	public void setUp() throws Exception {
		input = getClass().getResourceAsStream(PartialIssueValidator.RESOURCE_FILE);
		Configuration conf = new ConfigurationMock();
		testee = new IssuesParser(conf);
	}

	@After
	public void tearDown() throws Exception {
		input.close();
	}

	@Test
	public void testParseResponse() throws Exception {
		Issues ct = testee.parseResponse(input, HttpStatus.SC_OK);
		
		assertNotNull(ct);
		assertNotNull(ct.getAll());
		assertEquals(PartialIssueValidator.COUNT, ct.getAll().size());
		
		PartialIssueValidator.validate1(ct.getAll().get(5));
	}
	
}
