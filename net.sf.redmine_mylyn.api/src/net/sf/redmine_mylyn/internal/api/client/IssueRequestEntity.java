package net.sf.redmine_mylyn.internal.api.client;

import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;

import net.sf.redmine_mylyn.api.exception.RedmineApiErrorException;
import net.sf.redmine_mylyn.api.model.CustomValue;
import net.sf.redmine_mylyn.api.model.Issue;
import net.sf.redmine_mylyn.api.model.TimeEntry;
import net.sf.redmine_mylyn.api.model.container.CustomValues;

import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.json.JSONException;
import org.json.JSONWriter;

public class IssueRequestEntity extends StringRequestEntity {

	public IssueRequestEntity(Issue issue) throws UnsupportedEncodingException, RedmineApiErrorException {
		super(writeIssue(issue, null, null), "application/json", "UTF-8");
	}

	public IssueRequestEntity(Issue issue, String comment, TimeEntry timeEntry) throws UnsupportedEncodingException, RedmineApiErrorException {
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
			throw new RedmineApiErrorException("Creation of Submit-JSON failed", e);
		}
	}
	
	private static void writeComment(JSONWriter jsonWriter, String comment) throws JSONException  {
		if(comment!=null && !comment.trim().isEmpty()) {
			jsonWriter.key("notes").value(comment);
		}
	}

	private static void writeTimeEntry(JSONWriter jsonWriter, TimeEntry timeEntry) throws JSONException {
		if(timeEntry!=null) {
			jsonWriter.key("time_entry").object();
		
			writeValue(jsonWriter, "hours", ""+timeEntry.getHours());
			writeValue(jsonWriter, "activity_id", ""+timeEntry.getActivityId());
			writeValue(jsonWriter, "comments", ""+timeEntry.getComments());
			writeCustomValues(jsonWriter, timeEntry.getCustomValues());
			
			jsonWriter.endObject();
		} 
	}

	private static void writeIssueValues(JSONWriter jsonWriter, Issue issue) throws JSONException {
		jsonWriter.key("issue").object();
		
		Field[] fields = Issue.class.getDeclaredFields();
		for (Field field : fields) {
			if(field.isAnnotationPresent(IssuePropertyMapping.class)) {
				IssuePropertyMapping annotation = (IssuePropertyMapping)field.getAnnotation(IssuePropertyMapping.class);
				String key = annotation.value().getSubmitKey();
				String value = annotation.value().getSubmitValue(field, issue);
				writeValue(jsonWriter, key, value);
			}
		}
		
		writeCustomValues(jsonWriter, issue.getCustomValues());
		
		jsonWriter.endObject();
	}
	
	private static void writeCustomValues(JSONWriter jsonWriter, CustomValues values) throws JSONException {
		if(values!=null && values.getAll().size()>0) {
			jsonWriter.key("custom_field_values").object();
			
			for (CustomValue customValue : values.getAll()) {
				writeValue(jsonWriter, ""+customValue.getCustomFieldId(), customValue.getValue());
			}
			
			jsonWriter.endObject();
		}
	}
	
	private static void writeValue(JSONWriter jsonWriter, String key, String value) throws JSONException {
		jsonWriter.key(key).value(value==null ? "" : value);
	}
	
}
