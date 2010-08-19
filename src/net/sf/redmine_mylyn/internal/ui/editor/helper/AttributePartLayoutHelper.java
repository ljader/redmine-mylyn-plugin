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
		
		Label label = editor.getLabelControl();
		GridData gd = GridDataFactory.fillDefaults()
				.align(SWT.RIGHT, SWT.CENTER)
				.hint(LABEL_WIDTH, SWT.DEFAULT)
				.create();
		
		if (currentColumn > 1) {
			gd.horizontalIndent = COLUMN_GAP;
			gd.widthHint = LABEL_WIDTH + COLUMN_GAP;
		}
		label.setLayoutData(gd);
		currentColumn++;

		LayoutHint layoutHint = editor.getLayoutHint();
		gd = new GridData(SWT.FILL, SWT.CENTER, false, false);
		if (layoutHint != null && !(layoutHint.rowSpan == RowSpan.SINGLE && layoutHint.columnSpan == ColumnSpan.SINGLE)) {
			if (layoutHint.rowSpan == RowSpan.MULTIPLE) {
				gd.heightHint = MULTI_ROW_HEIGHT;
			}
			if (layoutHint.columnSpan == ColumnSpan.SINGLE) {
				gd.widthHint = COLUMN_WIDTH;
				gd.horizontalSpan = 1;
			} else {
				gd.widthHint = MULTI_COLUMN_WIDTH;
				gd.horizontalSpan = numColumns - currentColumn;
			}
		} else {
			gd.widthHint = COLUMN_WIDTH;
			gd.horizontalSpan = 1;
		}
		editor.getControl().setLayoutData(gd);
		
		currentColumn += gd.horizontalSpan;
		currentColumn %= numColumns;

	}
	
}
