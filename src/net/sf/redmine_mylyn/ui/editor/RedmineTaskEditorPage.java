package net.sf.redmine_mylyn.ui.editor;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.sf.redmine_mylyn.api.model.Configuration;
import net.sf.redmine_mylyn.core.IRedmineConstants;
import net.sf.redmine_mylyn.core.RedmineAttribute;
import net.sf.redmine_mylyn.core.RedmineCorePlugin;
import net.sf.redmine_mylyn.core.RedmineRepositoryConnector;
import net.sf.redmine_mylyn.internal.ui.editor.EstimatedEditor;
import net.sf.redmine_mylyn.internal.ui.editor.PlanningEditorPart;
import net.sf.redmine_mylyn.internal.ui.editor.NewTimeEntryEditorPart;

import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.editors.AbstractAttributeEditor;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPage;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPart;
import org.eclipse.mylyn.tasks.ui.editors.AttributeEditorFactory;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorPartDescriptor;

public class RedmineTaskEditorPage extends AbstractTaskEditorPage {


//	private final IRedmineAttributeChangedListener STATUS_LISTENER;
//	
//	private final TaskDataModelListener MODEL_LISTENER;
//
//	private RedmineTaskDataValidator validator;
//	
//	private AbstractAttributeEditor statusChangeEditor;
//
//	private AbstractAttributeEditor trackerEditor;
//	
	private Map<TaskAttribute, AbstractAttributeEditor> attributeEditors = new HashMap<TaskAttribute, AbstractAttributeEditor>();
//	
//	private Map<Integer, RedmineCustomField> customFields = new HashMap<Integer, RedmineCustomField>();
	
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
//	
//		MODEL_LISTENER = new TaskDataModelListener() {
//			@Override
//			public void attributeChanged(TaskDataModelEvent event) {
//				RedmineTaskDataValidatorResult result = validator.validateTaskAttribute(getModel().getTaskData(), event.getTaskAttribute());
//				if(result.hasErrors()) {
//					getTaskEditor().setMessage(result.getFirstErrorMessage(), IMessageProvider.WARNING);
//				} else {
//					getTaskEditor().setMessage("", IMessageProvider.NONE); //$NON-NLS-1$
//				}
//				
//				TaskAttribute changedAttribute = event.getTaskAttribute();
//				if(changedAttribute.getId().equals(RedmineAttribute.STATUS_CHG.getTaskKey())) {
//					TaskDataModel model = event.getModel();
//					TaskAttribute markasOperation = model.getTaskData().getRoot().getAttribute(TaskAttribute.PREFIX_OPERATION + RedmineOperation.markas.toString());
//					if(markasOperation!=null) {
//						TaskAttribute operation = model.getTaskData().getRoot().getAttribute(TaskAttribute.OPERATION);
//						model.getTaskData().getAttributeMapper().setValue(operation, RedmineOperation.markas.toString());
//						model.attributeChanged(operation);
//						
//						if(statusChangeEditor!=null) {
//							Control control = statusChangeEditor.getControl();
//							if(control!=null && control instanceof CCombo) {
//								Listener[] listeners = control.getListeners(SWT.Selection);
//								if(listeners!=null && listeners.length==2) {
//									Event e = new Event();
//									e.widget = control;
//									e.type = SWT.Selection;
//									/*
//									 * Excpected listeners:
//									 * 0: AttributeEditor
//									 * 1: ActionButton
//									 */
//									listeners[1].handleEvent(e);
//								}
//							}
//						}
//					}
//				}
//				
//				if(changedAttribute.getId().equals(RedmineAttribute.TRACKER.getTaskKey())) {
//					try {
//						refreshCustomFields(Integer.parseInt(changedAttribute.getValue()));
//						TaskAttribute rootAttribute = changedAttribute.getTaskData().getRoot();
//						
//						//remove old CutomFields from Form and Map
//						for (TaskAttribute attribute : new ArrayList<TaskAttribute>(attributeEditors.keySet())) {
//							if(attribute.getId().startsWith(IRedmineConstants.TASK_KEY_PREFIX_TICKET_CF)) {
//								attributeEditors.get(attribute).getLabelControl().dispose();
//								attributeEditors.get(attribute).getControl().dispose();
//								attributeEditors.remove(attribute);
//							}
//						}
//							
//						//create and add new CustomFields
//						Composite parent = trackerEditor.getControl().getParent();
//						RedmineAttributePartLayoutHelper layoutHelper = new RedmineAttributePartLayoutHelper(parent);
//						
//						for (RedmineCustomField cf : customFields.values()) {
//							TaskAttribute cfAttribute = rootAttribute.getAttribute(IRedmineConstants.TASK_KEY_PREFIX_TICKET_CF + cf.getId());
//							if (cfAttribute!=null) {
//								AbstractAttributeEditor cfEditor = getAttributeEditorFactory().createEditor(cfAttribute.getMetaData().getType(), cfAttribute);
//
//								cfEditor.createLabelControl(parent, getManagedForm().getToolkit());
//								cfEditor.createControl(parent, getManagedForm().getToolkit());
//								layoutHelper.setLayoutData(cfEditor);
//								
//								getAttributeEditorToolkit().adapt(cfEditor);
//								attributeEditors.put(cfAttribute, cfEditor);
//							}
//						}
//						
//						parent.layout();
//							
//					} catch (NumberFormatException e) {
//						StatusHandler.fail(RedmineCorePlugin.toStatus(e, null, Messages.RedmineTaskEditorPage_INVALID_TRACKER_ID_MSG_WITH_PARAM, changedAttribute.getValue()));
//					}
//				}
//			}
//		};
		

		
	}

