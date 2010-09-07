package net.sf.redmine_mylyn.internal.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.sf.redmine_mylyn.api.TestData;
import net.sf.redmine_mylyn.api.model.Attachment;
import net.sf.redmine_mylyn.api.model.Configuration;
import net.sf.redmine_mylyn.api.model.CustomValue;
import net.sf.redmine_mylyn.api.model.Issue;
import net.sf.redmine_mylyn.api.model.Journal;
import net.sf.redmine_mylyn.api.model.TimeEntry;
import net.sf.redmine_mylyn.api.model.container.CustomValues;
import net.sf.redmine_mylyn.core.IRedmineConstants;
import net.sf.redmine_mylyn.core.RedmineAttribute;
import net.sf.redmine_mylyn.core.RedmineCorePlugin;
import net.sf.redmine_mylyn.core.RedmineOperation;
import net.sf.redmine_mylyn.core.RedmineRepositoryConnector;
import net.sf.redmine_mylyn.core.RedmineTaskAttributeMapper;
import net.sf.redmine_mylyn.core.RedmineTaskDataHandler;

import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.junit.Before;
import org.junit.Test;

public class IssueMapperTest {

	final static String URL = "http://localhost";
	
	private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	
	private TaskRepository repository;
	
	private RedmineTaskDataHandler taskDataHandler;
	
	private Configuration cfg;
	
	@Before
	public void setUp() throws Exception {
		repository = new TaskRepository(RedmineCorePlugin.REPOSITORY_KIND, URL);
		
		taskDataHandler = new RedmineTaskDataHandler(new RedmineRepositoryConnector(){
			@Override
			public Configuration getRepositoryConfiguration(TaskRepository repository) {
				return IssueMapperTest.this.cfg;
			}
		});
		
		cfg = TestData.cfg;
	}
	
