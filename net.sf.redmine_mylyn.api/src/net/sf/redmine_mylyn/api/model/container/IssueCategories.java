package net.sf.redmine_mylyn.api.model.container;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import net.sf.redmine_mylyn.api.model.IssueCategory;

@XmlRootElement(name="issueCategories")
@XmlAccessorType(XmlAccessType.NONE)
public class IssueCategories extends AbstractPropertyContainer<IssueCategory> {

	private static final long serialVersionUID = 1L;

	protected List<IssueCategory> issueCategories;
	
	@Override
	@XmlElement(name="issueCategory")
	protected List<IssueCategory> getModifiableList() {
		if(issueCategories==null) {
			issueCategories = new ArrayList<IssueCategory>();
		}
		return issueCategories;
	}

}
