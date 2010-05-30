package net.sf.redmine_mylyn.api.parser;

import java.io.InputStream;
import java.util.List;

import junit.framework.TestCase;
import net.sf.redmine_mylyn.api.model.IssueStatus;
import net.sf.redmine_mylyn.api.model.container.IssueStatuses;

import org.apache.commons.httpclient.HttpStatus;

public class IssueStatusParserTest extends TestCase {

	InputStream input;
	AttributeParser<IssueStatuses, IssueStatus> testee;
	
	
	public IssueStatusParserTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		input = getClass().getResourceAsStream("/xmldata/issuesstatus.xml");
		testee = new  AttributeParser<IssueStatuses, IssueStatus>(IssueStatuses.class, IssueStatus.class);
	}
	

	protected void tearDown() throws Exception {
		input.close();
	}

	public void testParseResponse() throws Exception {
		IssueStatuses statuses = testee.parseResponse(input, HttpStatus.SC_OK);
		
		assertNotNull(statuses);
		assertEquals(6, statuses.getAll().size());
		
		//status 5
		IssueStatus status = statuses.get(5);
		assertNotNull(status);
		assertEquals(5, status.getId());
		assertEquals("Closed", status.getName());
		assertTrue(status.isClosed());
		assertTrue(status.isDefault());
		
		//sorting - order: 1 2 6 5 4 3
		List<IssueStatus> all = statuses.getAll();
		int pos = 0;
		assertEquals(1, all.get(pos++).getId());
		assertEquals(2, all.get(pos++).getId());
		assertEquals(6, all.get(pos++).getId());
		assertEquals(5, all.get(pos++).getId());
		assertEquals(4, all.get(pos++).getId());
		assertEquals(3, all.get(pos++).getId());
	}

}
