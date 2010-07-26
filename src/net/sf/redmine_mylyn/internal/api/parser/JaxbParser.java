package net.sf.redmine_mylyn.internal.api.parser;

import java.io.InputStream;
import java.util.Arrays;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.sax.SAXSource;

import net.sf.redmine_mylyn.api.exception.RedmineApiErrorException;

public class JaxbParser<T extends Object> {

	protected Class<T> clazz;
	
	protected Class<?>[] classes;
	
	protected JAXBContext ctx;
	
	JaxbParser(Class<T> modelClass) {
		this(modelClass, new Class<?>[0]);
	}

	JaxbParser(Class<T> modelClass, Class<?>... requiredClasses) {
		clazz = modelClass;
		classes = Arrays.copyOf(requiredClasses, requiredClasses.length+1);
		classes[requiredClasses.length] = modelClass;
	}
	
	public T parseInputStream(InputStream stream) throws RedmineApiErrorException {
		try {
			Unmarshaller unmarshaller = getUnmarshaller();
			XMLInputFactory inputFactory = XMLInputFactory.newInstance();
			XMLStreamReader reader;
			
			reader = inputFactory.createXMLStreamReader(stream);
			Object obj = unmarshaller.unmarshal(reader);
			
			return clazz.cast(obj);

		} catch (Exception e) {
			throw new RedmineApiErrorException("Parsing of InputStream failed", e);
		}
	}
	public T parseInputStream(SAXSource source) throws RedmineApiErrorException {
		try {
			Unmarshaller unmarshaller = getUnmarshaller();
			Object obj = unmarshaller.unmarshal(source);
			
			return clazz.cast(obj);
			
		} catch (Exception e) {
			throw new RedmineApiErrorException("Parsing of InputStream failed", e);
		}
	}
	
	protected Unmarshaller getUnmarshaller() throws JAXBException {
		if (ctx==null) {
			ctx = JAXBContext.newInstance(clazz);
		}
		
		return ctx.createUnmarshaller();
	}
}
