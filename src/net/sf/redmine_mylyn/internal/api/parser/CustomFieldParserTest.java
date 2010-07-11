package net.sf.redmine_mylyn.internal.api.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;

import net.sf.redmine_mylyn.api.model.container.CustomFields;
import net.sf.redmine_mylyn.internal.api.CustomFieldValidator;

import org.apache.commons.httpclient.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CustomFieldParserTest {

	InputStream input;
	AttributeParser<CustomFields> testee;
	
	@Before
	public void setUp() throws Exception {
		input = getClass().getResourceAsStream(CustomFieldValidator.RESOURCE_FILE);
		testee = new  AttributeParser<CustomFields>(CustomFields.class);
	}
	
	@After
	public void tearDown() throws Exception {
		input.close();
	}

	@Test
	public void testParseResponse() throws Exception {
		CustomFields ct = testee.parseResponse(input, HttpStatus.SC_OK);
		
		assertNotNull(ct);
		assertEquals(CustomFieldValidator.COUNT, ct.getAll().size());
		
		CustomFieldValidator.validate1(ct.getById(1));
		CustomFieldValidator.validate2(ct.getById(2));
		CustomFieldValidator.validate7(ct.getById(7));
		
		int idx=0;
		assertNotNull(ct.getIssueCustomFields());
		assertEquals(5, ct.getIssueCustomFields().size());
		assertEquals(1, ct.getIssueCustomFields().get(idx++).getId());
		assertEquals(2, ct.getIssueCustomFields().get(idx++).getId());
		assertEquals(6, ct.getIssueCustomFields().get(idx++).getId());
		assertEquals(8, ct.getIssueCustomFields().get(idx++).getId());
		assertEquals(9, ct.getIssueCustomFields().get(idx++).getId());

		assertNotNull(ct.getTimeEntryActivityCustomFields());
		assertEquals(1, ct.getTimeEntryActivityCustomFields().size());
		assertEquals(7, ct.getTimeEntryActivityCustomFields().get(0).getId());
	}

}
