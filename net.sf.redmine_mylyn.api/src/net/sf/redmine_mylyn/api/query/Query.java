package net.sf.redmine_mylyn.api.query;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.redmine_mylyn.api.exception.RedmineApiErrorException;
import net.sf.redmine_mylyn.api.model.Configuration;
import net.sf.redmine_mylyn.internal.api.Messages;

import org.apache.commons.httpclient.NameValuePair;

public class Query {
	
	private Map<String, QueryFilter> filterByQueryFieldValue = new LinkedHashMap<String, QueryFilter>();
	
	private List<NameValuePair> params;
	
	public void addFilter(QueryField queryField, CompareOperator operator) {
		addFilter(queryField, queryField, operator, null);
	}
	
	public void addFilter(QueryField queryField, CompareOperator operator, String value) {
		addFilter(queryField, queryField, operator, value);
	}

	public void addFilter(IQueryField queryField, QueryField definition, CompareOperator operator) {
		addFilter(queryField, definition, operator, null);
	}
	
	public void addFilter(IQueryField queryField, QueryField definition, CompareOperator operator, String value) {
		QueryFilter filter = filterByQueryFieldValue.get(queryField.getQueryValue());
		if (filter == null) {
			filter = new QueryFilter(queryField, definition);
			filter.setOperator(operator);
			filterByQueryFieldValue.put(queryField.getQueryValue(), filter);
		}
		filter.addValue(value);
		
		if(params!=null) {
			params.clear();
		}
	}
	
	public QueryFilter getQueryFilter(IQueryField queryField) {
		return filterByQueryFieldValue.get(queryField.getQueryValue());
	}
	
	public List<NameValuePair> getParams() throws RedmineApiErrorException {
		if(params==null) {
			params = new ArrayList<NameValuePair>();
		}
		
		if(params.isEmpty()) {
			for (QueryFilter queryFilter : filterByQueryFieldValue.values()) {
				queryFilter.appendParams(params);
			}
		}
		
		return params;
	}
	
	public String toUrl(String encoding) throws RedmineApiErrorException {
		if(encoding==null) {
			encoding="UTF-8"; //$NON-NLS-1$
		}
		StringBuilder builder = new StringBuilder();

		try {
			
			for (NameValuePair nvp : getParams()) {
				builder.append("&").append(nvp.getName()); //$NON-NLS-1$
				builder.append("=").append(URLEncoder.encode(nvp.getValue(), encoding)); //$NON-NLS-1$
			}
		} catch (UnsupportedEncodingException e) {
			throw new RedmineApiErrorException(Messages.ERRMSG_INVALID_ENCODING_X, e, encoding);
		}
		
		if(builder.length()>0) {
			builder.deleteCharAt(0);
			builder.insert(0, "?"); //$NON-NLS-1$
		}
		
		return builder.toString();
	}
	
	public static Query fromUrl(String url, String encoding, Configuration configuration) throws RedmineApiErrorException {
		if(encoding==null) {
			encoding="UTF-8"; //$NON-NLS-1$
		}
		if(url==null || url.indexOf("query_id=")<0 && url.indexOf('&')<0) { //$NON-NLS-1$
			return null;
		}

		List<NameValuePair> nvp = new ArrayList<NameValuePair>();
		
		if(url.charAt(0)=='?' && url.length()>=1) {
			url =  url.substring(1);
		}

		/* URL to NamedValuePairs */
		for(String part : url.split("&")) { //$NON-NLS-1$
			String[] namedValue = part.split("="); //$NON-NLS-1$
			if(namedValue.length!=2) {
				continue;
			}
			
			try {
				namedValue[1] = URLDecoder.decode(namedValue[1], encoding);
			} catch (UnsupportedEncodingException e) {
				throw new RedmineApiErrorException(Messages.ERRMSG_INVALID_ENCODING_X, e, encoding);
			}
			
			nvp.add(new NameValuePair(namedValue[0], namedValue[1]));
		}
		
		return fromNameValuePairs(nvp, configuration);
	}
	

	public static Query fromNameValuePairs(List<NameValuePair> pairs, Configuration configuration) {
		Query query = new Query();

		HashMap<String, CompareOperator> operators = new HashMap<String, CompareOperator>();
		HashMap<String, ArrayList<String>> values = new HashMap<String, ArrayList<String>>();
		
		for(NameValuePair nvp : pairs) {
			QueryFilter filter = QueryFilter.fromNameValuePair(nvp, configuration);
			if(filter!=null) {
				query.filterByQueryFieldValue.put(filter.getQueryField().getQueryValue(), filter);
				continue;
			}
			
			String name = QueryFilter.findNamefromNameValuePair(nvp);
			if(name==null) {
				continue;
			}
			
			CompareOperator operator = QueryFilter.findOperatorFromNameValuePair(nvp);
			if(operator!=null) {
				operators.put(name, operator);
				continue;
			}
				
			String value = QueryFilter.findValueFromNameValuePair(nvp);
			if(value!=null) {
				if(!values.containsKey(name)) {
					values.put(name, new ArrayList<String>());
				}
				values.get(name).add(value);
			}
		}
		

		for(Entry<String, CompareOperator> entry : operators.entrySet()) {
			if (query.filterByQueryFieldValue.containsKey(entry.getKey())) {
				query.filterByQueryFieldValue.get(entry.getKey()).setOperator(entry.getValue());
			} else if(values.containsKey(entry.getKey())){
				values.remove(entry.getKey());
			}
		}
		
		for(Entry<String, ArrayList<String>> entry : values.entrySet()) {
			QueryFilter filter = query.filterByQueryFieldValue.get(entry.getKey());
			for(String value : entry.getValue()) {
				filter.addValue(value);
			} 
		}

		return query;
	}

	public boolean isStoredQuery() {
		return getQueryFilter(QueryField.STOREDQUERY)!=null;
	}
}
