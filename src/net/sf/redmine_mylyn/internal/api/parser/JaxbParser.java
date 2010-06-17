package net.sf.redmine_mylyn.internal.api.parser;

import java.io.InputStream;
import java.util.Arrays;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.sax.SAXSource;

import net.sf.redmine_mylyn.api.client.RedmineApiPlugin;
import net.sf.redmine_mylyn.api.client.RedmineApiStatusException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

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
	
	public T parseInputStream(InputStream stream) throws RedmineApiStatusException {
		try {
			Unmarshaller unmarshaller = getUnmarshaller();
			XMLInputFactory inputFactory = XMLInputFactory.newInstance();
			XMLStreamReader reader;
			
			reader = inputFactory.createXMLStreamReader(stream);
			Object obj = unmarshaller.unmarshal(reader);
			
			return clazz.cast(obj);

		} catch (Exception e) {
			IStatus status = new Status(IStatus.ERROR, RedmineApiPlugin.PLUGIN_ID, "Execution of method failed", e);
			throw new RedmineApiStatusException(status);
		}
	}
	public T parseInputStream(SAXSource source) throws RedmineApiStatusException {
		try {
			Unmarshaller unmarshaller = getUnmarshaller();
			Object obj = unmarshaller.unmarshal(source);
			
			return clazz.cast(obj);
			
		} catch (Exception e) {
			IStatus status = new Status(IStatus.ERROR, RedmineApiPlugin.PLUGIN_ID, "Execution of method failed", e);
			throw new RedmineApiStatusException(status);
		}
	}
	
	protected Unmarshaller getUnmarshaller() throws JAXBException {
		if (ctx==null) {
			ctx = JAXBContext.newInstance(clazz);
		}
		
		return ctx.createUnmarshaller();
	}
}
