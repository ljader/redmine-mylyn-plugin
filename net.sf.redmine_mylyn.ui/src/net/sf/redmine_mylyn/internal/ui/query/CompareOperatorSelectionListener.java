package net.sf.redmine_mylyn.internal.ui.query;

import net.sf.redmine_mylyn.api.query.CompareOperator;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Control;

public class CompareOperatorSelectionListener implements
		ISelectionChangedListener {

	private final Control control;

	CompareOperatorSelectionListener(Control control) {
		this.control = control;
	}

	public void selectionChanged(SelectionChangedEvent event) {

		if (event.getSelection() instanceof IStructuredSelection) {
			IStructuredSelection selection = (IStructuredSelection)event.getSelection();

			Object selected = selection.getFirstElement();
			control.setEnabled(selected instanceof CompareOperator && ((CompareOperator)selected).isValueBased());
		}
	}

}
