package net.sf.redmine_mylyn.internal.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import net.sf.redmine_mylyn.api.model.IssuePriority;
import net.sf.redmine_mylyn.api.model.container.IssuePriorities;

public class IssuePriorityValidator {

	public final static String RESOURCE_FILE = "/xmldata/issuepriorities.xml";
	
	public final static int COUNT = 5;
	
	public static void validate4(IssuePriority obj) {
		assertNotNull(obj);
		assertEquals(4, obj.getId());
		assertEquals("Low", obj.getName());
		assertFalse(obj.isDefault());
	}

	public static void validateOrder(IssuePriorities ct) {
		//order: 4 6 5 7 8
		List<IssuePriority> all = ct.getAll();
		int pos = 0;
		assertEquals(4, all.get(pos++).getId());
		assertEquals(6, all.get(pos++).getId());
		assertEquals(5, all.get(pos++).getId());
		assertEquals(7, all.get(pos++).getId());
		assertEquals(8, all.get(pos++).getId());
	}
	
	public static void validateDefault(IssuePriority obj) {
		assertNotNull(obj);
		assertEquals(5, obj.getId());
	}
}
