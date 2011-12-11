package net.sf.redmine_mylyn.core;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.sf.redmine_mylyn.api.model.CustomField;
import net.sf.redmine_mylyn.internal.core.Messages;

import net.sf.redmine_mylyn.api.model.CustomField;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.tasks.core.IRepositoryPerson;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;

public class RedmineUtil {
	
	private final static SimpleDateFormat df = new SimpleDateFormat(IRedmineConstants.DATE_FORMAT);
	
	public static boolean isInteger(String  val) {
		return val.matches(IRedmineConstants.REGEX_INTEGER);
	}
	
	public static boolean isInteger(String  val) {
		return val.matches("^\\d+$");
	}
	
	public static int parseIntegerId(String intVal) {
		if(intVal!=null && !intVal.isEmpty()) {
			try {
				return Integer.parseInt(intVal);
			} catch(NumberFormatException e) {
				IStatus status = RedmineCorePlugin.toStatus(e, Messages.ERRMSG_X_VALID_INTEGER, intVal);
				StatusHandler.log(status);
			}
		}
		return 0;
	} 

	public static Boolean parseBoolean(String value) {
		return value!=null && value.trim().equals(IRedmineConstants.BOOLEAN_TRUE_SUBMIT_VALUE) ? Boolean.TRUE : Boolean.parseBoolean(value);
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
					IStatus status = RedmineCorePlugin.toStatus(e, Messages.ERRMSG_X_VALID_UNIXTIME_DATE, value);
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
		case VERSION:
			type = TaskAttribute.TYPE_SINGLE_SELECT;
			break;
		case DATE:
			type = TaskAttribute.TYPE_DATE;
			break;
		case BOOL:
			type = TaskAttribute.TYPE_BOOLEAN;
			break;
		case USER:
			type = IRedmineConstants.EDITOR_TYPE_PERSON;
			break;
		default:
			type = TaskAttribute.TYPE_SHORT_TEXT;
		}
		return type;
	}

	public static String formatUserPresentation (IRepositoryPerson person) {
		return formatUserPresentation(person.getPersonId(), person.getName());
	}
	
	public static String formatUserPresentation (String login, String name) {
		if (login!=null && !login.isEmpty() && !login.equals("0") ) { //$NON-NLS-1$
			return name + " <" + login + ">"; //$NON-NLS-1$ //$NON-NLS-2$
			
		}
		return name;
	}
	
	public static String findUserLogin(String userPresentation) {
		if (userPresentation!=null && !userPresentation.isEmpty()) {
			int ltr = userPresentation.lastIndexOf('<');
			int rtr = userPresentation.lastIndexOf('>');
			
			if (ltr>-1 && rtr>ltr+1) {
				return userPresentation.substring(ltr+1, rtr);
			}
			
		}
		return null;
	}
}
