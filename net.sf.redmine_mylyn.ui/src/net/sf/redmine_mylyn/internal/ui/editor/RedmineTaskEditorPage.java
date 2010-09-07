package net.sf.redmine_mylyn.internal.ui.editor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.sf.redmine_mylyn.api.model.Configuration;
import net.sf.redmine_mylyn.api.model.CustomField;
import net.sf.redmine_mylyn.api.model.Project;
import net.sf.redmine_mylyn.api.model.Property;
import net.sf.redmine_mylyn.core.IRedmineConstants;
import net.sf.redmine_mylyn.core.RedmineAttribute;
import net.sf.redmine_mylyn.core.RedmineCorePlugin;
import net.sf.redmine_mylyn.core.RedmineOperation;
import net.sf.redmine_mylyn.core.RedmineRepositoryConnector;
import net.sf.redmine_mylyn.internal.ui.editor.TaskDataValidator.ErrorMessageCollector;
import net.sf.redmine_mylyn.internal.ui.editor.helper.AttributePartLayoutHelper;
import net.sf.redmine_mylyn.internal.ui.editor.parts.NewTimeEntryEditorPart;
import net.sf.redmine_mylyn.internal.ui.editor.parts.PlanningEditorPart;
import net.sf.redmine_mylyn.internal.ui.editor.parts.TimeEntryEditorPart;
import net.sf.redmine_mylyn.ui.RedmineUiPlugin;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.eclipse.mylyn.tasks.core.data.TaskDataModelEvent;
import org.eclipse.mylyn.tasks.core.data.TaskDataModelListener;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.editors.AbstractAttributeEditor;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPage;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPart;
import org.eclipse.mylyn.tasks.ui.editors.AttributeEditorFactory;
import org.eclipse.mylyn.tasks.ui.editors.LayoutHint;
import org.eclipse.mylyn.tasks.ui.editors.LayoutHint.ColumnSpan;
import org.eclipse.mylyn.tasks.ui.editors.LayoutHint.RowSpan;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorPartDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;

public class RedmineTaskEditorPage extends AbstractTaskEditorPage {


//	private final IRedmineAttributeChangedListener STATUS_LISTENER;
	

	private final TaskDataModelListener projectAttributeListener;
	private final TaskDataModelListener trackerAttributeListener;
	private final TaskDataModelListener validatorAttributeListener;
	private final TaskDataModelListener statusAttributeListener;

	private TaskDataValidator validator;
//	
//	private AbstractAttributeEditor statusChangeEditor;
//
//	private AbstractAttributeEditor trackerEditor;
//	
	private Configuration cfg;
	
	Map<TaskAttribute, AbstractAttributeEditor> attributeEditors = new HashMap<TaskAttribute, AbstractAttributeEditor>();
//	
	Map<String, CustomField> customFields = new HashMap<String, CustomField>();
	
	public RedmineTaskEditorPage(TaskEditor editor) {
		super(editor, RedmineCorePlugin.REPOSITORY_KIND);

		setNeedsPrivateSection(true);
		setNeedsSubmitButton(true);
		
//		STATUS_LISTENER = new IRedmineAttributeChangedListener() {
//			public void attributeChanged(ITask task, TaskAttribute attribute) {
//				if(getTask()==task) {
//					if(attribute.getId().equals(RedmineAttribute.STATUS_CHG.getTaskKey())) {
//						TaskDataModel model = getModel();
//						TaskAttribute modelAttribute = model.getTaskData().getRoot().getAttribute(attribute.getId());
//						
//						if(!modelAttribute.getValue().equals(attribute.getValue())) {
//							modelAttribute.setValue(attribute.getValue());
//							model.attributeChanged(modelAttribute);
//							
//						}
//					}
//				}
//			}
//		};
	
		projectAttributeListener = new ProjectTaskDataModelListener();
		trackerAttributeListener = new TrackerTaskDataModelListener();
		validatorAttributeListener = new ValidatorTaskDataModelListener();
		statusAttributeListener = new StatusTaskDataModelListener();
		
		
	}
	
	
	@Override
	public void init(IEditorSite site, IEditorInput input) {
		super.init(site, input);
		
		RedmineRepositoryConnector connector = (RedmineRepositoryConnector) TasksUi.getRepositoryConnector(getModel().getTaskRepository().getConnectorKind());
		cfg = connector.getRepositoryConfiguration(getTaskRepository());
		validator = new TaskDataValidator(cfg);
		
		
		getModel().addModelListener(projectAttributeListener);
		getModel().addModelListener(trackerAttributeListener);
		getModel().addModelListener(validatorAttributeListener);
		getModel().addModelListener(statusAttributeListener);

		//TODO
//		RedmineUiPlugin.getDefault().addAttributeChangedListener(STATUS_LISTENER);
	}
	
