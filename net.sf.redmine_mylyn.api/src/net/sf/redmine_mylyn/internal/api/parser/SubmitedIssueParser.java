package net.sf.redmine_mylyn.internal.api.parser;

import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;

import net.sf.redmine_mylyn.api.RedmineApiPlugin;
import net.sf.redmine_mylyn.api.exception.RedmineApiErrorException;
import net.sf.redmine_mylyn.common.logging.ILogService;
import net.sf.redmine_mylyn.internal.api.Messages;
import net.sf.redmine_mylyn.internal.api.parser.adapter.type.PartialIssueType;
import net.sf.redmine_mylyn.internal.api.parser.adapter.type.SubmitError;

import org.apache.commons.httpclient.HttpStatus;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

public class SubmitedIssueParser implements IModelParser<Object> {

	public final static String FAKE_NS = "http://redmin-mylyncon.sf.net/api";  //$NON-NLS-1$
	
	protected JaxbParser<PartialIssueType> successParser;
	protected JaxbParser<SubmitError> errorParser;
	
	protected SAXParserFactory parserFactory;

	private ILogService log;

	public SubmitedIssueParser() {
		errorParser = new JaxbParser<SubmitError>(SubmitError.class);
		successParser = new JaxbParser<PartialIssueType>(PartialIssueType.class);

		parserFactory = SAXParserFactory.newInstance();
		parserFactory.setNamespaceAware(false);

		log = RedmineApiPlugin.getLogService(SubmitedIssueParser.class);
	}
	
	@Override
	public Object parseResponse(InputStream input, int sc) throws RedmineApiErrorException {
		if(sc==HttpStatus.SC_OK) {
			return null;
		}
		
		try {
			JaxbParser<?> parser = successParser;
			if (sc==HttpStatus.SC_UNPROCESSABLE_ENTITY) {
				parser = errorParser;
			}
		
			XMLFilterImpl filter = new MissingNamespaceFilter();
			SAXSource source = new SAXSource(filter, new InputSource(input));
			
			return parser.parseInputStream(source);
		} catch (Exception e) {
			throw new RedmineApiErrorException(Messages.ERRMSG_INPUTSTREAM_PARSING_FAILED, e);
		}
	}

	private class MissingNamespaceFilter extends XMLFilterImpl {
		
		public MissingNamespaceFilter() throws SAXException, ParserConfigurationException {
			super(parserFactory.newSAXParser().getXMLReader());
		}
		
		@Override
		public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
			int idx = atts.getIndex("authenticated"); //$NON-NLS-1$
			if(idx>0) {
				boolean authenticated = Boolean.parseBoolean(atts.getValue(idx));
				if(authenticated) {
					String authenticatedAs = atts.getValue(atts.getIndex("authenticatedAs")); //$NON-NLS-1$
					log.debug("AUTHENTICATED AS {0}", authenticatedAs); //$NON-NLS-1$
				} else {
					log.debug("NOT AUTHENTICATED"); //$NON-NLS-1$
				}
			}

			super.startElement(FAKE_NS, localName, qName, atts);
		}

	}
}



