package net.sf.redmine_mylyn.internal.core;

import java.io.InputStream;

import net.sf.redmine_mylyn.core.RedmineRepositoryConnector;
import net.sf.redmine_mylyn.core.RedmineStatusException;
import net.sf.redmine_mylyn.core.RedmineUtil;
import net.sf.redmine_mylyn.core.client.IClient;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskAttachmentHandler;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskAttachmentSource;
import org.eclipse.mylyn.tasks.core.data.TaskAttachmentMapper;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;

public class RedmineAttachmentHandler extends AbstractTaskAttachmentHandler {
	
	final private RedmineRepositoryConnector connector;
	
	public RedmineAttachmentHandler(RedmineRepositoryConnector connector) {
		super();
		this.connector = connector;
	}

	@Override
	public boolean canGetContent(TaskRepository repository, ITask task) {
		return true;
	}

	@Override
	public boolean canPostContent(TaskRepository repository, ITask task) {
		return true;
	}

	@Override
	public InputStream getContent(TaskRepository repository, ITask task, TaskAttribute attachmentAttribute, IProgressMonitor monitor) throws CoreException {
		TaskAttachmentMapper attachment = TaskAttachmentMapper.createFrom(attachmentAttribute);
		try {
			IClient client;
			client = connector.getClientManager().getClient(repository);
			return client.getAttachmentContent(RedmineUtil.parseIntegerId(attachment.getAttachmentId()), attachment.getFileName(), monitor);
		} catch (RedmineStatusException e) {
			throw new CoreException(e.getStatus());
		}
	}

	@Override
	public void postContent(TaskRepository repository, ITask task, AbstractTaskAttachmentSource source, String comment, TaskAttribute attachmentAttribute, IProgressMonitor monitor) throws CoreException {
		String fileName = source.getName();
		String description = source.getDescription();
		
		if (attachmentAttribute!=null) {
			TaskAttachmentMapper mapper = TaskAttachmentMapper.createFrom(attachmentAttribute);
			if (mapper.getFileName() != null) {
				fileName = mapper.getFileName();
			}
			if (mapper.getComment() != null) {
				comment = mapper.getComment();
			}
			if (mapper.getDescription() != null) {
				description = mapper.getDescription();
			}
		}
		
		try {
			IClient client = connector.getClientManager().getClient(repository);
			client.uploadAttachment(RedmineUtil.parseIntegerId(task.getTaskId()), fileName, description, source, comment, monitor);
		} catch (RedmineStatusException e) {
			throw new CoreException(e.getStatus());
		}
	}

}
