package net.sf.redmine_mylyn.common.logging;

import org.eclipse.osgi.util.NLS;
import org.osgi.service.log.LogService;


public class LogServiceOsgiImpl implements ILogService {

	private final LogService logger;
	
	public LogServiceOsgiImpl(LogService logger) {
		this.logger = logger;
	}
	
	@Override
	public void error(Throwable t, String message, String... values) {
		logger.log(LogService.LOG_ERROR, NLS.bind(message, values), t);
	}

	@Override
	public void error(String message, String... values) {
		logger.log(LogService.LOG_ERROR, NLS.bind(message, values));
	}

	@Override
	public void info(String message, String... values) {
		logger.log(LogService.LOG_INFO, NLS.bind(message, values));
	}

	@Override
	public void debug(String message, String... values) {
		logger.log(LogService.LOG_DEBUG, NLS.bind(message, values));
	}
	
}
