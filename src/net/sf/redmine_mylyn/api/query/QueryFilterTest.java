package net.sf.redmine_mylyn.api.query;

import static net.sf.redmine_mylyn.api.query.CompareOperator.CLOSED;
import static net.sf.redmine_mylyn.api.query.CompareOperator.DAY_AGO_MORE_THEN;
import static net.sf.redmine_mylyn.api.query.CompareOperator.GTE;
import static net.sf.redmine_mylyn.api.query.CompareOperator.IS;
import static net.sf.redmine_mylyn.api.query.CompareOperator.IS_NOT;
import static net.sf.redmine_mylyn.api.query.CompareOperator.OPEN;
import static net.sf.redmine_mylyn.api.query.CompareOperator.TODAY;
import static net.sf.redmine_mylyn.api.query.QueryField.BOOLEAN_TYPE;
import static net.sf.redmine_mylyn.api.query.QueryField.DATE_CREATED;
import static net.sf.redmine_mylyn.api.query.QueryField.DATE_START;
import static net.sf.redmine_mylyn.api.query.QueryField.DONE_RATIO;
import static net.sf.redmine_mylyn.api.query.QueryField.STATUS;
import static net.sf.redmine_mylyn.api.query.QueryField.TRACKER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.NameValuePair;
import org.junit.Before;
import org.junit.Test;

public class QueryFilterTest {

	QueryFilter testee;
	
	List<NameValuePair> params;
	
	@Before
	public void setup() {
		params = new ArrayList<NameValuePair>();
	}
	
	@Test
	public void testQueryFilter() throws Exception {
		Field field = QueryFilter.class.getDeclaredField("operator");
		field.setAccessible(true);

		testee = new QueryFilter(STATUS);

		//Standard Operator
		assertEquals(IS, field.get(testee));
	}

	@Test
	public void testAddValue() throws Exception {
		Field field = QueryFilter.class.getDeclaredField("values");
		field.setAccessible(true);
		
		//ValueBased
		testee = new QueryFilter(STATUS);
		testee.setOperator(IS);
		testee.addValue("1");
		testee.addValue("3");
		List<?> values = (List<?>)field.get(testee);
		
		assertEquals(2, values.size());
		assertEquals("1", values.get(0));
		assertEquals("3", values.get(1));

		//NonValueBased
		testee = new QueryFilter(STATUS);
		testee.setOperator(OPEN);
		testee.addValue("1");
		testee.addValue("3");
		values = (List<?>)field.get(testee);
		
		assertEquals(0, values.size());
		
		//MissingOperator
		testee = new QueryFilter(STATUS);
		testee.setOperator(TODAY); //illegal Operator => null
		testee.addValue("1");
		testee.addValue("3");
		values = (List<?>)field.get(testee);
		
		assertEquals(0, values.size());
	}

	@Test
	public void testSetOperator() throws Exception {
		Field field = QueryFilter.class.getDeclaredField("operator");
		field.setAccessible(true);

		//correct Operator
		testee = new QueryFilter(STATUS);
		testee.setOperator(OPEN);
		assertEquals(OPEN, field.get(testee));

		//illegal Operator
		testee = new QueryFilter(STATUS);
		testee.setOperator(TODAY);
		assertNull(field.get(testee));
	}

	@Test
	public void testAppendParamsNonValueBasedStatus() throws Exception {
		testee = new QueryFilter(STATUS);
		
		testee.setOperator(OPEN);
		testee.appendParams(params);
		assertEquals("fields[]", params.get(0).getName());
		assertEquals("status_id", params.get(0).getValue());
		assertEquals("operators[status_id]", params.get(1).getName());
		assertEquals("o", params.get(1).getValue());
		assertEquals("values[status_id][]", params.get(2).getName());
		assertEquals("", params.get(2).getValue());
		
		params.clear();
		testee.setOperator(CLOSED);
		testee.appendParams(params);
		assertEquals("fields[]", params.get(0).getName());
		assertEquals("status_id", params.get(0).getValue());
		assertEquals("operators[status_id]", params.get(1).getName());
		assertEquals("c", params.get(1).getValue());
		assertEquals("values[status_id][]", params.get(2).getName());
		assertEquals("", params.get(2).getValue());
	}

