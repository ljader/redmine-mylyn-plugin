package net.sf.redmine_mylyn.internal.ui.editor.parts;

import net.sf.redmine_mylyn.core.RedmineAttribute;
import net.sf.redmine_mylyn.core.RedmineUtil;
import net.sf.redmine_mylyn.internal.ui.editor.RedminePersonEditor;
import net.sf.redmine_mylyn.internal.ui.editor.RedmineWatchersEditor;
import net.sf.redmine_mylyn.internal.ui.editor.helper.AttributePartLayoutHelper;

import org.eclipse.mylyn.tasks.core.IRepositoryPerson;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.ui.editors.AbstractAttributeEditor;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPart;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

public class RedminePeoplePart extends AbstractTaskEditorPart {

	public final static String PART_ID = "net.sf.redmine_mylyn.ui.editor.part.people";

	private RedmineWatchersEditor watchersEditor;
	
	public RedminePeoplePart() {
		super();
		setPartName("People");
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
		
		TaskAttribute attribute = getTaskData().getRoot().getMappedAttribute(RedmineAttribute.WATCHERS.getTaskKey());
		if (attribute!=null && (editor = createAttributeEditor(attribute))!=null && editor instanceof RedmineWatchersEditor ) {
			
			attribute = attribute.getAttribute(RedmineAttribute.WATCHERS_ADD.getTaskKey()); 
			if (attribute != null) {
				
				RedminePersonEditor personEditor = new RedminePersonEditor(getModel(), attribute) {
					@Override
					public void setValue(String value) {
						value = RedmineUtil.findUserLogin(value);
						if(value!=null && !value.isEmpty()) {
							IRepositoryPerson person = getModel().getTaskRepository().createPerson(value);
							watchersEditor.addWatcher(person);
							text.setText("");
						}
					};
					
					@Override
					public String getValue() {
						return "";
					};
					
				};
				
				personEditor.createLabelControl(composite, toolkit);
				personEditor.createControl(composite, toolkit);
				personEditor.setDecorationEnabled(false);
				layoutHelper.setLayoutData(personEditor);
				getTaskEditorPage().getAttributeEditorToolkit().adapt(personEditor);
			}


			editor.createLabelControl(composite, toolkit);
			editor.createControl(composite, toolkit);
			layoutHelper.setLayoutData(editor);
			getTaskEditorPage().getAttributeEditorToolkit().adapt(editor);
			watchersEditor = (RedmineWatchersEditor)editor;
		}
		
		toolkit.paintBordersFor(composite);
		section.setClient(composite);
		setSection(toolkit, section);
		
	}

}
