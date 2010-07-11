package net.sf.redmine_mylyn.internal.api.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;
import java.util.List;

import net.sf.redmine_mylyn.api.model.Version;
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
		
		VersionValidator.validate1(ct.getById(1));
		VersionValidator.validate2(ct.getById(2));
		VersionValidator.validate3(ct.getById(3));
		
		List<Version> versions = ct.getById(new int[]{2,3});
		assertNotNull(versions);
		assertEquals(2, versions.size());
		VersionValidator.validate2(versions.get(0));
		VersionValidator.validate3(versions.get(1));

		versions = ct.getOpenById(new int[]{1,2,3});
		assertEquals(2, versions.size());
		assertEquals(2, versions.get(0).getId());
		assertEquals(3, versions.get(1).getId());
	}

}
