package net.sf.redmine_mylyn.internal.ui.editor;

import java.util.Map;

import net.sf.redmine_mylyn.core.RedmineUtil;
import net.sf.redmine_mylyn.internal.ui.RedminePersonProposalLabelProvider;
import net.sf.redmine_mylyn.internal.ui.RedminePersonProposalProvider;

import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.viewers.ILabelProvider;
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

	private Text text;
	public RedminePersonEditor(TaskDataModel manager, TaskAttribute taskAttribute) {
		super(manager, taskAttribute);
		setLayoutHint(new LayoutHint(RowSpan.SINGLE, ColumnSpan.SINGLE));
	}
	
//	protected Text getText() {
//		return text;
//	}

	@Override
	public void createControl(Composite parent, FormToolkit toolkit) {
		if (isReadOnly()) {
			text = new Text(parent, SWT.FLAT | SWT.READ_ONLY);
//			text.setFont(EditorUtil.TEXT_FONT);
			text.setData(FormToolkit.KEY_DRAW_BORDER, Boolean.FALSE);
			text.setToolTipText(getDescription());
			text.setText(getValue());
		} else {
			text = toolkit.createText(parent, getValue(), SWT.FLAT);
//			text.setFont(EditorUtil.TEXT_FONT);
			text.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TREE_BORDER);
			text.setToolTipText(getDescription());
			text.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					setValue(text.getText());
//					CommonFormUtil.ensureVisible(text);
				}
			});
		}
		toolkit.adapt(text, false, false);
		setControl(text);
		
		attachContentProposalProvider();
	}

	public String getValue() {
		String value = getTaskAttribute().getValue();
		
		if (!value.isEmpty()) {
			
			Map<String, String> options = getAttributeMapper().getOptions(getTaskAttribute());
			if (options.containsKey(value)) {
				return RedmineUtil.formatUserPresentation(value, options.get(value));
			}

			
		}
		
		return getAttributeMapper().getRepositoryPerson(getTaskAttribute()).toString();
	}

	public void setValue(String text) {
		String value = RedmineUtil.findUserLogin(text);
		if(value==null) {
			value = "";
		}
		System.out.println("NEUER WERT: "+value);
		getAttributeMapper().setValue(getTaskAttribute(), value);
		attributeChanged();
	}

	private void attachContentProposalProvider() {
		Map<String, String> persons = getAttributeMapper().getOptions(getTaskAttribute());
		
		IContentProposalProvider contentProposalProvider = new RedminePersonProposalProvider(getModel().getTask(), getTaskAttribute().getTaskData(), persons);
		ILabelProvider labelPropsalProvider = new RedminePersonProposalLabelProvider();
		
		ContentAssistCommandAdapter adapter = new ContentAssistCommandAdapter(text,
				new TextContentAdapter(), contentProposalProvider,
				ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS, new char[0], true);
		
		adapter.setLabelProvider(labelPropsalProvider);
		adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);
		
	}
}
