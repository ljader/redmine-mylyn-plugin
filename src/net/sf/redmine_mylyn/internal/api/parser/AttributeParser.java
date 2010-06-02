package net.sf.redmine_mylyn.internal.api.parser;

import java.io.InputStream;

import net.sf.redmine_mylyn.api.client.RedmineApiStatusException;
import net.sf.redmine_mylyn.api.model.Property;
import net.sf.redmine_mylyn.api.model.container.AbstractPropertyContainer;

public class AttributeParser<T extends AbstractPropertyContainer<? extends Property>> implements IModelParser<T> {

	protected JaxbParser<T> parser;
	
	public AttributeParser(Class<T> containerClass) {
		parser = new JaxbParser<T>(containerClass);
	}
	
	@Override
	public T parseResponse(InputStream input, int sc) throws RedmineApiStatusException {
		return parser.parseInputStream(input);
	}

}
