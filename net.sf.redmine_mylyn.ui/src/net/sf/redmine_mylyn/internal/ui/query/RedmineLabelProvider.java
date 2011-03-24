package net.sf.redmine_mylyn.internal.ui.query;

import java.text.MessageFormat;

import net.sf.redmine_mylyn.api.model.Property;
import net.sf.redmine_mylyn.api.model.Query;
import net.sf.redmine_mylyn.internal.ui.Messages;

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
				return MessageFormat.format(Messages.LBL_X_ALL_PROJECTS, storedQuery.getName());
			} else {
				return MessageFormat.format(Messages.LBL_X_PROJECT_X, storedQuery.getName(), storedQuery.getProjectId());
			}
		}
		
		
		if (element instanceof Property) {
			return ((Property)element).getName();
		}
		
		if(element==title) {
			return MessageFormat.format(Messages.LBL_SPECIAL_QUERY_PARAM_X, title);
		}
		
		return super.getText(element);
	}

}
