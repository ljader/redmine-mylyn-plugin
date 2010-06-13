package net.sf.redmine_mylyn.internal.api.parser.adapter.type;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import net.sf.redmine_mylyn.api.model.Issue;
import net.sf.redmine_mylyn.api.model.container.AbstractTypedContainer;

@XmlRootElement(name="issues")
@XmlAccessorType(XmlAccessType.NONE)
public class Issues extends AbstractTypedContainer<Issue> {

	private List<Issue> issues;

	@XmlElement(name="issue")
	@Override
	protected List<Issue> getModifiableList() {
		if(issues==null) {
			issues = new ArrayList<Issue>();
		}
		return issues;
	}

}
