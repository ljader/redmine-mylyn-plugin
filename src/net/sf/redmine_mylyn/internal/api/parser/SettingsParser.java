package net.sf.redmine_mylyn.internal.api.parser;

import java.io.InputStream;

import net.sf.redmine_mylyn.api.client.RedmineApiStatusException;
import net.sf.redmine_mylyn.api.model.Settings;

public class SettingsParser implements IModelParser<Settings> {

	protected JaxbParser<Settings> parser;
	
	public SettingsParser() {
		parser = new JaxbParser<Settings>(Settings.class);
	}
	
	@Override
	public Settings parseResponse(InputStream input, int sc) throws RedmineApiStatusException {
		return parser.parseInputStream(input);
	}

}
