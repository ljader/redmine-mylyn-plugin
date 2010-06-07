package net.sf.redmine_mylyn.internal.api.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;

import net.sf.redmine_mylyn.api.model.container.Users;
import net.sf.redmine_mylyn.internal.api.UserValidator;

import org.apache.commons.httpclient.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class UserParserTest {

	InputStream input;
	AttributeParser<Users> testee;
	
	@Before
	public void setUp() throws Exception {
		input = getClass().getResourceAsStream(UserValidator.RESOURCE_FILE);
		testee = new  AttributeParser<Users>(Users.class);
	}
	
	@After
	public void tearDown() throws Exception {
		input.close();
	}

	@Test
	public void testParseResponse() throws Exception {
		Users ct = testee.parseResponse(input, HttpStatus.SC_OK);
		
		assertNotNull(ct);
		assertEquals(UserValidator.COUNT, ct.getAll().size());
		
		UserValidator.validate2(ct.get(2));
	}

}
