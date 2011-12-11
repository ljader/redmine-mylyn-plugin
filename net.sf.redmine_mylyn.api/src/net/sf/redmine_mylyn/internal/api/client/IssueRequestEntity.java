package net.sf.redmine_mylyn.internal.api.client;

import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.redmine_mylyn.api.client.RedmineApiIssueProperty;
import net.sf.redmine_mylyn.api.exception.RedmineApiErrorException;
import net.sf.redmine_mylyn.api.model.CustomValue;
import net.sf.redmine_mylyn.api.model.Issue;
import net.sf.redmine_mylyn.api.model.TimeEntry;
import net.sf.redmine_mylyn.api.model.container.CustomValues;
import net.sf.redmine_mylyn.internal.api.Messages;

import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.json.JSONException;
import org.json.JSONWriter;

public class IssueRequestEntity extends StringRequestEntity {

	public IssueRequestEntity(Issue issue) throws UnsupportedEncodingException, RedmineApiErrorException {
		super(writeIssue(issue, null, null), "application/json", "UTF-8"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public IssueRequestEntity(Issue issue, String comment, TimeEntry timeEntry) throws UnsupportedEncodingException, RedmineApiErrorException {
		super(writeIssue(issue, comment, timeEntry), "application/json", "UTF-8"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public IssueRequestEntity(Map<RedmineApiIssueProperty, String> issue, String comment, TimeEntry timeEntry) throws UnsupportedEncodingException, RedmineApiErrorException {
		super(writeIssue(issue, comment, timeEntry), "application/json", "UTF-8"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public IssueRequestEntity(Map<RedmineApiIssueProperty, String> issue, String comment, TimeEntry timeEntry) throws UnsupportedEncodingException, RedmineApiErrorException {
		super(writeIssue(issue, comment, timeEntry), "application/json", "UTF-8");
	}
	
	private static String writeIssue(Issue issue, String comment, TimeEntry timeEntry) throws RedmineApiErrorException {
		try {
			StringWriter stringWriter = new StringWriter();
			
			JSONWriter jsonWriter = new JSONWriter(stringWriter);
			
			jsonWriter.object();
			writeIssueValues(jsonWriter, issue);
			writeComment(jsonWriter, comment);
			writeTimeEntry(jsonWriter, timeEntry);
			jsonWriter.endObject();
			
			
			return stringWriter.toString();
		} catch (JSONException e) {
			e.printStackTrace();
			throw new RedmineApiErrorException(Messages.ERRMSG_CREATION_OF_SUBMIT_DATA_FAILED, e);
		}
	}
	
	private static String writeIssue(Map<RedmineApiIssueProperty, String> issue, String comment, TimeEntry timeEntry) throws RedmineApiErrorException {
		try {
			StringWriter stringWriter = new StringWriter();
			
			JSONWriter jsonWriter = new JSONWriter(stringWriter);
			
			jsonWriter.object();
			writeIssueValues(jsonWriter, issue);
			writeComment(jsonWriter, comment);
			writeTimeEntry(jsonWriter, timeEntry);
			jsonWriter.endObject();
			
			
			return stringWriter.toString();
		} catch (JSONException e) {
			e.printStackTrace();
			throw new RedmineApiErrorException(Messages.ERRMSG_CREATION_OF_SUBMIT_DATA_FAILED, e);
		}
	}
	
	private static String writeIssue(Map<RedmineApiIssueProperty, String> issue, String comment, TimeEntry timeEntry) throws RedmineApiErrorException {
		try {
			StringWriter stringWriter = new StringWriter();
			
			JSONWriter jsonWriter = new JSONWriter(stringWriter);
			
			jsonWriter.object();
			writeIssueValues(jsonWriter, issue);
			writeComment(jsonWriter, comment);
			writeTimeEntry(jsonWriter, timeEntry);
			jsonWriter.endObject();
			
			
			return stringWriter.toString();
		} catch (JSONException e) {
			e.printStackTrace();
			throw new RedmineApiErrorException("Creation of Submit-JSON failed", e);
		}
	}
	
	private static void writeComment(JSONWriter jsonWriter, String comment) throws JSONException  {
		if(comment!=null && !comment.trim().isEmpty()) {
			jsonWriter.key("notes").value(comment); //$NON-NLS-1$
		}
	}

	private static void writeTimeEntry(JSONWriter jsonWriter, TimeEntry timeEntry) throws JSONException {
		if(timeEntry!=null) {
			jsonWriter.key("time_entry").object(); //$NON-NLS-1$
		
			writeValue(jsonWriter, "hours", ""+timeEntry.getHours()); //$NON-NLS-1$ //$NON-NLS-2$
			writeValue(jsonWriter, "activity_id", ""+timeEntry.getActivityId()); //$NON-NLS-1$ //$NON-NLS-2$
			writeValue(jsonWriter, "comments", ""+timeEntry.getComments()); //$NON-NLS-1$ //$NON-NLS-2$
			writeCustomValues(jsonWriter, timeEntry.getCustomValues());
			writeMappedValues(jsonWriter, timeEntry.getExtensionValues());
			
			jsonWriter.endObject();
		} 
	}

	private static void writeIssueValues(JSONWriter jsonWriter, Issue issue) throws JSONException {
		jsonWriter.key("issue").object(); //$NON-NLS-1$
		
		Field[] fields = Issue.class.getDeclaredFields();
		for (Field field : fields) {
			if(field.isAnnotationPresent(IssuePropertyMapping.class)) {
				IssuePropertyMapping annotation = (IssuePropertyMapping)field.getAnnotation(IssuePropertyMapping.class);
				String key = annotation.value().getSubmitKey();
				String value = annotation.value().getSubmitValue(field, issue);
				writeValue(jsonWriter, key, value);
			}
		}
		
		if (issue.isWatchersAddAllowed() || issue.isWatchersDeleteAllowed() ) {
			jsonWriter.key("watcher_user_ids").array();
			for (int userId : issue.getWatcherIds()) {
				jsonWriter.value(userId);
			}
			jsonWriter.endArray();
		}

		writeCustomValues(jsonWriter, issue.getCustomValues());
		
		jsonWriter.endObject();
	}
	
	private static void writeIssueValues(JSONWriter jsonWriter, Map<RedmineApiIssueProperty, String> issue) throws JSONException {
		jsonWriter.key("issue").object(); //$NON-NLS-1$
		
		for (Entry<RedmineApiIssueProperty, String> entry : issue.entrySet()) {
			writeValue(jsonWriter, entry.getKey().getSubmitKey(), entry.getValue());
		}
		
		jsonWriter.endObject();
	}
	
	private static void writeCustomValues(JSONWriter jsonWriter, CustomValues values) throws JSONException {
		if(values!=null && values.getAll().size()>0) {
			jsonWriter.key("custom_field_values").object(); //$NON-NLS-1$
			
			for (CustomValue customValue : values.getAll()) {
				writeValue(jsonWriter, ""+customValue.getCustomFieldId(), customValue.getValue()); //$NON-NLS-1$
			}
			
			jsonWriter.endObject();
		}
	}
	
	
	private static void writeMappedValues(JSONWriter jsonWriter, Map<String, String> values) throws JSONException {
		if(values!=null && values.size()>0) {
			
			for (Entry<String, String> entry : values.entrySet()) {
				if(!entry.getKey().isEmpty()) {
					
				}
				writeValue(jsonWriter, entry.getKey(), entry.getValue());
			}
			
		}
	}
	
	private static void writeValue(JSONWriter jsonWriter, String key, String value) throws JSONException {
		jsonWriter.key(key).value(value==null ? "" : value); //$NON-NLS-1$
	}
	
}
