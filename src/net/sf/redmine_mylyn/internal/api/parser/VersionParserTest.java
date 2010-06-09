package net.sf.redmine_mylyn.internal.api.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;

import net.sf.redmine_mylyn.api.model.container.Versions;
import net.sf.redmine_mylyn.internal.api.VersionValidator;

import org.apache.commons.httpclient.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class VersionParserTest {

	InputStream input;
	AttributeParser<Versions> testee;
	
	@Before
	public void setUp() throws Exception {
		input = getClass().getResourceAsStream(VersionValidator.RESOURCE_FILE);
		testee = new  AttributeParser<Versions>(Versions.class);
	}
	
	@After
	public void tearDown() throws Exception {
		input.close();
	}

	@Test
	public void testParseResponse() throws Exception {
		Versions ct = testee.parseResponse(input, HttpStatus.SC_OK);
		
		assertNotNull(ct);
		assertEquals(VersionValidator.COUNT, ct.getAll().size());
		
		VersionValidator.validate1(ct.get(1));
		VersionValidator.validate2(ct.get(2));
		VersionValidator.validate3(ct.get(3));
	}

}
