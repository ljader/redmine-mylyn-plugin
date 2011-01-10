package net.sf.redmine_mylyn.internal.ui.query;

import java.text.MessageFormat;

import net.sf.redmine_mylyn.api.model.Property;
import net.sf.redmine_mylyn.api.model.Query;

import org.eclipse.jface.viewers.LabelProvider;

public class RedmineLabelProvider extends LabelProvider {
	
	private String title = null;
	
	public RedmineLabelProvider() {
		super();
	}

	public RedmineLabelProvider(String title) {
		this.title = title;
	}

	@Override
	public String getText(Object element) {
		if(element instanceof Query) {
			Query storedQuery = (Query)element;
			if(storedQuery.getProjectId()<1) {
				return MessageFormat.format("{0} (all projects)", storedQuery.getName());
			} else {
				return MessageFormat.format("{0} (Project {1})", storedQuery.getName(), storedQuery.getProjectId());
			}
		}
		
		
		if (element instanceof Property) {
			return ((Property)element).getName();
		}
		
		if(element==title) {
			return MessageFormat.format("<< {0} >>", title);
		}
		
		return super.getText(element);
	}

}
