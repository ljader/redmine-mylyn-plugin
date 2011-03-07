package net.sf.redmine_mylyn.ui;

import net.sf.redmine_mylyn.api.model.Configuration;
import net.sf.redmine_mylyn.api.model.Project;
import net.sf.redmine_mylyn.api.model.TimeEntryActivity;
import net.sf.redmine_mylyn.api.model.container.TimeEntryActivities;
import net.sf.redmine_mylyn.core.RedmineAttribute;
import net.sf.redmine_mylyn.core.RedmineCorePlugin;
import net.sf.redmine_mylyn.core.RedmineRepositoryConnector;
import net.sf.redmine_mylyn.internal.ui.editor.RedmineTaskEditorPage;

import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.editor.IFormPage;

public class RedmineTasksUiUtil {

	public static TimeEntryActivity getDefaultTimeEntryActivity(ITask task, TaskData taskData) {
		TimeEntryActivity activity = null;
		AbstractRepositoryConnector connector = TasksUi.getRepositoryConnector(RedmineCorePlugin.REPOSITORY_KIND);
		
		if (connector!=null && connector instanceof RedmineRepositoryConnector){
			
			Configuration conf = ((RedmineRepositoryConnector) connector)
			.getRepositoryConfiguration(TasksUi
					.getRepositoryManager().getRepository(
							task.getConnectorKind(),
							task.getRepositoryUrl()));				
			
			Integer projectId = taskData.getAttributeMapper().getIntegerValue(
					taskData.getRoot().getAttribute(RedmineAttribute.PROJECT.getTaskKey()));
			
			Project project = conf.getProjects().getById(projectId.intValue());
			
			if(project!=null) {
				TimeEntryActivities activities = project.getTimeEntryActivities();
				activity = activities.getDefault();
				if(activity==null && activities.getAll().size()>0) {
					activity = activities.getAll().get(0);
				}
			}
		}
		
		return activity;
	}

	public static  boolean isTaskOpen(ITask task) {
		IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
		for (IWorkbenchWindow window : windows) {
			IEditorReference[] editorReferences = window.getActivePage().getEditorReferences();
			for (IEditorReference editorReference : editorReferences) {
				try {
					if (editorReference.getEditorInput() instanceof TaskEditorInput) {
						TaskEditorInput input = (TaskEditorInput) editorReference.getEditorInput();
						if (input.getTask()!=null && (input.getTask()==task || input.getTask().getHandleIdentifier().equals(task.getHandleIdentifier()))) {
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

	public static TaskDataModel findOpenTaskModel(ITask task) {
		IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
		for (IWorkbenchWindow window : windows) {
			IEditorReference[] editorReferences = window.getActivePage().getEditorReferences();
			for (IEditorReference editorReference : editorReferences) {
				
				IEditorPart editorPart = editorReference.getEditor(false);
				if (editorPart instanceof TaskEditor) {
					
					TaskEditor taskEditorPart = (TaskEditor)editorPart;
					IFormPage page = taskEditorPart.findPage(RedmineTaskEditorPage.ID);
					if(page!=null) {

						TaskDataModel model = ((RedmineTaskEditorPage)page).getModel();
						ITask editorTask = model.getTask();
						if (editorTask!=null && (editorTask==task || editorTask.getHandleIdentifier().equals(task.getHandleIdentifier()))) {
							return model;
						}
					}
				}
			}
		}
		return null;
	}
	
}
