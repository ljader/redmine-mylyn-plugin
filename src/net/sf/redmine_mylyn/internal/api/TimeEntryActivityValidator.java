package net.sf.redmine_mylyn.internal.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import net.sf.redmine_mylyn.api.model.TimeEntryActivity;
import net.sf.redmine_mylyn.api.model.container.TimeEntryActivities;

public class TimeEntryActivityValidator {

	public final static String RESOURCE_FILE = "/xmldata/timeentryactivities.xml";
	
	public final static int COUNT = 3;
	
	public static void validate11(TimeEntryActivity obj) {
		assertNotNull(obj);
		assertEquals(11, obj.getId());
		assertEquals("QA", obj.getName());
		assertFalse(obj.isDefault());
	}

	public static void validateOrder(TimeEntryActivities ct) {
		//order: 1 2 3
		List<TimeEntryActivity> all = ct.getAll();
		int pos = 0;
		assertEquals(9, all.get(pos++).getId());
		assertEquals(10, all.get(pos++).getId());
		assertEquals(11, all.get(pos++).getId());
	}
	
	public static void validateDefault(TimeEntryActivity obj) {
		assertNotNull(obj);
		assertEquals(10, obj.getId());
	}
}
