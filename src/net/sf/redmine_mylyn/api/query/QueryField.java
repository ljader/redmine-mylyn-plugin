package net.sf.redmine_mylyn.api.query;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public enum QueryField implements IQueryField {

	//TODO generische ... mit enumset lösen
//	LIST_BASED("LIST_BASED", true, false, true, CompareOperator.IS, CompareOperator.IS_NOT, CompareOperator.NONE, CompareOperator.ALL),
//	TEXT_BASED("TEXT_BASED", true, false, false, CompareOperator.IS, CompareOperator.IS_NOT, CompareOperator.CONTAINS, CompareOperator.CONTAINS_NOT),
//	DATE_BASED("DATE_BASED", true, false, false, CompareOperator.DAY_AGO_MORE_THEN,
//			CompareOperator.DAY_AGO_LESS_THEN, CompareOperator.DAY_AGO,
//			CompareOperator.TODAY, CompareOperator.CURRENT_WEEK,
//			CompareOperator.DAY_LATER, CompareOperator.DAY_LATER_LESS_THEN,
//			CompareOperator.DAY_LATER_MORE_THEN),
	
	BOOLEAN_TYPE("BOOLEAN_BASED", CompareOperator.IS, CompareOperator.IS_NOT),
	
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

	final static EnumSet<QueryField> ABSTRACT = EnumSet.of(BOOLEAN_TYPE);
	final static EnumSet<QueryField> REQUIRED = EnumSet.of(STATUS);
	final static EnumSet<QueryField> CROSS_PROJECT = EnumSet.complementOf(EnumSet.of(FIXED_VERSION, CATEGORY));
	final static EnumSet<QueryField> LIST_TYPE = EnumSet.of(STATUS, PRIORITY, TRACKER, FIXED_VERSION, ASSIGNED_TO, AUTHOR, CATEGORY);
	final static EnumSet<QueryField> DATE_TYPE = EnumSet.of(DATE_CREATED, DATE_UPDATED, DATE_START, DATE_DUE);
	

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

	public String getQueryValue() {
		return fieldName;
	}
	
	public boolean isDateType() {
		return DATE_TYPE.contains(this);
	}

	public boolean isListType() {
		return LIST_TYPE.contains(this);
	}

	@Override
	public String getLabel() {
		return fieldName;
	}

	@Override
	public boolean isCrossProjectUsable() {
		return CROSS_PROJECT.contains(this);
	}
	
	
}