	@Test
	public void testAppendParamsNonValueBasedStartDate() throws Exception {
		testee = new QueryFilter(DATE_START);
		
		testee.setOperator(TODAY);
		testee.appendParams(params);
		assertEquals("fields[]", params.get(0).getName());
		assertEquals("start_date", params.get(0).getValue());
		assertEquals("operators[start_date]", params.get(1).getName());
		assertEquals("t", params.get(1).getValue());
		assertEquals("values[start_date][]", params.get(2).getName());
		assertEquals("", params.get(2).getValue());
	}
	
	@Test
	public void testAppendParamsListBasedTracker() throws Exception {
		testee = new QueryFilter(TRACKER);
		
		testee.setOperator(IS_NOT);
		testee.appendParams(params);
		assertEquals(0, params.size());
		
		testee.addValue("1");
		testee.addValue("3");
		testee.appendParams(params);

		assertEquals(4, params.size());
		assertEquals("fields[]", params.get(0).getName());
		assertEquals("tracker_id", params.get(0).getValue());
		assertEquals("operators[tracker_id]", params.get(1).getName());
		assertEquals("!", params.get(1).getValue());
		assertEquals("values[tracker_id][]", params.get(2).getName());
		assertEquals("1", params.get(2).getValue());
		assertEquals("values[tracker_id][]", params.get(3).getName());
		assertEquals("3", params.get(3).getValue());
	}

	@Test
	public void testAppendParamsDateBasedCreateDate() throws Exception {
		testee = new QueryFilter(DATE_CREATED);
		
		testee.setOperator(DAY_AGO_MORE_THEN);
		testee.appendParams(params);
		assertEquals(0, params.size());
		
		testee.addValue("1");
		testee.appendParams(params);
		assertEquals(3, params.size());
		assertEquals("fields[]", params.get(0).getName());
		assertEquals("created_on", params.get(0).getValue());
		assertEquals("operators[created_on]", params.get(1).getName());
		assertEquals("<t-", params.get(1).getValue());
		assertEquals("values[created_on][]", params.get(2).getName());
		assertEquals("1", params.get(2).getValue());
		
		params.clear();
		testee.addValue("3");
		testee.appendParams(params);
		assertEquals(0, params.size());
	}
	
	@Test
	public void testAppendParamsBooleanBased() throws Exception {
		//TODO rewrite, use cf
		testee = new QueryFilter(BOOLEAN_TYPE);
		
		testee.setOperator(IS);
		testee.appendParams(params);
		assertEquals(0, params.size());
		
		testee.addValue("2");
		testee.appendParams(params);
		assertEquals(0, params.size());
		
		testee = new QueryFilter(BOOLEAN_TYPE);
		testee.setOperator(IS);
		testee.addValue("1");
		testee.appendParams(params);
		
		assertEquals(3, params.size());
		assertEquals("fields[]", params.get(0).getName());
		assertEquals("BOOLEAN_BASED", params.get(0).getValue());
		assertEquals("operators[BOOLEAN_BASED]", params.get(1).getName());
		assertEquals("=", params.get(1).getValue());
		assertEquals("values[BOOLEAN_BASED][]", params.get(2).getName());
		assertEquals("1", params.get(2).getValue());
	}
	
	@Test
	public void testAppendParamsDoneRatio() throws Exception {
		testee = new QueryFilter(DONE_RATIO);
		
		testee.setOperator(GTE);
		testee.appendParams(params);
		assertEquals(0, params.size());
		
		testee.addValue("101");
		testee.appendParams(params);
		assertEquals(0, params.size());
		
		testee = new QueryFilter(DONE_RATIO);
		testee.setOperator(GTE);
		testee.addValue("50");
		testee.appendParams(params);
		
		assertEquals(3, params.size());
		assertEquals("fields[]", params.get(0).getName());
		assertEquals("done_ratio", params.get(0).getValue());
		assertEquals("operators[done_ratio]", params.get(1).getName());
		assertEquals(">=", params.get(1).getValue());
		assertEquals("values[done_ratio][]", params.get(2).getName());
		assertEquals("50", params.get(2).getValue());
	}

}
