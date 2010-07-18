package net.sf.redmine_mylyn.internal.ui.query;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.redmine_mylyn.api.client.RedmineApiStatusException;
import net.sf.redmine_mylyn.api.model.Configuration;
import net.sf.redmine_mylyn.api.model.CustomField;
import net.sf.redmine_mylyn.api.model.Project;
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
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
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
		queryStructuredViewer.get(QueryField.PROJECT).addSelectionChangedListener(new ProjectSelectionListener(this));
		searchOperators.get(QueryField.PROJECT).addSelectionChangedListener(new ProjectOperatorSelectionListener(this));

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
		
		for (QueryField queryField : QueryField.ORDERED) {
			Control control = createInputControl(parent, queryField, queryField);
			ComboViewer combo = createOperatorComboViewer(parent, queryField, queryField);
			combo.addSelectionChangedListener(new CompareOperatorSelectionListener(control));
		}
	}
	
	private void createCustomItemGroup(Composite parent) {
		for (CustomField customField : configuration.getCustomFields().getIssueCustomFields()) {
			QueryField queryField = customField.getQueryField();

			if(!customField.isFilter() || queryField==null)
				continue;

			Control control = createInputControl(parent, queryField, customField);
			ComboViewer combo = createOperatorComboViewer(parent, queryField, customField);
			combo.addSelectionChangedListener(new CompareOperatorSelectionListener(control));
		}
	}
	
	private Control createInputControl(Composite parent, QueryField definition, IQueryField queryField) {
		Control control = null;
		if(definition==QueryField.PROJECT) {
			ListViewer list = new ListViewer(parent, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL);
			list.setLabelProvider(new RedmineLabelProvider());
			list.setContentProvider(new RedmineContentProvider());
			list.getControl().setEnabled(false);
			
			control = list.getControl();
			queryStructuredViewer.put(queryField, list);
		} else 
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

	private ComboViewer createOperatorComboViewer(Composite parent, QueryField definition, IQueryField queryField) {
		ComboViewer combo = new ComboViewer(parent, SWT.READ_ONLY | SWT.DROP_DOWN);

		String defaultValue = definition.isRequired() ? null : "Disabled";
		combo.setContentProvider(new RedmineContentProvider(defaultValue));
		combo.setLabelProvider(new RedmineLabelProvider());
		combo.setInput(definition.getCompareOperators());
		combo.setSelection(new StructuredSelection(combo.getElementAt(0)));
		
		searchOperators.put(queryField, combo);
		return combo;
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
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					//TODO async initial ???
//					if (data == null) {
//						if (getControl() != null && !getControl().isDisposed()) {
						RedmineRepositoryQueryPage.this.updateAttributesFromRepository(false);
//						}
//					}
	
					if (RedmineRepositoryQueryPage.this.query != null && RedmineRepositoryQueryPage.this.query.getUrl() != null) {
						RedmineRepositoryQueryPage.this.restoreQuery(RedmineRepositoryQueryPage.this.query);
					} else {
						//TODO check
//						projectViewer.setSelection(new StructuredSelection(PROJECT_SELECT_TITLE));
//						storedQueryViewer.setInput(new String[]{QUERY_SELECT_TITLE});
//						storedQueryViewer.setSelection(new StructuredSelection(QUERY_SELECT_TITLE));
					}
				}
			});
		}
	}

	private void updateAttributesFromRepository(final boolean force) {

		if (force /*|| !client.hasAttributes()*/) {
			try {
				IRunnableWithProgress runnable = new IRunnableWithProgress() {
					public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
						try {
							RedmineRepositoryQueryPage.this.connector.updateRepositoryConfiguration(getTaskRepository(), monitor);
						} catch (CoreException e) {
							StatusHandler.log(e.getStatus());
							throw new InvocationTargetException(e, e.getMessage());
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

	void updateProjectAttributes(Project project) {
		StructuredViewer viewer;
		
		/* AssignedTo */
		viewer = queryStructuredViewer.get(QueryField.ASSIGNED_TO);
		viewer.setInput(project==null ? null : configuration.getUsers().getById(project.getAssignableMemberIds()));

		/* Version */
		viewer = queryStructuredViewer.get(QueryField.FIXED_VERSION);
		viewer.setInput(project==null ? null : configuration.getVersions().getById(project.getVersionIds()));
		
		/* Tracker */
		viewer = queryStructuredViewer.get(QueryField.TRACKER);
		viewer.setInput(project==null ? null : configuration.getTrackers().getById(project.getTrackerIds()));
		
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
		for(IQueryField queryField : Collections.unmodifiableSet(queryStructuredViewer.keySet())) {
			if(queryField instanceof CustomField) {
				queryStructuredViewer.remove(queryField);
				searchOperators.remove(queryField);
			}
		}
		for(IQueryField queryField : Collections.unmodifiableSet(queryText.keySet())) {
			if(queryField instanceof CustomField) {
				queryStructuredViewer.remove(queryField);
				searchOperators.remove(queryField);
			}
		}
		
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
	
	private void restoreQuery(IRepositoryQuery repositoryQuery) {
		titleText.setText(repositoryQuery.getSummary());
		Query query = null;
		
		try {
			query = Query.fromUrl(repositoryQuery.getUrl(), getTaskRepository().getCharacterEncoding(), configuration);
		} catch (RedmineApiStatusException e) {
			StatusHandler.log(e.getStatus());
			setErrorMessage("Restore of query failed");
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
		
		QueryBuilder.restoreTextQueryPart(query, configuration, searchOperators, queryText);
		QueryBuilder.restoreStructuredQueryPart(query, configuration, searchOperators, queryStructuredViewer);
		
		getContainer().updateButtons();
	}
	
	/**
	 * Deselect / clear all Settings / Attributes
	 */
	void clearSettings() {
		clearListSettings(searchOperators, queryStructuredViewer);
		clearTextSettings(searchOperators, queryText);
	}
	
	private void clearListSettings(Map<IQueryField, ComboViewer> operatorCombo, Map<IQueryField, StructuredViewer> valueViewer) {
		for (Entry<IQueryField, StructuredViewer> entry : valueViewer.entrySet()) {
			if(entry.getKey()!=QueryField.PROJECT) {
				entry.getValue().setSelection(new StructuredSelection());
				entry.getValue().getControl().setEnabled(false);
				ComboViewer operator = operatorCombo.get(entry.getKey());
				operator.setSelection(new StructuredSelection(operator.getElementAt(0)));
			}
		}
	}

	private void clearTextSettings(Map<IQueryField, ComboViewer> operators, Map<IQueryField, Text> textValues) {
		for (Entry<IQueryField, Text> entry : textValues.entrySet()) {
			if (entry.getValue().getEditable()) {
				entry.getValue().setText("");
			}
			entry.getValue().setEnabled(false);
			ComboViewer operator = operators.get(entry.getKey());
			operator.setSelection(new StructuredSelection(operator.getElementAt(0)));
		}
	}
	
	void switchOperatorState(boolean state, boolean crossProjectOnly) {
		List<Entry<IQueryField, ComboViewer>> operatorViewer = new ArrayList<Entry<IQueryField, ComboViewer>>();
		operatorViewer.addAll(searchOperators.entrySet());
		
		for (Entry<IQueryField, ComboViewer> entry : operatorViewer) {
			if(state) {
				entry.getValue().getControl().setEnabled(true);
			} else if (!(crossProjectOnly && entry.getKey().isCrossProjectUsable())) {
				entry.getValue().getControl().setEnabled(false);
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

//	private RedmineTicketAttribute getFirstSelectedEntry(Viewer viewer) {
//		if (!viewer.getSelection().isEmpty() && viewer.getSelection() instanceof StructuredSelection) {
//			Object selected = ((IStructuredSelection)viewer.getSelection()).getFirstElement();
//			if (selected instanceof RedmineTicketAttribute) {
//				return (RedmineTicketAttribute) selected;
//			}
//		}
//		return null;
//	}
	
	@Override
	public void applyTo(IRepositoryQuery repositoryQuery) {
		repositoryQuery.setSummary(getQueryTitle());
		
		Query query = QueryBuilder.buildQuery(searchOperators, queryText, queryStructuredViewer);
		try {
			repositoryQuery.setUrl(query.toUrl(getTaskRepository().getCharacterEncoding()));
		} catch (RedmineApiStatusException e) {
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