	@Test
	public void testUpdateTaskData() throws Exception{
		Issue issue = TestData.issue2;
		TaskData taskData = buildEmptyTaskData(issue);
		
		TaskAttribute root = taskData.getRoot();
		TaskAttribute attribute = null;

		IssueMapper.updateTaskData(repository, taskData, cfg, issue);

		assertEquals(issue.getSubject(), root.getAttribute(RedmineAttribute.SUMMARY.getTaskKey()).getValue());
		assertEquals(issue.getDescription(), root.getAttribute(RedmineAttribute.DESCRIPTION.getTaskKey()).getValue());
		assertEquals(""+issue.getCreatedOn().getTime(), root.getAttribute(RedmineAttribute.DATE_SUBMITTED.getTaskKey()).getValue());
		assertEquals(""+issue.getUpdatedOn().getTime(), root.getAttribute(RedmineAttribute.DATE_UPDATED.getTaskKey()).getValue());
		assertEquals(""+issue.getTrackerId(), root.getAttribute(RedmineAttribute.TRACKER.getTaskKey()).getValue());
		assertEquals(""+issue.getProjectId(), root.getAttribute(RedmineAttribute.PROJECT.getTaskKey()).getValue());
		assertEquals(""+issue.getStatusId(), root.getAttribute(RedmineAttribute.STATUS.getTaskKey()).getValue());
		assertEquals(""+issue.getStatusId(), root.getAttribute(RedmineAttribute.STATUS_CHG.getTaskKey()).getValue());
		assertEquals(""+issue.getPriorityId(), root.getAttribute(RedmineAttribute.PRIORITY.getTaskKey()).getValue());
		//TODO watched
		//TODO watchers
		assertEquals(""+issue.getStartDate().getTime(), root.getAttribute(RedmineAttribute.DATE_START.getTaskKey()).getValue());
		assertEquals("", root.getAttribute(RedmineAttribute.DATE_DUE.getTaskKey()).getValue());
		assertEquals(""+issue.getDoneRatio(), root.getAttribute(RedmineAttribute.PROGRESS.getTaskKey()).getValue());
		assertEquals(""+issue.getEstimatedHours(), root.getAttribute(RedmineAttribute.ESTIMATED.getTaskKey()).getValue());
		assertEquals(""+issue.getAuthorId(), root.getAttribute(RedmineAttribute.REPORTER.getTaskKey()).getValue());
		assertEquals("", root.getAttribute(RedmineAttribute.CATEGORY.getTaskKey()).getValue());
		assertEquals(""+issue.getAssignedToId(), root.getAttribute(RedmineAttribute.ASSIGNED_TO.getTaskKey()).getValue());
		assertEquals(""+issue.getFixedVersionId(), root.getAttribute(RedmineAttribute.VERSION.getTaskKey()).getValue());
		assertEquals(""+issue.getParentId(), root.getAttribute(RedmineAttribute.PARENT.getTaskKey()).getValue());
		//TODO available Status

		/* CustomValues */
		CustomValue cv = issue.getCustomValues().get(5);
		attribute = root.getAttribute(IRedmineConstants.TASK_KEY_PREFIX_ISSUE_CF+cv.getCustomFieldId());
		assertNotNull(attribute);
		assertEquals("", attribute.getValue());
		cv = issue.getCustomValues().get(11);
		attribute = root.getAttribute(IRedmineConstants.TASK_KEY_PREFIX_ISSUE_CF+cv.getCustomFieldId());
		assertNotNull(attribute);
		assertEquals(cv.getValue(), attribute.getValue());
		cv = issue.getCustomValues().get(12);
		attribute = root.getAttribute(IRedmineConstants.TASK_KEY_PREFIX_ISSUE_CF+cv.getCustomFieldId());
		assertNotNull(attribute);
		assertEquals(cv.getValue(), attribute.getValue());
		cv = issue.getCustomValues().get(13);
		attribute = root.getAttribute(IRedmineConstants.TASK_KEY_PREFIX_ISSUE_CF+cv.getCustomFieldId());
		assertNotNull(attribute);
		assertEquals(""+df.parse(cv.getValue()).getTime(), attribute.getValue());

		/* Journals */
		/* affected by TaskAttributeMapper, RedmineTaskAttributeMapper */
		attribute = root.getAttribute(TaskAttribute.PREFIX_COMMENT + "3");
		Journal journal = issue.getJournals().get(3);
		assertNotNull(attribute);
		assertEquals(""+journal.getId(), attribute.getValue());
		assertEquals("1", attribute.getAttribute(TaskAttribute.COMMENT_NUMBER).getValue());
		assertEquals(""+journal.getUserId(), attribute.getAttribute(TaskAttribute.COMMENT_AUTHOR).getValue());
		assertEquals(cfg.getUsers().getById(journal.getUserId()).getName(), attribute.getAttribute(TaskAttribute.COMMENT_AUTHOR).getAttribute(TaskAttribute.PERSON_NAME).getValue());
		assertEquals(""+journal.getCreatedOn().getTime(), attribute.getAttribute(TaskAttribute.COMMENT_DATE).getValue());
		assertEquals(""+journal.getNotes(), attribute.getAttribute(TaskAttribute.COMMENT_TEXT).getValue());
		assertEquals(URL + "/issues/show/" + issue.getId() + "#note-3", attribute.getAttribute(TaskAttribute.COMMENT_URL).getValue());
		
		/* Attachments */
		/* affected by TaskAttributeMapper, RedmineTaskAttributeMapper */
		attribute = root.getAttribute(TaskAttribute.PREFIX_ATTACHMENT + "10");
		Attachment attachment = issue.getAttachments().get(10);
		assertNotNull(attribute);
		assertEquals(""+attachment.getId(), attribute.getValue());
		assertEquals(""+attachment.getAuthorId(), attribute.getAttribute(TaskAttribute.ATTACHMENT_AUTHOR).getValue());
		assertEquals(cfg.getUsers().getById(attachment.getAuthorId()).getName(), attribute.getAttribute(TaskAttribute.ATTACHMENT_AUTHOR).getAttribute(TaskAttribute.PERSON_NAME).getValue());
		assertEquals(""+attachment.getCreatedOn().getTime(), attribute.getAttribute(TaskAttribute.ATTACHMENT_DATE).getValue());
		assertEquals(attachment.getFilename(), attribute.getAttribute(TaskAttribute.ATTACHMENT_FILENAME).getValue());
		assertEquals(""+attachment.getFilesize(), attribute.getAttribute(TaskAttribute.ATTACHMENT_SIZE).getValue());
		assertEquals(attachment.getDescription(), attribute.getAttribute(TaskAttribute.ATTACHMENT_DESCRIPTION).getValue());
		assertEquals(URL + "/attachments/download/10", attribute.getAttribute(TaskAttribute.ATTACHMENT_URL).getValue());
		
		/* IssueRelations */
		/* TimeEntries + sum*/
		/* affected by TaskAttributeMapper, RedmineTaskAttributeMapper, TaskTimeEntryMapper */
//		assertEquals(""+issue.getTimeEntries().getSum(), root.getAttribute(IRedmineConstants.TASK_ATTRIBUTE_TIMEENTRY_TOTAL).getValue());
		assertEquals("4.25", root.getAttribute(IRedmineConstants.TASK_ATTRIBUTE_TIMEENTRY_TOTAL).getValue());
		
		TimeEntry te = issue.getTimeEntries().get(1);
		attribute = root.getAttribute(IRedmineConstants.TASK_ATTRIBUTE_TIMEENTRY_PREFIX + te.getId());
		assertNotNull(attribute);
		assertEquals(""+te.getId(), attribute.getValue());

		assertEquals(""+te.getUserId(), attribute.getAttribute(IRedmineConstants.TASK_ATTRIBUTE_TIMEENTRY_AUTHOR).getValue());
		assertEquals(""+te.getActivityId(), attribute.getAttribute(IRedmineConstants.TASK_ATTRIBUTE_TIMEENTRY_ACTIVITY).getValue());
		assertEquals(""+te.getHours(), attribute.getAttribute(IRedmineConstants.TASK_ATTRIBUTE_TIMEENTRY_HOURS).getValue());
		assertEquals(""+te.getComments(), attribute.getAttribute(IRedmineConstants.TASK_ATTRIBUTE_TIMEENTRY_COMMENTS).getValue());
		assertEquals(""+te.getSpentOn().getTime(), attribute.getAttribute(IRedmineConstants.TASK_ATTRIBUTE_TIMEENTRY_SPENTON).getValue());

		cv = te.getCustomValues().get(5);
		//TODO was ist die benötigte string-repräsentation von Booleschen werten?
		assertEquals("1", attribute.getAttribute(IRedmineConstants.TASK_KEY_PREFIX_TIMEENTRY_CF+cv.getCustomFieldId()).getValue());
		
		fail("Not finished yet implemented");
	}

