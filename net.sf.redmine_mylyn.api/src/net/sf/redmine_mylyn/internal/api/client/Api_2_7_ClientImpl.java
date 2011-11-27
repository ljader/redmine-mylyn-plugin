package net.sf.redmine_mylyn.internal.api.client;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.redmine_mylyn.api.client.IRedmineApiErrorCollector;
import net.sf.redmine_mylyn.api.client.IRedmineApiWebHelper;
import net.sf.redmine_mylyn.api.client.RedmineApiIssueProperty;
import net.sf.redmine_mylyn.api.client.RedmineServerVersion;
import net.sf.redmine_mylyn.api.exception.RedmineApiErrorException;
import net.sf.redmine_mylyn.api.exception.RedmineApiInvalidDataException;
import net.sf.redmine_mylyn.api.exception.RedmineApiRemoteException;
import net.sf.redmine_mylyn.api.model.Attachment;
import net.sf.redmine_mylyn.api.model.Configuration;
import net.sf.redmine_mylyn.api.model.Issue;
import net.sf.redmine_mylyn.api.model.TimeEntry;
import net.sf.redmine_mylyn.api.model.container.AbstractPropertyContainer;
import net.sf.redmine_mylyn.api.model.container.CustomFields;
import net.sf.redmine_mylyn.api.model.container.IssueCategories;
import net.sf.redmine_mylyn.api.model.container.IssuePriorities;
import net.sf.redmine_mylyn.api.model.container.IssueStatuses;
import net.sf.redmine_mylyn.api.model.container.Projects;
import net.sf.redmine_mylyn.api.model.container.Queries;
import net.sf.redmine_mylyn.api.model.container.Trackers;
import net.sf.redmine_mylyn.api.model.container.Users;
import net.sf.redmine_mylyn.api.model.container.Versions;
import net.sf.redmine_mylyn.api.query.Query;
import net.sf.redmine_mylyn.internal.api.Messages;
import net.sf.redmine_mylyn.internal.api.parser.AttachmentParser;
import net.sf.redmine_mylyn.internal.api.parser.AttributeParser;
import net.sf.redmine_mylyn.internal.api.parser.IModelParser;
import net.sf.redmine_mylyn.internal.api.parser.IssueParser;
import net.sf.redmine_mylyn.internal.api.parser.IssuesParser;
import net.sf.redmine_mylyn.internal.api.parser.SettingsParser;
import net.sf.redmine_mylyn.internal.api.parser.StringParser;
import net.sf.redmine_mylyn.internal.api.parser.SubmitedIssueParser;
import net.sf.redmine_mylyn.internal.api.parser.TypedParser;
import net.sf.redmine_mylyn.internal.api.parser.adapter.type.Issues;
import net.sf.redmine_mylyn.internal.api.parser.adapter.type.PartialIssueType;
import net.sf.redmine_mylyn.internal.api.parser.adapter.type.SubmitError;
import net.sf.redmine_mylyn.internal.api.parser.adapter.type.UpdatedIssuesType;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.mylyn.commons.net.Policy;

public class Api_2_7_ClientImpl extends AbstractClient {

	private final static String URL_SERVER_VERSION = "/mylyn/version"; //$NON-NLS-1$
	private final static String URL_ISSUE_STATUS = "/mylyn/issuestatus"; //$NON-NLS-1$
	private final static String URL_ISSUE_CATEGORIES = "/mylyn/issuecategories"; //$NON-NLS-1$
	private final static String URL_ISSUE_PRIORITIES = "/mylyn/issuepriorities"; //$NON-NLS-1$
	private final static String URL_TRACKERS = "/mylyn/trackers"; //$NON-NLS-1$
	private final static String URL_CUSTOMFIELDS = "/mylyn/customfields"; //$NON-NLS-1$
	private final static String URL_USERS = "/mylyn/users"; //$NON-NLS-1$
	private final static String URL_QUERIES = "/mylyn/queries"; //$NON-NLS-1$
	private final static String URL_PROJECTS = "/mylyn/projects"; //$NON-NLS-1$
	private final static String URL_VERSIONS = "/mylyn/versions"; //$NON-NLS-1$
	private final static String URL_SETTINGS = "/mylyn/settings"; //$NON-NLS-1$

