package net.sf.redmine_mylyn.api.query;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sf.redmine_mylyn.api.client.RedmineApiStatusException;

import org.apache.commons.httpclient.NameValuePair;

public class Query {

	private Map<QueryField, QueryFilter> filterBySearchField = new LinkedHashMap<QueryField, QueryFilter>();
	
	private List<NameValuePair> params;
	
	public void addFilter(QueryField queryField, CompareOperator operator) {
		addFilter(queryField, operator, null);
	}
	
	public void addFilter(QueryField queryField, CompareOperator operator, String value) {
		QueryFilter filter = filterBySearchField.get(queryField);
		if (filter == null) {
			filter = new QueryFilter(queryField);
			filter.setOperator(operator);
			filterBySearchField.put(queryField, filter);
		}
		filter.addValue(value);
	
		if(params!=null) {
			params.clear();
		}
	}
	
	public List<NameValuePair> getParams() throws RedmineApiStatusException {
		if(params==null) {
			params = new ArrayList<NameValuePair>();
		}
		
		if(params.isEmpty()) {
			for (QueryFilter queryFilter : filterBySearchField.values()) {
				queryFilter.appendParams(params);
			}
			//TODO CFs
		}
		
		return params;
	}

}
