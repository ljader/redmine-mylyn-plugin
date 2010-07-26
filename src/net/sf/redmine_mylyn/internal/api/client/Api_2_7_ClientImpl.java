package net.sf.redmine_mylyn.internal.api.client;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.redmine_mylyn.api.client.IRedmineApiErrorCollector;
import net.sf.redmine_mylyn.api.client.RedmineServerVersion;
import net.sf.redmine_mylyn.api.exception.RedmineApiErrorException;
import net.sf.redmine_mylyn.api.exception.RedmineApiInvalidDataException;
import net.sf.redmine_mylyn.api.model.Configuration;
import net.sf.redmine_mylyn.api.model.CustomValue;
import net.sf.redmine_mylyn.api.model.Issue;
import net.sf.redmine_mylyn.api.model.container.AbstractPropertyContainer;
import net.sf.redmine_mylyn.api.model.container.CustomFields;
import net.sf.redmine_mylyn.api.model.container.CustomValues;
import net.sf.redmine_mylyn.api.model.container.IssueCategories;
import net.sf.redmine_mylyn.api.model.container.IssuePriorities;
import net.sf.redmine_mylyn.api.model.container.IssueStatuses;
import net.sf.redmine_mylyn.api.model.container.Projects;
import net.sf.redmine_mylyn.api.model.container.Queries;
import net.sf.redmine_mylyn.api.model.container.TimeEntryActivities;
import net.sf.redmine_mylyn.api.model.container.Trackers;
import net.sf.redmine_mylyn.api.model.container.Users;
import net.sf.redmine_mylyn.api.model.container.Versions;
import net.sf.redmine_mylyn.api.query.Query;
import net.sf.redmine_mylyn.internal.api.parser.AttributeParser;
import net.sf.redmine_mylyn.internal.api.parser.IModelParser;
import net.sf.redmine_mylyn.internal.api.parser.PartialIssueParser;
import net.sf.redmine_mylyn.internal.api.parser.SettingsParser;
import net.sf.redmine_mylyn.internal.api.parser.SubmitedIssueParser;
import net.sf.redmine_mylyn.internal.api.parser.TypedParser;
import net.sf.redmine_mylyn.internal.api.parser.adapter.type.Issues;
import net.sf.redmine_mylyn.internal.api.parser.adapter.type.PartialIssueType;
import net.sf.redmine_mylyn.internal.api.parser.adapter.type.PartialIssues;
import net.sf.redmine_mylyn.internal.api.parser.adapter.type.SubmitError;
import net.sf.redmine_mylyn.internal.api.parser.adapter.type.UpdatedIssuesType;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.mylyn.commons.net.AbstractWebLocation;
import org.eclipse.mylyn.commons.net.Policy;

public class Api_2_7_ClientImpl extends AbstractClient {

	private final static String URL_SERVER_VERSION = "/mylyn/version";
	private final static String URL_ISSUE_STATUS = "/mylyn/issuestatus";
	private final static String URL_ISSUE_CATEGORIES = "/mylyn/issuecategories";
	private final static String URL_ISSUE_PRIORITIES = "/mylyn/issuepriorities";
	private final static String URL_TRACKERS = "/mylyn/trackers";
	private final static String URL_CUSTOMFIELDS = "/mylyn/customfields";
	private final static String URL_USERS = "/mylyn/users";
	private final static String URL_TIMEENTRY_ACTIVITIES = "/mylyn/timeentryactivities";
	private final static String URL_QUERIES = "/mylyn/queries";
	private final static String URL_PROJECTS = "/mylyn/projects";
	private final static String URL_VERSIONS = "/mylyn/versions";
	private final static String URL_SETTINGS = "/mylyn/settings";

	private final static String URL_ISSUES_UPDATED = "/mylyn/issues/updatedsince?issues=%s&unixtime=%d";
	private final static String URL_ISSUES_LIST = "/mylyn/issues/list?issues=%s";
	private final static String URL_ISSUE = "/mylyn/issue/%d";
	
	private final static String URL_QUERY = "/issues.xml";

	
	private Map<String, IModelParser<? extends AbstractPropertyContainer<?>>> parserByClass;
	
