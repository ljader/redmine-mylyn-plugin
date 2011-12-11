package net.sf.redmine_mylyn.api.query;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.redmine_mylyn.api.RedmineApiPlugin;
import net.sf.redmine_mylyn.api.exception.RedmineApiErrorException;
import net.sf.redmine_mylyn.api.model.Configuration;
import net.sf.redmine_mylyn.api.model.CustomField;
import net.sf.redmine_mylyn.internal.api.Messages;

import org.apache.commons.httpclient.NameValuePair;

public class QueryFilter {

	private final IQueryField queryField;

	private final QueryField definition;
	
	private CompareOperator operator = CompareOperator.IS;
	
	private List<String> values = new ArrayList<String>();

	public final static String CUSTOM_FIELD_PREFIX = "cf_"; //$NON-NLS-1$
	
	private final static Pattern FIND_QUERY_NAME_OPARATOR_PATTERN = Pattern.compile("^operators\\[(\\w+)\\]$"); //$NON-NLS-1$
	private final static Pattern FIND_QUERY_NAME_VALUES_PATTERN = Pattern.compile("^values\\[(\\w+)\\]\\[\\]$"); //$NON-NLS-1$
	
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
	
	public CompareOperator getOperator() {
		return operator;
	}
	
	public List<String> getValues() {
		return values;
	}
	
	void appendParams(List<NameValuePair> parts) throws RedmineApiErrorException {
		if(queryField==null || definition==null || operator==null || !definition.containsOperator(operator)) {
			return;
		}
		
		try {
			//TODO check tests 
			//TODO use QueryField.IValidator
			//TODO skip empty values???
			if(operator.isValueBased()) {
				if(values.size()<1) {
					return;
				}
				
				//Must be: Single Value >= 0 ???
				if(definition.isDateType() && (values.size()>1 || (Integer.parseInt(values.get(0)) < 0))) {
					return;
				}
				//Must be: Single Value 0 or 1
				if(definition==QueryField.BOOLEAN_TYPE) {
					int v =  Integer.parseInt(values.get(0));
					if(v<0 || v>1) {
						return;
					}
				}
				
				//Must be: Single Value 0-100
				if(definition==QueryField.DONE_RATIO) {
					int v =  Integer.parseInt(values.get(0));
					if(v<0 || v>100) {
						return;
					}
				}
			}
		} catch (NumberFormatException e) {
			throw new RedmineApiErrorException(Messages.ERRMSG_QUERY_FIELD_INVALID_INTEGER_X_X, e, ""+values.get(0), queryField.getQueryValue()); //$NON-NLS-1$
		}
		
		if((queryField==QueryField.PROJECT || queryField==QueryField.STOREDQUERY) && values.size()==1 && operator==CompareOperator.IS) {
			parts.add(new NameValuePair(queryField.getQueryValue(), values.get(0)));
		} else {
			appendFieldAndOperator(parts);
			appendValues(parts);
		}
	}
	
	private void appendFieldAndOperator(List<NameValuePair> parts) {
		parts.add(new NameValuePair("fields[]", queryField.getQueryValue())); //$NON-NLS-1$
		parts.add(new NameValuePair(String.format("operators[%s]", queryField.getQueryValue()), operator.getQueryValue())); //$NON-NLS-1$
	}

	private void appendValues(List<NameValuePair> parts) {
		if (values.size() > 0) {
			for (String value : values) {
				parts.add(new NameValuePair(String.format("values[%s][]", queryField.getQueryValue()), value)); //$NON-NLS-1$
			}
		} else {
			parts.add(new NameValuePair(String.format("values[%s][]", queryField.getQueryValue()), "")); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	static QueryFilter fromNameValuePair(NameValuePair nvp, Configuration configuration) {
		QueryFilter filter = null;
		
		if(nvp.getName().equals("fields[]")) { //$NON-NLS-1$
			if(nvp.getValue().startsWith(CUSTOM_FIELD_PREFIX)) {
				try {
					int cfId = Integer.parseInt(nvp.getValue().substring(3));
					CustomField customField = configuration.getCustomFields().getById(cfId);
					if(customField!=null && customField.getQueryField()!=null) {
						filter = new QueryFilter(customField, customField.getQueryField());
					}
				} catch (NumberFormatException e){
					RedmineApiPlugin.getLogService(QueryFilter.class).error(e, Messages.ERRMSG_CANT_RESTORE_QUERYFILTER_INVALID_FIELDID_X, nvp.getValue().substring(3));
				}
				
			} else {
				QueryField queryField = QueryField.fromQueryValue(nvp.getValue());
				if(queryField!=null) {
					filter = new QueryFilter(queryField);
				}
			}
		} else if(nvp.getName().equals(QueryField.PROJECT.getQueryValue())) {
			filter = new QueryFilter(QueryField.PROJECT);
			filter.setOperator(CompareOperator.IS);
			filter.addValue(nvp.getValue());
		} else if(nvp.getName().equals(QueryField.STOREDQUERY.getQueryValue())) {
			filter = new QueryFilter(QueryField.STOREDQUERY);
			filter.setOperator(CompareOperator.IS);
			filter.addValue(nvp.getValue());
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
