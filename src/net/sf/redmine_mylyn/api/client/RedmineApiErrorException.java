package net.sf.redmine_mylyn.api.client;

public class RedmineApiErrorException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public RedmineApiErrorException(String message, String... params) {
		super(placeParams(message, params));
	}

	public RedmineApiErrorException(String message, Throwable cause, String... params) {
		super(placeParams(message, params), cause);
	}
	
	private static String placeParams(String message, String[] params) {
		for (String param : params) {
			message.replaceFirst("{}", param==null ? "<NULL>" : param);
		}
		
		message = message.replaceAll("\\{\\d+\\}", "");
		
		return message;
	}
	
}
