package net.sf.redmine_mylyn.internal.api.parser;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import net.sf.redmine_mylyn.api.exception.RedmineApiErrorException;

import org.apache.commons.httpclient.HttpStatus;

public class AttachmentParser implements IModelParser<InputStream> {

	@Override
	public InputStream parseResponse(InputStream input, int sc) throws RedmineApiErrorException {
		if (sc==HttpStatus.SC_OK) {
			InputStream response = null;
			try {
				ByteArrayOutputStream output = new ByteArrayOutputStream(input.available());
				try {
					byte[] buffer = new byte[4096];
					int len = 0;
					while ((len=input.read(buffer))>0) {
						output.write(buffer, 0, len); 
					}
					response = new ByteArrayInputStream(output.toByteArray());
				} finally {
					output.close();
				}
			} catch (IOException e) {
				throw new RedmineApiErrorException(e.getMessage(), e);
			}
			return response;
		}
		return null;
	}

}
