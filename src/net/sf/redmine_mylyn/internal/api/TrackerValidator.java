package net.sf.redmine_mylyn.internal.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import net.sf.redmine_mylyn.api.model.Tracker;
import net.sf.redmine_mylyn.api.model.container.Trackers;

public class TrackerValidator {

	public final static String RESOURCE_FILE = "/xmldata/trackers.xml";
	
	public final static int COUNT = 3;
	
	public static void validate2(Tracker obj) {
		assertNotNull(obj);
		assertEquals(2, obj.getId());
		assertEquals("Feature request", obj.getName());
	}

	public static void validateOrder(Trackers ct) {
		//order: 1 3 2
		List<Tracker> all = ct.getAll();
		int pos = 0;
		assertEquals(1, all.get(pos++).getId());
		assertEquals(3, all.get(pos++).getId());
		assertEquals(2, all.get(pos++).getId());
	}
}
