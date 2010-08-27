package net.sf.redmine_mylyn.internal.api.client;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.httpclient.methods.multipart.PartSource;

import net.sf.redmine_mylyn.api.model.Attachment;

public class AttachmentPartSource implements PartSource {

	final private Attachment attachment; 
	
	final private InputStream input;
	
	public AttachmentPartSource(Attachment attachment, InputStream input) {
		this.attachment = attachment;
		this.input = input;
	}

	@Override
	public long getLength() {
		return attachment.getFilesize();
	}

	@Override
	public String getFileName() {
		return attachment.getFilename();
	}

	@Override
	public InputStream createInputStream() throws IOException {
		return input;
	}
}
