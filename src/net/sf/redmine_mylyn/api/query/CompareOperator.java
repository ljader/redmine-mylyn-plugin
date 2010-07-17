package net.sf.redmine_mylyn.api.query;

import java.util.EnumSet;

public enum CompareOperator {

	CONTAINS("~", "contains"), 
	CONTAINS_NOT("!~", "does not contain"),
	IS("=", "is"),
	IS_NOT("!", "is not"),
	ALL("*", "all"), 
	NONE("!*", "none"), 
	OPEN("o", "open"), 
	CLOSED("c", "closed"), 
	GTE(">=", "greater then"), 
	LTE("<=", "less then"), 
	DAY_AGO_MORE_THEN("<t-", "more then (days) ago"), 
	DAY_AGO_LESS_THEN(">t-", "less then (days) ago"), 
	DAY_AGO("t-", "day ago"), 
	TODAY("t", "today"), 
	CURRENT_WEEK("w", "current week"), 
	DAY_LATER("t+", "day later"), 
	DAY_LATER_LESS_THEN("<t+", "less then (days) later"), 
	DAY_LATER_MORE_THEN(">t+", "more then (days) later");
	
	final static EnumSet<CompareOperator> VALUE_BASED = EnumSet.complementOf(EnumSet.of(ALL, NONE, OPEN, CLOSED, TODAY, CURRENT_WEEK));

	private final String queryValue;
	
	private final String label;
	
	CompareOperator(String queryValue, String label) {
		this.queryValue = queryValue;
		this.label = label;
	}

	public String getQueryValue() {
		return queryValue;
	}

	@Override
	public String toString() {
		return label;
	}
	
	public boolean isValueBased() {
		return VALUE_BASED.contains(this);
	}

	static CompareOperator fromQueryValue(String value) {
		for(CompareOperator operator : CompareOperator.values()) {
			if (operator.getQueryValue().equals(value)) {
				return operator;
			}
		}
		return null;
	}
}
