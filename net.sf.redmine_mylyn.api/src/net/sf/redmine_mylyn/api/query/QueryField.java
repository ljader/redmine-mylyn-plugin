package net.sf.redmine_mylyn.api.query;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public enum QueryField implements IQueryField {
	
	LIST_TYPE("LIST_BASED", CompareOperator.IS, CompareOperator.IS_NOT, CompareOperator.NONE, CompareOperator.ALL), //$NON-NLS-1$
	TEXT_TYPE("TEXT_BASED", CompareOperator.IS, CompareOperator.IS_NOT, CompareOperator.CONTAINS, CompareOperator.CONTAINS_NOT), //$NON-NLS-1$
	INT_TYPE("TEXT_BASED", new IntegerValidator(), CompareOperator.IS, CompareOperator.IS_NOT, CompareOperator.CONTAINS, CompareOperator.CONTAINS_NOT), //$NON-NLS-1$
	FLOAT_TYPE("TEXT_BASED", new FloatValidator(), CompareOperator.IS, CompareOperator.IS_NOT, CompareOperator.CONTAINS, CompareOperator.CONTAINS_NOT), //$NON-NLS-1$
	DATE_TYPE("DATE_BASED", new DateValidator(),  //$NON-NLS-1$
			CompareOperator.DAY_AGO_MORE_THEN,
			CompareOperator.DAY_AGO_LESS_THEN, CompareOperator.DAY_AGO,
			CompareOperator.TODAY, CompareOperator.CURRENT_WEEK,
			CompareOperator.DAY_LATER, CompareOperator.DAY_LATER_LESS_THEN,
			CompareOperator.DAY_LATER_MORE_THEN),
	BOOLEAN_TYPE("BOOLEAN_BASED", CompareOperator.IS, CompareOperator.IS_NOT), //$NON-NLS-1$
	
	//TODO not tested
	PROJECT("project_id", CompareOperator.IS, CompareOperator.IS_NOT), //$NON-NLS-1$
	STOREDQUERY("query_id", CompareOperator.IS), //$NON-NLS-1$

	//TODO use RedmineApiIssueProperty instead of status_id, ...
	STATUS("status_id", CompareOperator.OPEN, CompareOperator.IS, CompareOperator.IS_NOT, CompareOperator.CLOSED, CompareOperator.ALL), //$NON-NLS-1$
	PRIORITY("priority_id", CompareOperator.IS, CompareOperator.IS_NOT), //$NON-NLS-1$
	TRACKER("tracker_id", CompareOperator.IS, CompareOperator.IS_NOT), //$NON-NLS-1$
	FIXED_VERSION("fixed_version_id", CompareOperator.IS, CompareOperator.IS_NOT, CompareOperator.NONE, CompareOperator.ALL), //$NON-NLS-1$
	ASSIGNED_TO("assigned_to_id", CompareOperator.IS, CompareOperator.IS_NOT, CompareOperator.NONE, CompareOperator.ALL), //$NON-NLS-1$
	AUTHOR("author_id", CompareOperator.IS, CompareOperator.IS_NOT), //$NON-NLS-1$
	CATEGORY("category_id",  //$NON-NLS-1$
			CompareOperator.IS,	
			CompareOperator.IS_NOT, 
			CompareOperator.ALL, 
			CompareOperator.NONE),
	SUBJECT("subject",CompareOperator.CONTAINS, CompareOperator.CONTAINS_NOT), //$NON-NLS-1$
	DATE_CREATED("created_on", new DateValidator(), //$NON-NLS-1$
			CompareOperator.DAY_AGO_MORE_THEN, 
			CompareOperator.DAY_AGO_LESS_THEN, CompareOperator.DAY_AGO,
			CompareOperator.TODAY, CompareOperator.CURRENT_WEEK),
	DATE_UPDATED("updated_on", new DateValidator(), //$NON-NLS-1$
			CompareOperator.DAY_AGO_MORE_THEN,
			CompareOperator.DAY_AGO_LESS_THEN, CompareOperator.DAY_AGO,
			CompareOperator.TODAY, CompareOperator.CURRENT_WEEK),
	DATE_START("start_date", new DateValidator(), //$NON-NLS-1$
			CompareOperator.DAY_AGO_MORE_THEN,
			CompareOperator.DAY_AGO_LESS_THEN, CompareOperator.DAY_AGO,
			CompareOperator.TODAY, CompareOperator.CURRENT_WEEK,
			CompareOperator.DAY_LATER, CompareOperator.DAY_LATER_LESS_THEN,
			CompareOperator.DAY_LATER_MORE_THEN),
	DATE_DUE("due_date",new DateValidator(), //$NON-NLS-1$
			CompareOperator.DAY_AGO_MORE_THEN,
			CompareOperator.DAY_AGO_LESS_THEN, CompareOperator.DAY_AGO,
			CompareOperator.TODAY, CompareOperator.CURRENT_WEEK,
			CompareOperator.DAY_LATER, CompareOperator.DAY_LATER_LESS_THEN,
			CompareOperator.DAY_LATER_MORE_THEN),
	DONE_RATIO("done_ratio", new DoneRatioValidator(), CompareOperator.GTE, CompareOperator.LTE); //$NON-NLS-1$

	final static EnumSet<QueryField> ABSTRACT = EnumSet.of(BOOLEAN_TYPE, LIST_TYPE, TEXT_TYPE, DATE_TYPE);
	final static EnumSet<QueryField> REQUIRED = EnumSet.of(STATUS);
	final static EnumSet<QueryField> CROSS_PROJECT = EnumSet.complementOf(EnumSet.of(FIXED_VERSION, CATEGORY));
	final static EnumSet<QueryField> LIST_TYPES = EnumSet.of(PROJECT, TRACKER, STATUS, PRIORITY, FIXED_VERSION, ASSIGNED_TO, AUTHOR, CATEGORY, LIST_TYPE);
	final static EnumSet<QueryField> DATE_TYPES = EnumSet.of(DATE_CREATED, DATE_UPDATED, DATE_START, DATE_DUE, DATE_TYPE);
	final static EnumSet<QueryField> PERSON_TYPES = EnumSet.of(AUTHOR, ASSIGNED_TO);

	public final static EnumSet<QueryField> ORDERED = EnumSet.of(SUBJECT, DATE_CREATED, DATE_UPDATED, DATE_START, DATE_DUE, DONE_RATIO, PROJECT, TRACKER, STATUS, PRIORITY, FIXED_VERSION, ASSIGNED_TO, AUTHOR, CATEGORY);

	public final static String VALUE_PERSON_ME = "me"; //$NON-NLS-1$

	private final String fieldName;
	
	private final List<CompareOperator> operators;
	
	private final IValidator validator;
	
	QueryField(String fieldName, CompareOperator... operators) {
		this(fieldName, null, operators);
	}

	QueryField(String fieldName, IValidator validator, CompareOperator... operators) {
		this.fieldName = fieldName;
		this.validator = validator;
		
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
	
	public IValidator getValidator() {
		return validator;
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
	
	public static interface IValidator {
		public boolean isValid(String value);
	}
	
	static class IntegerValidator implements IValidator {
		@Override
		public boolean isValid(String value) {
			try {
				Integer.parseInt(value);
				return true;
			} catch (NumberFormatException e) {
				return false;
			}
		}
	}

	static class FloatValidator implements IValidator {
		@Override
		public boolean isValid(String value) {
			try {
				NumberFormat nf = NumberFormat.getInstance();
				nf.parse(value);
				return true;
			} catch (ParseException e) {
				return false;
			}
		}
	}
	
	static class DoneRatioValidator implements IValidator {
		@Override
		public boolean isValid(String value) {
			try {
				int val = Integer.parseInt(value);
				return 0 <= val && val <= 100;
			} catch (NumberFormatException e) {
				return false;
			}
		}
	}
	
	static class DateValidator implements IValidator {
		@Override
		public boolean isValid(String value) {
			try {
				int val = Integer.parseInt(value);
				return 0 <= val;
			} catch (NumberFormatException e) {
				return false;
			}
		}
	}
}
