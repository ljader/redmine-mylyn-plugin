package net.sf.redmine_mylyn.internal.api.parser.mock;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import net.sf.redmine_mylyn.api.model.Configuration;
import net.sf.redmine_mylyn.api.model.IssueStatus;
import net.sf.redmine_mylyn.api.model.Property;
import net.sf.redmine_mylyn.api.model.container.IssueStatuses;

@SuppressWarnings("serial")
public class ConfigurationMock extends Configuration {

	private IssueStatuses issueStatus;
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public IssueStatuses getIssueStatuses() {
		if (issueStatus==null) {
			issueStatus = new IssueStatuses();
			
			try {
				Method m = IssueStatuses.class.getDeclaredMethod("getModifiableList");
				m.setAccessible(true);
				List list = (List)m.invoke(issueStatus);
				
				Field id = Property.class.getDeclaredField("id");
				id.setAccessible(true);
				
				IssueStatus i = new IssueStatus();
				id.setInt(i, 1);
				i.setClosed(true);
				list.add(i);
				
				i = new IssueStatus();
				id.setInt(i, 2);
				i.setClosed(false);
				list.add(i);
				
			} catch (Exception e) {
				issueStatus=null;
				e.printStackTrace();
			}
		}
		
		return issueStatus;
	}
}