	private final static String URL_ISSUES_UPDATED = "/mylyn/issues/updatedsince?issues=%s&unixtime=%d"; //$NON-NLS-1$
	private final static String URL_ISSUES_LIST = "/mylyn/issues/list?issues=%s"; //$NON-NLS-1$
	private final static String URL_ISSUE = "/mylyn/issue/%d"; //$NON-NLS-1$
	private final static String URL_QUERY = "/mylyn/issues"; //$NON-NLS-1$

	private final static String URL_UPDATE_ISSUE = "/issues/%d.xml"; //$NON-NLS-1$
	
	private final static String URL_GET_ATTACHMENT = "/mylyn/attachment/%d/%s"; //$NON-NLS-1$

	private final static String URL_GET_AUTHENTICITY_TOKEN = "/mylyn/token"; //$NON-NLS-1$
	
	private Map<String, IModelParser<? extends AbstractPropertyContainer<?>>> parserByClass;
	
	private SettingsParser settingsParser;
	private TypedParser<UpdatedIssuesType> updatedIssuesParser;
	private IssueParser issueParser;
	private IssuesParser issuesParser;
	private TypedParser<RedmineServerVersion> versionParser;
	
	private SubmitedIssueParser submitIssueParser;
	private AttachmentParser attachmentParser;
	private StringParser stringParser;
	
	private Configuration configuration;
	
	Api_2_7_ClientImpl(IRedmineApiWebHelper webHelper) {
		this(webHelper, new Configuration());
	}

	public Api_2_7_ClientImpl(IRedmineApiWebHelper webHelper, Configuration initialConfiguration) {
		super(webHelper);
		
		this.configuration = initialConfiguration;
		buildParser();
	}
	
	@Override
	public Configuration getConfiguration() {
		return configuration;
	}

