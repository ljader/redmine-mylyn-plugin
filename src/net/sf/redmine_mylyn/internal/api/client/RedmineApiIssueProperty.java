package net.sf.redmine_mylyn.internal.api.client;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EnumSet;

import net.sf.redmine_mylyn.api.model.Issue;

public enum RedmineApiIssueProperty {
	SUBJECT, DESCRIPTION, ASSIGNED_TO, CATEGORY, DONE_RATIO, DUE_DATE, ESTIMATED_HOURS, FIXED_VERSION, PARENT, PRIORITY, PROJECT, START_DATE, STATUS, TRACKER;
	
	private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	
	private static EnumSet<RedmineApiIssueProperty> REFERENCES = EnumSet.of(PROJECT, TRACKER, PARENT, STATUS, PRIORITY, CATEGORY, FIXED_VERSION, ASSIGNED_TO);

	private static EnumSet<RedmineApiIssueProperty> DATE = EnumSet.of(START_DATE, DUE_DATE);
	
	private String submitKey;
	
	public String getSubmitKey() {
		if (submitKey==null) {
			submitKey = name().toLowerCase();
			if(REFERENCES.contains(this)) {
				submitKey += "_id";
			}
		}
		return submitKey;
	}
	
	public String getSubmitValue(Field field, Issue issue) {
		boolean accessible = field.isAccessible();
		field.setAccessible(true);
		
		String value = "";
		try {
			if(REFERENCES.contains(this)) {
				int id = field.getInt(issue);
				return id<1 ? "" : ""+id;
			}
			
			if(DATE.contains(this)) {
				Date date = (Date)field.get(issue);
				return date==null ? "" : DATE_FORMAT.format(date);
			}
//			Class<?> type = field.getType();
			
			Object obj = field.get(issue);
			if(obj!=null) {
				value  =obj.toString();
			}
		} catch (Exception e) {
			//TODO logging
			e.printStackTrace();
		} finally {
			field.setAccessible(accessible);
		}
		
		return value;
	}
}
