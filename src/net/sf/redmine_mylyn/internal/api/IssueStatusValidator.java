package net.sf.redmine_mylyn.internal.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import net.sf.redmine_mylyn.api.model.IssueStatus;
import net.sf.redmine_mylyn.api.model.container.IssueStatuses;

public class IssueStatusValidator {

	public final static String RESOURCE_FILE = "/xmldata/issuestatus.xml";
	
	public final static int COUNT = 6;
	
	public static void validateIssueStatus5(IssueStatus status) {
		assertNotNull(status);
		assertEquals(5, status.getId());
		assertEquals("Closed", status.getName());
		assertTrue(status.isClosed());
		assertTrue(status.isDefault());
	}

	public static void validateOrder(IssueStatuses statuses) {
		//order: 1 2 6 5 4 3
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
