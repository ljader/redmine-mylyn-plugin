package net.sf.redmine_mylyn.internal.ui.action;

import net.sf.redmine_mylyn.common.logging.ILogService;
import net.sf.redmine_mylyn.core.RedmineAttribute;
import net.sf.redmine_mylyn.core.RedmineCorePlugin;
import net.sf.redmine_mylyn.internal.ui.Messages;
import net.sf.redmine_mylyn.ui.RedmineTasksUiUtil;
import net.sf.redmine_mylyn.ui.RedmineUiPlugin;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.ITaskDataManager;
import org.eclipse.mylyn.tasks.core.data.ITaskDataWorkingCopy;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.eclipse.mylyn.tasks.ui.TasksUi;

public abstract class AbstractRedmineAttributeChangeAction extends Action {

	protected final RedmineAttribute[] attributes;

	protected final ITask[] tasks;

	abstract protected String getValue(RedmineAttribute attribute, TaskData taskData);

	public AbstractRedmineAttributeChangeAction(RedmineAttribute attribute, ITask... tasks) {
		this(new RedmineAttribute[]{attribute}, tasks);
	}
	
	public AbstractRedmineAttributeChangeAction(ITask task, RedmineAttribute... attributes) {
		this(attributes, new ITask[]{task});
	}
	
	public AbstractRedmineAttributeChangeAction(RedmineAttribute[] attributes, ITask[] tasks) {
		super();
		Assert.isNotNull(tasks);
		Assert.isNotNull(attributes);
		
		this.attributes = attributes;
		this.tasks = tasks;
	}

	
	@Override
	public void run() {
		ITaskDataManager taskDataManager = TasksUi.getTaskDataManager();
		TaskRepository repository = TasksUi.getRepositoryManager().getRepository(RedmineCorePlugin.REPOSITORY_KIND, tasks[0].getRepositoryUrl());
		
		for (ITask task : tasks) {
			if(taskDataManager.hasTaskData(task)) {
				String attributeName = null;
				try {
					
					boolean openTask = true;
					TaskDataModel model = RedmineTasksUiUtil.findOpenTaskModel(task);
					
					if(model==null) {
						openTask = false;
						ITaskDataWorkingCopy copy = taskDataManager.getWorkingCopy(task);
						model = new TaskDataModel(repository, task, copy);
					}

					TaskData taskData = model.getTaskData();
					
					
					for (RedmineAttribute redmineAttribute : attributes) {
						if(redmineAttribute!=null) {
							attributeName = redmineAttribute.name();
							TaskAttribute attribute = taskData.getRoot().getAttribute(redmineAttribute.getTaskKey());

							String newValue = getValue(redmineAttribute, taskData);
							if(!attribute.getValue().equals(newValue)) {
								if(openTask) {
									setOpenTaskValue(attribute, newValue, taskData, model);
								} else {
									setClosedTaskValue(attribute, newValue, taskData, model);
								}
							}
							
						}
					}

					if(!openTask) {
						model.save(new NullProgressMonitor());
					}

										
				} catch (CoreException e) {
					ILogService log = RedmineUiPlugin.getLogService(getClass());
					log.error(e, Messages.ERRMSG_CANT_SET_ATTRIBUTE_VALUE_X, attributeName);
				}
			}
		}
	}
	
	protected void setClosedTaskValue(TaskAttribute attribute, String value, TaskData taskData, TaskDataModel model) {
		attribute.setValue(value);
		model.attributeChanged(attribute);
	}

	protected void setOpenTaskValue(TaskAttribute attribute, String value, TaskData taskData, TaskDataModel model) {
		attribute.setValue(value);
		model.attributeChanged(attribute);
	}
	
}
