package net.sf.redmine_mylyn.internal.ui.editor;

import java.util.Map;

import net.sf.redmine_mylyn.core.RedmineUtil;
import net.sf.redmine_mylyn.internal.ui.RedminePersonProposalLabelProvider;
import net.sf.redmine_mylyn.internal.ui.RedminePersonProposalProvider;

import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.mylyn.tasks.core.IRepositoryPerson;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.eclipse.mylyn.tasks.ui.editors.AbstractAttributeEditor;
import org.eclipse.mylyn.tasks.ui.editors.LayoutHint;
import org.eclipse.mylyn.tasks.ui.editors.LayoutHint.ColumnSpan;
import org.eclipse.mylyn.tasks.ui.editors.LayoutHint.RowSpan;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.fieldassist.ContentAssistCommandAdapter;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;

public class RedminePersonEditor extends AbstractAttributeEditor {

	protected Text text;

	protected RedminePersonProposalProvider contentProposalProvider;
//	private final TaskDataModelListener modelListener;

	public RedminePersonEditor(TaskDataModel manager, TaskAttribute taskAttribute) {
		super(manager, taskAttribute);
		setLayoutHint(new LayoutHint(RowSpan.SINGLE, ColumnSpan.SINGLE));

//		modelListener = new TaskDataModelListener() {
//			@Override
//			public void attributeChanged(TaskDataModelEvent event) {
//				if(event.getTaskAttribute().getId().equals(getTaskAttribute().getId())) {
//					IRepositoryPerson person = getAttributeMapper().getRepositoryPerson(event.getTaskAttribute());
//					String personString = RedmineUtil.formatUserPresentation(person); 
//					if (!text.getText().equals(personString)) {
//						text.setText(personString);
//					}
//				}
//			}
//		};
	}
	
	@Override
	public void createControl(Composite parent, FormToolkit toolkit) {
		if (isReadOnly()) {
			text = new Text(parent, SWT.FLAT | SWT.READ_ONLY);
			text.setData(FormToolkit.KEY_DRAW_BORDER, Boolean.FALSE);
			text.setToolTipText(getDescription());
			text.setText(getValue());
		} else {
			text = toolkit.createText(parent, getValue(), SWT.FLAT);
			text.setToolTipText(getDescription());
			text.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					setValue(text.getText());
				}
			});
		}
		toolkit.adapt(text, false, false);
		setControl(text);
		
		attachContentProposalProvider();

//		text.addDisposeListener(new DisposeListener() {
//			@Override
//			public void widgetDisposed(DisposeEvent e) {
//				getModel().removeModelListener(modelListener);
//			}
//		});
//		getModel().addModelListener(modelListener);
}

	public String getValue() {
		return RedmineUtil.formatUserPresentation(getAttributeMapper().getRepositoryPerson(getTaskAttribute()));
	}

	public void setValue(String text) {
		if (text.isEmpty()) {
			getTaskAttribute().setValue(text);
			attributeChanged();
		} else {
			
			String value = RedmineUtil.findUserLogin(text);
			if(value!=null && !value.isEmpty()) {
				IRepositoryPerson person = getModel().getTaskRepository().createPerson(value);
				getAttributeMapper().setRepositoryPerson(getTaskAttribute(), person);
				attributeChanged();
			}

		}
	}

	@Override
	public void refresh() {
		Map<String, String> persons = getAttributeMapper().getOptions(getTaskAttribute());
		contentProposalProvider.setProposals(persons);
		
		if (!persons.containsKey(getTaskAttribute().getValue())) {
			text.setText("");
		}
	}
	
	private void attachContentProposalProvider() {
		Map<String, String> persons = getAttributeMapper().getOptions(getTaskAttribute());
		
		contentProposalProvider = new RedminePersonProposalProvider(getModel().getTask(), getTaskAttribute().getTaskData(), persons);
		ILabelProvider labelPropsalProvider = new RedminePersonProposalLabelProvider();
		
		ContentAssistCommandAdapter adapter = new ContentAssistCommandAdapter(text,
				new TextContentAdapter(), contentProposalProvider,
				ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS, new char[0], true);
		
		adapter.setLabelProvider(labelPropsalProvider);
		adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);
		
	}
}
