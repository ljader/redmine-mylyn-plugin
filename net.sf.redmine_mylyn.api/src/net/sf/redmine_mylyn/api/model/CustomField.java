package net.sf.redmine_mylyn.api.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

import net.sf.redmine_mylyn.api.query.IQueryField;
import net.sf.redmine_mylyn.api.query.QueryField;
import net.sf.redmine_mylyn.api.query.QueryFilter;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="customField", propOrder={
		"type", 
		"fieldFormat", 
		"minLength", 
		"maxLength", 
		"regexp", 
		"possibleValues", 
		"defaultValue", 
		"isRequired", 
		"isFilter", 
		"isForAll"})
public class CustomField extends Property implements IQueryField {
	
	private static final long serialVersionUID = 1L;

	public enum Type {IssueCustomField, TimeEntryCustomField};

	public enum Format {
		@XmlEnumValue("string")
		STRING, 
		@XmlEnumValue("text")
		TEXT, 
		@XmlEnumValue("int")
		INT, 
		@XmlEnumValue("float")
		FLOAT, 
		@XmlEnumValue("list")
		LIST, 
		@XmlEnumValue("date")
		DATE, 
		@XmlEnumValue("bool")
		BOOL,
		@XmlEnumValue("version")
		VERSION,
		@XmlEnumValue("user")
		USER;
		
		public String getLabel() {
			return name().toLowerCase();
		}
		
		public boolean isListType() {
			return this==LIST || this==VERSION || this==USER;
		}
	};

	private Type type;
	
	private Format fieldFormat;
	
	@XmlElementWrapper(name="possibleValues")
	@XmlElement(name="possibleValue")
	private List<String> possibleValues;
	
	private String regexp;
	
	private int minLength;

	private int maxLength;
	
	private boolean isRequired;
	
	private boolean isForAll;

	private boolean isFilter;
	
	private String defaultValue;

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public Format getFieldFormat() {
		return fieldFormat;
	}

	public void setFieldFormat(Format fieldFormat) {
		this.fieldFormat = fieldFormat;
	}

	public List<String> getPossibleValues() {
		return possibleValues;
	}

	public void setPossibleValues(List<String> possibleValues) {
		this.possibleValues = possibleValues;
	}

	public String getRegexp() {
		return regexp;
	}

	public void setRegexp(String regexp) {
		this.regexp = regexp;
	}

	public int getMinLength() {
		return minLength;
	}

	public void setMinLength(int minLength) {
		this.minLength = minLength;
	}

	public int getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}

	public boolean isRequired() {
		return isRequired;
	}

	public void setRequired(boolean isRequired) {
		this.isRequired = isRequired;
	}

	public boolean isForAll() {
		return isForAll;
	}

	public void setForAll(boolean isForAll) {
		this.isForAll = isForAll;
	}

	public boolean isFilter() {
		return isFilter && getFieldFormat()!=Format.USER && getFieldFormat()!=Format.VERSION;
	}

	public void setFilter(boolean isFilter) {
		this.isFilter = isFilter;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	@Override
	public String getQueryValue() {
		return QueryFilter.CUSTOM_FIELD_PREFIX + getId();
	}

	@Override
	public String getLabel() {
		return getName();
	}

	@Override
	public boolean isCrossProjectUsable() {
		return isForAll;
	}
	
	public QueryField getQueryField() {
		if(isFilter()) {
			switch (getFieldFormat()) {
			case LIST: return QueryField.LIST_TYPE;
			case BOOL: return QueryField.BOOLEAN_TYPE;
			case DATE: return QueryField.DATE_TYPE;
			case STRING:
			case TEXT: return QueryField.TEXT_TYPE;
			case INT: return QueryField.INT_TYPE;
			case FLOAT: return QueryField.FLOAT_TYPE;
			
			default: return null;
			}
		}
		return null;
	}
}