	@Test
	public void testUpdateTaskData_empty() throws Exception{
		Issue issue = new Issue();
		issue.setProjectId(1);
		issue.setTrackerId(1);
		TaskData taskData = buildEmptyTaskData(issue);
		
		TaskAttribute root = taskData.getRoot();
		IssueMapper.updateTaskData(repository, taskData, cfg, issue);
		
		assertEquals("", root.getAttribute(RedmineAttribute.SUMMARY.getTaskKey()).getValue());
		assertEquals("", root.getAttribute(RedmineAttribute.DESCRIPTION.getTaskKey()).getValue());
		assertEquals("1", root.getAttribute(RedmineAttribute.TRACKER.getTaskKey()).getValue());
		assertEquals("1", root.getAttribute(RedmineAttribute.PROJECT.getTaskKey()).getValue());
		assertEquals("", root.getAttribute(RedmineAttribute.STATUS.getTaskKey()).getValue());
		assertEquals("", root.getAttribute(RedmineAttribute.STATUS_CHG.getTaskKey()).getValue());
		assertEquals("", root.getAttribute(RedmineAttribute.PRIORITY.getTaskKey()).getValue());
		//TODO watched
		//TODO watchers
		assertEquals("", root.getAttribute(RedmineAttribute.DATE_START.getTaskKey()).getValue());
		assertEquals("", root.getAttribute(RedmineAttribute.DATE_DUE.getTaskKey()).getValue());
		assertEquals("", root.getAttribute(RedmineAttribute.PROGRESS.getTaskKey()).getValue());
//		assertEquals("", root.getAttribute(RedmineAttribute.ESTIMATED.getTaskKey()).getValue());
		assertEquals("", root.getAttribute(RedmineAttribute.CATEGORY.getTaskKey()).getValue());
		assertEquals("", root.getAttribute(RedmineAttribute.ASSIGNED_TO.getTaskKey()).getValue());
		assertEquals("", root.getAttribute(RedmineAttribute.VERSION.getTaskKey()).getValue());
		assertEquals("", root.getAttribute(RedmineAttribute.PARENT.getTaskKey()).getValue());
		//TODO available Status
	}
	
