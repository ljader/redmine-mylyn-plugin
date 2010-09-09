package net.sf.redmine_mylyn.common.logging;

public class NullLogService implements ILogService {

	@Override
	public void error(Throwable t, String message, String... values) {
	}

	@Override
	public void error(String message, String... values) {
	}

	@Override
	public void info(String message, String... values) {
	}

	@Override
	public void debug(String message, String... values) {
	}
	
}
