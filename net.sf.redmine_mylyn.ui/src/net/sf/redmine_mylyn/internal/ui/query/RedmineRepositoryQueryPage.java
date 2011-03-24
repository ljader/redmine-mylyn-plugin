package net.sf.redmine_mylyn.internal.ui.query;

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
import net.sf.redmine_mylyn.core.RedmineRepositoryConnector;
import net.sf.redmine_mylyn.internal.ui.Messages;
import net.sf.redmine_mylyn.ui.RedmineUiPlugin;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class RedmineRepositoryQueryPage extends AbstractRedmineRepositoryQueryPage {

	private static final String TITLE = Messages.CREATE_QUERY;
	
	private static final String DESCRIPTION = Messages.ENTER_QUERY_PARAMETER;

	private final Configuration configuration;
	
	private Text titleText;

	protected ScrolledComposite pageScroll;
	protected Composite pageComposite;
	protected Composite itemComposite;

	
	protected final Map<IQueryField, ComboViewer> searchOperators;
	protected final Map<IQueryField, Text> queryText;
	protected final Map<IQueryField, StructuredViewer> queryStructuredViewer;
	
	protected final List<CustomField> customFields;
	
	private boolean initialized;

	public RedmineRepositoryQueryPage(TaskRepository repository, IRepositoryQuery query, RedmineRepositoryConnector connector, Configuration configuration) {
		super(TITLE, repository, query, connector, configuration);

		this.configuration = getConfiguration();
		
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
		 */
		createTitleGroup(pageComposite);

		itemComposite = new Composite(pageComposite, SWT.NONE);
		itemComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		itemComposite.setLayout(new GridLayout(1, false));
		
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
		titleLabel.setText(Messages.QUERY_TITLE);

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
		for (CustomField customField : getConfiguration().getCustomFields().getIssueCustomFields()) {
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
			
			String constrArg = null;
			if(definition.isPersonType()) {
				constrArg = QueryField.VALUE_PERSON_ME;
			}
			
			ListViewer list = new ListViewer(parent, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
			list.setLabelProvider(new RedmineLabelProvider(constrArg));
			list.setContentProvider(new RedmineContentProvider(constrArg));
			list.getControl().setEnabled(false);
			
			control = list.getControl();
			queryStructuredViewer.put(queryField, list);
		} else {
			Text text = new Text(parent, SWT.BORDER);
			text.setEnabled(false);
			if(definition.getValidator()!=null) {
				text.addListener(SWT.Verify, new TextVerifyListener(definition.getValidator()));
			}
			
			control = text;
			queryText.put(queryField, text);

			if (definition.isBooleanType()) {
				text.setText(Messages.YES);
				text.setEditable(false);
			}
		}
		return control;
	}

	private ComboViewer createOperatorComboViewer(Composite parent, QueryField definition, final IQueryField queryField) {
		ComboViewer combo = new ComboViewer(parent, SWT.READ_ONLY | SWT.DROP_DOWN);

		String defaultValue = definition.isRequired() ? null : Messages.DISABLED;
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
	
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible && !initialized) {
			initialized = true;
			
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					updateRepositoryConfiguration(false);
					
					//Init QueryPage with default state
					switchOperatorState();
					updateProjectAttributes();

					restoreQuery();
				}
			});
		}
	}

	@Override
	protected void configurationChanged() {
		/* Projects */
		StructuredViewer viewer = queryStructuredViewer.get(QueryField.PROJECT);
		viewer.setInput(getConfiguration().getProjects());

		/* Status */
		viewer = queryStructuredViewer.get(QueryField.STATUS);
		viewer.setInput(getConfiguration().getIssueStatuses());
		
		/* Priority */
		viewer = queryStructuredViewer.get(QueryField.PRIORITY);
		viewer.setInput(getConfiguration().getIssuePriorities());

		/* Author */
		viewer = queryStructuredViewer.get(QueryField.AUTHOR);
		viewer.setInput(getConfiguration().getUsers());
		
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

		/* Create new/updated CustomFields */
		customFields.clear();
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
	
	private void updateCustomItemOptions() {
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
	
	private HashSet<Integer> findAvailableCustomFields(Project project) {
		
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
	
	private CompareOperator getSelectedOperator(IQueryField queryField) {
		ISelection selection = searchOperators.get(queryField).getSelection();
		if(selection!=null && !selection.isEmpty() && selection instanceof StructuredSelection) {
			Object selected = ((StructuredSelection)selection).getFirstElement();
			if(selected instanceof CompareOperator) {
				return (CompareOperator)selected;
			}
		}
		return null;
	}
	
	private Project getSelectedProject() {
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
	
	private void restoreQuery() {
		Query query = getRedmineQuery();
		if(query!=null) {
			titleText.setText(getQuery().getSummary());

			QueryBuilder.restoreTextQueryPart(query, configuration, searchOperators, queryText);
			QueryBuilder.restoreStructuredQueryPart(query, configuration, searchOperators, queryStructuredViewer);
		}
		
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
			IStatus status = RedmineUiPlugin.toStatus(e, Messages.ERRMSG_QUERY_CREATION_FAILED);
			StatusHandler.log(status);
			setErrorMessage(status.getMessage());
		}
		
	}

	@Override
	public String getQueryTitle() {
		return (titleText != null) ? titleText.getText() : Messages.QUERY_TITLE_FALLBACK;
	}
	
}