	@Test
	public void createIssue() throws Exception {
		TaskData taskData = buildEmptyTaskData(TestData.issue2);
		fillTaskData(taskData, TestData.issue2);
		
		Issue issue = IssueMapper.createIssue(repository, taskData, null, cfg);
		assertNotNull(issue);
		
		assertEquals(2, issue.getId());
		assertEquals("Add ingredients categories", issue.getSubject());
		assertEquals("Ingredients of the recipe should be classified by categories", issue.getDescription());
		assertEquals(1, issue.getProjectId());
		assertEquals(2, issue.getTrackerId());
		assertEquals(2, issue.getStatusId());
		assertEquals(1, issue.getParentId());
		//TODO change parent and validate again
		assertEquals(5, issue.getPriorityId());
		assertEquals(0, issue.getCategoryId());
		assertEquals(3, issue.getAssignedToId());
		assertEquals(2, issue.getFixedVersionId());
		assertEquals(df.parse("2010-05-08"), issue.getStartDate());
		assertNull(issue.getDueDate());
		assertEquals(3.5f, issue.getEstimatedHours(), 0.0);
		assertEquals(10, issue.getDoneRatio());
		//TODO parentId

		//CustomValues
		CustomValues customValues = issue.getCustomValues();
		assertNotNull(customValues);
		assertNotNull(customValues.getAll());
		assertEquals(4, customValues.getAll().size());
		assertEquals("", customValues.getByCustomFieldId(2).getValue());
		assertEquals("2.05", customValues.getByCustomFieldId(6).getValue());
		assertEquals("Oracle", customValues.getByCustomFieldId(1).getValue());
		assertEquals("2009-12-01", customValues.getByCustomFieldId(9).getValue());
		
		fail("Not finished yet implemented");
	}

	@Test
	public void createIssue_statusChanged() throws Exception {
		TaskData taskData = buildEmptyTaskData(TestData.issue2);
		fillTaskData(taskData, TestData.issue2);

		//Change Status (mark as)
		TaskAttribute attribute = taskData.getRoot().getAttribute(TaskAttribute.OPERATION);
		attribute.setValue(RedmineOperation.markas.getTaskKey());

		attribute = taskData.getRoot().getAttribute(RedmineAttribute.STATUS_CHG.getTaskKey());
		attribute.setValue("4");
		
		Issue issue = IssueMapper.createIssue(repository, taskData, null, cfg);
		assertNotNull(issue);
		
		assertEquals(2, issue.getId());
		assertEquals(4, issue.getStatusId());
	}
	
	@Test
	public void createTimeEntry() throws Exception {
		TaskData taskData = buildEmptyTaskData(TestData.issue2);
		fillTaskData(taskData, TestData.issue2);
		
		TimeEntry timeEntry = IssueMapper.createTimeEntry(repository, taskData, null, cfg);
		assertNotNull(timeEntry);
		
		assertEquals(1.5, timeEntry.getHours(), 0.0);
		assertEquals(1, timeEntry.getActivityId());
		assertEquals("hard work", timeEntry.getComments());

		//CustomValues
		CustomValues customValues = timeEntry.getCustomValues();
		assertNotNull(customValues);
		assertNotNull(customValues.getAll());
		assertEquals(2, customValues.getAll().size());
		assertEquals("1", customValues.getByCustomFieldId(7).getValue());
		assertEquals("2010-08-20", customValues.getByCustomFieldId(10).getValue());
	}

	TaskData buildEmptyTaskData(Issue issue) throws Exception {
		TaskData taskData = new TaskData(new RedmineTaskAttributeMapper(repository, cfg), RedmineCorePlugin.REPOSITORY_KIND, repository.getUrl(), "" + issue.getId());
	
		Method m = RedmineTaskDataHandler.class.getDeclaredMethod("createAttributes", TaskData.class, Issue.class, Configuration.class);
		m.setAccessible(true);
		m.invoke(taskDataHandler, taskData, issue, cfg);
		
		m = RedmineTaskDataHandler.class.getDeclaredMethod("createOperations", TaskData.class, Issue.class, Configuration.class);
		m.setAccessible(true);
		m.invoke(taskDataHandler, taskData, issue, cfg);
		
		return taskData;
	}
	
