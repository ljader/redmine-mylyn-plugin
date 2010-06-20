package net.sf.redmine_mylyn.internal.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import net.sf.redmine_mylyn.api.client.RedmineServerVersion;

public class ServerVersionValidator {

	public final static String RESOURCE_FILE = "/xmldata/version.xml";
	
	public static void validate(RedmineServerVersion obj) {
		assertNotNull(obj);

		assertNotNull(obj.redmine);
		assertEquals(0, obj.redmine.major);
		assertEquals(9, obj.redmine.minor);
		assertEquals(4, obj.redmine.tiny);
		
		assertNotNull(obj.plugin);
		assertEquals(2, obj.plugin.major);
		assertEquals(7, obj.plugin.minor);
		assertEquals(0, obj.plugin.tiny);
	}

}
