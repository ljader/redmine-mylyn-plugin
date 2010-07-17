package net.sf.redmine_mylyn.api.query;

import static net.sf.redmine_mylyn.api.query.CompareOperator.IS_NOT;
import static net.sf.redmine_mylyn.api.query.CompareOperator.OPEN;
import static net.sf.redmine_mylyn.api.query.QueryField.STATUS;
import static net.sf.redmine_mylyn.api.query.QueryField.TRACKER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.sf.redmine_mylyn.api.TestData;

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
	
	@Test
	public void testIntegratedFromParamMap() throws Exception {
		Comparator<NameValuePair> comparator = new Comparator<NameValuePair>() {
			@Override
			public int compare(NameValuePair o1, NameValuePair o2) {
				return o1.getName().compareTo(o2.getName());
			}
		};
		
		ArrayList<NameValuePair> valid = new ArrayList<NameValuePair>();
		valid.add(new NameValuePair("values[tracker_id][]", "1"));
		valid.add(new NameValuePair("operators[tracker_id]", "!"));
		valid.add(new NameValuePair("fields[]", "tracker_id"));
		valid.add(new NameValuePair("values[status_id][]", ""));
		valid.add(new NameValuePair("values[tracker_id][]", "3"));
		valid.add(new NameValuePair("fields[]", "status_id"));
		valid.add(new NameValuePair("operators[status_id]", "o"));
		
		ArrayList<NameValuePair> invalid = new ArrayList<NameValuePair>(valid);
		invalid.add(new NameValuePair("values[status_id][]", "1")); //invalid value
		invalid.add(new NameValuePair("fields[]", "authorId")); //invalid QueryField
		invalid.add(new NameValuePair("operators[authorId]", "!"));
		invalid.add(new NameValuePair("values[authorId][]", "1"));
		
		
		Query query = Query.fromNameValuePairs(invalid, TestData.cfg);
		assertNotNull(query);
		
		List<NameValuePair> result = query.getParams();
		Collections.sort(valid, comparator);
		Collections.sort(result, comparator);
		assertEquals(valid, result);
	}
	
	@Test
	public void testIntegratedToUrl() throws Exception {
		testee.addFilter(TRACKER, IS_NOT, "1");
		testee.addFilter(TRACKER, IS_NOT, "3");
		testee.addFilter(STATUS, OPEN);
		
		String enc = "UTF-8";
		StringBuilder expected = new StringBuilder();
		expected.append("&fields[]=").append(URLEncoder.encode("tracker_id", enc));
		expected.append("&operators[tracker_id]=").append(URLEncoder.encode("!", enc));
		expected.append("&values[tracker_id][]=").append(URLEncoder.encode("1", enc));
		expected.append("&values[tracker_id][]=").append(URLEncoder.encode("3", enc));
		expected.append("&fields[]=").append(URLEncoder.encode("status_id", enc));
		expected.append("&operators[status_id]=").append(URLEncoder.encode("o", enc));
		expected.append("&values[status_id][]=").append(URLEncoder.encode("", enc));
		
		assertEquals(expected.toString(), testee.toUrl(enc));
		
		fail("Incomplete");
	}

	@Test
	public void testIntegratedFromUrl() throws Exception {
		String enc = "UTF-8";
		StringBuilder url = new StringBuilder();
		url.append("&fields[]=").append(URLEncoder.encode("tracker_id", enc));
		url.append("&operators[tracker_id]=").append(URLEncoder.encode("!", enc));
		url.append("&values[tracker_id][]=").append(URLEncoder.encode("1", enc));
		url.append("&values[tracker_id][]=").append(URLEncoder.encode("3", enc));
		url.append("&fields[]=").append(URLEncoder.encode("status_id", enc));
		url.append("&operators[status_id]=").append(URLEncoder.encode("o", enc));
		url.append("&values[status_id][]=").append(URLEncoder.encode("", enc));
		
		Query query = Query.fromUrl(url.toString(), enc, TestData.cfg);
		assertNotNull(query);

		ArrayList<NameValuePair> expected = new ArrayList<NameValuePair>();
		expected.add(new NameValuePair("fields[]", "tracker_id"));
		expected.add(new NameValuePair("operators[tracker_id]", "!"));
		expected.add(new NameValuePair("values[tracker_id][]", "1"));
		expected.add(new NameValuePair("values[tracker_id][]", "3"));
		expected.add(new NameValuePair("fields[]", "status_id"));
		expected.add(new NameValuePair("operators[status_id]", "o"));
		expected.add(new NameValuePair("values[status_id][]", ""));
		
		assertEquals(expected, query.getParams());
		
		fail("Incomplete");
}
	
}
