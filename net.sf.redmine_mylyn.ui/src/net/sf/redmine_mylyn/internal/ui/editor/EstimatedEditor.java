package net.sf.redmine_mylyn.internal.ui.editor;

import net.sf.redmine_mylyn.core.RedmineCorePlugin;
import net.sf.redmine_mylyn.internal.ui.Images;
import net.sf.redmine_mylyn.internal.ui.Messages;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.eclipse.mylyn.tasks.core.data.TaskDataModelEvent;
import org.eclipse.mylyn.tasks.core.data.TaskDataModelListener;
import org.eclipse.mylyn.tasks.ui.editors.AbstractAttributeEditor;
import org.eclipse.mylyn.tasks.ui.editors.LayoutHint;
import org.eclipse.mylyn.tasks.ui.editors.LayoutHint.ColumnSpan;
import org.eclipse.mylyn.tasks.ui.editors.LayoutHint.RowSpan;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
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

	private final static int STEPS = 25;
	
	private Spinner spinner;
	
	private final TaskDataModelListener modelListener;
	
	public EstimatedEditor(TaskDataModel manager, TaskAttribute taskAttribute) {
		super(manager, taskAttribute);
		setLayoutHint(new LayoutHint(RowSpan.SINGLE, ColumnSpan.SINGLE));
		
		modelListener = new TaskDataModelListener() {
			@Override
			public void attributeChanged(TaskDataModelEvent event) {
				if(event.getTaskAttribute().getId().equals(getTaskAttribute().getId())) {
					if (spinner!=null && !spinner.isDisposed()) {
						int newValue = toSelectionValue(event.getTaskAttribute().getValue());
						if(spinner.getSelection()!=newValue) {
							spinner.setSelection(newValue);
						}
					}
				}
			}
		};
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
			spinner.setIncrement(STEPS);
			spinner.setSelection(getValue());
			
//			!PlatformUtil.spinnerHasNativeBorder()
			if (!("carbon".equals(SWT.getPlatform()) || "cocoa".equals(SWT.getPlatform()))) { //$NON-NLS-1$ //$NON-NLS-2$
				spinner.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
			}
			
			spinner.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					EstimatedEditor.this.setValue(spinner.getText());
				}
			});
			
			ImageHyperlink clearEstimated = toolkit.createImageHyperlink(composite, SWT.NONE);
			clearEstimated.setImage(Images.getImage(Images.CLEAR));
			clearEstimated.setToolTipText(Messages.Clear);
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

		
		getControl().addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				getModel().removeModelListener(modelListener);
			}
		});
		getModel().addModelListener(modelListener);
	}

	private int getValue() {
		return toSelectionValue(getTaskAttribute().getValue());
	}

	private void setValue(String val) {
		if(!val.equals(getTaskAttribute().getValue())) {
			if(Float.valueOf(val) == 0) {
				val = "";
			}
			getTaskAttribute().setValue(val);
			attributeChanged();
		}
	}
	
	private int toSelectionValue(String val) {
		float value = 0;
		if(!val.isEmpty()) {
			try {
				
				value = Float.parseFloat(val);
				value *= 100;
				
			} catch (NumberFormatException e) {
				IStatus status = RedmineCorePlugin.toStatus(e, Messages.ERRMSG_INVALID_REDMINE_HOURS, getTaskAttribute().getValue());
				StatusHandler.log(status);
			}
		}
		return (int)value;
	}
	

}
