package net.sf.redmine_mylyn.api.query;

public interface IQueryField {

	public String getQueryValue();
	
	public String getLabel();
	
	public boolean isCrossProjectUsable();

}
