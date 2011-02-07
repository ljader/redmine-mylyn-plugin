package net.sf.redmine_mylyn.ui.editor;

import net.sf.redmine_mylyn.core.RedmineCorePlugin;
import net.sf.redmine_mylyn.internal.ui.editor.RedmineTaskEditorPage;

import org.eclipse.mylyn.tasks.ui.ITasksUiConstants;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPageFactory;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorInput;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.forms.editor.IFormPage;

public class RedmineTaskEditorPageFactory extends AbstractTaskEditorPageFactory {

	@Override
	public boolean canCreatePageFor(TaskEditorInput input) {
		if (input.getTask().getConnectorKind().equals(RedmineCorePlugin.REPOSITORY_KIND)) {
			return true;
		} else if (TasksUiUtil.isOutgoingNewTask(input.getTask(), RedmineCorePlugin.REPOSITORY_KIND)) {
			return true;
		}
		return false;
	}

	@Override
	public IFormPage createPage(TaskEditor parentEditor) {
		return new RedmineTaskEditorPage(parentEditor);
	}
	
	@Override
	public Image getPageImage() {
		return null;
	}

	@Override
	public String getPageText() {
		return "Redmine";
	}
	
	@Override
	public int getPriority() {
		return PRIORITY_TASK;
	}
	
	@Override
	public String[] getConflictingIds(TaskEditorInput input) {
		return new String[] { ITasksUiConstants.ID_PAGE_PLANNING };
	}

}
