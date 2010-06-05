package net.sf.redmine_mylyn.internal.api.client;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.redmine_mylyn.api.client.RedmineApiStatusException;
import net.sf.redmine_mylyn.api.model.Configuration;
import net.sf.redmine_mylyn.api.model.container.AbstractPropertyContainer;
import net.sf.redmine_mylyn.api.model.container.IssueCategories;
import net.sf.redmine_mylyn.api.model.container.IssuePriorities;
import net.sf.redmine_mylyn.api.model.container.IssueStatuses;
import net.sf.redmine_mylyn.api.model.container.Trackers;
import net.sf.redmine_mylyn.internal.api.parser.AttributeParser;
import net.sf.redmine_mylyn.internal.api.parser.IModelParser;

import org.apache.commons.httpclient.methods.GetMethod;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.mylyn.commons.net.AbstractWebLocation;
import org.eclipse.mylyn.commons.net.Policy;

public class Api_2_7_ClientImpl extends AbstractClient {

	private final static String URL_ISSUE_STATUS = "/mylyn/issuestatus";
	private final static String URL_ISSUE_CATEGORIES = "/mylyn/issuecategories";
	private final static String URL_ISSUE_PRIORITIES = "/mylyn/issuepriorities";
	private final static String URL_TRACKERS = "/mylyn/trackers";
	
	private Map<String, IModelParser<? extends AbstractPropertyContainer<?>>> parserByClass;

	private Configuration configuration;
	
	public Api_2_7_ClientImpl(AbstractWebLocation location) {
		super(location);
		
		buildParser();
	}
	
	@Override
	public Configuration getConfiguration() {
		if(configuration==null) {
			configuration = new Configuration();
		}
		return configuration;
	}

	@Override
	public void updateConfiguration(IProgressMonitor monitor, boolean force) throws RedmineApiStatusException {
		//TODO force / one update every 24 hours
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
		
		getConfiguration().copy(conf);
	}

	private void buildParser() {
		parserByClass = new HashMap<String, IModelParser<? extends AbstractPropertyContainer<?>>>();
		parserByClass.put(URL_ISSUE_STATUS, new AttributeParser<IssueStatuses>(IssueStatuses.class));
		parserByClass.put(URL_ISSUE_CATEGORIES, new AttributeParser<IssueCategories>(IssueCategories.class));
		parserByClass.put(URL_ISSUE_PRIORITIES, new AttributeParser<IssuePriorities>(IssuePriorities.class));
		parserByClass.put(URL_TRACKERS, new AttributeParser<Trackers>(Trackers.class));
	}
}