	@Override
	public RedmineServerVersion detectServerVersion(IProgressMonitor monitor) throws RedmineApiErrorException {
		monitor = Policy.monitorFor(monitor);
		monitor.beginTask(Messages.PROGRESS_DETECT_REDMINE_VERSION, 1);

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
		
		monitor.beginTask(Messages.PROGRESS_UPDATING_ATTRIBUTES, parserByClass.size()+1);
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
	public int[] getUpdatedIssueIds(int[] issues, Date updatedSince, IProgressMonitor monitor) throws RedmineApiErrorException {
		if (issues==null || issues.length==0) {
			return null;
		}
		
		long unixtime = updatedSince.getTime()/1000l;
		//workaround: bug in redmine plugin
		unixtime += 1l;
		
		monitor = Policy.monitorFor(monitor);
		monitor.beginTask(Messages.PROGRESS_SEARCH_UPDATED_ISSUES, 1);

		String uri = String.format(URL_ISSUES_UPDATED, Arrays.toString(issues).replaceAll("[\\[\\] ]", ""), unixtime); //$NON-NLS-1$ //$NON-NLS-2$
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
		monitor.beginTask(Messages.PROGRESS_FETCH_ISSUE, 1);

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
		monitor.beginTask(Messages.PROGRESS_FETCH_ISSUES, 1);

		String uri = String.format(URL_ISSUES_LIST, Arrays.toString(issueIds).replaceAll("[\\[\\] ]", "")); //$NON-NLS-1$ //$NON-NLS-2$
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
		monitor.beginTask(Messages.PROGRESS_EXECUTE_QUERY, 1);

		GetMethod method = new GetMethod(URL_QUERY);
		List<NameValuePair> params = query.getParams();
		
		if(params.size()>0) {
			method.setQueryString(params.toArray(new NameValuePair[params.size()]));
		}
		Issues partialIssues = executeMethod(method, issuesParser, monitor);

		if(monitor.isCanceled()) {
			throw new OperationCanceledException();
		} else {
			monitor.worked(1);
		}
		
		return partialIssues.getAll().toArray(new Issue[partialIssues.getAll().size()]);
	}

	@Override
	public Issue createIssue(Issue issue, IRedmineApiErrorCollector errorCollector, IProgressMonitor monitor) throws RedmineApiInvalidDataException, RedmineApiErrorException {
		monitor = Policy.monitorFor(monitor);
		monitor.beginTask(Messages.PROGRESS_UPLOAD_TASK, 1);
		
		PostMethod method = new PostMethod("/issues.xml"); //$NON-NLS-1$
		try {
			//Workaround: remote method CREATE dosn't support API-Keys, we need a session
			getAuthenticityToken(monitor);
			
			method.setRequestEntity(new IssueRequestEntity(issue));
		} catch (UnsupportedEncodingException e) {
			throw new RedmineApiErrorException(Messages.ERRMSG_METHOD_EXECUTION_FAILED_INVALID_ENCODING, e, "UTF-8"); //$NON-NLS-2$ //$NON-NLS-1$
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
	
	@Override
	public void updateIssue(int issueId, Map<RedmineApiIssueProperty, String> issueValues, String comment, TimeEntry timeEntry, IRedmineApiErrorCollector errorCollector, IProgressMonitor monitor) throws RedmineApiInvalidDataException, RedmineApiErrorException {
		monitor = Policy.monitorFor(monitor);
		monitor.beginTask(Messages.PROGRESS_UPLOAD_TASK, 1);

		try {
			updateIssue(issueId, new IssueRequestEntity(issueValues, comment, timeEntry), errorCollector, monitor);
		} catch (UnsupportedEncodingException e) {
			throw new RedmineApiErrorException(Messages.ERRMSG_METHOD_EXECUTION_FAILED_INVALID_ENCODING, e, "UTF-8"); //$NON-NLS-2$ //$NON-NLS-1$
		} finally {
			if(monitor.isCanceled()) {
				throw new OperationCanceledException();
			} else {
				monitor.worked(1);
			}
		}
	}
	
	@Override
	public void updateIssue(Issue issue, String comment, TimeEntry timeEntry, IRedmineApiErrorCollector errorCollector, IProgressMonitor monitor) throws RedmineApiInvalidDataException, RedmineApiErrorException {
		monitor = Policy.monitorFor(monitor);
		monitor.beginTask(Messages.PROGRESS_UPLOAD_TASK, 1);

		try {
			updateIssue(issue.getId(), new IssueRequestEntity(issue, comment, timeEntry), errorCollector, monitor);
		} catch (UnsupportedEncodingException e) {
			throw new RedmineApiErrorException(Messages.ERRMSG_METHOD_EXECUTION_FAILED_INVALID_ENCODING, e, "UTF-8"); //$NON-NLS-2$ //$NON-NLS-1$
		} finally {
			if(monitor.isCanceled()) {
				throw new OperationCanceledException();
			} else {
				monitor.worked(1);
			}
		}
	}
	
	private void updateIssue(int issueId, IssueRequestEntity requestEntity, IRedmineApiErrorCollector errorCollector, IProgressMonitor monitor) throws RedmineApiInvalidDataException, RedmineApiErrorException {
		//Workaround: remote method UPDATE dosn't support API-Keys, we need a session
		getAuthenticityToken(monitor);
		
		PutMethod method = new PutMethod(String.format(URL_UPDATE_ISSUE, issueId));
		method.setRequestEntity(requestEntity);
		Object response = executeMethod(method, submitIssueParser, monitor, HttpStatus.SC_OK, HttpStatus.SC_UNPROCESSABLE_ENTITY);
		
		if(response instanceof SubmitError) {
			SubmitError error = (SubmitError)response;
			for (String errMsg : error.errors) {
				errorCollector.accept(errMsg);
			}
			
			throw new RedmineApiInvalidDataException();
		}
	}
	
	@Override
	public InputStream getAttachmentContent(int attachmentId, String fileName, IProgressMonitor monitor) throws RedmineApiErrorException {
		monitor = Policy.monitorFor(monitor);
		monitor.beginTask(Messages.PROGRESS_DOWNLOAD_ATTACHMENT, 1);
		
		//WOrkaround: Attachments::download dosn't support API-AUTH - this starts a session
		String token = getAuthenticityToken(monitor);
		if(token==null) {
			throw new RedmineApiRemoteException(Messages.ERRMSG_AUTH_TOKEN_REQUEST_FAILED);
		}
		
		GetMethod method = new GetMethod(String.format(URL_GET_ATTACHMENT, attachmentId, fileName));
		InputStream attachment = executeMethod(method, attachmentParser, monitor, HttpStatus.SC_OK, HttpStatus.SC_NOT_FOUND);

		if(monitor.isCanceled()) {
			throw new OperationCanceledException();
		} else {
			monitor.worked(1);
		}

		return attachment;
	}
	
	public void uploadAttachment(int issueId, final Attachment attachment, final InputStream content, String comment, IRedmineApiErrorCollector errorCollector, IProgressMonitor monitor) throws RedmineApiInvalidDataException, RedmineApiErrorException {
		monitor = Policy.monitorFor(monitor);
		monitor.beginTask(Messages.PROGRESS_UPLOAD_ATTACHMENT, 1);
		
		Object response = null;
		
		String token = getAuthenticityToken(monitor);
		if(token==null) {
			throw new RedmineApiRemoteException(Messages.ERRMSG_AUTH_TOKEN_REQUEST_FAILED);
		}
		
		try {
			PutMethod method = new PutMethod(String.format(URL_UPDATE_ISSUE, issueId));
			
			Part[] parts = new Part[4];
			parts[0] = new StringPart("authenticity_token", token, characterEncoding); //$NON-NLS-1$
			parts[1] = new StringPart("attachments[1][description]", attachment.getDescription(), characterEncoding); //$NON-NLS-1$
			parts[2] = new StringPart("notes", comment, characterEncoding); //$NON-NLS-1$
			
			//Workaround: http://rack.lighthouseapp.com/projects/22435/tickets/79-multipart-handling-incorrectly-assuming-file-upload
			for(int i=2;i>=0;i--) {
				((StringPart)parts[i]).setContentType(null);
			}
			
			parts[3] = new FilePart("attachments[1][file]", new AttachmentPartSource(attachment, content), attachment.getContentType(), null) { //$NON-NLS-1$
				//Workaround: avoid 'Content-Type image/png; charset=iso8859-1'
				public String getCharSet() {
					return null;
				};
			};
			method.setRequestEntity(new MultipartRequestEntity(parts, method.getParams()));
			
			response = executeMethod(method, submitIssueParser, monitor, HttpStatus.SC_OK, HttpStatus.SC_UNPROCESSABLE_ENTITY);
		} finally {
			if(monitor.isCanceled()) {
				throw new OperationCanceledException();
			} else {
				monitor.worked(1);
			}
		}
		
		if(response instanceof SubmitError) {
			SubmitError error = (SubmitError)response;
			for (String errMsg : error.errors) {
				errorCollector.accept(errMsg);
			}
			
			throw new RedmineApiInvalidDataException();
		}
	}
	
	private String getAuthenticityToken(IProgressMonitor monitor) throws RedmineApiErrorException {
		monitor.beginTask(Messages.PROGRESS_REQUEST_AUTHTOKEN, 1);
		
		GetMethod method = new GetMethod(URL_GET_AUTHENTICITY_TOKEN);
		String token = executeMethod(method, stringParser, monitor);
		
		if(monitor.isCanceled()) {
			throw new OperationCanceledException();
		} else {
			monitor.worked(1);
		}
		
		return token;
	}
	
	private void buildParser() {
		parserByClass = new HashMap<String, IModelParser<? extends AbstractPropertyContainer<?>>>();
		parserByClass.put(URL_ISSUE_STATUS, new AttributeParser<IssueStatuses>(IssueStatuses.class));
		parserByClass.put(URL_ISSUE_CATEGORIES, new AttributeParser<IssueCategories>(IssueCategories.class));
		parserByClass.put(URL_ISSUE_PRIORITIES, new AttributeParser<IssuePriorities>(IssuePriorities.class));
		parserByClass.put(URL_TRACKERS, new AttributeParser<Trackers>(Trackers.class));
		parserByClass.put(URL_CUSTOMFIELDS, new AttributeParser<CustomFields>(CustomFields.class));
		parserByClass.put(URL_USERS, new AttributeParser<Users>(Users.class));
		parserByClass.put(URL_QUERIES, new AttributeParser<Queries>(Queries.class));
		parserByClass.put(URL_PROJECTS, new AttributeParser<Projects>(Projects.class));
		parserByClass.put(URL_VERSIONS, new AttributeParser<Versions>(Versions.class));
		
		settingsParser = new SettingsParser();
		updatedIssuesParser = new TypedParser<UpdatedIssuesType>(UpdatedIssuesType.class);
		issueParser = new IssueParser(getConfiguration());
		issuesParser = new IssuesParser(getConfiguration());
		versionParser = new TypedParser<RedmineServerVersion>(RedmineServerVersion.class);
		attachmentParser = new AttachmentParser();

		stringParser = new StringParser();
		submitIssueParser = new SubmitedIssueParser();
	}
	
}
