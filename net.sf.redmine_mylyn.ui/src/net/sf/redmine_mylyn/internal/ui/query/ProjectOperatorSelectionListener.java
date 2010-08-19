package net.sf.redmine_mylyn.internal.ui.query;

import net.sf.redmine_mylyn.api.query.CompareOperator;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;

class ProjectOperatorSelectionListener implements ISelectionChangedListener {
		
		private final RedmineRepositoryQueryPage page;
		
		ProjectOperatorSelectionListener(RedmineRepositoryQueryPage page) {
			this.page = page;
		}
		
		public void selectionChanged(SelectionChangedEvent event) {
			if (!event.getSelection().isEmpty() && event.getSelection() instanceof IStructuredSelection) {
				
				Object selected = ((IStructuredSelection)event.getSelection()).getFirstElement();
				if (selected==CompareOperator.IS) {
//					RedmineRepositoryQueryPage.this.clearSettings();
					page.switchOperatorState(true, false);
//					RedmineRepositoryQueryPage.this.updateProjectAttributes(project);
//					RedmineRepositoryQueryPage.this.updateCustomFieldFilter(project.getName());
				} else {
//					RedmineRepositoryQueryPage.this.clearSettings();
					page.switchOperatorState(false, true);
//					RedmineRepositoryQueryPage.this.updateProjectAttributes(null);
//					RedmineRepositoryQueryPage.this.updateCustomFieldFilter(DATA_KEY_VALUE_CROSS_PROJECT);
				}
				
				//TODO
//				if (RedmineQueryPage.this.getContainer()!=null) {
//					RedmineQueryPage.this.getContainer().updateButtons();
//				}
			}
		}
	}