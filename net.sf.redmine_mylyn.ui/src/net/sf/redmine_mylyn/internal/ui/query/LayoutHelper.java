package net.sf.redmine_mylyn.internal.ui.query;

import java.util.Map;

import net.sf.redmine_mylyn.api.query.IQueryField;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

class LayoutHelper {


	public static void placeListElements(final Composite parent, int columns, final Map<? extends IQueryField, StructuredViewer> lstSearchValues, final Map<? extends IQueryField, ComboViewer> lstSearchOperators) {
		
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(columns * 2, true);
		composite.setLayout(layout);

		GridData commonGridData = new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false);
		commonGridData.horizontalAlignment = SWT.FILL;

		GridData listGridData = new GridData();
		listGridData.verticalSpan = 2;
		listGridData.heightHint = 100;
		listGridData.widthHint = 85;
		
		IQueryField[] fields = lstSearchValues.keySet().toArray(new IQueryField[lstSearchValues.size()]);
		
		for(int i=1; i<=fields.length; i++) {
			IQueryField queryField = fields[i-1];

			Label label = new Label(composite, SWT.NONE);
			label.setText(queryField.getLabel());
			label.setLayoutData(commonGridData);
			
			Control control = lstSearchValues.get(queryField).getControl();
			control.setParent(composite);
			control.setLayoutData(listGridData);

			if (i % columns == 0 || i == lstSearchValues.size()) {
				int sv = (i % columns == 0) ? i - columns : i - i % columns;
				if (i % columns != 0) {
					listGridData = new GridData();
					listGridData.verticalSpan = 2;
					listGridData.heightHint = 100;
					listGridData.horizontalSpan = (columns-(i % columns)) * 2 +1;
					listGridData.widthHint = 85;
					control.setLayoutData(listGridData);
				}
				for (int j = sv; j < i; j++) {
					IQueryField tmpSearchField = fields[j];
					
					ComboViewer combo = lstSearchOperators.get(tmpSearchField);
					combo.getControl().setParent(composite);
					combo.getControl().setLayoutData(commonGridData);
				}
			}
		}
	}
	
	public static void placeTextElements(final Composite parent, final Map<? extends IQueryField, Text> txtSearchValues, final Map<? extends IQueryField, ComboViewer> txtSearchOperators) {
		
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(3, false);
		composite.setLayout(layout);
		
		GridData commonGridData = new GridData();
		GridData textGridData = new GridData(SWT.FILL, SWT.CENTER, true, true);
		textGridData.minimumWidth=300;

		for (IQueryField queryField : txtSearchValues.keySet()) {
			Label label = new Label(composite, SWT.NONE);
			label.setText(queryField.getLabel());
			label.setLayoutData(commonGridData);

			ComboViewer combo = txtSearchOperators.get(queryField);
			combo.getControl().setParent(composite);
			combo.getControl().setLayoutData(commonGridData);

			Control text = txtSearchValues.get(queryField);
			text.setParent(composite);
			text.setLayoutData(textGridData);
		}
	}
	
}
