package net.sf.redmine_mylyn.internal.api.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;

import net.sf.redmine_mylyn.api.model.Configuration;
import net.sf.redmine_mylyn.api.model.Issue;
import net.sf.redmine_mylyn.internal.api.IssueValidator;
import net.sf.redmine_mylyn.internal.api.parser.adapter.type.Issues;
import net.sf.redmine_mylyn.internal.api.parser.mock.ConfigurationMock;

import org.apache.commons.httpclient.HttpStatus;
import org.junit.Before;
import org.junit.Test;

public class IssueParserTest {

	IssueParser testee;
	IssuesParser testee2;
	
	@Before
	public void setUp() throws Exception {
		Configuration conf = new ConfigurationMock();
		testee = new IssueParser(conf);
		testee2 = new IssuesParser(conf);
	}
	
	@Test
	public void testParseResponse() throws Exception {
		InputStream in1 = getClass().getResourceAsStream(IssueValidator.RESOURCE_FILE_ISSUE_1);
		InputStream in2 = getClass().getResourceAsStream(IssueValidator.RESOURCE_FILE_ISSUE_2);
		InputStream inL = getClass().getResourceAsStream(IssueValidator.RESOURCE_FILE_LIST);
		
		try {
			Issue issue = testee.parseResponse(in1, HttpStatus.SC_OK);
			assertNotNull(issue);
			IssueValidator.validate1(issue);

			issue = testee.parseResponse(in2, HttpStatus.SC_OK);
			IssueValidator.validate2(issue);
			
			Issues issues = testee2.parseResponse(inL, HttpStatus.SC_OK);
			assertNotNull(issues);
			assertEquals(3, issues.getAll().size());
			IssueValidator.validate1(issues.get(1));

		} finally {
			in1.close();
			in2.close();
			inL.close();
		}
		
	}
	
}
