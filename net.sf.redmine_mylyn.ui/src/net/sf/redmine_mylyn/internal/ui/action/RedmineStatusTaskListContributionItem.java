package net.sf.redmine_mylyn.internal.ui.action;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.sf.redmine_mylyn.common.logging.ILogService;
import net.sf.redmine_mylyn.core.RedmineAttribute;
import net.sf.redmine_mylyn.internal.ui.Messages;
import net.sf.redmine_mylyn.ui.RedmineUiPlugin;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.data.ITaskDataManager;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.ui.TasksUi;

public class RedmineStatusTaskListContributionItem extends AbstractRedmineTaskListContributionItem {

	private MenuManager subMenuManager;
	
	@Override
	protected MenuManager getSubMenuManager() {
		if(subMenuManager==null) {
			subMenuManager = new MenuManager();
			
			Map<String, String> statusMap = buildStatusMap();
			if(statusMap!=null) {
				
				for (Entry<String, String> entry : statusMap.entrySet()) {
					List<ITask> taskList = getSelectedTasks();
					ITask[] tasks = taskList.toArray(new ITask[taskList.size()]);
					
					IContributionItem item = new ActionContributionItem(new RedmineStatusAttributeChangeAction(entry.getKey(), entry.getValue(), tasks));
					subMenuManager.add(item);
				}
			}
		}
		
		return subMenuManager;
	}

	private Map<String, String> buildStatusMap() {

		Map<String, String> statusMap = null;
		List<ITask> tasks = getSelectedTasks();
		
		ITaskDataManager taskDataManager = TasksUi.getTaskDataManager();
		
		try {
			if (tasks.size()>0) {
				ITask firstTask = tasks.get(0);
				String repositoryUrl = firstTask.getRepositoryUrl();
				
				if (repositoryUrl!=null && taskDataManager.hasTaskData(firstTask)) {
					TaskData taskData = taskDataManager.getTaskData(firstTask);
					TaskAttribute attribute = taskData.getRoot().getAttribute(RedmineAttribute.STATUS.getTaskKey());
					Set<String> commonIds = new HashSet<String>(attribute.getOptions().keySet());
					
					for (ITask task : tasks) {
						if (task.getRepositoryUrl().equals(repositoryUrl)) { //Same-Repository-Policy
							if (taskDataManager.hasTaskData(task)) {
								taskData = taskDataManager.getTaskData(task);
								attribute = taskData.getRoot().getAttribute(RedmineAttribute.STATUS.getTaskKey());
								commonIds.retainAll(attribute.getOptions().keySet());
							}
						} else {
							return null; //Same-Repository-Policy
						}
					}
					
					if(commonIds.size()>0) {
						statusMap = new HashMap<String, String>(commonIds.size());
						for (String key : commonIds) {
							statusMap.put(key, attribute.getOption(key));
						}
					}
				}
			}
			
		} catch (NullPointerException e) {
			ILogService log = RedmineUiPlugin.getLogService(getClass());
			log.error(e, Messages.ERRMSG_CANT_FILL_MARKAS_MENU);
		} catch (CoreException e) {
			ILogService log = RedmineUiPlugin.getLogService(getClass());
			log.error(e, Messages.ERRMSG_CANT_FILL_MARKAS_MENU);
		}
		
		return statusMap;
	}
	
	
}
