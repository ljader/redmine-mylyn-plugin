package net.sf.redmine_mylyn.internal.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import net.sf.redmine_mylyn.api.model.IssueCategory;

public class IssueCategoryValidator {

	public final static String RESOURCE_FILE = "/xmldata/issuecategories.xml";
	
	public final static int COUNT = 4;
	
	public static void validate3(IssueCategory category) {
		assertNotNull(category);
		assertEquals(3, category.getId());
		assertEquals("Stock management", category.getName());
	}

}
