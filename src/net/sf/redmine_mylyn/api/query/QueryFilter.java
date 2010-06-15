package net.sf.redmine_mylyn.api.query;

import java.util.ArrayList;
import java.util.List;

import net.sf.redmine_mylyn.api.client.RedmineApiPlugin;
import net.sf.redmine_mylyn.api.client.RedmineApiStatusException;

import org.apache.commons.httpclient.NameValuePair;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class QueryFilter {

	private QueryField queryField;
	
	private CompareOperator operator = CompareOperator.IS;
	
	private List<String> values = new ArrayList<String>();

	QueryFilter(QueryField queryField) {
		this.queryField = queryField;
	}
	
	void addValue(String value) {
		if (operator!=null && operator.isValueBased()) {
			values.add(value);
		}
	}

	void setOperator(CompareOperator operator) {
		if (queryField.containsOperator(operator)) {
			this.operator = operator;
		} else {
			this.operator = null;
		}
		values.clear();
	}

	void appendParams(List<NameValuePair> parts) throws RedmineApiStatusException {
		if(queryField==null || operator==null || !queryField.containsOperator(operator)) {
			return;
		}
		
		try {
			if(operator.isValueBased()) {
				if(values.size()<1) {
					return;
				}
				
				if(queryField.isDateType() && values.size()>1 || (Integer.parseInt(values.get(0)) < 1)) {
					return;
				}
				
				if(queryField==QueryField.BOOLEAN_TYPE) {
					int v =  Integer.parseInt(values.get(0));
					if(v<0 || v>1) {
						return;
					}
				}
				
				if(queryField==QueryField.DONE_RATIO) {
					int v =  Integer.parseInt(values.get(0));
					if(v<0 || v>100) {
						return;
					}
				}
			}
		} catch (NumberFormatException e) {
			IStatus status = new Status(IStatus.ERROR, RedmineApiPlugin.PLUGIN_ID, "Invalid value of Query-Field", e);
			throw new RedmineApiStatusException(status);
		}
		
		appendFieldAndOperator(parts);
		appendValues(parts);
	}
	
	private void appendFieldAndOperator(List<NameValuePair> parts) {
		parts.add(new NameValuePair("fields[]", queryField.getQueryValue()));
		parts.add(new NameValuePair(String.format("operators[%s]", queryField.getQueryValue()), operator.getQueryValue()));
	}

	private void appendValues(List<NameValuePair> parts) {
		if (values.size() > 0) {
			for (String value : values) {
				parts.add(new NameValuePair(String.format("values[%s][]", queryField.getQueryValue()), value));
			}
		} else {
			parts.add(new NameValuePair(String.format("values[%s][]", queryField.getQueryValue()), ""));
		}
	}

}