	@Override
	public void dispose() {
		getModel().removeModelListener(projectAttributeListener);
		getModel().removeModelListener(trackerAttributeListener);
		getModel().removeModelListener(validatorAttributeListener);
		
		getModel().removeModelListener(statusAttributeListener);
		//TODO
//		RedmineUiPlugin.getDefault().removeAttributeChangedListener(STATUS_LISTENER);
		super.dispose();
	}
	
	@Override
	protected Set<TaskEditorPartDescriptor> createPartDescriptors() {
		Set<TaskEditorPartDescriptor> descriptors = super.createPartDescriptors();

		TaskAttribute rootAttribute = getModel().getTaskData().getRoot();
		
		descriptors.add(new TaskEditorPartDescriptor(PlanningEditorPart.PART_ID) {
			@Override
			public AbstractTaskEditorPart createPart() {
				return new PlanningEditorPart();
			}
		}.setPath(PATH_ATTRIBUTES));

		if (rootAttribute.getAttribute(RedmineAttribute.TIME_ENTRY_TOTAL.getTaskKey())!=null) {
			descriptors.add(new TaskEditorPartDescriptor(TimeEntryEditorPart.PART_ID) {
				@Override
				public AbstractTaskEditorPart createPart() {
					return new TimeEntryEditorPart();
				}
			}.setPath(PATH_COMMENTS));
		}

		if (!getModel().getTask().isCompleted() && rootAttribute.getAttribute(RedmineAttribute.TIME_ENTRY_HOURS.getTaskKey())!=null) {
			descriptors.add(new TaskEditorPartDescriptor(NewTimeEntryEditorPart.PART_ID) {
				@Override
				public AbstractTaskEditorPart createPart() {
					return new NewTimeEntryEditorPart();
				}
			}.setPath(PATH_COMMENTS));
		}
		
		return descriptors;
	}

//	//WORKARAOUND Zugriff auf nicht initialisierte Page
//	//TODO Ticket erstellen
//	@Override
//	public IManagedForm getManagedForm() {
//		IManagedForm form = super.getManagedForm();
//		if (form==null) {
//			FormEditor editor = getTaskEditor();
//			if (editor!= null && !isActive()) {
//				editor.setActivePage(getId());
//				form = super.getManagedForm();
//			}
//		}
//		return form;
//	}
	
	@Override
	protected void createParts() {
		Project project = cfg.getProjects().getById(getAttributeId(RedmineAttribute.PROJECT));
		int trackerId = getAttributeId(RedmineAttribute.TRACKER);
		
		if(project!=null && trackerId>0) {
			refreshCustomFields(project, trackerId);
		} else {
			getTaskEditor().setMessage("Problem occured when creating attributes", IMessageProvider.ERROR);
		}
			
		attributeEditors.clear();
		super.createParts();
	}
	
	@Override
	protected AttributeEditorFactory createAttributeEditorFactory() {
		return new AttributeEditorFactory(getModel(), getTaskRepository(), getEditorSite()) {

			@Override
			public AbstractAttributeEditor createEditor(String type, TaskAttribute taskAttribute) {

				//CustomAttribute usable for Project and Tracker?
				if(taskAttribute.getId().startsWith(IRedmineConstants.TASK_KEY_PREFIX_ISSUE_CF)) {
					if(!RedmineTaskEditorPage.this.customFields.containsKey(taskAttribute.getId())) {
						return null;
					}
				}

				final AbstractAttributeEditor editor;
				if(IRedmineConstants.EDITOR_TYPE_ESTIMATED.equals(type)) {
					editor = new EstimatedEditor(getModel(), taskAttribute);
				} else if(IRedmineConstants.EDITOR_TYPE_PARENTTASK.equals(type)) {
					editor = super.createEditor(TaskAttribute.TYPE_TASK_DEPENDENCY, taskAttribute);
					editor.setLayoutHint(new LayoutHint(RowSpan.SINGLE, ColumnSpan.SINGLE));
				} else {
					editor = super.createEditor(type, taskAttribute);
//						if(taskAttribute.getId().equals(RedmineAttribute.STATUS_CHG.getTaskKey())) {
//							statusChangeEditor = editor;
//						} else if (taskAttribute.getId().equals(RedmineAttribute.TRACKER.getTaskKey())) {
//							trackerEditor = editor;
//						} else if (TaskAttribute.TYPE_BOOLEAN.equals(type)) {
//							editor.setDecorationEnabled(false);
//						}
				}
				
				registerEditor(taskAttribute, editor);
				return editor;
			}
			
		};
	}

