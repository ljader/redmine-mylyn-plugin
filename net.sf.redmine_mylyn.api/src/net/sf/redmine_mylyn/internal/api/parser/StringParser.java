package net.sf.redmine_mylyn.internal.api.parser;

import java.io.IOException;
import java.io.InputStream;

import net.sf.redmine_mylyn.api.exception.RedmineApiErrorException;

import org.apache.commons.httpclient.HttpStatus;

public class StringParser implements IModelParser<String> {

	@Override
	public String parseResponse(InputStream input, int sc) throws RedmineApiErrorException {
		if (sc==HttpStatus.SC_OK) {
			try {
				byte[] buffer = new byte[44];
				if(input.read(buffer)>0) {
					return new String(buffer);
				}
			} catch (IOException e) {
				throw new RedmineApiErrorException(e.getMessage(), e);
			}
		}
		return null;
	}

}
