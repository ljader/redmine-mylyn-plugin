package net.sf.redmine_mylyn.api.model.container;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.redmine_mylyn.api.model.Property;

public abstract class AbstractPropertyContainer<T extends Property> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	protected Map<Integer, T> elementsMap;

	abstract protected List<T> getModifiableList();
	
	public boolean isEmpty() {
		return getModifiableList().isEmpty();
	}
	
	public List<T> getAll() {
		return Collections.unmodifiableList(getModifiableList());
	}
	
	public T getById(int id) {
		return getMap().get(Integer.valueOf(id));
	}

	public List<T> getById(int[] idlist) {
		if (idlist==null) {
			return new ArrayList<T>(0);
		}
		
		List<T> listed = new ArrayList<T>(idlist.length);
		T t;
		for (int id : idlist) {
			if ((t=getById(id))!=null) {
				listed.add(t);
			}
		}
		return listed;
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