	private SettingsParser settingsParser;
	private TypedParser<UpdatedIssuesType> updatedIssuesParser;
	private TypedParser<Issue> issueParser;
	private TypedParser<Issues> issuesParser;
	private TypedParser<RedmineServerVersion> versionParser;
	
	private PartialIssueParser queryParser;
	private SubmitedIssueParser submitIssueParser;
	
	private final IssueXmlWriter issueWriter;
	
	private Configuration configuration;
	
	public Api_2_7_ClientImpl(AbstractWebLocation location) {
		super(location);
		
		buildParser();
		issueWriter = new IssueXmlWriter();
	}

	public Api_2_7_ClientImpl(AbstractWebLocation location, Configuration initialConfiguration) {
		this(location);
		
		this.configuration = initialConfiguration;
	}
	
	@Override
	public Configuration getConfiguration() {
		if(configuration==null) {
			configuration = new Configuration();
		}
		return configuration;
	}

	@Override
	public RedmineServerVersion detectServerVersion(IProgressMonitor monitor) throws RedmineApiErrorException {
		monitor = Policy.monitorFor(monitor);
		monitor.beginTask("Detect version of Redmine", 1);

		GetMethod method = new GetMethod(URL_SERVER_VERSION);
		RedmineServerVersion version = executeMethod(method, versionParser, monitor);

		if(monitor.isCanceled()) {
			throw new OperationCanceledException();
		} else {
			monitor.worked(1);
		}

		return version;
	}
	
	@Override
	public void updateConfiguration(IProgressMonitor monitor) throws RedmineApiErrorException {
		monitor = Policy.monitorFor(monitor);

		Configuration conf = new Configuration();
		
		monitor.beginTask("Updating Attributes", parserByClass.size());
		GetMethod method = null;
		
		for (Entry<String, IModelParser<? extends AbstractPropertyContainer<?>>> entry : parserByClass.entrySet()) {
			method = new GetMethod(entry.getKey());
			AbstractPropertyContainer<?> propCt = executeMethod(method, entry.getValue(), monitor);

			if(monitor.isCanceled()) {
				throw new OperationCanceledException();
			} else {
				conf.setPropertyContainer(propCt);
				monitor.worked(1);
			}
		}

		method = new GetMethod(URL_SETTINGS);
		conf.setSettings(executeMethod(method, settingsParser, monitor));
		if(monitor.isCanceled()) {
			throw new OperationCanceledException();
		} else {
			monitor.worked(1);
		}

		getConfiguration().copy(conf);
	}
	
	@Override
	public int[] getUpdatedIssueIds(int[] issues, long updatedSince, IProgressMonitor monitor) throws RedmineApiErrorException {
		if (issues==null || issues.length==0) {
			return null;
		}
		
		monitor = Policy.monitorFor(monitor);
		monitor.beginTask("Search updated issues", 1);

		String uri = String.format(URL_ISSUES_UPDATED, Arrays.toString(issues).replaceAll("[\\[\\] ]", ""), updatedSince);
		GetMethod method = new GetMethod(uri);
		
		UpdatedIssuesType result = executeMethod(method, updatedIssuesParser, monitor);

		if(monitor.isCanceled()) {
			throw new OperationCanceledException();
		} else {
			monitor.worked(1);
		}
		
		return result.updatedIssueIds;
	}
	
	@Override
	public Issue getIssue(int id, IProgressMonitor monitor) throws RedmineApiErrorException {
		if(id < 1) {
			return null;
		}
		
		monitor = Policy.monitorFor(monitor);
		monitor.beginTask("Fetch issue", 1);

		String uri = String.format(URL_ISSUE, id);
		GetMethod method = new GetMethod(uri);
		
		Issue issue = executeMethod(method, issueParser, monitor);

		if(monitor.isCanceled()) {
			throw new OperationCanceledException();
		} else {
			monitor.worked(1);
		}
		
		return issue;
	}
	
