package net.sf.redmine_mylyn.api.query;

import static net.sf.redmine_mylyn.api.query.CompareOperator.IS_NOT;
import static net.sf.redmine_mylyn.api.query.CompareOperator.OPEN;
import static net.sf.redmine_mylyn.api.query.QueryField.STATUS;
import static net.sf.redmine_mylyn.api.query.QueryField.TRACKER;
import static org.junit.Assert.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.NameValuePair;
import org.junit.Before;
import org.junit.Test;

public class QueryTest {

	private Query testee;
	
	@Before
	public void setUp() throws Exception {
		testee = new Query();
	}

	@Test
	public void testIntegretedAddFilterGetParams() throws Exception {
		testee.addFilter(TRACKER, IS_NOT, "1");
		testee.addFilter(TRACKER, IS_NOT, "3");
		List<NameValuePair> params = testee.getParams();
		assertEquals(4, params.size());
		
		testee.addFilter(STATUS, OPEN);
		params = testee.getParams();
		assertEquals(7, params.size());

		int idx=0;
		
		assertEquals("fields[]", params.get(idx).getName());
		assertEquals("tracker_id", params.get(idx++).getValue());
		assertEquals("operators[tracker_id]", params.get(idx).getName());
		assertEquals("!", params.get(idx++).getValue());
		assertEquals("values[tracker_id][]", params.get(idx).getName());
		assertEquals("1", params.get(idx++).getValue());
		assertEquals("values[tracker_id][]", params.get(idx).getName());
		assertEquals("3", params.get(idx++).getValue());

		assertEquals("fields[]", params.get(idx).getName());
		assertEquals("status_id", params.get(idx++).getValue());
		assertEquals("operators[status_id]", params.get(idx).getName());
		assertEquals("o", params.get(idx++).getValue());
		assertEquals("values[status_id][]", params.get(idx).getName());
		assertEquals("", params.get(idx++).getValue());

	}

}
