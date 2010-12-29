package net.sf.redmine_mylyn.api.query;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public enum QueryField implements IQueryField {
	
	LIST_TYPE("LIST_BASED", CompareOperator.IS, CompareOperator.IS_NOT, CompareOperator.NONE, CompareOperator.ALL),
	TEXT_TYPE("TEXT_BASED", CompareOperator.IS, CompareOperator.IS_NOT, CompareOperator.CONTAINS, CompareOperator.CONTAINS_NOT),
	DATE_TYPE("DATE_BASED", CompareOperator.DAY_AGO_MORE_THEN,
			CompareOperator.DAY_AGO_LESS_THEN, CompareOperator.DAY_AGO,
			CompareOperator.TODAY, CompareOperator.CURRENT_WEEK,
			CompareOperator.DAY_LATER, CompareOperator.DAY_LATER_LESS_THEN,
			CompareOperator.DAY_LATER_MORE_THEN),
	BOOLEAN_TYPE("BOOLEAN_BASED", CompareOperator.IS, CompareOperator.IS_NOT),
	
	//TODO not tested
	PROJECT("project_id", CompareOperator.IS, CompareOperator.IS_NOT),

	STATUS("status_id", CompareOperator.OPEN, CompareOperator.IS, CompareOperator.IS_NOT, CompareOperator.CLOSED, CompareOperator.ALL),
	PRIORITY("priority_id", CompareOperator.IS, CompareOperator.IS_NOT),
	TRACKER("tracker_id", CompareOperator.IS, CompareOperator.IS_NOT),
	FIXED_VERSION("fixed_version_id", CompareOperator.IS, CompareOperator.IS_NOT, CompareOperator.NONE, CompareOperator.ALL),
	ASSIGNED_TO("assigned_to_id", CompareOperator.IS, CompareOperator.IS_NOT, CompareOperator.NONE, CompareOperator.ALL),
	AUTHOR("author_id", CompareOperator.IS, CompareOperator.IS_NOT),
	CATEGORY("category_id", 
			CompareOperator.IS,	
			CompareOperator.IS_NOT, 
			CompareOperator.ALL, 
			CompareOperator.NONE),
	SUBJECT("subject",CompareOperator.CONTAINS, CompareOperator.CONTAINS_NOT),
	DATE_CREATED("created_on", CompareOperator.DAY_AGO_MORE_THEN, 
			CompareOperator.DAY_AGO_LESS_THEN, CompareOperator.DAY_AGO,
			CompareOperator.TODAY, CompareOperator.CURRENT_WEEK),
	DATE_UPDATED("updated_on", CompareOperator.DAY_AGO_MORE_THEN,
			CompareOperator.DAY_AGO_LESS_THEN, CompareOperator.DAY_AGO,
			CompareOperator.TODAY, CompareOperator.CURRENT_WEEK),
	DATE_START("start_date", CompareOperator.DAY_AGO_MORE_THEN,
			CompareOperator.DAY_AGO_LESS_THEN, CompareOperator.DAY_AGO,
			CompareOperator.TODAY, CompareOperator.CURRENT_WEEK,
			CompareOperator.DAY_LATER, CompareOperator.DAY_LATER_LESS_THEN,
			CompareOperator.DAY_LATER_MORE_THEN),
	DATE_DUE("start_date",CompareOperator.DAY_AGO_MORE_THEN,
			CompareOperator.DAY_AGO_LESS_THEN, CompareOperator.DAY_AGO,
			CompareOperator.TODAY, CompareOperator.CURRENT_WEEK,
			CompareOperator.DAY_LATER, CompareOperator.DAY_LATER_LESS_THEN,
			CompareOperator.DAY_LATER_MORE_THEN),
	DONE_RATIO("done_ratio",CompareOperator.GTE, CompareOperator.LTE);

	final static EnumSet<QueryField> ABSTRACT = EnumSet.of(BOOLEAN_TYPE, LIST_TYPE, TEXT_TYPE, DATE_TYPE);
	final static EnumSet<QueryField> REQUIRED = EnumSet.of(STATUS);
	final static EnumSet<QueryField> CROSS_PROJECT = EnumSet.complementOf(EnumSet.of(FIXED_VERSION, CATEGORY));
	final static EnumSet<QueryField> LIST_TYPES = EnumSet.of(PROJECT, TRACKER, STATUS, PRIORITY, FIXED_VERSION, ASSIGNED_TO, AUTHOR, CATEGORY, LIST_TYPE);
	final static EnumSet<QueryField> DATE_TYPES = EnumSet.of(DATE_CREATED, DATE_UPDATED, DATE_START, DATE_DUE, DATE_TYPE);
	final static EnumSet<QueryField> PERSON_TYPES = EnumSet.of(AUTHOR, ASSIGNED_TO);

	public final static EnumSet<QueryField> ORDERED = EnumSet.of(SUBJECT, DATE_CREATED, DATE_UPDATED, DATE_START, DATE_DUE, DONE_RATIO, PROJECT, TRACKER, STATUS, PRIORITY, FIXED_VERSION, ASSIGNED_TO, AUTHOR, CATEGORY);

	public final static String VALUE_PERSON_ME = "me";

	private final String fieldName;
	
	private final List<CompareOperator> operators;
	
	QueryField(String fieldName, CompareOperator... operators) {
		this.fieldName = fieldName;
		
		this.operators = new ArrayList<CompareOperator>(operators.length);
		for (CompareOperator compareOperator : operators) {
			this.operators.add(compareOperator);
		}
	}

	boolean containsOperator(CompareOperator operator) {
		return operators.contains(operator);
	}

	public List<CompareOperator> getCompareOperators() {
		return operators;
	}
	
	public String getQueryValue() {
		return fieldName;
	}
	
	public boolean isDateType() {
		return DATE_TYPES.contains(this);
	}

	public boolean isListType() {
		return LIST_TYPES.contains(this);
	}
	
	public boolean isBooleanType() {
		return this==BOOLEAN_TYPE;
	}

	public boolean isPersonType() {
		return PERSON_TYPES.contains(this);
	}
	
	public boolean isRequired() {
		return REQUIRED.contains(this);
	}
	
	@Override
	public String getLabel() {
		return fieldName;
	}

	@Override
	public boolean isCrossProjectUsable() {
		return CROSS_PROJECT.contains(this);
	}
	
	static QueryField fromQueryValue(String value) {
		for(QueryField field : QueryField.values()) {
			if (field.getQueryValue().equals(value)) {
				return field;
			}
		}
		return null;
	}
}
