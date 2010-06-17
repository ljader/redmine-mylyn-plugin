package net.sf.redmine_mylyn.internal.api.parser;

import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;

import net.sf.redmine_mylyn.api.client.RedmineApiPlugin;
import net.sf.redmine_mylyn.api.client.RedmineApiStatusException;
import net.sf.redmine_mylyn.internal.api.parser.adapter.type.PartialIssues;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

public class PartialIssueParser implements IModelParser<PartialIssues> {

	public final static String FAKE_NS = "http://redmin-mylyncon.sf.net/api"; 
	
	protected JaxbParser<PartialIssues> parser;
	
	protected SAXParserFactory parserFactory;
	
	public PartialIssueParser() {
		parser = new JaxbParser<PartialIssues>(PartialIssues.class);

		parserFactory = SAXParserFactory.newInstance();
		parserFactory.setNamespaceAware(false);
}
	
	@Override
	public PartialIssues parseResponse(InputStream input, int sc) throws RedmineApiStatusException {
		try {
			XMLFilterImpl filter = new MissingNamespaceFilter();
			SAXSource source = new SAXSource(filter, new InputSource(input));
			
			return parser.parseInputStream(source);
		} catch (Exception e) {
			IStatus status = new Status(IStatus.ERROR, RedmineApiPlugin.PLUGIN_ID, "Execution of method failed", e);
			throw new RedmineApiStatusException(status);
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



