package net.sf.redmine_mylyn.api.query;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.redmine_mylyn.api.client.RedmineApiPlugin;
import net.sf.redmine_mylyn.api.client.RedmineApiStatusException;
import net.sf.redmine_mylyn.api.model.Configuration;
import net.sf.redmine_mylyn.api.model.CustomField;

import org.apache.commons.httpclient.NameValuePair;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class QueryFilter {

	private final IQueryField queryField;

	private final QueryField definition;
	
	private CompareOperator operator = CompareOperator.IS;
	
	private List<String> values = new ArrayList<String>();

	public final static String CUSTOM_FIELD_PREFIX = "cf_";
	
	private final static Pattern FIND_QUERY_NAME_OPARATOR_PATTERN = Pattern.compile("^operators\\[(\\w+)\\]$");
	private final static Pattern FIND_QUERY_NAME_VALUES_PATTERN = Pattern.compile("^values\\[(\\w+)\\]\\[\\]$");
	
	QueryFilter(QueryField queryField) {
		this(queryField, queryField);
	}
	
	QueryFilter(IQueryField queryField, QueryField definition) {
		this.queryField = queryField;
		this.definition = definition;
	}
	
	void addValue(String value) {
		if (operator!=null && operator.isValueBased()) {
			values.add(value);
		}
	}

	void setOperator(CompareOperator operator) {
		if (definition.containsOperator(operator)) {
			this.operator = operator;
		} else {
			this.operator = null;
		}
		values.clear();
	}

	IQueryField getQueryField() {
		return queryField;
	}
	
	void appendParams(List<NameValuePair> parts) throws RedmineApiStatusException {
		if(queryField==null || definition==null || operator==null || !definition.containsOperator(operator)) {
			return;
		}
		
		try {
			if(operator.isValueBased()) {
				if(values.size()<1) {
					return;
				}
				
				if(definition.isDateType() && values.size()>1 || (Integer.parseInt(values.get(0)) < 1)) {
					return;
				}
				
				if(definition==QueryField.BOOLEAN_TYPE) {
					int v =  Integer.parseInt(values.get(0));
					if(v<0 || v>1) {
						return;
					}
				}
				
				if(definition==QueryField.DONE_RATIO) {
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

	static QueryFilter fromNameValuePair(NameValuePair nvp, Configuration configuration) {
		QueryFilter filter = null;
		
		if(nvp.getName().equals("fields[]")) {
			if(nvp.getValue().startsWith(CUSTOM_FIELD_PREFIX)) {
				try {
					int cfId = Integer.parseInt(nvp.getValue().substring(3));
					CustomField customField = configuration.getCustomFields().getById(cfId);
					if(customField!=null && customField.getQueryField()!=null) {
						filter = new QueryFilter(customField, customField.getQueryField());
					}
				} catch (NumberFormatException e){
					//TODO log
				}
				
			} else {
				QueryField queryField = QueryField.fromQueryValue(nvp.getValue());
				if(queryField!=null) {
					filter = new QueryFilter(queryField);
				}
			}
		}
		
		return filter;
	}

	static CompareOperator findOperatorFromNameValuePair(NameValuePair nvp) {
		CompareOperator operator = null;
		if (FIND_QUERY_NAME_OPARATOR_PATTERN.matcher(nvp.getName()).matches()) {
			operator = CompareOperator.fromQueryValue(nvp.getValue());
		}
		return operator;
	}
	
	static String findValueFromNameValuePair(NameValuePair nvp) {
		if (FIND_QUERY_NAME_VALUES_PATTERN.matcher(nvp.getName()).matches()) {
			return nvp.getValue();
		}
		return null;
	}
	
	static String findNamefromNameValuePair(NameValuePair nvp) {
		Matcher matcher = FIND_QUERY_NAME_OPARATOR_PATTERN.matcher(nvp.getName());
		if (matcher.matches()) {
			return matcher.group(1);
		}
		matcher = FIND_QUERY_NAME_VALUES_PATTERN.matcher(nvp.getName());
		if (matcher.matches()) {
			return matcher.group(1);
		}
		return null;
	}
}
