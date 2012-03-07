package net.sf.redmine_mylyn.internal.ui;

import net.sf.redmine_mylyn.api.model.Configuration;
import net.sf.redmine_mylyn.api.model.Project;
import net.sf.redmine_mylyn.core.IRedmineConstants;
import net.sf.redmine_mylyn.core.RedmineAttribute;
import net.sf.redmine_mylyn.core.RedmineRepositoryConnector;
import net.sf.redmine_mylyn.core.RedmineUtil;
import net.sf.redmine_mylyn.core.client.IClient;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;

public class RedmineRevisionHyperlink implements IHyperlink {

	private final IRegion region;
	private final TaskRepository taskRepository;
	private final ITask task;
	private final String revision;

	public RedmineRevisionHyperlink(IRegion region, TaskRepository taskRepository, ITask task, String revision) {
		super();
		
		this.region = region;
		this.taskRepository = taskRepository;
		this.task = task;
		this.revision = revision;
	}

	public IRegion getHyperlinkRegion() {
		return region;
	}

	public String getHyperlinkText() {
		return String.format(Messages.OPEN_REVISION_INTEGER_STRING, revision, taskRepository.getRepositoryLabel());
	}

	public String getTypeLabel() {
		return null;
	}

	public void open() {
			String product = "";
			
			try {
				TaskData taskData = TasksUi.getTaskDataManager().getTaskData(task);
				product = taskData.getRoot().getMappedAttribute(RedmineAttribute.PROJECT.getTaskKey()).getValue();
				
				AbstractRepositoryConnector reposConn = TasksUi.getRepositoryConnector(taskRepository.getConnectorKind());
				if (reposConn instanceof RedmineRepositoryConnector) {
					RedmineRepositoryConnector redmineReposConnector = (RedmineRepositoryConnector)reposConn;
					
					IClient client = redmineReposConnector.getClientManager().getClient(taskRepository);
					Configuration conf = client.getConfiguration();
					if(conf != null) {
						Project project = conf.getProjects().getById(RedmineUtil.parseIntegerId(product));
						if(project != null) {
							product = ""+project.getIdentifier(); //$NON-NLS-1$
						}
					}
					
				}
			} catch (Exception e) {
				product = ""; //$NON-NLS-1$
			}
				
			
			StringBuilder builder = new StringBuilder(taskRepository.getRepositoryUrl());
			builder.append(IRedmineConstants.REDMINE_URL_REVISION);
			builder.append(product);
			builder.append('/');
			builder.append(revision);
			TasksUiUtil.openUrl(builder.toString());
		}

}