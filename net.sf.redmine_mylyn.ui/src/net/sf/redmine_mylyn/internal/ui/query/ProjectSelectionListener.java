package net.sf.redmine_mylyn.internal.ui.query;

import net.sf.redmine_mylyn.api.model.Project;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;

class ProjectSelectionListener implements ISelectionChangedListener {
		private final RedmineRepositoryQueryPage page;
		
		ProjectSelectionListener(RedmineRepositoryQueryPage page) {
			this.page = page;
		}

		public void selectionChanged(SelectionChangedEvent event) {
			if (!event.getSelection().isEmpty() && event.getSelection() instanceof IStructuredSelection) {
				
				Object selected = ((IStructuredSelection)event.getSelection()).getFirstElement();
				if (selected instanceof Project) {

					page.clearSettings();
//					switchOperatorState(true, false);
					page.updateProjectAttributes((Project)selected);
//					RedmineRepositoryQueryPage.this.updateCustomFieldFilter(project.getName());
				} else {
					page.clearSettings();
//					switchOperatorState(false, true);
					page.updateProjectAttributes(null);
//					RedmineRepositoryQueryPage.this.updateCustomFieldFilter(DATA_KEY_VALUE_CROSS_PROJECT);
				}

				//TODO
//				if (RedmineQueryPage.this.getContainer()!=null) {
//					RedmineQueryPage.this.getContainer().updateButtons();
//				}
			}
		}
	}