	@Override
	public void doSubmit() {
//		TODO FOCUS
//		AbstractTaskEditorPart part = getPart(ID ID_PART_SUMMARY);
//		if (part != null) {
//			part.setFocus();
//		}
		
		ErrorMessageCollector collector = validator.validateTaskData(getModel().getTaskData());
		if (collector.hasErrors()) {
			getTaskEditor().setMessage(collector.getFirstErrorMessage(), IMessageProvider.ERROR);
			return;
		}

		getTaskEditor().setMessage("", IMessageProvider.NONE);
		super.doSubmit();
	}
	
	protected void registerEditor(TaskAttribute taskAttribute, AbstractAttributeEditor editor) {
		if(taskAttribute!=null && editor!=null) {
			attributeEditors.put(taskAttribute, editor);
		}
	}
	
	protected void refreshEditor(TaskAttribute taskAttribute) {
		if(attributeEditors.containsKey(taskAttribute)) {
			attributeEditors.get(taskAttribute).refresh();
		}
	}
	
	protected void removeEditor(TaskAttribute taskAttribute) {
		AbstractAttributeEditor editor = attributeEditors.remove(taskAttribute);
		if(editor!=null) {
			editor.getLabelControl().dispose();
			editor.getControl().dispose();
		}
	}
	
	private void refreshCustomFields(Project project, int trackerId) {
		customFields.clear();
		int[] ids = project.getCustomFieldIdsByTrackerId(trackerId);
		
		if(ids!=null) {
			Arrays.sort(ids);
			for (CustomField customField : cfg.getCustomFields().getIssueCustomFields()) {
				if(Arrays.binarySearch(ids, customField.getId())>=0) {
					customFields.put(IRedmineConstants.TASK_KEY_PREFIX_ISSUE_CF+customField.getId(), customField);
				}
			}
		}
	}
	
	private void refreshCustomFieldsComposite() {
		TaskAttribute rootAttribute = getModel().getTaskData().getRoot();
		TaskAttribute trackerAttribute = rootAttribute.getAttribute(RedmineAttribute.TRACKER.getTaskKey());
		Composite parent =  attributeEditors.get(trackerAttribute).getControl().getParent();

		//remove old CutomFields from Form
		for (TaskAttribute attribute : new ArrayList<TaskAttribute>(attributeEditors.keySet())) {
			if(attribute.getId().startsWith(IRedmineConstants.TASK_KEY_PREFIX_ISSUE_CF)) {
				removeEditor(attribute);
			}
		}
		
		//remove empty labels
		for(Control control : parent.getChildren()) {
			if(control instanceof Label && ((Label)control).getText().isEmpty()) {
				control.dispose();
			}
		}
		
		getEditorComposite().layout();
			
		//create and add new CustomFields
		AttributePartLayoutHelper layoutHelper = new AttributePartLayoutHelper(parent, getManagedForm().getToolkit());
		
		for (CustomField cf : customFields.values()) {
			TaskAttribute cfAttribute = rootAttribute.getAttribute(IRedmineConstants.TASK_KEY_PREFIX_ISSUE_CF + cf.getId());
			if (cfAttribute!=null) {
				AbstractAttributeEditor cfEditor = getAttributeEditorFactory().createEditor(cfAttribute.getMetaData().getType(), cfAttribute);

				cfEditor.createLabelControl(parent, getManagedForm().getToolkit());
				cfEditor.createControl(parent, getManagedForm().getToolkit());
				layoutHelper.setLayoutData(cfEditor);
				
				getAttributeEditorToolkit().adapt(cfEditor);
				attributeEditors.put(cfAttribute, cfEditor);
			}
		}
		
		parent.layout();
		getEditorComposite().layout(true);
		
	}

	private int getAttributeId(RedmineAttribute attribute) {
		int id =-1;
		TaskAttribute taskAttribute = getModel().getTaskData().getRoot().getAttribute(attribute.getTaskKey());
		if(taskAttribute==null) {
			IStatus status = new Status(IStatus.ERROR, RedmineUiPlugin.PLUGIN_ID, "Invalid Attribute "+attribute.name()); 
			StatusHandler.fail(status);
		} else {
			try {
				if(!taskAttribute.getValue().isEmpty()) {
					id = Integer.parseInt(taskAttribute.getValue());
				}
			} catch (NumberFormatException e) {
				//TODO Plugin-ID
				IStatus status = RedmineCorePlugin.toStatus(e, "Invalid {0}-ID {1}", attribute.name(), taskAttribute.getValue());
				StatusHandler.fail(status);
				getTaskEditor().setMessage("Problem occured when updating attributes", IMessageProvider.ERROR);
			}
		}
		return id;
	} 

	private class StatusTaskDataModelListener extends TaskDataModelListener {

