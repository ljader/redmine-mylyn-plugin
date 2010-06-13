package net.sf.redmine_mylyn.api.model.container;

import java.util.ArrayList;
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
	
	@Override
	@XmlElement(name="customValue")
	protected List<CustomValue> getModifiableList() {
		if(customValues==null) {
			customValues = new ArrayList<CustomValue>();
		}
		return customValues;
	}

}
