package net.sf.redmine_mylyn.internal.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;

import net.sf.redmine_mylyn.api.model.Issue;

public class PartialIssueValidator {

	public final static String RESOURCE_FILE = "/xmldata/issues/issues.xml";

	
	public final static int COUNT = 6;
	
	public static void validate1(Issue obj) throws Exception {
		assertNotNull(obj);
		assertEquals(1, obj.getId());
		assertEquals("Can't print recipes", obj.getSubject());
		assertEquals(1, obj.getStatusId());
		assertEquals(4, obj.getPriorityId());
		assertEquals(1, obj.getProjectId());
		assertEquals(new Date(1273356000000l), obj.getUpdatedOn());
	}

}