	@Override
	public Issue[] getIssues(IProgressMonitor monitor, int... issueIds) throws RedmineApiErrorException {
		if (issueIds==null || issueIds.length==0) {
			return null;
		}
		
		monitor = Policy.monitorFor(monitor);
		monitor.beginTask("Fetch issues", 1);

		String uri = String.format(URL_ISSUES_LIST, Arrays.toString(issueIds).replaceAll("[\\[\\] ]", ""));
		GetMethod method = new GetMethod(uri);
		
		Issues issues = executeMethod(method, issuesParser, monitor);

		if(monitor.isCanceled()) {
			throw new OperationCanceledException();
		} else {
			monitor.worked(1);
		}
		
		return issues.getAll().toArray(new Issue[issues.getAll().size()]);
	}

	@Override
	public Issue[] query(Query query, IProgressMonitor monitor) throws RedmineApiErrorException {
		monitor = Policy.monitorFor(monitor);
		monitor.beginTask("Execute query", 1);

		GetMethod method = new GetMethod(URL_QUERY);
		
		List<NameValuePair> params = query.getParams();
		method.setQueryString(params.toArray(new NameValuePair[params.size()]));
		
		PartialIssues issues = executeMethod(method, queryParser, monitor);

		if(monitor.isCanceled()) {
			throw new OperationCanceledException();
		} else {
			monitor.worked(1);
		}

		return issues.issues;
	}

	@Override
	public Issue createIssue(Issue issue, IRedmineApiErrorCollector errorCollector, IProgressMonitor monitor) throws RedmineApiInvalidDataException, RedmineApiErrorException {
		monitor = Policy.monitorFor(monitor);
		monitor.beginTask("Upload Task", 1);
		
		PostMethod method = new PostMethod("/issues.xml");
		
		
		
		try {
			String requestBody = issueWriter.writeIssue(issue);
			method.setRequestEntity(new StringRequestEntity(requestBody, "text/xml", "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new RedmineApiErrorException("Execution of method failed - Invalid encoding {}", e, "UTF-8");
		}
		
		Object response = executeMethod(method, submitIssueParser, monitor, HttpStatus.SC_CREATED, HttpStatus.SC_UNPROCESSABLE_ENTITY);
		
		if(monitor.isCanceled()) {
			throw new OperationCanceledException();
		} else {
			monitor.worked(1);
		}

		if(response instanceof PartialIssueType) {
			return ((PartialIssueType)response).toIssue();
		} else {
			SubmitError error = (SubmitError)response;
			for (String errMsg : error.errors) {
				errorCollector.accept(errMsg);
			}
			
			throw new RedmineApiInvalidDataException();
		}
	}
	
	private void buildParser() {
		parserByClass = new HashMap<String, IModelParser<? extends AbstractPropertyContainer<?>>>();
		parserByClass.put(URL_ISSUE_STATUS, new AttributeParser<IssueStatuses>(IssueStatuses.class));
		parserByClass.put(URL_ISSUE_CATEGORIES, new AttributeParser<IssueCategories>(IssueCategories.class));
		parserByClass.put(URL_ISSUE_PRIORITIES, new AttributeParser<IssuePriorities>(IssuePriorities.class));
		parserByClass.put(URL_TRACKERS, new AttributeParser<Trackers>(Trackers.class));
		parserByClass.put(URL_CUSTOMFIELDS, new AttributeParser<CustomFields>(CustomFields.class));
		parserByClass.put(URL_USERS, new AttributeParser<Users>(Users.class));
		parserByClass.put(URL_TIMEENTRY_ACTIVITIES, new AttributeParser<TimeEntryActivities>(TimeEntryActivities.class));
		parserByClass.put(URL_QUERIES, new AttributeParser<Queries>(Queries.class));
		parserByClass.put(URL_PROJECTS, new AttributeParser<Projects>(Projects.class));
		parserByClass.put(URL_VERSIONS, new AttributeParser<Versions>(Versions.class));
		
		settingsParser = new SettingsParser();
		updatedIssuesParser = new TypedParser<UpdatedIssuesType>(UpdatedIssuesType.class);
		issueParser = new TypedParser<Issue>(Issue.class);
		issuesParser = new TypedParser<Issues>(Issues.class);
		versionParser = new TypedParser<RedmineServerVersion>(RedmineServerVersion.class);

		queryParser = new PartialIssueParser();
		submitIssueParser = new SubmitedIssueParser();
	}
	
}
