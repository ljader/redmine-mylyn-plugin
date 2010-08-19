package net.sf.redmine_mylyn.internal.ui.query;

import net.sf.redmine_mylyn.api.model.Property;

import org.eclipse.jface.viewers.LabelProvider;

public class RedmineLabelProvider extends LabelProvider {
	
	@Override
	public String getText(Object element) {
		if (element instanceof Property) {
			return ((Property)element).getName();
		}
		return super.getText(element);
	}

}
