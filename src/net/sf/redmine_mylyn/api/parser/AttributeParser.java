package net.sf.redmine_mylyn.api.parser;

import java.io.InputStream;

import net.sf.redmine_mylyn.api.client.RedmineApiStatusException;
import net.sf.redmine_mylyn.api.model.Property;
import net.sf.redmine_mylyn.api.model.container.AbstractPropertyContainer;

public class AttributeParser<T extends AbstractPropertyContainer<M>, M extends Property> implements IModelParser<T> {

	protected JaxbParser<T> parser;
	
	public AttributeParser(Class<T> containerClass, Class<M>modelClass) {
		parser = new JaxbParser<T>(containerClass, modelClass);
	}
	
	@Override
	public T parseResponse(InputStream input, int sc) throws RedmineApiStatusException {
		return parser.parseInputStream(input);
	}

}
