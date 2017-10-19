package net.sf.redmine_mylyn.internal.common.logging;

import java.io.File;
import net.sf.redmine_mylyn.common.RedmineCommonPlugin;
import net.sf.redmine_mylyn.common.logging.ILogService;
import net.sf.redmine_mylyn.common.logging.LogServiceImpl;

import org.eclipse.core.runtime.IPath;
import org.eclipse.equinox.log.ExtendedLogEntry;
import org.osgi.framework.Bundle;
import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogListener;
import org.osgi.service.log.LogReaderService;
import org.osgi.service.log.LogService;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;

public class LogWriter implements LogListener {

	protected LogReaderService logReaderService;
	
	protected ILoggerFactory loggerFactory;

	public synchronized void setLogReaderService(LogReaderService logReaderService) {
		this.logReaderService = logReaderService;
		logReaderService.addLogListener(this);
	}
	
	public synchronized void unsetLogReaderService(LogReaderService logReaderService) {
		if(this.logReaderService==logReaderService) {
			logReaderService.removeLogListener(this);
			this.logReaderService=null;
		}
	}

	private void configureLogback() {
		loggerFactory = LoggerFactory.getILoggerFactory();
		
		if (loggerFactory instanceof LoggerContext) {
			LoggerContext ctx = (LoggerContext)loggerFactory;

			IPath logDir = RedmineCommonPlugin.getDefault().getStateLocation();
			IPath logFile = logDir.append("redmine_connector.log"); //$NON-NLS-1$

			try {
				JoranConfigurator configurator = new JoranConfigurator();
				configurator.setContext(ctx);
				
				ctx.reset();
				ctx.putProperty("rmc.logdir", logDir.toString()); //$NON-NLS-1$
				//setting old property as fallback
				ctx.putProperty("rmc.logfile", logFile.toString()); //$NON-NLS-1$
				
				File customLogbackConfig = new File(logDir.toFile(), "logback.xml"); //$NON-NLS-1$
				if (customLogbackConfig.isFile()) {
					configurator.doConfigure(customLogbackConfig);
				} else {
					configurator.doConfigure(getClass().getResourceAsStream("/logback.xml")); //$NON-NLS-1$
				}
			} catch (JoranException e) {
				ILogService logService = LogServiceImpl.getInstance(RedmineCommonPlugin.getDefault().getBundle(), LogWriter.class);
				logService.error(e, "Logback configuration failed"); //$NON-NLS-1$
			}
			StatusPrinter.printInCaseOfErrorsOrWarnings(ctx);
		}
	}
	
	@Override
	public void logged(LogEntry entry) {
		Bundle bundle = entry.getBundle();
		if ( bundle.getState()==Bundle.ACTIVE && bundle.getSymbolicName().startsWith("net.sf.redmine_mylyn.")) { //$NON-NLS-1$
			writeLog(entry);
		}
	}
	
	private void writeLog(LogEntry entry) {
		if(loggerFactory==null) {
			configureLogback();
		}
		
		String loggerName = obtainLoggerNameIfSupportedFrom(entry);
		
		if(loggerName==null) {
			loggerName = "RedmineConnector"; //$NON-NLS-1$
		}

		Logger logger = loggerFactory.getLogger(loggerName);
		
		switch(entry.getLevel()) {
			case LogService.LOG_ERROR : logger.error(entry.getMessage(), entry.getException()); break;
			case LogService.LOG_INFO : logger.info(entry.getMessage(), entry.getException()); break;
			case LogService.LOG_DEBUG : logger.debug(entry.getMessage(), entry.getException()); break;
		}
		
		
	}

	private String obtainLoggerNameIfSupportedFrom(LogEntry entry) {
		if (entry instanceof ExtendedLogEntry) {
			return ((ExtendedLogEntry) entry).getLoggerName();
		} else {
			return null;
		}
	}
}
