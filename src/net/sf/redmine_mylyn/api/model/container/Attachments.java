package net.sf.redmine_mylyn.api.model.container;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import net.sf.redmine_mylyn.api.model.Attachment;

@XmlType(name="attachments")
@XmlAccessorType(XmlAccessType.NONE)
public class Attachments extends AbstractTypedContainer<Attachment> {

	private List<Attachment> attachments;
	
	@Override
	@XmlElement(name="attachment")
	protected List<Attachment> getModifiableList() {
		if(attachments==null) {
			attachments = new ArrayList<Attachment>();
		}
		return attachments;
	}

}
