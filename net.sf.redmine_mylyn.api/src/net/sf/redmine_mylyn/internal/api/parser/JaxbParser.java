package net.sf.redmine_mylyn.internal.api.parser;

import java.io.InputStream;
import java.util.Arrays;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;

import net.sf.redmine_mylyn.api.RedmineApiPlugin;
import net.sf.redmine_mylyn.api.exception.RedmineApiErrorException;
import net.sf.redmine_mylyn.api.exception.RedmineApiRemoteException;
import net.sf.redmine_mylyn.common.logging.ILogService;
import net.sf.redmine_mylyn.internal.api.Messages;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

public class JaxbParser<T extends Object> {

	private final static String UNSUPPORTED_NS = "http://redmin-mylyncon.sf.net/schemas/WS-API-2.6"; //$NON-NLS-1$

	private final static String SUPPORTED_NS = "http://redmin-mylyncon.sf.net/api"; //$NON-NLS-1$
	
	protected Class<T> clazz;
	
	protected Class<?>[] classes;
	
	protected JAXBContext ctx;
	
	protected SAXParserFactory parserFactory;
	
	private ILogService log;
	
	JaxbParser(Class<T> modelClass) {
		this(modelClass, new Class<?>[0]);
	}

	JaxbParser(Class<T> modelClass, Class<?>... requiredClasses) {
		clazz = modelClass;
		classes = Arrays.copyOf(requiredClasses, requiredClasses.length+1);
		classes[requiredClasses.length] = modelClass;
		
		parserFactory = SAXParserFactory.newInstance();
		parserFactory.setNamespaceAware(true);
		
		log = RedmineApiPlugin.getLogService(JaxbParser.class);
	}
	
	public T parseInputStream(InputStream stream) throws RedmineApiErrorException {
		try {
			XMLFilterImpl filter = new RedminePluginFilter();
			SAXSource source = new SAXSource(filter, new InputSource(stream));

			return parseInputStream(source);
			
		} catch (ParserConfigurationException e) {
			RedmineApiErrorException exc = new RedmineApiErrorException(Messages.ERRMSG_INPUTSTREAM_PARSING_FAILED_CONFIG_ERROR_X, e.getMessage(), e);
			log.error(e, exc.getMessage());
			throw exc;
		} catch (SAXException e) {
			RedmineApiErrorException exc = new RedmineApiErrorException(Messages.ERRMSG_INPUTSTREAM_PARSING_FAILED_X, e.getMessage(), e);
			log.error(e, exc.getMessage());
			throw exc;
		}
	}
	
	public T parseInputStream(SAXSource source) throws RedmineApiErrorException {
		try {
			Unmarshaller unmarshaller = getUnmarshaller();
			Object obj = unmarshaller.unmarshal(source);
			
			return clazz.cast(obj);
			
		} catch (JAXBException e) {
			e.printStackTrace();
			if (e.getLinkedException() instanceof RedmineApiRemoteException) {
				throw (RedmineApiRemoteException)e.getLinkedException();
			}

			RedmineApiErrorException exc = new RedmineApiErrorException(Messages.ERRMSG_INPUTSTREAM_PARSING_FAILED, e);
			log.error(e, exc.getMessage());
			throw exc;
		}
	}
	
	protected Unmarshaller getUnmarshaller() throws JAXBException {
		if (ctx==null) {
			Thread thread = Thread.currentThread();
			ClassLoader classLoader = thread.getContextClassLoader();
			
			thread.setContextClassLoader(getClass().getClassLoader());
			ctx = JAXBContext.newInstance(clazz);
			
			thread.setContextClassLoader(classLoader);
		}
		return ctx.createUnmarshaller();
	}

	private class RedminePluginFilter extends XMLFilterImpl {
		
		RedminePluginFilter() throws SAXException, ParserConfigurationException {
			super(parserFactory.newSAXParser().getXMLReader());
		}
		
		@Override
		public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
			int idx = atts.getIndex("authenticated"); //$NON-NLS-1$
			if(idx>=0) {
				boolean authenticated = Boolean.parseBoolean(atts.getValue(idx));
				if(authenticated) {
					String authenticatedAs = atts.getValue(atts.getIndex("authenticatedAs")); //$NON-NLS-1$
					log.debug("AUTHENTICATED AS {0}", authenticatedAs); //$NON-NLS-1$
				} else {
					log.debug("NOT AUTHENTICATED"); //$NON-NLS-1$
				}
			}
			super.startElement(uri, localName, qName, atts);
		}
		
		@Override
		public void startPrefixMapping(String prefix, String uri) throws SAXException {
			if(uri.equals(UNSUPPORTED_NS)) {
				String msg = Messages.ERRMSG_UNSUPPORTED_REDMINE_VERSION;
				throw new SAXException(msg, new RedmineApiRemoteException(msg));
			}
			
			if(!uri.equals(SUPPORTED_NS)) {
				String msg = Messages.ERRMSG_INVALID_REDMINE_URL;
				throw new SAXException(msg, new RedmineApiRemoteException(msg));
			}
			
			super.startPrefixMapping(prefix, uri);
		}
	}
}
