package net.sf.redmine_mylyn.internal.ui.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.redmine_mylyn.ui.RedmineUiPlugin;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskContainer;
import org.eclipse.swt.widgets.Menu;

abstract public class AbstractRedmineTaskListContributionItem extends ContributionItem {

	abstract protected MenuManager getSubMenuManager();
	
	private List<ITask> taskList;
	
	@Override
	public void fill(Menu menu, int index) {
		MenuManager subMenuManager = getSubMenuManager();
		if(subMenuManager!=null) {
			for (IContributionItem item : subMenuManager.getItems()) {
				item.fill(menu, index++);
			}
		}
	}
	
	protected List<ITask> getSelectedTasks() {
		if(taskList==null) {
			IStructuredSelection selection = RedmineUiPlugin.getDefault().getTaskListSelection();
			taskList = new ArrayList<ITask>();
			
			if (selection!=null) {
				for (Iterator<?> iterator = selection.iterator(); iterator.hasNext();) {
					Object selectedElem = iterator.next();
					
					if(selectedElem instanceof ITask) {
						collectTask((ITask)selectedElem, taskList);
					} else if (selectedElem instanceof ITaskContainer) {
						collectTasks((ITaskContainer)selectedElem, taskList);
					}
				}
			}
			
		}
		
		return taskList;
	}
	
	protected void collectTask(ITask task, List<ITask> collected) {
		if(!collected.contains(task)) {
			collected.add(task);

			if (task instanceof ITaskContainer) {
				collectTasks((ITaskContainer)task, collected);
			}
		}
	}

	protected void collectTasks(ITaskContainer container, List<ITask> collected) {
		for (ITask task : container.getChildren()) {
			collectTask(task, collected);
		}
	}
}