	void fillTaskData(TaskData taskData, Issue issue) {
		TaskAttribute root = taskData.getRoot();

		setAttributeValue(root, RedmineAttribute.SUMMARY, issue.getSubject());
		setAttributeValue(root, RedmineAttribute.DESCRIPTION, issue.getDescription());
		setAttributeValue(root, RedmineAttribute.PROJECT, issue.getProjectId());
		setAttributeValue(root, RedmineAttribute.TRACKER, issue.getTrackerId());
		setAttributeValue(root, RedmineAttribute.STATUS, issue.getStatusId());
		setAttributeValue(root, RedmineAttribute.STATUS_CHG, issue.getStatusId());
		setAttributeValue(root, RedmineAttribute.PARENT, issue.getParentId());
		setAttributeValue(root, RedmineAttribute.PRIORITY, issue.getPriorityId());
		setAttributeValue(root, RedmineAttribute.CATEGORY, issue.getCategoryId());
		setAttributeValue(root, RedmineAttribute.REPORTER, issue.getAuthorId());
		setAttributeValue(root, RedmineAttribute.ASSIGNED_TO, issue.getAssignedToId());
		setAttributeValue(root, RedmineAttribute.VERSION, issue.getFixedVersionId());
		setAttributeValue(root, RedmineAttribute.DATE_SUBMITTED, issue.getCreatedOn());
		setAttributeValue(root, RedmineAttribute.DATE_UPDATED, issue.getUpdatedOn());
		setAttributeValue(root, RedmineAttribute.DATE_START, issue.getStartDate());
		setAttributeValue(root, RedmineAttribute.DATE_DUE, issue.getDueDate());
		setAttributeValue(root, RedmineAttribute.ESTIMATED, issue.getEstimatedHours());
		setAttributeValue(root, RedmineAttribute.PROGRESS, issue.getDoneRatio());
//		setAttributeValue(root, RedmineAttribute.COMMENT, "");
		
		//new time entry
		root.getAttribute(RedmineAttribute.TIME_ENTRY_HOURS.getTaskKey()).setValue("1.5");
		root.getAttribute(RedmineAttribute.TIME_ENTRY_ACTIVITY.getTaskKey()).setValue("1");
		root.getAttribute(RedmineAttribute.TIME_ENTRY_COMMENTS.getTaskKey()).setValue("hard work");
		root.getAttribute(IRedmineConstants.TASK_KEY_PREFIX_TIMEENTRY_CF+"7").setValue("TRUE");
		root.getAttribute(IRedmineConstants.TASK_KEY_PREFIX_TIMEENTRY_CF+"10").setValue("1282255200000");
		
		//CustomFields
		for (CustomValue cv : issue.getCustomValues().getAll()) {
			TaskAttribute attribute = root.getAttribute(IRedmineConstants.TASK_KEY_PREFIX_ISSUE_CF + cv.getCustomFieldId()); 
			attribute.setValue(cv.getValue()==null ? "" : cv.getValue());
		}
		root.getAttribute(IRedmineConstants.TASK_KEY_PREFIX_ISSUE_CF+"9").setValue("1259622000000");
		
		
		//operation
	}
	
	void setAttributeValue(TaskAttribute root, RedmineAttribute redmineAttribute, String value) {
		TaskAttribute attribute = root.getAttribute(redmineAttribute.getTaskKey());
		if(attribute!=null && value!=null) {
			attribute.setValue(value);
		}
	}

	void setAttributeValue(TaskAttribute root, RedmineAttribute redmineAttribute, Date value) {
		TaskAttribute attribute = root.getAttribute(redmineAttribute.getTaskKey());
		if(attribute!=null && value!=null) {
			attribute.setValue(""+value.getTime());
		}
	}

	void setAttributeValue(TaskAttribute root, RedmineAttribute redmineAttribute, int value) {
		TaskAttribute attribute = root.getAttribute(redmineAttribute.getTaskKey());
		if(attribute!=null) {
			attribute.setValue(""+value);
		}
	}

	void setAttributeValue(TaskAttribute root, RedmineAttribute redmineAttribute, float value) {
		TaskAttribute attribute = root.getAttribute(redmineAttribute.getTaskKey());
		if(attribute!=null) {
			attribute.setValue(""+value);
		}
	}

}
