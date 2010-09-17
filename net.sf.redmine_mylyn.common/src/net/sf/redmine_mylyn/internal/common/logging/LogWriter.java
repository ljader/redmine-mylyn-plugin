package net.sf.redmine_mylyn.internal.common.logging;

import net.sf.redmine_mylyn.common.RedmineCommonPlugin;
import net.sf.redmine_mylyn.common.logging.ILogService;
import net.sf.redmine_mylyn.common.logging.LogServiceImpl;

import org.eclipse.core.runtime.IPath;
import org.eclipse.equinox.log.ExtendedLogEntry;
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
			IPath path = RedmineCommonPlugin.getDefault().getLogFilePath();
			
			try {
				JoranConfigurator configurator = new JoranConfigurator();
				configurator.setContext(ctx);
				
				ctx.reset();
				ctx.putProperty("rmc.logfile", path.toString());
				
				configurator.doConfigure(getClass().getResourceAsStream("/logback.xml"));
			} catch (JoranException e) {
				ILogService logService = LogServiceImpl.getInstance(RedmineCommonPlugin.getDefault().getBundle(), LogWriter.class);
				logService.error(e, "Logback configuration failed");
			}
		}
	}
	
	@Override
	public void logged(LogEntry entry) {
		if (entry.getBundle().getSymbolicName().startsWith("net.sf.redmine_mylyn.")) {
			writeLog(entry);
		}
	}
	
	private void writeLog(LogEntry entry) {
		if(loggerFactory==null) {
			configureLogback();
		}
		
		String loggerName = null;
		if(entry instanceof ExtendedLogEntry) {
			loggerName = ((ExtendedLogEntry)entry).getLoggerName();
		}
		
		if(loggerName==null) {
			loggerName = "RedmineConnector";
		}

		Logger logger = loggerFactory.getLogger(loggerName);
		
		switch(entry.getLevel()) {
			case LogService.LOG_ERROR : logger.error(entry.getMessage(), entry.getException()); break;
			case LogService.LOG_INFO : logger.info(entry.getMessage(), entry.getException()); break;
			case LogService.LOG_DEBUG : logger.debug(entry.getMessage(), entry.getException()); break;
		}
		
		
	}
}
