package net.sf.redmine_mylyn.internal.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import net.sf.redmine_mylyn.api.model.Project;
import net.sf.redmine_mylyn.api.model.container.TimeEntryActivities;

public class ProjectValidator {

	public final static String RESOURCE_FILE = "/xmldata/projects.xml";
	
	public final static int COUNT = 2;
	
	public static void validate1(Project obj) {
		assertNotNull(obj);
		assertEquals(1, obj.getId());
		assertTrue(obj.isNewIssueAllowed());
		assertTrue(obj.isMoveIssueAllowed());
		assertEquals("eCookbook", obj.getName());
		assertEquals("ecookbook", obj.getIdentifier());
		
		int[] idlist=null;
		idlist = obj.getTrackerIds();
		assertNotNull(idlist);
		assertEquals(3, idlist.length);
		assertEquals("[1, 2, 3]", Arrays.toString(idlist));
		
		idlist=null;
		idlist = obj.getVersionIds();
		assertNotNull(idlist);
		assertEquals(6, idlist.length);
		assertEquals("[1, 2, 3, 4, 6, 7]", Arrays.toString(idlist));
		
		idlist=null;
		idlist = obj.getIssueCategoryIds();
		assertNotNull(idlist);
		assertEquals(2, idlist.length);
		assertEquals("[1, 2]", Arrays.toString(idlist));
		
		assertNotNull(obj.getMembers());
		assertEquals(2, obj.getMembers().size());
		assertFalse(obj.getMembers().get(0).isAssignable());
		assertEquals(2, obj.getMembers().get(0).getUserId());
		assertTrue(obj.getMembers().get(1).isAssignable());
		assertEquals(3, obj.getMembers().get(1).getUserId());
		
		assertNotNull(obj.getCustomFieldIdsByTrackerId());
		assertEquals(3, obj.getCustomFieldIdsByTrackerId().size());
		assertEquals("[1, 2, 6]", Arrays.toString(obj.getCustomFieldIdsByTrackerId().get(1)));
		assertEquals("[6]", Arrays.toString(obj.getCustomFieldIdsByTrackerId().get(2)));
		assertEquals("[2, 6]", Arrays.toString(obj.getCustomFieldIdsByTrackerId(3)));
		
		TimeEntryActivities ct = obj.getTimeEntryActivities();
		assertNotNull(ct);
		assertEquals(TimeEntryActivityValidator.COUNT, ct.getAll().size());
		
		TimeEntryActivityValidator.validate11(ct.getById(11));
		TimeEntryActivityValidator.validateOrder(ct);
		TimeEntryActivityValidator.validateDefault(ct.getDefault());

	}
	
	
	public static void validate3(Project obj) {
		assertNotNull(obj);

		TimeEntryActivities ct = obj.getTimeEntryActivities();
		assertNotNull(ct);
		assertNotNull(ct.getAll());
		assertEquals(0, ct.getAll().size());
	}

}
