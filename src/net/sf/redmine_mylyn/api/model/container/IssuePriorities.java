package net.sf.redmine_mylyn.api.model.container;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import net.sf.redmine_mylyn.api.model.IssuePriority;

@XmlRootElement(name="issuePriorities")
@XmlAccessorType(XmlAccessType.NONE)
public class IssuePriorities extends AbstractSortedPropertyContainer<IssuePriority> {

	protected List<IssuePriority> issuePriorities;
	
	protected IssuePriority defaultPriority;
	
	@Override
	@XmlElement(name="issuePriority")
	protected List<IssuePriority> getModifiableList() {
		if(issuePriorities==null) {
			issuePriorities = new ArrayList<IssuePriority>() {

				private static final long serialVersionUID = -690772074652255323L;

				@Override
				public boolean add(IssuePriority e) {
					if(defaultPriority==null || e.isDefault()) {
						defaultPriority = e;
					}
					return super.add(e);
				}
			};
		}
		return issuePriorities;
	}

	public IssuePriority getDefault() {
		return defaultPriority;
	}
}
