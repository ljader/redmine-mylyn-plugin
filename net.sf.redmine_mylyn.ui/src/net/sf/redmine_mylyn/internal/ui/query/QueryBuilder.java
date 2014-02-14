package net.sf.redmine_mylyn.internal.ui.query;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.redmine_mylyn.api.model.Configuration;
import net.sf.redmine_mylyn.api.model.CustomField;
import net.sf.redmine_mylyn.api.model.Property;
import net.sf.redmine_mylyn.api.query.CompareOperator;
import net.sf.redmine_mylyn.api.query.IQueryField;
import net.sf.redmine_mylyn.api.query.Query;
import net.sf.redmine_mylyn.api.query.QueryField;
import net.sf.redmine_mylyn.api.query.QueryFilter;
import net.sf.redmine_mylyn.core.IRedmineConstants;
import net.sf.redmine_mylyn.core.RedmineUtil;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.widgets.Text;

public class QueryBuilder {

	static Query buildQuery(Map<IQueryField, ComboViewer> searchOperators,  Map<IQueryField, Text> textValues, Map<IQueryField, StructuredViewer> structuredValues) {
		Query query = new Query();

		buildStructuredQueryPart(query, searchOperators, structuredValues);
		buildTextQueryPart(query, searchOperators, textValues);
		
		return query;
	}
	
	private static void buildStructuredQueryPart(Query query, Map<IQueryField, ComboViewer> operators,  Map<IQueryField, StructuredViewer> values) {
		for (Entry<IQueryField, StructuredViewer> entry : values.entrySet()) {
			IQueryField queryField = entry.getKey();
			StructuredViewer viewer = entry.getValue();
			ComboViewer opCombo = operators.get(queryField);

			QueryField definition = queryField instanceof CustomField ? ((CustomField) queryField)
					.getQueryField() : (QueryField) queryField;
			
			IStructuredSelection selection = (IStructuredSelection)opCombo.getSelection();
			if (selection.getFirstElement() instanceof CompareOperator) {
				CompareOperator operator = (CompareOperator)selection.getFirstElement();
				
				selection = (IStructuredSelection)viewer.getSelection();
				if (selection.isEmpty()) {
					query.addFilter(queryField, definition, operator, ""); //$NON-NLS-1$
				} else {
					Iterator<?> valIterator = selection.iterator();
					while(valIterator.hasNext()) {
						Object obj = valIterator.next();
						if  (obj instanceof Property) {
							Property property = (Property)obj; 
							query.addFilter(queryField, definition, operator, ""+property.getId()); //$NON-NLS-1$
						} else {
							query.addFilter(queryField, definition, operator, obj.toString());
						}
					}
				}
			}
		}
	}

	private static void buildTextQueryPart(Query query, Map<IQueryField, ComboViewer> operators,  Map<IQueryField, Text> values) {
		for (Entry<IQueryField, Text> entry : values.entrySet()) {
			IQueryField queryField = entry.getKey();
			Text text = entry.getValue();
			ComboViewer opCombo = operators.get(queryField);
			
			IStructuredSelection selection = (IStructuredSelection)opCombo.getSelection();
			if (selection.getFirstElement() instanceof CompareOperator) {
				CompareOperator operator = (CompareOperator)selection.getFirstElement();
				if (queryField instanceof CustomField) {
					CustomField customField = (CustomField)queryField;
					if(customField.getFieldFormat()==CustomField.Format.BOOL) {
						query.addFilter(customField, QueryField.BOOLEAN_TYPE, operator, IRedmineConstants.BOOLEAN_TRUE_SUBMIT_VALUE);
					} else {
						QueryField definition = customField.getQueryField();
						query.addFilter(customField, definition, operator, text.getText().trim());
					}
				} else {
					query.addFilter((QueryField)queryField, operator, text.getText().trim());
				}
			}
		}
	}
	
	public static void restoreTextQueryPart(Query query, Configuration configuration, Map<IQueryField, ComboViewer> operators,  Map<IQueryField, Text> values) {
		for(Entry<IQueryField, Text> entry : values.entrySet()) {
			IQueryField queryField = entry.getKey();
			QueryFilter queryFilter = query.getQueryFilter(queryField);
			if(queryFilter==null) {
				continue;
			}
			
			Text control = entry.getValue();
			ComboViewer operatorCombo = operators.get(queryField);
			
			operatorCombo.setSelection(new StructuredSelection(queryFilter.getOperator()));
			if(queryFilter.getOperator().isValueBased()) {
				control.setEnabled(true);
				
				List<String> filterValues = queryFilter.getValues();
				if(filterValues.size()>0) {
					control.setText(filterValues.get(0));
				}
			}
		}
	}

	public static void restoreStructuredQueryPart(Query query, Configuration configuration, Map<IQueryField, ComboViewer> operators,  Map<IQueryField, StructuredViewer> values) {
		for(Entry<IQueryField, StructuredViewer> entry : values.entrySet()) {
			IQueryField queryField = entry.getKey();
			QueryFilter queryFilter = query.getQueryFilter(queryField);
			if(queryFilter==null) {
				continue;
			}
			
			StructuredViewer viewer = entry.getValue();
			ComboViewer operatorCombo = operators.get(queryField);
			
			operatorCombo.setSelection(new StructuredSelection(queryFilter.getOperator()));
			if(queryFilter.getOperator().isValueBased()) {
				viewer.getControl().setEnabled(true);
				
				List<String> filterValues = queryFilter.getValues();
				if(queryField instanceof QueryField) {
					List<Object> properties = new ArrayList<Object>(filterValues.size());
					for(String value : filterValues) {
						if(RedmineUtil.isInteger(value)) {
							Property property = queryFieldValue2Property(RedmineUtil.parseIntegerId(value), (QueryField)queryField, configuration);
							if(property!=null) {
								properties.add(property);
							}
						} else {
							properties.add(value);
						}
						viewer.setSelection(new StructuredSelection(properties));
					}
				} else {
					viewer.setSelection(new StructuredSelection(filterValues));
				}
			}
			
		}
	}

	private static Property queryFieldValue2Property(int propertyId, QueryField queryField, Configuration configuration) {
		switch (queryField) {
		case PROJECT:
			return configuration.getProjects().getById(propertyId);
		case STATUS:
			return configuration.getIssueStatuses().getById(propertyId);
		case PRIORITY:
			return configuration.getIssuePriorities().getById(propertyId);
		case TRACKER:
			return configuration.getTrackers().getById(propertyId);
		case FIXED_VERSION:
			return configuration.getVersions().getById(propertyId);
		case CATEGORY:
			return configuration.getIssueCategories().getById(propertyId);
		case AUTHOR:
		case ASSIGNED_TO:
			return configuration.getUsers().getById(propertyId);
		
		default:
			return null;
		}
	}
}
