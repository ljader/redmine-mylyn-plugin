package net.sf.redmine_mylyn.api.model.container;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import net.sf.redmine_mylyn.api.model.CustomValue;

@XmlType(name="customValues")
@XmlAccessorType(XmlAccessType.NONE)
public class CustomValues extends AbstractTypedContainer<CustomValue> {

	private List<CustomValue> customValues;
	
	private HashMap<Integer, CustomValue> byCustomFieldId;
	
	@Override
	@XmlElement(name="customValue")
	protected List<CustomValue> getModifiableList() {
		if(customValues==null) {
			byCustomFieldId = new HashMap<Integer, CustomValue>();
			
			customValues = new ArrayList<CustomValue>() {
				private static final long serialVersionUID = 1L;
				
				public boolean add(CustomValue e) {
					if(super.add(e)) {
						byCustomFieldId.put(Integer.valueOf(e.getCustomFieldId()), e);
						return true;
					}
					return false;
				};
				
			};
		}
		return customValues;
	}
	
	public CustomValue getByCustomFieldId(int customFieldId) {
		if(byCustomFieldId!=null) {
			return byCustomFieldId.get(Integer.valueOf(customFieldId));
		}
		return null;
	}
	
	public void setCustomValue(int customFieldId, String value) {
		CustomValue customValue= getByCustomFieldId(Integer.valueOf(customFieldId));
		if(customValue==null) {
			customValue = new CustomValue();
			customValue.setCustomFieldId(customFieldId);
			customValue.setValue(value);
			getModifiableList().add(customValue);
		} else {
			customValue.setValue(value);
		}
	}

}
