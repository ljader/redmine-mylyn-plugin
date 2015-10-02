package net.sf.redmine_mylyn.internal.core.client;

import java.io.InputStream;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import net.sf.redmine_mylyn.api.client.IRedmineApiClient;
import net.sf.redmine_mylyn.api.client.RedmineApiIssueProperty;
import net.sf.redmine_mylyn.api.client.RedmineServerVersion;
import net.sf.redmine_mylyn.api.exception.RedmineApiErrorException;
import net.sf.redmine_mylyn.api.exception.RedmineApiInvalidDataException;
import net.sf.redmine_mylyn.api.model.Attachment;
import net.sf.redmine_mylyn.api.model.Configuration;
import net.sf.redmine_mylyn.api.model.Issue;
import net.sf.redmine_mylyn.api.model.TimeEntry;
import net.sf.redmine_mylyn.api.query.Query;
import net.sf.redmine_mylyn.core.RedmineCorePlugin;
import net.sf.redmine_mylyn.core.RedmineStatusException;
import net.sf.redmine_mylyn.core.RedmineUtil;
import net.sf.redmine_mylyn.core.client.IClient;
import net.sf.redmine_mylyn.internal.core.Messages;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskAttachmentSource;

public class Client implements IClient {

	private final IRedmineApiClient apiClient;

	public Client(final IRedmineApiClient apiClient) {
		this.apiClient = apiClient;
	}

	@Override
	public Configuration getConfiguration(){
		return apiClient.getConfiguration();
	}

	@Override
	public void updateConfiguration(final IProgressMonitor monitor) throws RedmineStatusException {
		try {
			apiClient.updateConfiguration(monitor);
		} catch (final RedmineApiErrorException e) {
			throw new RedmineStatusException(e, Messages.ERRMSG_CONFIGURATION_UPDATE_FAILED, e.getMessage());
		}
	}

	@Override
	public RedmineServerVersion checkClientConnection(final IProgressMonitor monitor) throws RedmineStatusException {

		RedmineServerVersion version;
		try {
			version = apiClient.detectServerVersion(monitor);
		} catch (final RedmineApiErrorException e) {
			throw new RedmineStatusException(e);
		}

		return version;
	}

	@Override
	public Issue getIssue(final int id, final IProgressMonitor monitor) throws RedmineStatusException {
		try {
			return apiClient.getIssue(id, monitor);
		} catch (final RedmineApiErrorException e) {
			throw new RedmineStatusException(e);
		}
	}

	@Override
	public Issue[] getIssues(final Set<String> taskIds, final IProgressMonitor monitor) throws RedmineStatusException {
		final int[] ids = new int[taskIds.size()];
		int lv=0;
		for(final String taskId : taskIds) {
			final int id = RedmineUtil.parseIntegerId(taskId);
			if(id>0) {
				ids[lv++] = id;
			}
		}

		try {
			return apiClient.getIssues(monitor, ids);
		} catch (final RedmineApiErrorException e) {
			throw new RedmineStatusException(e);
		}
	}

	@Override
	public int[] getUpdatedIssueIds(final Set<ITask> tasks, final Date updatedSince, final IProgressMonitor monitor) throws RedmineStatusException {
		final int[] ids = new int[tasks.size()];
		int lv=0;
		for(final ITask task : tasks) {
			ids[lv++] = RedmineUtil.parseIntegerId(task.getTaskId());
		}

		try {
			return apiClient.getUpdatedIssueIds(ids, updatedSince, monitor);
		} catch (final RedmineApiErrorException e) {
			throw new RedmineStatusException(e);
		}
	}

	@Override
	public Issue[] query(final Query query, final IProgressMonitor monitor) throws RedmineStatusException {
		try {
			return apiClient.query(query, monitor);
		} catch (final RedmineApiErrorException e) {
			throw new RedmineStatusException(e);
		}
	}

	@Override
	public int createIssue(final Issue issue, final IProgressMonitor monitor) throws RedmineStatusException {
		final ErrrorCollector errorCollector = new ErrrorCollector();
		try {
			return apiClient.createIssue(issue, errorCollector, monitor).getId();
		} catch (final RedmineApiErrorException e) {
			throw new RedmineStatusException(e);
		} catch (final RedmineApiInvalidDataException e) {
			final IStatus status = new Status(IStatus.ERROR, RedmineCorePlugin.PLUGIN_ID, errorCollector.getErrorString(), e);
			throw new RedmineStatusException(status);
		}
	}

	@Override
	public void updateIssue(final Issue issue, final String comment, final Date lastModified, final TimeEntry timeEntry, final IProgressMonitor monitor) throws RedmineStatusException {
		final ErrrorCollector errorCollector = new ErrrorCollector();
		try {
			apiClient.updateIssue(issue, comment, lastModified, timeEntry, errorCollector, monitor);
		} catch (final RedmineApiErrorException e) {
			throw new RedmineStatusException(e);
		} catch (final RedmineApiInvalidDataException e) {
			final IStatus status = new Status(IStatus.ERROR, RedmineCorePlugin.PLUGIN_ID, errorCollector.getErrorString(), e);
			throw new RedmineStatusException(status);
		}
	}

	@Override
	public void updateIssue(final int issueId, final Map<RedmineApiIssueProperty, String> issueValues, final String comment, final TimeEntry timeEntry, final IProgressMonitor monitor) throws RedmineStatusException {
		final ErrrorCollector errorCollector = new ErrrorCollector();
		try {
			apiClient.updateIssue(issueId, issueValues, comment, timeEntry, errorCollector, monitor);
		} catch (final RedmineApiErrorException e) {
			throw new RedmineStatusException(e);
		} catch (final RedmineApiInvalidDataException e) {
			final IStatus status = new Status(IStatus.ERROR, RedmineCorePlugin.PLUGIN_ID, errorCollector.getErrorString(), e);
			throw new RedmineStatusException(status);
		}
	}

	@Override
	public void uploadAttachment(final int issueId, final String fileName, final String description, final AbstractTaskAttachmentSource source, final String comment, final IProgressMonitor monitor) throws RedmineStatusException {
		final ErrrorCollector errorCollector = new ErrrorCollector();
		try {
			final Attachment attachment = new Attachment();
			attachment.setFilename(fileName);
			attachment.setDescription(description);
			attachment.setFilesize(source.getLength());
			attachment.setContentType(source.getContentType());

			apiClient.uploadAttachment(issueId, attachment, source.createInputStream(monitor), comment, errorCollector, monitor);
		} catch (final CoreException e) {
			new RedmineStatusException(e.getStatus());
		} catch (final RedmineApiErrorException e) {
			throw new RedmineStatusException(e);
		} catch (final RedmineApiInvalidDataException e) {
			final IStatus status = new Status(IStatus.ERROR, RedmineCorePlugin.PLUGIN_ID, errorCollector.getErrorString(), e);
			throw new RedmineStatusException(status);
		}
	}

	@Override
	public InputStream getAttachmentContent(final int attachmentId, final String fileName, final IProgressMonitor monitor) throws RedmineStatusException {
		try {
			return apiClient.getAttachmentContent(attachmentId, fileName, monitor);
		} catch (final RedmineApiErrorException e) {
			throw new RedmineStatusException(e);
		}
	}
}
