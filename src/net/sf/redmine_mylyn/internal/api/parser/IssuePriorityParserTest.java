package net.sf.redmine_mylyn.internal.api.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;

import net.sf.redmine_mylyn.api.model.container.IssuePriorities;
import net.sf.redmine_mylyn.internal.api.IssuePriorityValidator;

import org.apache.commons.httpclient.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class IssuePriorityParserTest {

	InputStream input;
	AttributeParser<IssuePriorities> testee;
	
	@Before
	public void setUp() throws Exception {
		input = getClass().getResourceAsStream(IssuePriorityValidator.RESOURCE_FILE);
		testee = new  AttributeParser<IssuePriorities>(IssuePriorities.class);
	}
	
	@After
	public void tearDown() throws Exception {
		input.close();
	}

	@Test
	public void testParseResponse() throws Exception {
		IssuePriorities ct = testee.parseResponse(input, HttpStatus.SC_OK);
		
		assertNotNull(ct);
		assertEquals(IssuePriorityValidator.COUNT, ct.getAll().size());
		
		IssuePriorityValidator.validate4(ct.get(4));
		IssuePriorityValidator.validateOrder(ct);
		IssuePriorityValidator.validateDefault(ct.getDefault());
	}

}