//	@Override
//	public void init(IEditorSite site, IEditorInput input) {
//		super.init(site, input);
//		
//		TaskRepository repository = getTaskRepository();
//		AbstractRepositoryConnector connector = TasksUi.getRepositoryManager().getRepositoryConnector(repository.getConnectorKind());
//		if (connector instanceof RedmineRepositoryConnector) {
//			validator = ((RedmineRepositoryConnector)connector).createNewTaskDataValidator(repository);
//
//			getModel().addModelListener(MODEL_LISTENER);
//		}
//
//		RedmineUiPlugin.getDefault().addAttributeChangedListener(STATUS_LISTENER);
//	}
	
//	@Override
//	public void dispose() {
//		getModel().removeModelListener(MODEL_LISTENER);
//		RedmineUiPlugin.getDefault().removeAttributeChangedListener(STATUS_LISTENER);
//		super.dispose();
//	}
	
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
//
//		if (rootAttribute.getAttribute(RedmineAttribute.TIME_ENTRY_TOTAL.getTaskKey())!=null) {
//			//TODO change ID
//			descriptors.add(new TaskEditorPartDescriptor(IRedmineConstants.TASK_EDITOR_PART_TIMEENTRIES) {
//				@Override
//				public AbstractTaskEditorPart createPart() {
//					return new RedmineTimeEntryEditorPart();
//				}
//			}.setPath(PATH_COMMENTS));
//		}
//
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
	
//	@Override
//	protected void createParts() {
//		TaskAttribute rootAttribute = getModel().getTaskData().getRoot(); 
//		customFields.clear();
//		try {
//			int trackerId = Integer.parseInt(rootAttribute.getAttribute(RedmineAttribute.TRACKER.getTaskKey()).getValue());
//			refreshCustomFields(trackerId);
//		} catch(NumberFormatException e) {
//			StatusHandler.fail(RedmineCorePlugin.toStatus(e, null, Messages.RedmineTaskEditorPage_INVALID_TRACKER_ID_MSG_WITH_PARAM, rootAttribute.getAttribute(RedmineAttribute.TRACKER.getTaskKey()).getValue()));
//		}
//
//		attributeEditors.clear();
//		super.createParts();
//	}
	
