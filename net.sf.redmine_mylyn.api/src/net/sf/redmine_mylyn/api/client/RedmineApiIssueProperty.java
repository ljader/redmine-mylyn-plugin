package net.sf.redmine_mylyn.api.client;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EnumSet;

import net.sf.redmine_mylyn.api.RedmineApiPlugin;
import net.sf.redmine_mylyn.api.model.Issue;
import net.sf.redmine_mylyn.internal.api.Messages;

public enum RedmineApiIssueProperty {
	SUBJECT, DESCRIPTION, ASSIGNED_TO, CATEGORY, DONE_RATIO, DUE_DATE, ESTIMATED_HOURS, FIXED_VERSION, PARENT("parent_issue_id"), PRIORITY, PROJECT, START_DATE, STATUS, TRACKER; //$NON-NLS-1$
	
	private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	
	private static EnumSet<RedmineApiIssueProperty> REFERENCES = EnumSet.of(PROJECT, TRACKER, PARENT, STATUS, PRIORITY, CATEGORY, FIXED_VERSION, ASSIGNED_TO);

	private static EnumSet<RedmineApiIssueProperty> DATE = EnumSet.of(START_DATE, DUE_DATE);
	
	private String submitKey;
	
	private RedmineApiIssueProperty() {
	}
	
	private RedmineApiIssueProperty(String submitKey) {
		this.submitKey = submitKey;
	}
	
	public String getSubmitKey() {
		if (submitKey==null) {
			submitKey = name().toLowerCase();
			if(REFERENCES.contains(this)) {
				submitKey += "_id"; //$NON-NLS-1$
			}
		}
		return submitKey;
	}
	
	public String getSubmitValue(Field field, Issue issue) {
		boolean accessible = field.isAccessible();
		field.setAccessible(true);
		
		String value = ""; //$NON-NLS-1$
		try {
			if(REFERENCES.contains(this)) {
				int id = field.getInt(issue);
				return id<1 ? "" : ""+id; //$NON-NLS-1$ //$NON-NLS-2$
			}
			
			if(DATE.contains(this)) {
				Date date = (Date)field.get(issue);
				return date==null ? "" : DATE_FORMAT.format(date); //$NON-NLS-1$
			}
			
			Object obj = field.get(issue);
			if(obj!=null) {
				value  =obj.toString();
			}
		} catch (Exception e) {
			RedmineApiPlugin.getLogService(getClass()).error(e, Messages.ERRMSG_CANT_PICK_SUBMIT_VALUE, field.getName());
		} finally {
			field.setAccessible(accessible);
		}
		
		return value;
	}

	public String checkSubmitValue(String value) {
		if(value==null || value.isEmpty()) {
			return ""; //$NON-NLS-1$
		}
		
		if(REFERENCES.contains(this)) {
			if(!value.isEmpty()) {
				try {
					int refId = Integer.parseInt(value);
					value = (refId<1) ? "" : Integer.toString(refId); //$NON-NLS-1$
				} catch(NumberFormatException e) {
					RedmineApiPlugin.getLogService(this.getClass()).error(e, Messages.ERRMSG_PARAMETER_X_INVALID_INTEGER, value);
				}
			}
		}
		
		if(DATE.contains(this)) {
			if(value!=null && !value.isEmpty()) {
				try {
					Date date = new Date(Long.parseLong(value));
					value = DATE_FORMAT.format(date);
				} catch(NumberFormatException e) {
					RedmineApiPlugin.getLogService(this.getClass()).error(e, Messages.ERRMSG_PARAMETER_X_INVALID_MILISEC, value);
				}
			}
		}
		
		return value;
	}
}
