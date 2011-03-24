package net.sf.redmine_mylyn.internal.ui.action;

import net.sf.redmine_mylyn.internal.ui.Images;
import net.sf.redmine_mylyn.internal.ui.Messages;
import net.sf.redmine_mylyn.ui.RedmineUiPlugin;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylyn.tasks.core.ITask;

public class RedmineResetUncapturedActivityTimeAction extends Action {

	protected final ITask task;
	
	public RedmineResetUncapturedActivityTimeAction(ITask task) {
		this.task = task;
	}
	
	@Override
	public ImageDescriptor getImageDescriptor() {
		return Images.getImageDescriptor(Images.FIND_CLEAR);
	}
	
	@Override
	public ImageDescriptor getDisabledImageDescriptor() {
		return Images.getImageDescriptor(Images.FIND_CLEAR_DISABLED);
	}
	
	@Override
	public String getToolTipText() {
		return Messages.RESET_UNCAPTURED_ACTIVETIME;
	}
	
	@Override
	public void run() {
		if(task!=null) {
			RedmineUiPlugin.getDefault().getSpentTimeManager().resetUncapturedSpentTime(task);
		}
	}
	
}
