package net.sf.redmine_mylyn.api.query;

import java.util.EnumSet;

import net.sf.redmine_mylyn.internal.api.Messages;

public enum CompareOperator {

	CONTAINS("~", Messages.Q_CONTAINS),  //$NON-NLS-1$
	CONTAINS_NOT("!~", Messages.Q_CONTAINS_NOT), //$NON-NLS-1$
	IS("=", Messages.Q_IS), //$NON-NLS-1$
	IS_NOT("!", Messages.Q_IS_NOT), //$NON-NLS-1$
	ALL("*", Messages.Q_ALL),  //$NON-NLS-1$
	NONE("!*", Messages.Q_NONE),  //$NON-NLS-1$
	OPEN("o", Messages.Q_OPEN),  //$NON-NLS-1$
	CLOSED("c", Messages.Q_CLOSED),  //$NON-NLS-1$
	GTE(">=", Messages.Q_GREATER_THEN),  //$NON-NLS-1$
	LTE("<=", Messages.Q_LESS_THEN),  //$NON-NLS-1$
	DAY_AGO_MORE_THEN("<t-", Messages.Q_MORE_THEN_DAYS_AGOU),  //$NON-NLS-1$
	DAY_AGO_LESS_THEN(">t-", Messages.Q_LESS_THE_DAYS_AGO),  //$NON-NLS-1$
	DAY_AGO("t-", Messages.Q_DAY_AGO),  //$NON-NLS-1$
	TODAY("t", Messages.Q_TODAY),  //$NON-NLS-1$
	CURRENT_WEEK("w", Messages.Q_CURRENT_WEEK),  //$NON-NLS-1$
	DAY_LATER("t+", Messages.Q_DAYS_LATER),  //$NON-NLS-1$
	DAY_LATER_LESS_THEN("<t+", Messages.Q_LESS_THEN_DAYS_LATER),  //$NON-NLS-1$
	DAY_LATER_MORE_THEN(">t+", Messages.Q_MORE_THEN_DAYS_LATER); //$NON-NLS-1$
	
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
