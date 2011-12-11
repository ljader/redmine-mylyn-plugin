package net.sf.redmine_mylyn.internal.ui.editor.parts;

import java.util.ArrayList;
import java.util.List;

import net.sf.redmine_mylyn.core.IRedmineConstants;
import net.sf.redmine_mylyn.core.IRedmineSpentTimeManager;
import net.sf.redmine_mylyn.core.IRedmineSpentTimeManagerListener;
import net.sf.redmine_mylyn.core.RedmineAttribute;
import net.sf.redmine_mylyn.internal.ui.Messages;
import net.sf.redmine_mylyn.internal.ui.action.RedmineCaptureActivityTimeAction;
import net.sf.redmine_mylyn.internal.ui.action.RedmineResetUncapturedActivityTimeAction;
import net.sf.redmine_mylyn.internal.ui.editor.helper.AttributePartLayoutHelper;
import net.sf.redmine_mylyn.ui.RedmineUiPlugin;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.mylyn.commons.core.DateUtil;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskDataModelEvent;
import org.eclipse.mylyn.tasks.core.data.TaskDataModelListener;
import org.eclipse.mylyn.tasks.ui.editors.AbstractAttributeEditor;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPart;
import org.eclipse.mylyn.tasks.ui.editors.AttributeEditorToolkit;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

public class NewTimeEntryEditorPart extends AbstractTaskEditorPart {

	public final static String PART_ID = "net.sf.redmine_mylyn.ui.editor.part.newtimeentry"; //$NON-NLS-1$

	private Section section;

	private TaskDataModelListener modelListener;

	private Action resetActiveTimeAction;

	private Action captureActiveTimeAction;
	
	private Label uncapturedTimeValueLabel;
	
	private final IRedmineSpentTimeManagerListener spentTimeListener;
	
	private final IRedmineSpentTimeManager spentTimeManager;
	
