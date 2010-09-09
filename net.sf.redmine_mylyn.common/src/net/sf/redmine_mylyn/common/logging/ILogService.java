package net.sf.redmine_mylyn.common.logging;

public interface ILogService {

	public void error(Throwable t, String message, String... values);

	public void error(String message, String... values);

	public void info(String message, String... values);

	public void debug(String message, String... values);
	
}
