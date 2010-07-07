package net.sf.redmine_mylyn.internal.ui.editor;

import net.sf.redmine_mylyn.core.RedmineCorePlugin;
import net.sf.redmine_mylyn.internal.ui.Images;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.eclipse.mylyn.tasks.ui.editors.AbstractAttributeEditor;
import org.eclipse.mylyn.tasks.ui.editors.LayoutHint;
import org.eclipse.mylyn.tasks.ui.editors.LayoutHint.ColumnSpan;
import org.eclipse.mylyn.tasks.ui.editors.LayoutHint.RowSpan;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;

public class EstimatedEditor extends AbstractAttributeEditor {

	Spinner spinner;
	
	public EstimatedEditor(TaskDataModel manager, TaskAttribute taskAttribute) {
		super(manager, taskAttribute);
		setLayoutHint(new LayoutHint(RowSpan.SINGLE, ColumnSpan.SINGLE));
	}

	@Override
	public void createControl(Composite parent, FormToolkit toolkit) {
		Control control = null;

		Composite composite = toolkit.createComposite(parent);
		GridLayout layout = new GridLayout(isReadOnly()?2:3, false);
		layout.marginHeight = 3;
		composite.setLayout(layout);

		if (isReadOnly()) {
			control = toolkit.createText(composite, getTaskAttribute().getValue(), SWT.FLAT | SWT.READ_ONLY);
			control.setData(FormToolkit.KEY_DRAW_BORDER, Boolean.FALSE);
		} else {
			spinner = new Spinner(composite, SWT.FLAT);
			spinner.setDigits(2);
			spinner.setMaximum(10000);
			spinner.setMinimum(0);
			spinner.setIncrement(25);
			spinner.setSelection(getValue());
			
//			!PlatformUtil.spinnerHasNativeBorder()
			if (!("carbon".equals(SWT.getPlatform()) || "cocoa".equals(SWT.getPlatform()))) {
				spinner.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
			}
			
			spinner.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					EstimatedEditor.this.setValue(spinner.getSelection());
				}
			});
			
			ImageHyperlink clearEstimated = toolkit.createImageHyperlink(composite, SWT.NONE);
			clearEstimated.setImage(Images.getImage(Images.FIND_CLEAR));
			clearEstimated.setToolTipText("Clear");
			clearEstimated.addHyperlinkListener(new HyperlinkAdapter() {
				@Override
				public void linkActivated(HyperlinkEvent e) {
					EstimatedEditor.this.spinner.setSelection(0);
				}
			});
			
			control = spinner;
		}
		
		toolkit.paintBordersFor(composite);
		toolkit.adapt(control, false, false);
		setControl(control);
	}

	private int getValue() {
		float estimatedHours = 0f;
		if (getTaskAttribute().getValue().length()>0) {
			try {
				estimatedHours = Float.valueOf(getTaskAttribute().getValue());
				estimatedHours *= 1e2;
			} catch (NumberFormatException e) {
				IStatus status = RedmineCorePlugin.toStatus(e, "INVALID_ESTIMATED_HOURS {0}", getTaskAttribute().getValue());
				StatusHandler.log(status);
			}
		}
		return (int)estimatedHours;
	}

	private void setValue(int val) {
		getTaskAttribute().setValue("" + (((float)(val))*1e-2));
		attributeChanged();
	}

}
