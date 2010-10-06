package net.sf.redmine_mylyn.internal.ui.query;

import net.sf.redmine_mylyn.api.model.Project;
import net.sf.redmine_mylyn.api.query.CompareOperator;
import net.sf.redmine_mylyn.api.query.QueryField;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;

class ProjectSelectionListener implements ISelectionChangedListener {
		private final RedmineRepositoryQueryPage page;
		
		ProjectSelectionListener(RedmineRepositoryQueryPage page) {
			this.page = page;
		}

		public void selectionChanged(SelectionChangedEvent event) {
			if(event.getSelection() instanceof IStructuredSelection) {
				IStructuredSelection selection = (IStructuredSelection)event.getSelection();
				
				
				if ( event.getSource() instanceof ComboViewer && !(selection.getFirstElement() instanceof CompareOperator)) {
					page.queryStructuredViewer.get(QueryField.PROJECT).setSelection(new StructuredSelection());
				} else {
					
					page.switchOperatorState();
					page.clearSettings();
					
					Object selected = selection.getFirstElement();
					if (selected instanceof Project) {
						page.updateProjectAttributes((Project)selected);
					} else {
						page.updateProjectAttributes(null);
					}
				}
				
			}
		}
	}