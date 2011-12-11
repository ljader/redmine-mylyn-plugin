package net.sf.redmine_mylyn.internal.ui.editor.helper;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.mylyn.tasks.ui.editors.AbstractAttributeEditor;
import org.eclipse.mylyn.tasks.ui.editors.LayoutHint;
import org.eclipse.mylyn.tasks.ui.editors.LayoutHint.ColumnSpan;
import org.eclipse.mylyn.tasks.ui.editors.LayoutHint.RowSpan;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class AttributePartLayoutHelper {

	/* Values from TaskEditorAttributePart */
	private static final int LABEL_WIDTH = 110;

	private static final int COLUMN_WIDTH = 140;

	private static final int COLUMN_GAP = 20;

	private static final int MULTI_COLUMN_WIDTH = COLUMN_WIDTH + 5 + COLUMN_GAP + LABEL_WIDTH + 5 + COLUMN_WIDTH;

	private static final int MULTI_ROW_HEIGHT = 55;
	
	private int numColumns;
	
	private int currentColumn;
	
	private int currentPriority = LayoutHint.DEFAULT_PRIORITY;
	
	private final FormToolkit toolkit;
	
	private final Composite parent;
	
	private boolean dynamic = false;
	
	public AttributePartLayoutHelper(Composite attributeComposite, FormToolkit toolkit, boolean dynamic) {
		this(attributeComposite,  toolkit);
		this.dynamic = dynamic;
	}
	
	public AttributePartLayoutHelper(Composite attributeComposite, FormToolkit toolkit) {
		this.parent = attributeComposite;
		this.toolkit = toolkit;
		
		if (attributeComposite.getLayout() instanceof GridLayout) {
			numColumns = ((GridLayout)attributeComposite.getLayout()).numColumns;

			int spanCount = 0;
			Control[] controls = attributeComposite.getChildren();
			for (int i = 1; i < controls.length; i+=2) { //+=2: skip label
				GridData gd = (GridData)controls[i].getLayoutData();
				spanCount += gd.horizontalSpan+1; //+1 label column
			}
			currentColumn = spanCount%numColumns;
		}
	}
	
	/**
 	 * Code adapted from TaskEditorAttributePart
	 *
	 * @param editor
	 */
	public void setLayoutData(AbstractAttributeEditor editor) {
		if(numColumns<1)
			return;

		//goto next row/first column if col-span and/or row-span changed
		int priority = (editor.getLayoutHint() != null) ? editor.getLayoutHint().getPriority() : LayoutHint.DEFAULT_PRIORITY;
		if (priority != currentPriority) {
			currentPriority = priority;
			if (currentColumn > 0) {
				while (currentColumn < numColumns) {
					toolkit.createLabel(parent, ""); //$NON-NLS-1$
					currentColumn++;
				}
				currentColumn = 0;
			}
		}
		
		LayoutHint layoutHint = editor.getLayoutHint();
		boolean isMultiRowEditor = layoutHint!=null && layoutHint.rowSpan==RowSpan.MULTIPLE;
		
		Label label = editor.getLabelControl();
		GridData labelGridData = GridDataFactory.fillDefaults()
				.align(SWT.LEFT, isMultiRowEditor ? SWT.TOP : SWT.CENTER)
				.hint((dynamic ? SWT.DEFAULT : LABEL_WIDTH), SWT.DEFAULT)
				.create();
		
		if (!label.getText().endsWith(":")) {
			label.setText(label.getText() + ":"); //$NON-NLS-1$
		}
		
		if (currentColumn > 1) {
			labelGridData.horizontalIndent = COLUMN_GAP;
			if(!dynamic) {
				labelGridData.widthHint = LABEL_WIDTH + COLUMN_GAP;
			}
		}
		label.setLayoutData(labelGridData);
		currentColumn++;

		GridData controlGridData = new GridData(SWT.FILL, SWT.CENTER, dynamic, false);
		if (layoutHint != null && !(layoutHint.rowSpan == RowSpan.SINGLE && layoutHint.columnSpan == ColumnSpan.SINGLE)) {
			if (isMultiRowEditor) {
				controlGridData.heightHint = MULTI_ROW_HEIGHT;
			}
			if (layoutHint.columnSpan == ColumnSpan.SINGLE) {
				controlGridData.widthHint = COLUMN_WIDTH;
				controlGridData.horizontalSpan = 1;
			} else {
				controlGridData.widthHint = MULTI_COLUMN_WIDTH;
				controlGridData.horizontalSpan = numColumns - currentColumn;
			}
		} else {
			controlGridData.widthHint = COLUMN_WIDTH;
			controlGridData.horizontalSpan = 1;
		}
		editor.getControl().setLayoutData(controlGridData);
		
		currentColumn += controlGridData.horizontalSpan;
		currentColumn %= numColumns;

	}
	
}
