package net.sf.redmine_mylyn.internal.ui.action;

import net.sf.redmine_mylyn.core.RedmineAttribute;
import net.sf.redmine_mylyn.core.RedmineOperation;

import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;

public class RedmineStatusAttributeChangeAction extends AbstractRedmineAttributeChangeAction {
	
	public RedmineStatusAttributeChangeAction(String statusId, String statusName, ITask[] tasks) {
		super(RedmineAttribute.STATUS_CHG, statusId, statusName, tasks);
	}
	
	@Override
	protected void setClosedTaskValue(TaskAttribute attribute, String value, TaskData taskData, TaskDataModel model) {
		super.setClosedTaskValue(attribute, value, taskData, model);

		TaskAttribute markasOperation = taskData.getRoot().getAttribute(TaskAttribute.PREFIX_OPERATION + RedmineOperation.markas.toString());
		if(markasOperation!=null) {
			TaskAttribute operation = taskData.getRoot().getAttribute(TaskAttribute.OPERATION);
			taskData.getAttributeMapper().setValue(operation, RedmineOperation.markas.toString());
			model.attributeChanged(operation);
		}
	}
	
}
