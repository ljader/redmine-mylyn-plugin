package net.sf.redmine_mylyn.core;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


import org.eclipse.core.runtime.IStatus;
import org.eclipse.mylyn.commons.core.StatusHandler;

public class RedmineUtil {
	
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

	public static Date parseRedmineDate(String value) {
		if(value!=null && !value.isEmpty()) {
			try {
				//try timestamp
				long timestamp = Long.parseLong(value);
				return new Date(timestamp*1000);
			} catch(NumberFormatException e) {
				try {
					//try formated date
					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
					return df.parse(value);
				} catch (ParseException e1) {
					IStatus status = RedmineCorePlugin.toStatus(e, "Parameter `{0}` isn't a valid unixtime(long) or formated date(yyyy-MM-dd )", value);
					StatusHandler.log(status);
				}
			}
		}
		return null;
	}

}
