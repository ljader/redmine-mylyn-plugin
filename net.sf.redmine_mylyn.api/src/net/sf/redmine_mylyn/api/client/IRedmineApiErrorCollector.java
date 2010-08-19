package net.sf.redmine_mylyn.api.client;

public interface IRedmineApiErrorCollector {

	public void accept(String errorMessage);
}
