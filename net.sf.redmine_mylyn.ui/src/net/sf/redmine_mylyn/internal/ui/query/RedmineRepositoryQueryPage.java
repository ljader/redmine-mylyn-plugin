package net.sf.redmine_mylyn.internal.ui.query;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.sf.redmine_mylyn.api.exception.RedmineApiErrorException;
import net.sf.redmine_mylyn.api.model.Configuration;
import net.sf.redmine_mylyn.api.model.CustomField;
import net.sf.redmine_mylyn.api.model.Project;
import net.sf.redmine_mylyn.api.model.Tracker;
import net.sf.redmine_mylyn.api.query.CompareOperator;
import net.sf.redmine_mylyn.api.query.IQueryField;
import net.sf.redmine_mylyn.api.query.Query;
import net.sf.redmine_mylyn.api.query.QueryField;
import net.sf.redmine_mylyn.core.RedmineCorePlugin;
import net.sf.redmine_mylyn.core.RedmineRepositoryConnector;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositoryQueryPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;

public class RedmineRepositoryQueryPage extends AbstractRepositoryQueryPage {

	private static final String TITLE = "Enter query parameters";
	
	private static final String DESCRIPTION = "Only predefined filters are supported.";

	private static final String OPERATOR_BOOLEAN_TRUE = "true";
	
	private IRepositoryQuery query;

	private Text titleText;

	private final RedmineRepositoryConnector connector;
	
	private final Configuration configuration;

	protected ScrolledComposite pageScroll;
	protected Composite pageComposite;
	protected Composite itemComposite;

	
	protected final Map<IQueryField, ComboViewer> searchOperators;
	protected final Map<IQueryField, Text> queryText;
	protected final Map<IQueryField, StructuredViewer> queryStructuredViewer;
	
	protected final List<CustomField> customFields;
	
	protected Button updateButton;

	public RedmineRepositoryQueryPage(TaskRepository repository, IRepositoryQuery query) {
		super(TITLE, repository, query);

		this.query=query;
		
		connector = (RedmineRepositoryConnector) TasksUi.getRepositoryManager().getRepositoryConnector(RedmineCorePlugin.REPOSITORY_KIND);
		configuration = connector.getRepositoryConfiguration(repository);
		Assert.isNotNull(configuration);

		setTitle(TITLE);
		setDescription(DESCRIPTION);


		searchOperators = new HashMap<IQueryField, ComboViewer>();
		queryText = new  LinkedHashMap<IQueryField, Text>();
		queryStructuredViewer = new LinkedHashMap<IQueryField, StructuredViewer>();
		customFields = new ArrayList<CustomField>();
	}

	public void createControl(final Composite parent) {
		pageScroll = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL);
		pageComposite = new Composite(pageScroll, SWT.NONE);
		pageComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		pageComposite.setLayout(new GridLayout(1, false));

		/*
		 * Page-Layout
		 * 
		 * TOP   : Title
		 * MIDDLE: Items
		 * BOTTOM: Update-Button 
		 */
		createTitleGroup(pageComposite);

		itemComposite = new Composite(pageComposite, SWT.NONE);
		itemComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		itemComposite.setLayout(new GridLayout(1, false));
		
		createUpdateButton(pageComposite);

		/* Create and layout items */
		createItemGroup(itemComposite);
		createCustomItemGroup(itemComposite);
		
		LayoutHelper.placeTextElements(itemComposite, queryText, searchOperators);
		LayoutHelper.placeListElements(itemComposite, 4, queryStructuredViewer, searchOperators);

		/* Project-Listener */
		ProjectSelectionListener projectListener = new ProjectSelectionListener(this);
		queryStructuredViewer.get(QueryField.PROJECT).addSelectionChangedListener(projectListener);
		searchOperators.get(QueryField.PROJECT).addSelectionChangedListener(projectListener);

