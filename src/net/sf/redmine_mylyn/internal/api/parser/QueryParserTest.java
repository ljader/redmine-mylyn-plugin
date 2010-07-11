package net.sf.redmine_mylyn.internal.api.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;

import net.sf.redmine_mylyn.api.model.container.Queries;
import net.sf.redmine_mylyn.internal.api.QueryValidator;

import org.apache.commons.httpclient.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class QueryParserTest {

	InputStream input;
	AttributeParser<Queries> testee;
	
	@Before
	public void setUp() throws Exception {
		input = getClass().getResourceAsStream(QueryValidator.RESOURCE_FILE);
		testee = new  AttributeParser<Queries>(Queries.class);
	}
	
	@After
	public void tearDown() throws Exception {
		input.close();
	}

	@Test
	public void testParseResponse() throws Exception {
		Queries ct = testee.parseResponse(input, HttpStatus.SC_OK);
		
		assertNotNull(ct);
		assertEquals(QueryValidator.COUNT, ct.getAll().size());
		
		QueryValidator.validate9(ct.getById(9));
	}

}
