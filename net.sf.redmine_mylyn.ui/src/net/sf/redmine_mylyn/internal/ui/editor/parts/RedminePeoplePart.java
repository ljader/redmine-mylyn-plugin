package net.sf.redmine_mylyn.internal.ui.editor.parts;

import net.sf.redmine_mylyn.core.RedmineAttribute;
import net.sf.redmine_mylyn.internal.ui.editor.RedminePersonEditor;
import net.sf.redmine_mylyn.internal.ui.editor.RedmineWatchersEditor;
import net.sf.redmine_mylyn.internal.ui.editor.helper.AttributePartLayoutHelper;

import org.eclipse.mylyn.tasks.core.IRepositoryPerson;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskDataModelEvent;
import org.eclipse.mylyn.tasks.core.data.TaskDataModelListener;
import org.eclipse.mylyn.tasks.ui.editors.AbstractAttributeEditor;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPart;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

public class RedminePeoplePart extends AbstractTaskEditorPart {

	public final static String PART_ID = "net.sf.redmine_mylyn.ui.editor.part.people";

	private final TaskDataModelListener modelListener;
	
	private RedminePersonEditor personEditor;
	
	private RedmineWatchersEditor watchersEditor;
	
	public RedminePeoplePart() {
		super();
		setPartName("People");
		
		modelListener = new TaskDataModelListener() {
			@Override
			public void attributeChanged(TaskDataModelEvent event) {
				// TODO Auto-generated method stub
				
				TaskAttribute attribute = event.getTaskAttribute();
				
				if (!attribute.getValue().isEmpty() && attribute.getId().equals(RedmineAttribute.WATCHERS_ADD.getTaskKey())) {
					IRepositoryPerson person = getTaskData().getAttributeMapper().getRepositoryPerson(attribute);
					watchersEditor.addWatcher(person);
					
					attribute.setValue(""); //$NON-NLS-1$
					getModel().attributeChanged(attribute);
				}
				
			}
		};
	}
	
	@Override
	public void createControl(Composite parent, FormToolkit toolkit) {
		Section section = createSection(parent, toolkit, true);

		Composite composite = toolkit.createComposite(section);
//		GridLayout layout = EditorUtil.createSectionClientLayout();
		composite.setLayout(new GridLayout(2, false));
		GridData gd = new GridData();
		gd.horizontalSpan = 4;
		composite.setLayoutData(gd);

		AttributePartLayoutHelper layoutHelper = new AttributePartLayoutHelper(composite, toolkit, true);

		
		AbstractAttributeEditor editor = createAttributeEditor(getTaskData().getRoot().getMappedAttribute(TaskAttribute.USER_ASSIGNED));
		if (editor!=null) {
			editor.createLabelControl(composite, toolkit);
			editor.createControl(composite, toolkit);
			layoutHelper.setLayoutData(editor);
			getTaskEditorPage().getAttributeEditorToolkit().adapt(editor);
		}
		
		editor = createAttributeEditor(getTaskData().getRoot().getMappedAttribute(TaskAttribute.USER_REPORTER));
		if (editor!=null) {
			editor.createLabelControl(composite, toolkit);
			editor.createControl(composite, toolkit);
			layoutHelper.setLayoutData(editor);
			getTaskEditorPage().getAttributeEditorToolkit().adapt(editor);
		}
		
		editor = createAttributeEditor(getTaskData().getRoot().getMappedAttribute(RedmineAttribute.WATCHERS_ADD.getTaskKey()));
		if (editor!=null) {
			editor.createLabelControl(composite, toolkit);
			editor.createControl(composite, toolkit);
			layoutHelper.setLayoutData(editor);
			getTaskEditorPage().getAttributeEditorToolkit().adapt(editor);
			
			if (editor instanceof RedminePersonEditor) {
				personEditor = (RedminePersonEditor)editor;
			}
		}
		
		editor = createAttributeEditor(getTaskData().getRoot().getMappedAttribute(RedmineAttribute.WATCHERS.getTaskKey()));
		if (editor!=null) {
			editor.createLabelControl(composite, toolkit);
			editor.createControl(composite, toolkit);
			layoutHelper.setLayoutData(editor);
			getTaskEditorPage().getAttributeEditorToolkit().adapt(editor);
			
			if (editor instanceof RedmineWatchersEditor) {
				watchersEditor = (RedmineWatchersEditor)editor;
			}
		}
		
		toolkit.paintBordersFor(composite);
		section.setClient(composite);
		setSection(toolkit, section);
		
		if (personEditor!=null && watchersEditor!=null) {
			getModel().addModelListener(modelListener);
		}
	}

	@Override
	public void dispose() {
		getModel().removeModelListener(modelListener);
		super.dispose();
	}
}
