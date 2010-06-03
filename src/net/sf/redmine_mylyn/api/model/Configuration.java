package net.sf.redmine_mylyn.api.model;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import net.sf.redmine_mylyn.api.client.RedmineApiPlugin;
import net.sf.redmine_mylyn.api.client.RedmineApiStatusException;
import net.sf.redmine_mylyn.api.model.container.AbstractPropertyContainer;
import net.sf.redmine_mylyn.api.model.container.IssueCategories;
import net.sf.redmine_mylyn.api.model.container.IssuePriorities;
import net.sf.redmine_mylyn.api.model.container.IssueStatuses;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class Configuration implements Serializable{

	private static final long serialVersionUID = 1L;

	private IssueStatuses issueStatuses;

	private IssueCategories issueCategories;
	
	private IssuePriorities issuePriorities;

	public void setPropertyContainer(AbstractPropertyContainer<? extends Property> container) throws RedmineApiStatusException {
		try {
			for (Field field : getClass().getDeclaredFields()) {
				if (field.getType().equals(container.getClass())) {
					field.set(this, container);
				}
			}
		} catch (Exception e) {
			IStatus status = new Status(IStatus.ERROR, RedmineApiPlugin.PLUGIN_ID, "Updating Attributes failed", e);
			throw new RedmineApiStatusException(status);
		}
	}
	
	public void copy(Configuration conf) throws RedmineApiStatusException {
		try {
			for (Field field : getClass().getDeclaredFields()) {
				if ((field.getModifiers()&Modifier.STATIC)!=Modifier.STATIC) {
					field.set(this, field.get(conf));
				}
			}
		} catch (Exception e) {
			IStatus status = new Status(IStatus.ERROR, RedmineApiPlugin.PLUGIN_ID, "Updating Attributes failed", e);
			throw new RedmineApiStatusException(status);
		}
	}

	public IssueStatuses getIssueStatuses() {
		if(issueStatuses==null) {
			issueStatuses = new IssueStatuses();
		}
		return issueStatuses;
	}

	public IssueCategories getIssueCategories() {
		if(issueCategories==null) {
			issueCategories = new IssueCategories();
		}
		return issueCategories;
	}
	
	public IssuePriorities getIssuePriorities() {
		if(issuePriorities==null) {
			issuePriorities = new IssuePriorities();
		}
		return issuePriorities;
	}
	
	
}
