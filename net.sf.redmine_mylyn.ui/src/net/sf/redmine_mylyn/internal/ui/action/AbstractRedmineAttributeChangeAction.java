package net.sf.redmine_mylyn.internal.ui.action;

import net.sf.redmine_mylyn.common.logging.ILogService;
import net.sf.redmine_mylyn.core.RedmineAttribute;
import net.sf.redmine_mylyn.core.RedmineCorePlugin;
import net.sf.redmine_mylyn.ui.RedmineUiPlugin;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.ITaskDataManager;
import org.eclipse.mylyn.tasks.core.data.ITaskDataWorkingCopy;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorInput;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public abstract class AbstractRedmineAttributeChangeAction extends Action {

	private RedmineAttribute attribute;

	protected final ITask[] tasks;

	protected String value;

	AbstractRedmineAttributeChangeAction(RedmineAttribute attribute, String value, ITask[] tasks) {
		this(attribute, value, value, tasks);
	}

	AbstractRedmineAttributeChangeAction(RedmineAttribute attribute, String value, String name, ITask[] tasks) {
		super(name, SWT.NONE);
		
		Assert.isNotNull(tasks);
		Assert.isNotNull(attribute);
		Assert.isTrue(tasks.length>0);
		
		this.attribute = attribute;
		this.value = value;
		this.tasks = tasks;
	}
	
	@Override
	public void run() {
		ITaskDataManager taskDataManager = TasksUi.getTaskDataManager();
		TaskRepository repository = TasksUi.getRepositoryManager().getRepository(RedmineCorePlugin.REPOSITORY_KIND, tasks[0].getRepositoryUrl());
		
		for (ITask task : tasks) {
			if(taskDataManager.hasTaskData(task)) {
				try {
					TaskAttribute attribute = null;
										
					if(isTaskOpen(task)) {
						TaskData taskData = taskDataManager.getTaskData(task);
						attribute = taskData.getRoot().getAttribute(this.attribute.getTaskKey());
						setOpenTaskValue(attribute, value, taskData);
					} else {
						ITaskDataWorkingCopy copy = taskDataManager.getWorkingCopy(task);
						TaskDataModel model = new TaskDataModel(repository, task, copy);
						TaskData taskData = model.getTaskData();
						
						attribute = taskData.getRoot().getAttribute(this.attribute.getTaskKey());
						if(!attribute.getValue().equals(value)) {
							setClosedTaskValue(attribute, value, taskData, model);
							model.save(new NullProgressMonitor());
						}
					}
					
					RedmineUiPlugin.getDefault().notifyAttributeChanged(task, attribute);
				} catch (CoreException e) {
					ILogService log = RedmineUiPlugin.getLogService(getClass());
					log.error(e, "Can't set value of attribute {0}", attribute.name());
				}
			}
		}
	}
	
	protected void setClosedTaskValue(TaskAttribute attribute, String value, TaskData taskData, TaskDataModel model) {
		attribute.setValue(value);
		model.attributeChanged(attribute);
	}

	protected void setOpenTaskValue(TaskAttribute attribute, String value, TaskData taskData) {
		attribute.setValue(value);
	}
	
	private boolean isTaskOpen(ITask task) {
		IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
		for (IWorkbenchWindow window : windows) {
			IEditorReference[] editorReferences = window.getActivePage().getEditorReferences();
			for (IEditorReference editorReference : editorReferences) {
				try {
					if (editorReference.getEditorInput() instanceof TaskEditorInput) {
						TaskEditorInput input = (TaskEditorInput) editorReference.getEditorInput();
						if (input.getTask()!=null && input.getTask()==task) {
							return true;
						}
					}
				} catch (PartInitException e) {
					// ignore
				}
			}
		}
		return false;
	}
}
