package net.sf.redmine_mylyn.internal.ui.action;

import java.util.Locale;

import net.sf.redmine_mylyn.api.model.Configuration;
import net.sf.redmine_mylyn.api.model.Project;
import net.sf.redmine_mylyn.api.model.TimeEntryActivity;
import net.sf.redmine_mylyn.api.model.container.TimeEntryActivities;
import net.sf.redmine_mylyn.core.RedmineAttribute;
import net.sf.redmine_mylyn.core.RedmineCorePlugin;
import net.sf.redmine_mylyn.core.RedmineRepositoryConnector;
import net.sf.redmine_mylyn.internal.ui.Images;
import net.sf.redmine_mylyn.ui.RedmineUiPlugin;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.ui.TasksUi;

public class RedmineCaptureActivityTimeAction extends AbstractRedmineAttributeChangeAction {

	private final ITask task;
	
	public RedmineCaptureActivityTimeAction(ITask task) {
		super(task, RedmineAttribute.TIME_ENTRY_HOURS, RedmineAttribute.TIME_ENTRY_ACTIVITY);
		this.task = task;
	}
	
	@Override
	protected String getValue(RedmineAttribute attribute, TaskData taskData) {
		switch (attribute) {
		case TIME_ENTRY_HOURS:
			
			long milisec = RedmineUiPlugin.getDefault().getSpentTimeManager().getAndClearUncapturedSpentTime(task);
			float hours = ((float)Math.ceil(milisec/1000/60))/60;
			//TODO without Local / implement universal
			return String.format(Locale.ENGLISH, "%.2f", hours);
			
		case TIME_ENTRY_ACTIVITY:
			/*
			 * Priority:
			 * 1 - current value
			 * 2 - default value
			 * 3 - first value
			 */
			
			String value = "";
			TaskAttributeMapper taskAttributeMapper = taskData.getAttributeMapper();
			//TODO doesn't work - taskData isn't the same as in editor
			Integer activityId = taskAttributeMapper.getIntegerValue(taskData.getRoot().getAttribute(RedmineAttribute.TIME_ENTRY_ACTIVITY.getTaskKey()));

			if(activityId==null) {
				AbstractRepositoryConnector connector = TasksUi.getRepositoryConnector(RedmineCorePlugin.REPOSITORY_KIND);
				if (connector!=null && connector instanceof RedmineRepositoryConnector){
					
					Configuration conf = ((RedmineRepositoryConnector) connector)
					.getRepositoryConfiguration(TasksUi
							.getRepositoryManager().getRepository(
									task.getConnectorKind(),
									task.getRepositoryUrl()));				
					
					Integer projectId = taskAttributeMapper.getIntegerValue(taskData.getRoot().getAttribute(RedmineAttribute.PROJECT.getTaskKey()));
					Project project = conf.getProjects().getById(projectId.intValue());
					
					if(project!=null) {
						TimeEntryActivities activities = project.getTimeEntryActivities();
						TimeEntryActivity activity = activities.getDefault();
						if(activity==null && activities.getAll().size()>0) {
							activities.getAll().get(0);
						}
						if(activity!=null) {
							value = Integer.toString(activity.getId());
						}
					}
				}
			} else {
				value = activityId.toString();
			}
			
			return value;
			
		}
		
		return "";
	}
	
	@Override
	public ImageDescriptor getImageDescriptor() {
		return Images.getImageDescriptor(Images.REPLY);
	}
}
