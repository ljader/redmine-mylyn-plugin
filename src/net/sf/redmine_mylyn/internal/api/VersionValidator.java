package net.sf.redmine_mylyn.internal.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import net.sf.redmine_mylyn.api.model.Version;
import net.sf.redmine_mylyn.api.model.Version.Status;

public class VersionValidator {

	public final static String RESOURCE_FILE = "/xmldata/versions.xml";
	
	public final static int COUNT = 7;

	public static void validate1(Version obj) {
		assertNotNull(obj);
		assertEquals(1, obj.getId());
		assertEquals("0.1", obj.getName());
		assertEquals(Status.CLOSED, obj.getStatus());
	}

	public static void validate2(Version obj) {
		assertNotNull(obj);
		assertEquals(2, obj.getId());
		assertEquals("1.0", obj.getName());
		assertEquals(Status.LOCKED, obj.getStatus());
	}
	
	public static void validate3(Version obj) {
		assertNotNull(obj);
		assertEquals(3, obj.getId());
		assertEquals("2.0", obj.getName());
		assertEquals(Status.OPEN, obj.getStatus());
	}
	
}
