package net.sf.redmine_mylyn.core;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


import net.sf.redmine_mylyn.api.model.CustomField;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;

public class RedmineUtil {
	
	private final static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	
	public static boolean isInteger(String  val) {
		return val.matches("^\\d+$");
	}
	
	public static int parseIntegerId(String intVal) {
		if(intVal!=null && !intVal.isEmpty()) {
			try {
				return Integer.parseInt(intVal);
			} catch(NumberFormatException e) {
				IStatus status = RedmineCorePlugin.toStatus(e, "Parameter `{0}` isn't a valid Integer value", intVal);
				StatusHandler.log(status);
			}
		}
		return 0;
	} 

	public static Boolean parseBoolean(String value) {
		return value!=null && value.trim().equals("1") ? Boolean.TRUE : Boolean.parseBoolean(value);
	}
	
	public static String formatDate(Date date) {
		if(date!=null) {
			return df.format(date);
		}
		return null;
	}
	
	public static Date parseDate(String value) {
		if(value!=null && !value.isEmpty()) {
			try {
				//try timestamp
				return new Date(Long.parseLong(value));
			} catch(NumberFormatException e) {
				;//nothing to do
			}
		}
		return new Date(0);
	}

	public static Date parseRedmineDate(String value) {
		if(value!=null && !value.isEmpty()) {
			try {
				//try timestamp
				long timestamp = Long.parseLong(value);
				return new Date(timestamp*1000);
			} catch(NumberFormatException e) {
				try {
					//try formated date
					return df.parse(value);
				} catch (ParseException e1) {
					IStatus status = RedmineCorePlugin.toStatus(e, "Parameter `{0}` isn't a valid unixtime(long) or formated date(yyyy-MM-dd )", value);
					StatusHandler.log(status);
				}
			}
		}
		return null;
	}

	public static String getTaskAttributeType(CustomField customField) {
		String type = TaskAttribute.TYPE_SHORT_TEXT;
		switch (customField.getFieldFormat()) {
		case TEXT:
			type = TaskAttribute.TYPE_LONG_TEXT;
			break;
		case LIST:
			type = TaskAttribute.TYPE_SINGLE_SELECT;
			break;
		case DATE:
			type = TaskAttribute.TYPE_DATE;
			break;
		case BOOL:
			type = TaskAttribute.TYPE_BOOLEAN;
			break;
		default:
			type = TaskAttribute.TYPE_SHORT_TEXT;
		}
		return type;
	}

}
