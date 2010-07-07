package net.sf.redmine_mylyn.internal.core;

import java.lang.reflect.Field;
import java.util.Date;

import net.sf.redmine_mylyn.api.model.Attachment;
import net.sf.redmine_mylyn.api.model.Configuration;
import net.sf.redmine_mylyn.api.model.CustomValue;
import net.sf.redmine_mylyn.api.model.Issue;
import net.sf.redmine_mylyn.api.model.Journal;
import net.sf.redmine_mylyn.api.model.TimeEntry;
import net.sf.redmine_mylyn.core.IRedmineConstants;
import net.sf.redmine_mylyn.core.RedmineAttribute;
import net.sf.redmine_mylyn.core.RedmineCorePlugin;
import net.sf.redmine_mylyn.core.RedmineRepositoryConnector;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttachmentMapper;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskCommentMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;

public class IssueMapper {

	public static void updateTaskData(TaskRepository repository, TaskData taskData, Configuration cfg, Issue issue) throws CoreException {

		TaskAttribute root = taskData.getRoot();
		TaskAttribute taskAttribute = null;
		
		/* Default Attributes */
		for (RedmineAttribute redmineAttribute : RedmineAttribute.values()) {
			Field field = redmineAttribute.getAttributeField();
			if(field!=null) {
				taskAttribute = root.getAttribute(redmineAttribute.getTaskKey());
				if(taskAttribute !=null ) {
					try {
						setValue(taskAttribute, field.get(issue));
					} catch (Exception e) {
						IStatus status = RedmineCorePlugin.toStatus(e, "Should never happens");
						StatusHandler.fail(status);
					}
				}
			}
		}

		/* Custom Attributes */
		for (CustomValue customValue : issue.getCustomValues().getAll()) {
			taskAttribute = taskData.getRoot().getAttribute(IRedmineConstants.TASK_KEY_PREFIX_ISSUE_CF + customValue.getCustomFieldId());
			if(taskAttribute!=null) {
				setValue(taskAttribute, customValue.getValue());
			}
		}
		
		/* Journals */
		int jrnlCount=1;
		for (Journal journal : issue.getJournals().getAll()) {
			TaskCommentMapper mapper = new TaskCommentMapper();
			mapper.setAuthor(repository.createPerson(""+journal.getUserId()));
			mapper.setCreationDate(journal.getCreatedOn());
			mapper.setText(journal.getNotes());
			String issueUrl = RedmineRepositoryConnector.getTaskUrl(repository.getUrl(), issue.getId());
			mapper.setUrl(issueUrl + String.format(IRedmineConstants.REDMINE_URL_PART_COMMENT, journal.getId()));
			mapper.setNumber(jrnlCount++);
			mapper.setCommentId(String.valueOf(journal.getId()));
			
			taskAttribute = taskData.getRoot().createAttribute(TaskAttribute.PREFIX_COMMENT + mapper.getCommentId());
			mapper.applyTo(taskAttribute);
		}

		/* Attachments */
		for (Attachment	attachment : issue.getAttachments().getAll()) {
			TaskAttachmentMapper mapper = new TaskAttachmentMapper();
			mapper.setAttachmentId("" + attachment.getId()); //$NON-NLS-1$
			mapper.setAuthor(repository.createPerson(""+attachment.getAuthorId()));
			mapper.setDescription(attachment.getDescription());
			mapper.setCreationDate(attachment.getCreatedOn());
			mapper.setContentType(attachment.getContentType());
			mapper.setFileName(attachment.getFilename());
			mapper.setLength((long)attachment.getFilesize());
			mapper.setUrl(String.format(IRedmineConstants.REDMINE_URL_ATTACHMENT_DOWNLOAD, repository.getUrl(), attachment.getId()));
			
			taskAttribute = taskData.getRoot().createAttribute(TaskAttribute.PREFIX_ATTACHMENT + mapper.getAttachmentId());
			mapper.applyTo(taskAttribute);
		}
		
		//TODO
		if(true /*ticket.getRight(RedmineAcl.TIMEENTRY_VIEW*/) {
			//TODO kind/label
			taskAttribute = taskData.getRoot().createAttribute(IRedmineConstants.TASK_ATTRIBUTE_TIMEENTRY_TOTAL);
			taskAttribute.setValue(""+issue.getTimeEntries().getSum());
			
			
			for (TimeEntry timeEntry : issue.getTimeEntries().getAll()) {
				TaskTimeEntryMapper mapper = new TaskTimeEntryMapper(cfg);
				mapper.setTimeEntryId(timeEntry.getId());
				mapper.setUser(repository.createPerson(""+timeEntry.getUserId()));
				mapper.setActivityId(timeEntry.getActivityId());
				mapper.setHours(timeEntry.getHours());
				mapper.setSpentOn(timeEntry.getSpentOn());
				mapper.setComments(timeEntry.getComments());
				mapper.setCustomValues(timeEntry.getCustomValues().getAll());

				taskAttribute = taskData.getRoot().createAttribute(IRedmineConstants.TASK_ATTRIBUTE_TIMEENTRY_PREFIX + mapper.getTimeEntryId());
				mapper.applyTo(taskAttribute);
			}
		}
	}

	
	private static void setValue(TaskAttribute attribute, Object value) {
		if(value==null) {
			setValue(attribute, "");
		} else if(value instanceof String) {
			setValue(attribute, (String)value);
		} else if(value instanceof Date) {
			setValue(attribute, (Date)value);
		} else if(value instanceof Integer) {
			setValue(attribute, ((Integer)value).intValue());
		} else {
			setValue(attribute, value.toString());
		}
	}

	private static void setValue(TaskAttribute attribute, String value) {
		if(value==null) {
			attribute.setValue("");
		} else if(attribute.getMetaData().getType().equals(TaskAttribute.TYPE_BOOLEAN)) {
			attribute.setValue(Util.parseBoolean(value).toString());
		} else if(attribute.getMetaData().getType().equals(TaskAttribute.TYPE_DATE) || attribute.getMetaData().getType().equals(TaskAttribute.TYPE_DATETIME)) {
			setValue(attribute, Util.parseRedmineDate(value));
		} else {
			attribute.setValue(value);
		}

	}

	private static void setValue(TaskAttribute attribute, Date value) {
		if(value==null) {
			attribute.setValue("");
		} else {
			attribute.setValue(""+value.getTime());
		}
	}
	
	private static void setValue(TaskAttribute attribute, int value) {
		if(attribute.getMetaData().getType().equals(TaskAttribute.TYPE_SINGLE_SELECT) && value<1) {
			attribute.setValue("");
		} else {
			attribute.setValue(""+value);
		}
	}
}
