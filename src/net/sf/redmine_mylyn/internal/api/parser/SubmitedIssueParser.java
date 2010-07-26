package net.sf.redmine_mylyn.internal.api.parser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;

import net.sf.redmine_mylyn.api.exception.RedmineApiErrorException;
import net.sf.redmine_mylyn.internal.api.parser.adapter.type.PartialIssueType;
import net.sf.redmine_mylyn.internal.api.parser.adapter.type.SubmitError;

import org.apache.commons.httpclient.HttpStatus;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

public class SubmitedIssueParser implements IModelParser<Object> {

	public final static String FAKE_NS = "http://redmin-mylyncon.sf.net/api"; 
	
	protected JaxbParser<PartialIssueType> successParser;
	protected JaxbParser<SubmitError> errorParser;
	
	protected SAXParserFactory parserFactory;
	
	public SubmitedIssueParser() {
		errorParser = new JaxbParser<SubmitError>(SubmitError.class);
		successParser = new JaxbParser<PartialIssueType>(PartialIssueType.class);

		parserFactory = SAXParserFactory.newInstance();
		parserFactory.setNamespaceAware(false);
	}
	
	@Override
	public Object parseResponse(InputStream input, int sc) throws RedmineApiErrorException {
		try {
			//TODO remove
//			BufferedReader reader = new BufferedReader(new InputStreamReader(input));
//			String l;
//			while((l=reader.readLine())!=null) {
//				System.out.println(l);
//			}
			
			JaxbParser<?> parser = successParser;
			if (sc!=HttpStatus.SC_CREATED) {
				parser = errorParser;
			}
		
			XMLFilterImpl filter = new MissingNamespaceFilter();
			SAXSource source = new SAXSource(filter, new InputSource(input));
			
			return parser.parseInputStream(source);
		} catch (Exception e) {
			throw new RedmineApiErrorException("Parsing of InputStream failed", e);
		}
	}

	private class MissingNamespaceFilter extends XMLFilterImpl {
		
		public MissingNamespaceFilter() throws SAXException, ParserConfigurationException {
			super(parserFactory.newSAXParser().getXMLReader());
		}
		
		@Override
		public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
			super.startElement(FAKE_NS, localName, qName, atts);
		}
		
	}
}