		@Override
		public void attributeChanged(TaskDataModelEvent event) {
			TaskAttribute changedAttribute = event.getTaskAttribute();
			
			if(changedAttribute.getId().equals(RedmineAttribute.STATUS_CHG.getTaskKey())) {
				TaskDataModel model = event.getModel();
				
				TaskAttribute markasOperation = model.getTaskData().getRoot().getAttribute(TaskAttribute.PREFIX_OPERATION + RedmineOperation.markas.toString());
				if(markasOperation!=null) {
					TaskAttribute operation = model.getTaskData().getRoot().getAttribute(TaskAttribute.OPERATION);
					model.getTaskData().getAttributeMapper().setValue(operation, RedmineOperation.markas.toString());
					model.attributeChanged(operation);
					
					AbstractAttributeEditor statusChgEditor = attributeEditors.get(changedAttribute);
					if(statusChgEditor!=null) {
						Control control = statusChgEditor.getControl();
						if(control!=null && control instanceof CCombo) {
							Listener[] listeners = control.getListeners(SWT.Selection);
							if(listeners!=null && listeners.length==2) {
								Event e = new Event();
								e.widget = control;
								e.type = SWT.Selection;
								/*
								 * Excpected listeners:
								 * 0: AttributeEditor
								 * 1: ActionButton
								 */
								listeners[1].handleEvent(e);
							}
						}
						
					}
				}
			}
			
		}
		
	}

	private class ProjectTaskDataModelListener extends TaskDataModelListener {
		
		@Override
		public void attributeChanged(TaskDataModelEvent event) {
			TaskAttribute root = getModel().getTaskData().getRoot();
			TaskAttribute changedAttribute = event.getTaskAttribute();
			if(changedAttribute.getId().equals(RedmineAttribute.PROJECT.getTaskKey())) {
				
				Project project = cfg.getProjects().getById(getAttributeId(RedmineAttribute.PROJECT));
				
				if(project==null) {
					getTaskEditor().setMessage("Problem occured when updating attributes", IMessageProvider.ERROR);
				} else {
					
					TaskAttribute taskAttribute = null;
					Map<RedmineAttribute, List<?extends Property>> projectSpecific = new HashMap<RedmineAttribute, List<? extends Property>>(4);
					projectSpecific.put(RedmineAttribute.TRACKER, cfg.getTrackers().getById(project.getTrackerIds()));
					projectSpecific.put(RedmineAttribute.CATEGORY, cfg.getIssueCategories().getById(project.getIssueCategoryIds()));
					projectSpecific.put(RedmineAttribute.VERSION, cfg.getVersions().getById(project.getVersionIds()));
					projectSpecific.put(RedmineAttribute.ASSIGNED_TO, cfg.getUsers().getById(project.getAssignableMemberIds()));
					
					for (Entry<RedmineAttribute, List<?extends Property>> entry : projectSpecific.entrySet()) {
						taskAttribute = root.getAttribute(entry.getKey().getTaskKey());
						
						if(taskAttribute!=null) {
							taskAttribute.clearOptions();
							if(!entry.getKey().isRequired()) {
								taskAttribute.putOption("", "");
							}
							for(Property property : entry.getValue()) {
								taskAttribute.putOption(""+property.getId(), property.getName());
							}
							if(entry.getKey().isRequired() && entry.getValue().size()==1) {
								taskAttribute.setValue(""+entry.getValue().get(0).getId());
							}
							
							refreshEditor(taskAttribute);
						} 
					}

					int trackerId = getAttributeId(RedmineAttribute.TRACKER);
					refreshCustomFields(project, trackerId);
					refreshCustomFieldsComposite();
				}
			}
		}
	}

	private class TrackerTaskDataModelListener extends TaskDataModelListener {
		
		@Override
		public void attributeChanged(TaskDataModelEvent event) {
			TaskAttribute changedAttribute = event.getTaskAttribute();
			if(changedAttribute.getId().equals(RedmineAttribute.TRACKER.getTaskKey())) {
				
				Project project = cfg.getProjects().getById(getAttributeId(RedmineAttribute.PROJECT));
				int trackerId = getAttributeId(RedmineAttribute.TRACKER);
				
				if(project!=null && trackerId>0) {
					refreshCustomFields(project, trackerId);
					refreshCustomFieldsComposite();
				} else {
					getTaskEditor().setMessage("Problem occured when updating attributes", IMessageProvider.ERROR);
				}
				
			}
		}
	}

	private class ValidatorTaskDataModelListener extends TaskDataModelListener {

		@Override
		public void attributeChanged(TaskDataModelEvent event) {
			ErrorMessageCollector collector = validator.validateTaskAttribute(getModel().getTaskData(), event.getTaskAttribute());
			if(collector!=null && collector.hasErrors()) {
				getTaskEditor().setMessage(collector.getFirstErrorMessage(), IMessageProvider.WARNING);
			} else {
				getTaskEditor().setMessage("", IMessageProvider.NONE); //$NON-NLS-1$
			}
		}
	}
}