//	private void  refreshCustomFields(int trackerId) {
//		customFields.clear();
//		TaskAttribute rootAttribute = getModel().getTaskData().getRoot(); 
//		String projectName = rootAttribute.getAttribute(RedmineAttribute.PROJECT.getTaskKey()).getValue();
//		RedmineProjectData projectData = getRepositoryConfiguration().getProjectFromName(projectName);
//		for(RedmineCustomField field : projectData.getCustomTicketFields(trackerId)) {
//			customFields.put(field.getId(), field);
//		}
//		
//	}

	@Override
	protected AttributeEditorFactory createAttributeEditorFactory() {
		AttributeEditorFactory factory = new AttributeEditorFactory(getModel(), getTaskRepository(), getEditorSite()) {
			@Override
			public AbstractAttributeEditor createEditor(String type, final TaskAttribute taskAttribute) {
//				//CustomAttribute usable for Project and Tracker?
//				if(taskAttribute.getId().startsWith(IRedmineConstants.TASK_KEY_PREFIX_TICKET_CF)) {
//					try {
//						int fieldId = Integer.parseInt(taskAttribute.getId().substring(IRedmineConstants.TASK_KEY_PREFIX_TICKET_CF.length()));
//						if(!customFields.containsKey(fieldId)) {
//							return null;
//						}
//					} catch (NumberFormatException e) {
//						StatusHandler.fail(RedmineCorePlugin.toStatus(e, null, Messages.RedmineTaskEditorPage_INVALID_CF_ID_MSG_WITH_PARAM, taskAttribute.getId().substring(IRedmineConstants.TASK_KEY_PREFIX_TICKET_CF.length())));
//					}
//				}

				AbstractAttributeEditor editor;
				if(IRedmineConstants.EDITOR_TYPE_ESTIMATED.equals(type)) {
					editor = new EstimatedEditor(getModel(), taskAttribute);
				} else {
					editor = super.createEditor(type, taskAttribute);
					
//					if(taskAttribute.getId().equals(RedmineAttribute.STATUS_CHG.getTaskKey())) {
//						statusChangeEditor = editor;
//					} else if (taskAttribute.getId().equals(RedmineAttribute.TRACKER.getTaskKey())) {
//						trackerEditor = editor;
//					} else if (TaskAttribute.TYPE_BOOLEAN.equals(type)) {
//						editor.setDecorationEnabled(false);
//					}
				}

				attributeEditors.put(taskAttribute, editor);
				return editor;
			}
		};
		return factory;
	}

	@Override
	public void doSubmit() {
		//TODO
//		TaskAttribute attribute = getModel().getTaskData().getRoot().getMappedAttribute(RedmineAttribute.SUMMARY.getTaskKey());
//		if (attribute != null && attribute.getValue().trim().length() == 0) {
//			getTaskEditor().setMessage(Messages.RedmineTaskEditorPage_MISSING_SUBJECT_MSG, IMessageProvider.ERROR);
//			AbstractTaskEditorPart part = getPart(ID_PART_SUMMARY);
//			if (part != null) {
//				part.setFocus();
//			}
//			return;
//		}
//
//		attribute = getModel().getTaskData().getRoot().getMappedAttribute(RedmineAttribute.DESCRIPTION.getTaskKey());
//		if (attribute != null && attribute.getValue().trim().length() == 0) {
//			getTaskEditor().setMessage(Messages.RedmineTaskEditorPage_MISSING_DESCRIPTION_MSG, IMessageProvider.ERROR);
//			AbstractTaskEditorPart part = getPart(ID_PART_DESCRIPTION);
//			if (part != null) {
//				part.setFocus();
//			}
//			return;
//		}
//
//		RedmineTaskDataValidatorResult result = validator.validateTaskData(getModel().getTaskData());
//		if (result.hasErrors()) {
//			getTaskEditor().setMessage(result.getFirstErrorMessage(), IMessageProvider.ERROR);
//			return;
//		}
//
//		getTaskEditor().setMessage("", IMessageProvider.NONE); //$NON-NLS-1$
//		super.doSubmit();
	}
	
	private Configuration getRepositoryConfiguration() {
		RedmineRepositoryConnector connector = (RedmineRepositoryConnector) TasksUi.getRepositoryConnector(getModel().getTaskRepository().getConnectorKind());
		return connector.getRepositoryConfiguration(getModel().getTaskRepository());
	}

}
