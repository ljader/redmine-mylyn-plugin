package net.sf.redmine_mylyn.internal.api.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;

import net.sf.redmine_mylyn.api.model.container.Projects;
import net.sf.redmine_mylyn.internal.api.ProjectValidator;

import org.apache.commons.httpclient.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ProjectParserTest {

	InputStream input;
	AttributeParser<Projects> testee;
	
	@Before
	public void setUp() throws Exception {
		input = getClass().getResourceAsStream(ProjectValidator.RESOURCE_FILE);
		testee = new  AttributeParser<Projects>(Projects.class);
	}
	
	@After
	public void tearDown() throws Exception {
		input.close();
	}

	@Test
	public void testParseResponse() throws Exception {
		Projects ct = testee.parseResponse(input, HttpStatus.SC_OK);
		
		assertNotNull(ct);
		assertEquals(ProjectValidator.COUNT, ct.getAll().size());
		
		ProjectValidator.validate1(ct.getById(1));
	}

}