	public NewTimeEntryEditorPart() {
		super();
		setPartName(Messages.NEW_TIMEENTRY_PART);
		setExpandVertically(true);
		
		spentTimeManager = RedmineUiPlugin.getDefault().getSpentTimeManager();
		
		spentTimeListener = new IRedmineSpentTimeManagerListener() {
			@Override
			public void uncapturedElapsedTimeUpdated(ITask task, final long newUncapturedElapsedTimeUpdated) {
				if (task.getHandleIdentifier().equals(getModel().getTask().getHandleIdentifier())) {
					PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
						public void run() {
							updateUncapturedSpenttime(newUncapturedElapsedTimeUpdated);
						}
					});
				}
			}
		};
	}

	private List<String> attributeList = new ArrayList<String>(3);
	
	@Override
	public void createControl(Composite parent, FormToolkit toolkit) {
		initialize();
		
		section = createSection(parent, toolkit, ExpandableComposite.TITLE_BAR | ExpandableComposite.EXPANDED);
		createSectionClient(section, toolkit);
		setSection(toolkit, section);

		Composite composite = toolkit.createComposite(section);
		composite.setLayout(new GridLayout(4, false));
		GridData gd = new GridData();
		gd.horizontalSpan = 4;
		composite.setLayoutData(gd);

		AttributeEditorToolkit editorToolkit = getTaskEditorPage().getAttributeEditorToolkit();
		TaskAttribute root = getTaskData().getRoot();
		AbstractAttributeEditor attributeEditor;
		TaskAttribute attribute;
		AttributePartLayoutHelper layoutHelper = new AttributePartLayoutHelper(composite, toolkit, true);

		attribute = root.getAttribute(RedmineAttribute.TIME_ENTRY_HOURS.getTaskKey());
		if (attribute != null) {
			//TODO WORKAROUND - remove later
			if (!attribute.getMetaData().getType().equals(IRedmineConstants.EDITOR_TYPE_DURATION)) {
				attribute.getMetaData().setType(IRedmineConstants.EDITOR_TYPE_DURATION);
			}
			attributeList.add(attribute.getId());
			attributeEditor = createAttributeEditor(attribute);
			attributeEditor.createLabelControl(composite, toolkit);
			attributeEditor.createControl(composite, toolkit);
			attributeEditor.setDecorationEnabled(false);
			layoutHelper.setLayoutData(attributeEditor);
			editorToolkit.adapt(attributeEditor);
			
			attributeEditor.getControl();
		}
		

		attribute = root.getAttribute(RedmineAttribute.TIME_ENTRY_ACTIVITY.getTaskKey());
		if (attribute != null) {
			attributeList.add(attribute.getId());
			attributeEditor = createAttributeEditor(attribute);
			attributeEditor.createLabelControl(composite, toolkit);
			attributeEditor.createControl(composite, toolkit);
			attributeEditor.setDecorationEnabled(false);
			layoutHelper.setLayoutData(attributeEditor);
			editorToolkit.adapt(attributeEditor);
		}
		
		attribute = root.getAttribute(RedmineAttribute.TIME_ENTRY_COMMENTS.getTaskKey());
		if (attribute != null) {
			attributeList.add(attribute.getId());
			attributeEditor = createAttributeEditor(attribute);
			attributeEditor.createLabelControl(composite, toolkit);
			attributeEditor.createControl(composite, toolkit);
			attributeEditor.setDecorationEnabled(false);
			layoutHelper.setLayoutData(attributeEditor);
			editorToolkit.adapt(attributeEditor);
		}

		for (TaskAttribute childAttribute : root.getAttributes().values()) {
			if(childAttribute.getId().startsWith(IRedmineConstants.TASK_KEY_PREFIX_TIMEENTRY_EX)) {
				attributeList.add(childAttribute.getId());
				attributeEditor = createAttributeEditor(childAttribute);
				attributeEditor.createLabelControl(composite, toolkit);
				attributeEditor.createControl(composite, toolkit);
				attributeEditor.setDecorationEnabled(false);
				layoutHelper.setLayoutData(attributeEditor);
				editorToolkit.adapt(attributeEditor);
			}
		}
		
		for (TaskAttribute childAttribute : root.getAttributes().values()) {
			if(childAttribute.getId().startsWith(IRedmineConstants.TASK_KEY_PREFIX_TIMEENTRY_CF)) {
				attributeList.add(childAttribute.getId());
				attributeEditor = createAttributeEditor(childAttribute);
				attributeEditor.createLabelControl(composite, toolkit);
				attributeEditor.createControl(composite, toolkit);
				attributeEditor.setDecorationEnabled(false);
				layoutHelper.setLayoutData(attributeEditor);
				editorToolkit.adapt(attributeEditor);
			}
		}
		
		toolkit.paintBordersFor(composite);
		section.setClient(composite);
		setSection(toolkit, section);
		
		updateUncapturedSpenttime(spentTimeManager.getUncapturedSpentTime(this.getModel().getTask()));
	}

	private void createSectionClient(Section section, FormToolkit toolkit) {
		if (section.getTextClient()==null) {
			Composite textClient = toolkit.createComposite(section);
			textClient.setBackground(null);
			
			RowLayout rowLayout = new RowLayout();
			rowLayout.center = true;
			rowLayout.marginLeft = 20;
			rowLayout.marginTop = 1;
			rowLayout.marginBottom = 1;
			textClient.setLayout(rowLayout);
			
			Label label = toolkit.createLabel(textClient, Messages.UNCAPTURED_TIME);
			label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
			label.setBackground(null);
			
			uncapturedTimeValueLabel = toolkit.createLabel(textClient, IRedmineConstants.EMPTY_DURATION_VALUE);
			uncapturedTimeValueLabel.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
			uncapturedTimeValueLabel.setBackground(null);

			ToolBarManager toolbar = new ToolBarManager(SWT.FLAT);
			fillToolBar(toolbar);
			toolbar.createControl(textClient);
			
			toolkit.paintBordersFor(textClient);
			section.setTextClient(textClient);
		}
		
	}

	@Override
	protected void fillToolBar(ToolBarManager toolBarManager) {
		resetActiveTimeAction = new RedmineResetUncapturedActivityTimeAction(getModel().getTask());
		toolBarManager.add(resetActiveTimeAction);
		
		captureActiveTimeAction = new RedmineCaptureActivityTimeAction(getModel().getTask());
		toolBarManager.add(captureActiveTimeAction);
	}

	private void initialize() {
		modelListener = new TaskDataModelListener() {
			@Override
			public void attributeChanged(TaskDataModelEvent event) {
				if(attributeList.contains(event.getTaskAttribute().getId())) {
					markDirty();
				}

				captureActiveTimeAction.setEnabled(spentTimeManager.getUncapturedSpentTime(getModel().getTask()) >= 60000);
			}
		};
		
		spentTimeManager.addRedmineSpentTimeManagerListener(spentTimeListener);
		getModel().addModelListener(modelListener);
	}
	
	private void updateUncapturedSpenttime(long uncapturedTimeValue) {
		boolean notEmpty = uncapturedTimeValue/1000>=60;
		
		if(uncapturedTimeValueLabel!=null && !uncapturedTimeValueLabel.isDisposed()) {
			String uncapturedTimeString = DateUtil.getFormattedDurationShort(uncapturedTimeValue);
			if (uncapturedTimeString.isEmpty()) {
				uncapturedTimeString = IRedmineConstants.EMPTY_DURATION_VALUE;
			}
			uncapturedTimeValueLabel.setText(uncapturedTimeString);
			
			resetActiveTimeAction.setEnabled(notEmpty);
			captureActiveTimeAction.setEnabled(notEmpty);
		}
	}
	
	@Override
	public void dispose() {
		getModel().removeModelListener(modelListener);
		spentTimeManager.removeRedmineSpentTimeManagerListener(spentTimeListener);
		super.dispose();
	}

}
