package net.sf.redmine_mylyn.internal.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.text.SimpleDateFormat;
import java.util.Arrays;

import net.sf.redmine_mylyn.api.model.Issue;
import net.sf.redmine_mylyn.api.model.container.Attachments;
import net.sf.redmine_mylyn.api.model.container.CustomValues;
import net.sf.redmine_mylyn.api.model.container.Journals;
import net.sf.redmine_mylyn.api.model.container.TimeEntries;

public class IssueValidator {

	public final static String RESOURCE_FILE_ISSUE_1 = "/xmldata/issue/issue_1.xml";

	public final static String RESOURCE_FILE_ISSUE_2 = "/xmldata/issue/issue_2.xml";

	public final static String RESOURCE_FILE_LIST = "/xmldata/issues/list_1_7_8.xml";

	public final static String RESOURCE_FILE_UPDATED = "/xmldata/issues/updatedsince_1_7_8.xml";
	
	public final static int COUNT = 3;
	
	public final static SimpleDateFormat REDMINE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	
	public static void validate1(Issue obj) throws Exception {
		
		assertNotNull(obj);
		assertEquals(1, obj.getId());
		assertFalse(obj.isEditAllowed());
		assertEquals("Can't print recipes", obj.getSubject());
		assertEquals("Unable to print recipes", obj.getDescription());
		assertEquals(1273183200000l, obj.getCreatedOn().getTime());
		assertEquals(1273356000000l, obj.getUpdatedOn().getTime());
		assertEquals(1, obj.getTrackerId());
		assertEquals(1, obj.getProjectId());
		assertEquals(1, obj.getStatusId());
		assertEquals(4, obj.getPriorityId());
		assertFalse(obj.isWatched());
		assertEquals(0, obj.getWatcherIds().length);
		assertEquals(REDMINE_DATE_FORMAT.parse("2010-05-09"), obj.getStartDate());
		assertEquals(REDMINE_DATE_FORMAT.parse("2010-05-20"), obj.getDueDate());
		assertEquals(0, obj.getDoneRatio());
		//EstimatedHours
		assertEquals(2, obj.getAuthorId());
		assertEquals(1, obj.getCategoryId());
		//AssignedTo
		//FixedVersion
		//parent
		assertEquals("[1]", Arrays.toString(obj.getAvailableStatusId()));
		
		CustomValues cfs = obj.getCustomValues();
		assertEquals(3, cfs.getAll().size());
		assertEquals(10, cfs.get(10).getId());
		assertEquals(6, cfs.get(10).getCustomFieldId());
		assertEquals("2.1", cfs.get(10).getValue());

		Journals comments = obj.getJournals();
		assertEquals(2, comments.getAll().size());
		assertEquals(2, comments.get(2).getId());
		assertEquals(2, comments.get(2).getUserId());
		assertEquals(1273356000000l, comments.get(2).getCreatedOn().getTime());
		assertEquals("Some notes with Redmine links: #2, r2.", comments.get(2).getNotes());
		
		Attachments attachments = obj.getAttachments();
		assertEquals(0, attachments.getAll().size());
		
		//IssueRelations
		
		TimeEntries tEntrys = obj.getTimeEntries();
		assertEquals(154.25f, tEntrys.getSum(), 0.0);
		assertTrue(tEntrys.isNewAllowed());
		assertTrue(tEntrys.isViewAllowed());
		assertEquals(2, tEntrys.getAll().size());
		assertEquals(4.25, tEntrys.get(1).getHours(), 0.0);
		assertEquals(9, tEntrys.get(1).getActivityId());
		assertEquals(2, tEntrys.get(1).getUserId());
		assertEquals(REDMINE_DATE_FORMAT.parse("2007-03-23"), tEntrys.get(1).getSpentOn());
		assertEquals("My hours", tEntrys.get(1).getComments());
		//CF
	}

	public static void validate2(Issue obj) throws Exception {
		assertNotNull(obj);
		assertEquals(2, obj.getId());
		assertTrue(obj.isEditAllowed());

		assertEquals(10, obj.getDoneRatio());
		assertEquals(3.5, obj.getEstimatedHours(), 0.0);
		assertTrue(obj.isWatched());
		assertEquals("[1, 3]", Arrays.toString(obj.getWatcherIds()));
		assertEquals(3, obj.getAssignedToId());
		assertEquals(2, obj.getFixedVersionId());
		assertEquals(1, obj.getParentId());
		
		TimeEntries tEntrys = obj.getTimeEntries();
		assertEquals(4.25f, tEntrys.getSum(), 0.0);
		assertEquals(1, tEntrys.getAll().size());
		assertEquals(4.25, tEntrys.get(1).getHours(), 0.0);
		assertEquals(9, tEntrys.get(1).getActivityId());
		assertEquals(2, tEntrys.get(1).getUserId());
		assertEquals(REDMINE_DATE_FORMAT.parse("2007-03-23"), tEntrys.get(1).getSpentOn());
		assertEquals("My hours", tEntrys.get(1).getComments());

		CustomValues cfs = tEntrys.get(1).getCustomValues();
		assertEquals(1, cfs.getAll().size());
		assertEquals(5, cfs.get(5).getId());
		assertEquals(2, cfs.get(5).getCustomFieldId());
		assertEquals("", cfs.get(5).getValue());
		
		//TODO
		//IssueRelations
		
		Attachments attachments = obj.getAttachments();
		assertEquals(1, attachments.getAll().size());
		assertEquals(10, attachments.get(10).getId());
		assertEquals(2, attachments.get(10).getAuthorId());
		assertEquals(1153336047000l, attachments.get(10).getCreatedOn().getTime());
		assertEquals("picture.jpg", attachments.get(10).getFilename());
		assertEquals(452, attachments.get(10).getFilesize());
		assertEquals("b91e08d0cf966d5c6ff411bd8c4cc3a2", attachments.get(10).getDigest());
		assertEquals("image/jpeg", attachments.get(10).getContentType());
		assertEquals("kurze Beschreibung", attachments.get(10).getDescription());
	}

}

