package net.sf.redmine_mylyn.core;

import java.util.Map;

public interface IRedmineExtensionField {

	public String getLabel();
	
	public String getSubmitKey();

	public String getTaskKey();
	
	public boolean isRequired();
	
	public String getEditorType();
	
	public Map<String, String> getOptions();
	
}
