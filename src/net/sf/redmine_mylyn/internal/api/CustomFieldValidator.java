package net.sf.redmine_mylyn.internal.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import net.sf.redmine_mylyn.api.model.CustomField;
import net.sf.redmine_mylyn.api.model.CustomField.Format;
import net.sf.redmine_mylyn.api.model.CustomField.Type;

public class CustomFieldValidator {

	public final static String RESOURCE_FILE = "/xmldata/customfields.xml";
	
	public final static int COUNT = 6;
	
	public static void validate1(CustomField obj) {
		assertNotNull(obj);
		assertEquals(1, obj.getId());
		assertEquals("Database", obj.getName());
		assertEquals(Type.IssueCustomField, obj.getType());
		assertEquals(Format.LIST, obj.getFieldFormat());
		
		List<String> possibleValues = obj.getPossibleValues();
		assertNotNull(possibleValues);
		assertEquals(3, possibleValues.size());
		assertEquals("MySQL", possibleValues.get(0));
		assertEquals("PostgreSQL", possibleValues.get(1));
		assertEquals("Oracle", possibleValues.get(2));

		assertEquals("", obj.getRegexp());
		assertEquals(0, obj.getMinLength());
		assertEquals(0, obj.getMaxLength());
		assertFalse(obj.isRequired());
		assertTrue(obj.isForAll());
		assertTrue(obj.isFilter());
		assertEquals("", obj.getDefaultValue());
	}

	public static void validate2(CustomField obj) {
		assertNotNull(obj);
		assertEquals(2, obj.getId());
		assertEquals("Searchable field", obj.getName());
		assertEquals(Type.IssueCustomField, obj.getType());
		assertEquals(Format.STRING, obj.getFieldFormat());
		
		List<String> possibleValues = obj.getPossibleValues();
		assertNotNull(possibleValues);
		assertEquals(0, possibleValues.size());
		
		assertEquals("^.*$", obj.getRegexp());
		assertEquals(1, obj.getMinLength());
		assertEquals(100, obj.getMaxLength());
		assertFalse(obj.isRequired());
		assertTrue(obj.isForAll());
		assertFalse(obj.isFilter());
		assertEquals("Default string", obj.getDefaultValue());
	}
	
	public static void validate7(CustomField obj) {
		assertNotNull(obj);
		assertEquals(7, obj.getId());
		assertEquals("Billable", obj.getName());
		assertEquals(Type.TimeEntryActivityCustomField, obj.getType());
		assertEquals(Format.BOOL, obj.getFieldFormat());
		
		List<String> possibleValues = obj.getPossibleValues();
		assertNotNull(possibleValues);
		assertEquals(0, possibleValues.size());
		
		assertEquals("", obj.getRegexp());
		assertEquals(0, obj.getMinLength());
		assertEquals(0, obj.getMaxLength());
		assertFalse(obj.isRequired());
		assertFalse(obj.isForAll());
		assertTrue(obj.isFilter());
		assertEquals("", obj.getDefaultValue());
	}
	
}
