package net.sf.redmine_mylyn.api.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import net.sf.redmine_mylyn.internal.api.CustomFieldValidator;
import net.sf.redmine_mylyn.internal.api.IssueCategoryValidator;
import net.sf.redmine_mylyn.internal.api.IssuePriorityValidator;
import net.sf.redmine_mylyn.internal.api.IssueStatusValidator;
import net.sf.redmine_mylyn.internal.api.ProjectValidator;
import net.sf.redmine_mylyn.internal.api.QueryValidator;
import net.sf.redmine_mylyn.internal.api.TimeEntryActivityValidator;
import net.sf.redmine_mylyn.internal.api.TrackerValidator;
import net.sf.redmine_mylyn.internal.api.UserValidator;
import net.sf.redmine_mylyn.internal.api.VersionValidator;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ConfigurationTest {

	InputStream input;
	
	OutputStream output;
	
	final static String FILE_PATH = "/xmldata/configuration.xml";
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		input = getClass().getResourceAsStream(FILE_PATH);
	}

	@After
	public void tearDown() throws Exception {
		if(input!=null) {
			input.close();
		}
	}

	@Test
	public void testFromStream() throws Exception {
		
		Configuration configuration = Configuration.fromStream(input);
		assertNotNull(configuration);
		
		assertEquals(IssueStatusValidator.COUNT, configuration.getIssueStatuses().getAll().size());
		
		assertEquals(IssueCategoryValidator.COUNT, configuration.getIssueCategories().getAll().size());
		
		assertEquals(IssuePriorityValidator.COUNT, configuration.getIssuePriorities().getAll().size());
		
		assertEquals(CustomFieldValidator.COUNT, configuration.getCustomFields().getAll().size());
		
		assertEquals(TrackerValidator.COUNT, configuration.getTrackers().getAll().size());
		
		assertEquals(UserValidator.COUNT, configuration.getUsers().getAll().size());
		
		assertEquals(TimeEntryActivityValidator.COUNT, configuration.getTimeEntryActivities().getAll().size());
		
		assertEquals(QueryValidator.COUNT, configuration.getQueries().getAll().size());
		
		assertEquals(ProjectValidator.COUNT, configuration.getProjects().getAll().size());
		
		assertEquals(VersionValidator.COUNT, configuration.getVersions().getAll().size());

		assertNotNull(configuration.getSettings());
	}

	@Test
	public void testWrite() throws Exception {
		ByteArrayOutputStream output  = new ByteArrayOutputStream();
		byte[] b = new byte[4096];
		int len=0;
		while((len=input.read(b))>0) {
			output.write(b, 0, len);
		}
		input.close();

		Configuration configuration = Configuration.fromStream(new ByteArrayInputStream(output.toByteArray()));
		output.reset();
		
		configuration.write(output);
		input = new ByteArrayInputStream(output.toByteArray());
		
		testFromStream();
		
	}
	
}
