package net.sf.redmine_mylyn.api.model.container;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import net.sf.redmine_mylyn.api.model.CustomValue;

import org.json.JSONArray;
import org.json.JSONException;

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

				@Override
				public boolean add(final CustomValue e) {
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

	public CustomValue getByCustomFieldId(final int customFieldId) {
		if(byCustomFieldId!=null) {
			return byCustomFieldId.get(Integer.valueOf(customFieldId));
		}
		return null;
	}

	public void setCustomValue(final int customFieldId, final String value) {
		CustomValue customValue= getByCustomFieldId(Integer.valueOf(customFieldId));
		if(customValue==null) {
			customValue = new CustomValue();
			customValue.setCustomFieldId(customFieldId);
			customValue.setValue(value);
			getModifiableList().add(customValue);
		} else {
			final String existingValue = customValue.getValue();
			if (existingValue == null || existingValue.isEmpty()) {
				customValue.setValue(value);
			} else {
				JSONArray array;
				try {
					array = new JSONArray(existingValue);
				} catch (final JSONException e) {
					array = new JSONArray();
					array.put(existingValue);
				}
				array.put(value);
				customValue.setValue(array.toString());
			}
		}
	}

}
