package net.sf.redmine_mylyn.core;

import net.sf.redmine_mylyn.internal.core.Messages;

public enum RedmineOperation {

	none(RedmineAttribute.STATUS, Messages.STATUS_LEAVE_AS_X, false, true),
	markas(RedmineAttribute.STATUS_CHG, Messages.STATUS_MARK_AS_X, true, false);
	
	private RedmineAttribute attribute;
	
	private String label;
	
	private boolean assiciated;
	
	private boolean restore;
	
	RedmineOperation(RedmineAttribute attribute, String label, boolean associated, boolean restoreDefault) {
		this.attribute = attribute;
		this.label = label;
		this.assiciated  = associated;
		this.restore = associated==false && restoreDefault;
	}
	
	public String getTaskKey() {
		return name();
	}

	public static RedmineOperation fromTaskKey(String taskKey) {
		return Enum.valueOf(RedmineOperation.class, taskKey);
	}

	public String getInputId() {
		return attribute.getTaskKey();
	}

	public String getLabel(Object... args) {
		if(args.length>0) {
			return String.format(label, args);
		}
		return label;
	}
	
	public String getType() {
		return attribute.getType();
	}

	public boolean isAssociated() {
		return assiciated;
	}
	
	public boolean needsRestoreValue() {
		return restore;
	}
	
}
