package net.sf.redmine_mylyn.api.model.container;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.redmine_mylyn.api.model.Property;

public abstract class AbstractPropertyContainer<T extends Property> {

	protected Map<Integer, T> elementsMap;

	abstract protected List<T> getModifiableList();
	
	public List<T> getAll() {
		return Collections.unmodifiableList(getModifiableList());
	}
	
	public T get(int id) {
		return getMap().get(Integer.valueOf(id));
	}
	
	private Map<Integer, T> getMap() {
		if(elementsMap==null) {
			elementsMap = new HashMap<Integer, T>(getAll().size());
		}
		for (T element : getAll()) {
			elementsMap.put(Integer.valueOf(element.getId()), element);
		}
		return elementsMap; 
	}
}
