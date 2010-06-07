package net.sf.redmine_mylyn.internal.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import net.sf.redmine_mylyn.api.model.User;

public class UserValidator {

	public final static String RESOURCE_FILE = "/xmldata/users.xml";
	
	public final static int COUNT = 9;
	
	public static void validate2(User obj) {
		assertNotNull(obj);
		assertEquals(2, obj.getId());
		assertEquals("John Smith", obj.getName());
		assertEquals("jsmith", obj.getLogin());
		assertEquals("John", obj.getFirstname());
		assertEquals("Smith", obj.getLastname());
		assertEquals("jsmith@somenet.foo", obj.getMail());
	}

}
