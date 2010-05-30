package net.sf.redmine_mylyn.api.model.container;

import java.util.Collections;
import java.util.List;

import net.sf.redmine_mylyn.api.model.SortedProperty;

public abstract class AbstractSortedPropertyContainer<T extends SortedProperty> extends AbstractPropertyContainer<T> {

	@Override
	public List<T> getAll() {
		Collections.sort(getModifiableList());
		return Collections.unmodifiableList(getModifiableList());
	}

}
