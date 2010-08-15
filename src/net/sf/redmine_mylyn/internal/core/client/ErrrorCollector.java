package net.sf.redmine_mylyn.internal.core.client;

import net.sf.redmine_mylyn.api.client.IRedmineApiErrorCollector;

public class ErrrorCollector implements IRedmineApiErrorCollector {

	StringBuilder builder;
	
	@Override
	public void accept(String errorMessage) {
		if(builder==null) {
			builder = new StringBuilder(errorMessage);
		} else {
			builder.append("\n");
			builder.append(errorMessage);
		}
	}
	
	public String getErrorString(){
		if(builder!=null) {
			return builder.toString();
		}
		return "";
	}

}
