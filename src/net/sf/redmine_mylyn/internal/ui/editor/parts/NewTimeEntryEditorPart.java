package net.sf.redmine_mylyn.internal.ui.editor.parts;

import java.util.ArrayList;
import java.util.List;

import net.sf.redmine_mylyn.core.IRedmineConstants;
import net.sf.redmine_mylyn.core.RedmineAttribute;

import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskDataModelEvent;
import org.eclipse.mylyn.tasks.core.data.TaskDataModelListener;
import org.eclipse.mylyn.tasks.ui.editors.AbstractAttributeEditor;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPart;
import org.eclipse.mylyn.tasks.ui.editors.AttributeEditorToolkit;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

public class NewTimeEntryEditorPart extends AbstractTaskEditorPart {

	public final static String PART_ID = "net.sf.redmine_mylyn.ui.editor.part.newtimeentry";

	private Section section;

	private TaskDataModelListener modelListener;
	
	public NewTimeEntryEditorPart() {
		super();
		setPartName("New Time Entry");
		setExpandVertically(true);
	}

	private List<String> attributeList = new ArrayList<String>(3);
	
	@Override
	public void createControl(Composite parent, FormToolkit toolkit) {
		//TODO layout / AttributePartLayoutHelper
		
		initialize();
		
		section = createSection(parent, toolkit, ExpandableComposite.TITLE_BAR | ExpandableComposite.EXPANDED);
		setSection(toolkit, section);
		

		GridLayout gl = new GridLayout();
		GridData gd = new GridData(SWT.FILL, SWT.NONE, false, false);
		gd.horizontalSpan = 4;
		section.setLayout(gl);
		section.setLayoutData(gd);

		Composite composite = toolkit.createComposite(section);
		composite.setLayout(new GridLayout(4, false));
		gd = new GridData();
		gd.horizontalSpan = 4;
		composite.setLayoutData(gd);

		AttributeEditorToolkit editorToolkit = getTaskEditorPage().getAttributeEditorToolkit();
		TaskAttribute root = getTaskData().getRoot();
		AbstractAttributeEditor attributeEditor;
		TaskAttribute attribute;

		attribute = root.getAttribute(RedmineAttribute.TIME_ENTRY_HOURS.getTaskKey());
		if (attribute != null) {
			attributeList.add(attribute.getId());
			attributeEditor = createAttributeEditor(attribute);
			attributeEditor.createLabelControl(composite, toolkit);
			attributeEditor.createControl(composite, toolkit);
			attributeEditor.setDecorationEnabled(false);
			editorToolkit.adapt(attributeEditor);
		}

		attribute = root.getAttribute(RedmineAttribute.TIME_ENTRY_ACTIVITY.getTaskKey());
		if (attribute != null) {
			attributeList.add(attribute.getId());
			attributeEditor = createAttributeEditor(attribute);
			attributeEditor.createLabelControl(composite, toolkit);
			attributeEditor.createControl(composite, toolkit);
			attributeEditor.setDecorationEnabled(false);
			editorToolkit.adapt(attributeEditor);
		}
		
		attribute = root.getAttribute(RedmineAttribute.TIME_ENTRY_COMMENTS.getTaskKey());
		if (attribute != null) {
			attributeList.add(attribute.getId());
			attributeEditor = createAttributeEditor(attribute);
			attributeEditor.createLabelControl(composite, toolkit);
			attributeEditor.createControl(composite, toolkit);
			attributeEditor.setDecorationEnabled(false);
			editorToolkit.adapt(attributeEditor);
			
			gd = new GridData();
			gd.horizontalSpan = 3;
			gd.horizontalAlignment = SWT.FILL;
			gd.verticalAlignment = SWT.FILL;
			gd.heightHint = 40;
			attributeEditor.getControl().setLayoutData(gd);
		}

		for (TaskAttribute childAttribute : root.getAttributes().values()) {
			if(childAttribute.getId().startsWith(IRedmineConstants.TASK_KEY_PREFIX_TIMEENTRY_CF)) {
				attributeList.add(childAttribute.getId());
				attributeEditor = createAttributeEditor(childAttribute);
				attributeEditor.createLabelControl(composite, toolkit);
				attributeEditor.createControl(composite, toolkit);
				attributeEditor.setDecorationEnabled(false);
				editorToolkit.adapt(attributeEditor);
			}
		}
		
		toolkit.paintBordersFor(composite);
		section.setClient(composite);
		setSection(toolkit, section);
	}

	private void initialize() {
		modelListener = new TaskDataModelListener() {
			@Override
			public void attributeChanged(TaskDataModelEvent event) {
				if(attributeList.contains(event.getTaskAttribute().getId())) {
					markDirty();
				}
			}
		};
		getModel().addModelListener(modelListener);
	}
	
	@Override
	public void dispose() {
		getModel().removeModelListener(modelListener);
		super.dispose();
	}
	
}