		ISelectionChangedListener listener = new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				switchOperatorState();
			}
		};
		queryStructuredViewer.get(QueryField.TRACKER).addSelectionChangedListener(listener);
		searchOperators.get(QueryField.TRACKER).addSelectionChangedListener(listener);

		/* Configure Scroll-Composite */
		pageScroll.setContent(pageComposite);
		pageScroll.setExpandHorizontal(true);
		pageScroll.setExpandVertical(true);
		pageScroll.setMinSize(pageComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		
		setControl(pageScroll);
	}

	private void createTitleGroup(Composite control) {
		if (inSearchContainer()) {
			return;
		}

		Label titleLabel = new Label(control, SWT.NONE);
		titleLabel.setText("Query Title");

		titleText = new Text(control, SWT.BORDER);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL	| GridData.GRAB_HORIZONTAL);
		titleText.setLayoutData(gd);
		titleText.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				getContainer().updateButtons();
			}
		});
	}

	private void createItemGroup(Composite parent) {
		
		for (final QueryField queryField : QueryField.ORDERED) {
			createInputControl(parent, queryField, queryField);
			createOperatorComboViewer(parent, queryField, queryField);
		}
	}
	
	private void createCustomItemGroup(Composite parent) {
		for (CustomField customField : configuration.getCustomFields().getIssueCustomFields()) {
			QueryField queryField = customField.getQueryField();
			if(!customField.isFilter() || queryField==null)
				continue;

			createInputControl(parent, queryField, customField);
			createOperatorComboViewer(parent, queryField, customField);
			
			customFields.add(customField);
		}
	}
	
	private Control createInputControl(Composite parent, QueryField definition, IQueryField queryField) {
		Control control = null;
		if (definition.isListType()) {
			ListViewer list = new ListViewer(parent, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
			list.setLabelProvider(new RedmineLabelProvider());
			list.setContentProvider(new RedmineContentProvider());
			list.getControl().setEnabled(false);
			
			control = list.getControl();
			queryStructuredViewer.put(queryField, list);
		} else {
			Text text = new Text(parent, SWT.BORDER);
			text.setEnabled(false);
			
			control = text;
			queryText.put(queryField, text);
		}
		
		return control;
	}

	private ComboViewer createOperatorComboViewer(Composite parent, QueryField definition, final IQueryField queryField) {
		ComboViewer combo = new ComboViewer(parent, SWT.READ_ONLY | SWT.DROP_DOWN);

		String defaultValue = definition.isRequired() ? null : "Disabled";
		combo.setContentProvider(new RedmineContentProvider(defaultValue));
		combo.setLabelProvider(new RedmineLabelProvider());
		combo.setInput(definition.getCompareOperators());
		combo.setSelection(new StructuredSelection(combo.getElementAt(0)));
		
		combo.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				setQueryFieldValueControlEnabled(queryField);
			}
		});
		
		searchOperators.put(queryField, combo);
		return combo;
	}
	
	private void setQueryFieldValueControlEnabled(IQueryField queryField) {
		ComboViewer comboViewer = searchOperators.get(queryField);
		boolean enabled = comboViewer.getControl().isEnabled();

		Control corresponding = queryText.get(queryField);
		if(corresponding==null) {
			corresponding = queryStructuredViewer.get(queryField).getControl();
		}
		
		if (enabled && comboViewer.getSelection() instanceof IStructuredSelection) {
			IStructuredSelection selection = (IStructuredSelection)comboViewer.getSelection();

			Object selected = selection.getFirstElement();
			corresponding.setEnabled(selected instanceof CompareOperator && ((CompareOperator)selected).isValueBased());
		} else {
			corresponding.setEnabled(false);
		}
	}
	
	protected void createUpdateButton(final Composite parent) {
		updateButton = new Button(parent, SWT.PUSH);
		updateButton.setText("Update Attributes from Repository");
		updateButton.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		updateButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (getTaskRepository() != null) {
					updateAttributesFromRepository(true);
				} else {
					MessageDialog
							.openInformation(Display.getCurrent()
									.getActiveShell(),
									"Update Attributes Failed",
									"No repository available, please add one using the Task Repositories view.");
				}
			}
		});

		/* Move button into buttonbar */
		if(getContainer() instanceof WizardDialog) {
			WizardDialog dialog = (WizardDialog)getContainer();

			if(dialog.buttonBar instanceof Composite) {
				Composite buttonBar = (Composite)dialog.buttonBar;
				((GridLayout) buttonBar.getLayout()).numColumns++;
				
				updateButton.setParent(buttonBar);
				updateButton.moveAbove(buttonBar.getChildren()[buttonBar.getChildren().length-2]);
			}
		}

	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					updateAttributesFromRepository(false);

					if (query != null && query.getUrl() != null) {
						restoreQuery(RedmineRepositoryQueryPage.this.query);
					}
				}
			});
		}
	}

	private void updateAttributesFromRepository(final boolean force) {
		try {
			IRunnableWithProgress runnable = new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					try {
						if (force || connector.isRepositoryConfigurationStale(getTaskRepository(), monitor)) {
							connector.updateRepositoryConfiguration(getTaskRepository(), monitor);
						}
					} catch (CoreException e) {
						throw new InvocationTargetException(e, "Updating of attributes failed");
					}
				}
			};

			if (getContainer() != null) {
				getContainer().run(true, true, runnable);
			} else if (getSearchContainer() != null) {
				getSearchContainer().getRunnableContext().run(true, true, runnable);
			} else {
				IProgressService service = PlatformUI.getWorkbench().getProgressService();
				service.busyCursorWhile(runnable);
			}
		} catch (InvocationTargetException e) {
			setErrorMessage(e.getMessage());
			return;
		} catch (InterruptedException e) {
			return;
		}

