package net.sf.redmine_mylyn.internal.common.logging;

import java.text.MessageFormat;

import net.sf.redmine_mylyn.common.logging.ILogService;

import org.eclipse.equinox.log.ExtendedLogService;
import org.eclipse.equinox.log.Logger;
import org.osgi.framework.Bundle;
import org.osgi.service.log.LogService;


/**
 * Logs entries via Equinox extended logging class (named Logger)
 */
public class ExtendedLogServiceImpl implements ILogService {

	private Logger logger;
	
	public ExtendedLogServiceImpl(ExtendedLogService logger, Bundle bundle, Class<?> loggerName) {
		if(logger!=null) {
			this.logger = logger.getLogger(bundle, loggerName.getName());
		}
	}
	
	@Override
	public void error(Throwable t, String message, Object... values) {
		if(logger!=null && logger.isLoggable(LogService.LOG_ERROR)) {
			logger.log(LogService.LOG_ERROR, MessageFormat.format(message, values), t);
		}
	}

	@Override
	public void error(String message, Object... values) {
		if(logger!=null && logger.isLoggable(LogService.LOG_ERROR)) {
			logger.log(LogService.LOG_ERROR, MessageFormat.format(message, values));
		}
	}

	@Override
	public void info(String message, Object... values) {
		if(logger!=null && logger.isLoggable(LogService.LOG_INFO)) {
			logger.log(LogService.LOG_INFO, MessageFormat.format(message, values));
		}
	}

	@Override
	public void debug(String message, Object... values) {
		if(logger!=null && logger.isLoggable(LogService.LOG_DEBUG)) {
			logger.log(LogService.LOG_DEBUG, MessageFormat.format(message, values));
		}
	}
	
}
