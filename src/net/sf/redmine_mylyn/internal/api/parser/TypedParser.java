package net.sf.redmine_mylyn.internal.api.parser;

import java.io.InputStream;

import net.sf.redmine_mylyn.api.exception.RedmineApiErrorException;

public class TypedParser<T> implements IModelParser<T> {

	protected JaxbParser<T> parser;
	
	public TypedParser(Class<T> containerClass) {
		parser = new JaxbParser<T>(containerClass);
	}
	
	@Override
	public T parseResponse(InputStream input, int sc) throws RedmineApiErrorException {
		return parser.parseInputStream(input);
	}

}
