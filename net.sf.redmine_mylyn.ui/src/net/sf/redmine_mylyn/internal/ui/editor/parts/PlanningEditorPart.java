package net.sf.redmine_mylyn.internal.ui.editor.parts;

import java.util.EnumSet;
import java.util.Set;

import net.sf.redmine_mylyn.core.RedmineAttribute;
import net.sf.redmine_mylyn.core.RedmineUtil;
import net.sf.redmine_mylyn.internal.ui.Messages;

import org.eclipse.core.runtime.Assert;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskDataModelEvent;
import org.eclipse.mylyn.tasks.core.data.TaskDataModelListener;
import org.eclipse.mylyn.tasks.ui.editors.AbstractAttributeEditor;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPart;
import org.eclipse.mylyn.tasks.ui.editors.AttributeEditorToolkit;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

public class PlanningEditorPart extends AbstractTaskEditorPart {

	public final static String PART_ID = "net.sf.redmine_mylyn.ui.editor.part.planning"; //$NON-NLS-1$



	private final static Set<RedmineAttribute> PLANNING_ATTRIBUTES = EnumSet.of(RedmineAttribute.DATE_START, RedmineAttribute.DATE_DUE,RedmineAttribute.ESTIMATED);

	private TaskDataModelListener modelListener;
	
	private boolean hasIncoming;
	
	public PlanningEditorPart() {
		super();
		setPartName(Messages.PLANNING_PART);
	}
	
	@Override
	public void createControl(Composite parent, FormToolkit toolkit) {
		initialize();
		Section timeSection = createSection(parent, toolkit, hasIncoming);

		Composite timeComposite = toolkit.createComposite(timeSection);
		timeComposite.setLayout(new GridLayout(6, false));
		GridData gd = new GridData();
		gd.horizontalSpan = 4;
		timeComposite.setLayoutData(gd);

		AttributeEditorToolkit editorToolkit = getTaskEditorPage().getAttributeEditorToolkit();
		TaskAttribute rootAttribute = getTaskData().getRoot();
		AbstractAttributeEditor attributeEditor;
		TaskAttribute attribute;

		attribute = rootAttribute.getAttribute(RedmineAttribute.DATE_START.getTaskKey());
		if (attribute != null) {
			attributeEditor = createAttributeEditor(attribute);
			attributeEditor.createLabelControl(timeComposite, toolkit);
			attributeEditor.createControl(timeComposite, toolkit);
			editorToolkit.adapt(attributeEditor);
		}

		attribute = rootAttribute.getAttribute(RedmineAttribute.DATE_DUE.getTaskKey());
		if (attribute != null) {
			attributeEditor = createAttributeEditor(attribute);
			attributeEditor.createLabelControl(timeComposite, toolkit);
			attributeEditor.createControl(timeComposite, toolkit);
			editorToolkit.adapt(attributeEditor);
		}

		attribute = rootAttribute.getAttribute(RedmineAttribute.ESTIMATED.getTaskKey());
		if (attribute != null) {
			attributeEditor = createAttributeEditor(attribute);
			attributeEditor.createLabelControl(timeComposite, toolkit);
			attributeEditor.createControl(timeComposite, toolkit);
			editorToolkit.adapt(attributeEditor);
		}

		toolkit.paintBordersFor(timeComposite);
		timeSection.setClient(timeComposite);
		setSection(toolkit, timeSection);
	}
	
	@Override
	public void commit(boolean onSave) {
		ITask task = getModel().getTask();
		Assert.isNotNull(task);

		TaskAttribute rootAttribute = getTaskData().getRoot();
		TaskAttribute attribute = null;

		attribute = rootAttribute.getAttribute(RedmineAttribute.DATE_DUE.getTaskKey());
		if(getModel().getChangedAttributes().contains(attribute)) {
			String dueValue = attribute.getValue();

			//Updating the Due-Date in Task-List
			if (dueValue.isEmpty()) {
				task.setDueDate(null);
			} else {
				task.setDueDate(RedmineUtil.parseDate(dueValue));
			}
		}
		super.commit(onSave);
	}

	private void initialize() {
		hasIncoming = false;

		modelListener = new TaskDataModelListener() {
			@Override
			public void attributeChanged(TaskDataModelEvent event) {
				if (RedmineAttribute.DATE_DUE.match(event.getTaskAttribute())) {						
					PlanningEditorPart.this.markDirty();
				}
			}
		};
		getModel().addModelListener(modelListener);

		TaskAttribute rootAttribute = getTaskData().getRoot();
		for (RedmineAttribute redmineAttribute : PLANNING_ATTRIBUTES) {
			TaskAttribute attribute = rootAttribute.getAttribute(redmineAttribute.getTaskKey());
			if (attribute != null && getModel().hasIncomingChanges(attribute)) {
				hasIncoming = true;
				break;
			}
		}
	}

	@Override
	public void dispose() {
		getModel().removeModelListener(modelListener);
		super.dispose();
	}

//	private ITask getTask() {
//		return getTaskEditorPage().getTask();
//	}

}