//		/* Projects */
		StructuredViewer viewer = queryStructuredViewer.get(QueryField.PROJECT);
		viewer.setInput(configuration.getProjects());

		/* Status */
		viewer = queryStructuredViewer.get(QueryField.STATUS);
		viewer.setInput(configuration.getIssueStatuses());
		
		/* Priority */
		viewer = queryStructuredViewer.get(QueryField.PRIORITY);
		viewer.setInput(configuration.getIssuePriorities());

		/* Author */
		viewer = queryStructuredViewer.get(QueryField.AUTHOR);
		viewer.setInput(configuration.getUsers());
		
		/* CustomFields */
		updateCustomItemGroup();

		/* CustomOptions */
		updateCustomItemOptions();
		
	}

	void updateProjectAttributes() {
		Project project = getSelectedProject();
		StructuredViewer viewer;
		
		/* AssignedTo */
		viewer = queryStructuredViewer.get(QueryField.ASSIGNED_TO);
		viewer.setInput(project==null 
				? configuration.getUsers().getAll() 
				: configuration.getUsers().getById(project.getAssignableMemberIds()));

		/* Version */
		viewer = queryStructuredViewer.get(QueryField.FIXED_VERSION);
		viewer.setInput(project==null ? null : configuration.getVersions().getById(project.getVersionIds()));
		
		/* Tracker */
		viewer = queryStructuredViewer.get(QueryField.TRACKER);
		viewer.setInput(project==null 
				? configuration.getTrackers().getAll() 
				: configuration.getTrackers().getById(project.getTrackerIds()));
		
		/* Category */
		viewer = queryStructuredViewer.get(QueryField.CATEGORY);
		viewer.setInput(project==null ? null : configuration.getIssueCategories().getById(project.getIssueCategoryIds()));
		
	}
	
	void updateCustomItemGroup() {
		/* Fetch the LayoutHelper Composites */
		List<Composite> oldComposites = new ArrayList<Composite>(2);
		for (Control child : itemComposite.getChildren()) {
			if (child instanceof Composite) {
				oldComposites.add((Composite)child);
			}
		}
		Assert.isTrue(oldComposites.size()==2);

		/* Remove old CustomFields */
		for(CustomField customField : customFields) {
//				queryStructuredViewer.remove(queryField);
//				queryStructuredViewer.remove(queryField);
//				searchOperators.remove(queryField);
		}
		customFields.clear();
		
		/* Create new/updated CustomFields */
		createCustomItemGroup(itemComposite);
		

		/* Positioning of items */
		LayoutHelper.placeTextElements(itemComposite, queryText, searchOperators);
		LayoutHelper.placeListElements(itemComposite, 4, queryStructuredViewer, searchOperators);

		/* Drop old LayoutHelper composites */
		oldComposites.get(0).dispose();
		oldComposites.get(1).dispose();

		/* Layout again */
		itemComposite.layout();
		pageComposite.layout();

		pageScroll.setMinSize(pageComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

	}
	
	void updateCustomItemOptions() {
		for (CustomField customField : configuration.getCustomFields().getIssueCustomFields()) {
			QueryField queryField = customField.getQueryField();
			if(!customField.isFilter() || queryField==null || !queryField.isListType())
				continue;

			StructuredViewer viewer = queryStructuredViewer.get(customField);
			if(viewer==null)
				continue;
			
			viewer.setInput(customField.getPossibleValues());
		}
	}
	
	HashSet<Integer> findAvailableCustomFields(Project project) {
		
		List<Tracker> availableTrackerList = project==null 
			? configuration.getTrackers().getAll() 
			: configuration.getTrackers().getById(project.getTrackerIds());
			
		CompareOperator op = getSelectedOperator(QueryField.TRACKER);
		StructuredSelection selection = (StructuredSelection)queryStructuredViewer.get(QueryField.TRACKER).getSelection();

		//Find related Trackers - Negate Tracker-Selection if necessary
		List<Tracker> selTrackerList = null;
		if(op==null || op==CompareOperator.IS_NOT && selection.isEmpty()) {
			selTrackerList = availableTrackerList;
		} else {
			selTrackerList = new ArrayList<Tracker>();
			if (op==CompareOperator.IS_NOT) {
				selTrackerList.addAll(availableTrackerList);
				for (Object selected : selection.toList()) {
					selTrackerList.remove(selected);
				}
			} else {
				for (Object selected : selection.toList()) {
					selTrackerList.add((Tracker)selected);
				}
			}
		}
		
		HashSet<Integer> collectedCustomFieldIds = new HashSet<Integer>();
		if (project==null) {
			for (Tracker tracker : selTrackerList) {
				for (Integer cfId : tracker.getIssueCustomFields()) {
					CustomField cf  = configuration.getCustomFields().getById(cfId);
					if(cf.isCrossProjectUsable()) {
						collectedCustomFieldIds.add(cfId);
					}
				}
			}
		} else {
			for (Tracker tracker : selTrackerList) {
				int[] cfIds = project.getCustomFieldIdsByTrackerId(tracker.getId());
				if (cfIds!=null) {
					for(int cfId : cfIds) {
						collectedCustomFieldIds.add(cfId);
					}
				}
			}
		}
		
		return collectedCustomFieldIds;
	}
	
	CompareOperator getSelectedOperator(IQueryField queryField) {
		ISelection selection = searchOperators.get(queryField).getSelection();
		if(selection!=null && !selection.isEmpty() && selection instanceof StructuredSelection) {
			Object selected = ((StructuredSelection)selection).getFirstElement();
			if(selected instanceof CompareOperator) {
				return (CompareOperator)selected;
			}
		}
		return null;
	}
	
	Project getSelectedProject() {
		if(getSelectedOperator(QueryField.PROJECT)==CompareOperator.IS) {
			if(queryStructuredViewer.get(QueryField.PROJECT).getSelection() instanceof StructuredSelection) {
				IStructuredSelection selection = (StructuredSelection)queryStructuredViewer.get(QueryField.PROJECT).getSelection();
				if(selection.size()==1) {
					return (Project)((StructuredSelection)selection).getFirstElement();
				}
			}
		}
		return null;
	}
	
	private void restoreQuery(IRepositoryQuery repositoryQuery) {
		titleText.setText(repositoryQuery.getSummary());
		Query query = null;
		
		try {
			query = Query.fromUrl(repositoryQuery.getUrl(), getTaskRepository().getCharacterEncoding(), configuration);
		} catch (RedmineApiErrorException e) {
			//TODO PluginId
			IStatus status = RedmineCorePlugin.toStatus(e, "Restore of Query failed");
			StatusHandler.log(status);
			setErrorMessage(status.getMessage());
		}

//		//NOTE : Don't call updateProjectAttributes() - projectViewer's SeletionListener call this method !!!
//		//Project-Query
//		projectViewer.setSelection(new StructuredSelection(projectData.getProject()));
//		search.setProjectId(projectData.getProject().getValue());
//
//		//Check StoredQuery usage
//		String sqIdVal = query.getAttribute(RedmineSearch.STORED_QUERY_ID);
//		int sqId = (sqIdVal==null || !sqIdVal.matches("^\\d+$")) ? 0 : Integer.parseInt(sqIdVal);
//		search.setStoredQueryId(sqId);
//		RedmineStoredQuery storedQuery = null;
//		if(sqId>0) {
//			storedQuery = queryData.getQuery(sqId);
//		}
		
		//TODO PRÜFEN oben steht NOTE... - evtl innnerhalb von restoreStructuredQueryPart extra behandeln
		switchOperatorState();
		updateProjectAttributes();
		
		QueryBuilder.restoreTextQueryPart(query, configuration, searchOperators, queryText);
		QueryBuilder.restoreStructuredQueryPart(query, configuration, searchOperators, queryStructuredViewer);
		
		getContainer().updateButtons();
	}
		
	void switchOperatorState() {
		Project project = getSelectedProject();
		boolean enabled = project!=null;
		Set<Integer> matchingCustomFieldIds = findAvailableCustomFields(project);
		
		for (Entry<IQueryField, ComboViewer> entry : searchOperators.entrySet()) {
			IQueryField queryField = entry.getKey();
			Control control = entry.getValue().getControl();
			
			if(queryField instanceof CustomField) {
				control.setEnabled(matchingCustomFieldIds.contains(((CustomField)queryField).getId()));
			} else {
				control.setEnabled(enabled || queryField.isCrossProjectUsable());
			}
		}
		
	}

	@Override
	public boolean canFlipToNextPage() {
		return false;
	}

	@Override
	public boolean isPageComplete() {
		return validate();
	}

	private boolean validate() {
		return (titleText != null && titleText.getText().length() > 0);
	}

	@Override
	public void applyTo(IRepositoryQuery repositoryQuery) {
		repositoryQuery.setSummary(getQueryTitle());
		
		Query query = QueryBuilder.buildQuery(searchOperators, queryText, queryStructuredViewer);
		try {
			repositoryQuery.setUrl(query.toUrl(getTaskRepository().getCharacterEncoding()));
		} catch (RedmineApiErrorException e) {
			//TODO PluginId
			IStatus status = RedmineCorePlugin.toStatus(e, "Creation of Query failed");
			StatusHandler.log(status);
			setErrorMessage(status.getMessage());
		}
		
	}

	@Override
	public String getQueryTitle() {
		return (titleText != null) ? titleText.getText() : "<search>";
	}
	
}
