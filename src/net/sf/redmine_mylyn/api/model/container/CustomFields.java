package net.sf.redmine_mylyn.api.model.container;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import net.sf.redmine_mylyn.api.model.CustomField;
import net.sf.redmine_mylyn.api.model.CustomField.Type;

@XmlRootElement(name = "customFields")
@XmlAccessorType(XmlAccessType.NONE)
public class CustomFields extends AbstractPropertyContainer<CustomField> {

	private static final long serialVersionUID = 1L;

	protected List<CustomField> customFields;

	protected Map<Type, List<CustomField>> customFieldsByType;

	@Override
	@XmlElement(name = "customField")
	protected List<CustomField> getModifiableList() {
		if (customFields == null) {
			customFields = new ArrayList<CustomField>() {

				private static final long serialVersionUID = 1L;

				@Override
				public boolean add(CustomField e) {
					if(e.getType()!=null) {
						getCustomFieldsByType(e.getType()).add(e);
						return super.add(e);
					}

					return false;
				}
			};
		}
		return customFields;
	}
	
	protected List<CustomField> getCustomFieldsByType(CustomField.Type type) {
		if(customFieldsByType==null) {
			customFieldsByType = new HashMap<Type, List<CustomField>>();
		}
		if(customFieldsByType.get(type)==null) {
			customFieldsByType.put(type, new ArrayList<CustomField>());
		}
		return customFieldsByType.get(type);
	}
	
	public List<CustomField> getIssueCustomFields() {
		return Collections.unmodifiableList(getCustomFieldsByType(Type.IssueCustomField));
	}

	public List<CustomField> getTimeEntryCustomFields() {
		return Collections.unmodifiableList(getCustomFieldsByType(Type.TimeEntryCustomField));
	}
	
}
