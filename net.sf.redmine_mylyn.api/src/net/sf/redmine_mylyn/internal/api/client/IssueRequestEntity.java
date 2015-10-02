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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONWriter;

public class IssueRequestEntity extends StringRequestEntity {

	public IssueRequestEntity(final Issue issue) throws UnsupportedEncodingException, RedmineApiErrorException {
		super(writeIssue(issue, null, null), "application/json", "UTF-8"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public IssueRequestEntity(final Issue issue, final String comment, final TimeEntry timeEntry) throws UnsupportedEncodingException, RedmineApiErrorException {
		super(writeIssue(issue, comment, timeEntry), "application/json", "UTF-8"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public IssueRequestEntity(final Map<RedmineApiIssueProperty, String> issue, final String comment, final TimeEntry timeEntry) throws UnsupportedEncodingException, RedmineApiErrorException {
		super(writeIssue(issue, comment, timeEntry), "application/json", "UTF-8"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	private static String writeIssue(final Issue issue, final String comment, final TimeEntry timeEntry) throws RedmineApiErrorException {
		try {
			final StringWriter stringWriter = new StringWriter();

			final JSONWriter jsonWriter = new JSONWriter(stringWriter);

			jsonWriter.object();
			writeIssueValues(jsonWriter, issue);
			writeComment(jsonWriter, comment);
			writeTimeEntry(jsonWriter, timeEntry);
			jsonWriter.endObject();


			return stringWriter.toString();
		} catch (final JSONException e) {
			e.printStackTrace();
			throw new RedmineApiErrorException(Messages.ERRMSG_CREATION_OF_SUBMIT_DATA_FAILED, e);
		}
	}

	private static String writeIssue(final Map<RedmineApiIssueProperty, String> issue, final String comment, final TimeEntry timeEntry) throws RedmineApiErrorException {
		try {
			final StringWriter stringWriter = new StringWriter();

			final JSONWriter jsonWriter = new JSONWriter(stringWriter);

			jsonWriter.object();
			writeIssueValues(jsonWriter, issue);
			writeComment(jsonWriter, comment);
			writeTimeEntry(jsonWriter, timeEntry);
			jsonWriter.endObject();


			return stringWriter.toString();
		} catch (final JSONException e) {
			e.printStackTrace();
			throw new RedmineApiErrorException(Messages.ERRMSG_CREATION_OF_SUBMIT_DATA_FAILED, e);
		}
	}

	private static void writeComment(final JSONWriter jsonWriter, final String comment) throws JSONException  {
		if(comment!=null && !comment.trim().isEmpty()) {
			jsonWriter.key("notes").value(comment); //$NON-NLS-1$
		}
	}

	private static void writeTimeEntry(final JSONWriter jsonWriter, final TimeEntry timeEntry) throws JSONException {
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

	private static void writeIssueValues(final JSONWriter jsonWriter, final Issue issue) throws JSONException {
		jsonWriter.key("issue").object(); //$NON-NLS-1$

		final Field[] fields = Issue.class.getDeclaredFields();
		for (final Field field : fields) {
			if(field.isAnnotationPresent(IssuePropertyMapping.class)) {
				final IssuePropertyMapping annotation = field.getAnnotation(IssuePropertyMapping.class);
				final String key = annotation.value().getSubmitKey();
				final String value = annotation.value().getSubmitValue(field, issue);
				writeValue(jsonWriter, key, value);
			}
		}

		if (issue.isWatchersAddAllowed() || issue.isWatchersDeleteAllowed() ) {
			jsonWriter.key("watcher_user_ids").array();
			for (final int userId : issue.getWatcherIds()) {
				jsonWriter.value(userId);
			}
			jsonWriter.endArray();
		}

		writeCustomValues(jsonWriter, issue.getCustomValues());

		jsonWriter.endObject();
	}

	private static void writeIssueValues(final JSONWriter jsonWriter, final Map<RedmineApiIssueProperty, String> issue) throws JSONException {
		jsonWriter.key("issue").object(); //$NON-NLS-1$

		for (final Entry<RedmineApiIssueProperty, String> entry : issue.entrySet()) {
			writeValue(jsonWriter, entry.getKey().getSubmitKey(), entry.getValue());
		}

		jsonWriter.endObject();
	}

	private static void writeCustomValues(final JSONWriter jsonWriter, final CustomValues values) throws JSONException {
		if(values!=null && values.getAll().size()>0) {
			jsonWriter.key("custom_field_values").object(); //$NON-NLS-1$

			for (final CustomValue customValue : values.getAll()) {
				writeValue(jsonWriter, ""+customValue.getCustomFieldId(), customValue.getValue()); //$NON-NLS-1$
			}

			jsonWriter.endObject();
		}
	}


	private static void writeMappedValues(final JSONWriter jsonWriter, final Map<String, String> values) throws JSONException {
		if(values!=null && values.size()>0) {

			for (final Entry<String, String> entry : values.entrySet()) {
				if(!entry.getKey().isEmpty()) {

				}
				writeValue(jsonWriter, entry.getKey(), entry.getValue());
			}

		}
	}

	private static void writeValue(final JSONWriter jsonWriter, final String key, final String value) throws JSONException {
		try {
			final JSONArray array = new JSONArray(value);
			jsonWriter.key(key).value(array);
		} catch (final JSONException e) {
			jsonWriter.key(key).value(value==null ? "" : value.replaceAll("\\n", "\r\n")); //$NON-NLS-1$
		}
	}

}
