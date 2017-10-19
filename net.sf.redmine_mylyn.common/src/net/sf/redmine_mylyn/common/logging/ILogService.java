package net.sf.redmine_mylyn.common.logging;

/**
 * Our plugin's log service
 */
public interface ILogService {

	public void error(Throwable t, String message, Object... values);

	public void error(String message, Object... values);

	public void info(String message, Object... values);

	public void debug(String message, Object... values);
	
}
