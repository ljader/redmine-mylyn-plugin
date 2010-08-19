package net.sf.redmine_mylyn.internal.api.parser.adapter.type;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="errors")
@XmlAccessorType(XmlAccessType.NONE)
public class SubmitError {

	@XmlElement(name="error")
	public List<String> errors = new ArrayList<String>();

}
