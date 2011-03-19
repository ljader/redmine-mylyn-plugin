package net.sf.redmine_mylyn.internal.ui.query;

import net.sf.redmine_mylyn.api.exception.RedmineApiErrorException;
import net.sf.redmine_mylyn.api.model.Configuration;
import net.sf.redmine_mylyn.api.query.CompareOperator;
import net.sf.redmine_mylyn.api.query.Query;
import net.sf.redmine_mylyn.api.query.QueryField;
import net.sf.redmine_mylyn.api.query.QueryFilter;
import net.sf.redmine_mylyn.core.RedmineRepositoryConnector;
import net.sf.redmine_mylyn.core.RedmineUtil;
import net.sf.redmine_mylyn.internal.ui.Messages;
import net.sf.redmine_mylyn.ui.RedmineUiPlugin;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class RedmineRepositoryStoredQueryPage extends AbstractRedmineRepositoryQueryPage {

	private Composite pageComposite;
	
	private Text titleText;
	
	private ComboViewer queryViewer;
	
	private boolean initialized;
	
	public RedmineRepositoryStoredQueryPage(TaskRepository repository, IRepositoryQuery query, RedmineRepositoryConnector connector, Configuration configuration) {
		super(Messages.SELECT_STORED_QUERY, repository, query, connector, configuration);

		setTitle(Messages.SELECT_STORED_QUERY);
		setDescription(Messages.SELECT_OR_CREATE_QUERY);
	}

	@Override
	public void createControl(Composite parent) {
		pageComposite = new Composite(parent, SWT.NONE);
		pageComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		pageComposite.setLayout(new GridLayout(1, false));
		
		queryViewer = new ComboViewer(pageComposite, SWT.BORDER | SWT.READ_ONLY);
		queryViewer.setContentProvider(new RedmineContentProvider(Messages.CREATE_QUERY));
		queryViewer.setLabelProvider(new RedmineLabelProvider(Messages.CREATE_QUERY));
		queryViewer.setInput(Messages.CREATE_QUERY);
		queryViewer.getControl().setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
		queryViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				RedmineRepositoryStoredQueryPage.this.getWizard().getContainer().updateButtons();
				titleText.setEnabled(getSelectedQuery()!=null);
			}
		});

		Label titleLabel = new Label(pageComposite, SWT.NONE);
		titleLabel.setText(Messages.QUERY_TITLE);

		titleText = new Text(pageComposite, SWT.BORDER);
		titleText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL	| GridData.GRAB_HORIZONTAL));

		setControl(pageComposite);
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
					titleText.setEnabled(false);
					queryViewer.setSelection(new StructuredSelection(queryViewer.getElementAt(0)));

					restoreQuery();
				}
			});
		}
	}

	@Override
	protected void configurationChanged() {
		queryViewer.setInput(getConfiguration().getQueries());
	}
	
	@Override
	public String getQueryTitle() {
		if(titleText.getText().trim().isEmpty()) {
			return getSelectedQuery().getName();
		}
		return titleText.getText().trim();
	}

	@Override
	public void applyTo(IRepositoryQuery repositoryQuery) {
		repositoryQuery.setSummary(getQueryTitle());
		
		Query query = new Query();
		net.sf.redmine_mylyn.api.model.Query selectedQuery = getSelectedQuery();

		query.addFilter(QueryField.STOREDQUERY, CompareOperator.IS, ""+selectedQuery.getId()); //$NON-NLS-1$
		if(selectedQuery.getProjectId()>=1) {
			query.addFilter(QueryField.PROJECT, CompareOperator.IS, ""+selectedQuery.getProjectId()); //$NON-NLS-1$
		}
		
		try {
			repositoryQuery.setUrl(query.toUrl(getTaskRepository().getCharacterEncoding()));
		} catch (RedmineApiErrorException e) {
			IStatus status = RedmineUiPlugin.toStatus(e, Messages.ERRMSG_QUERY_CREATION_FAILED);
			StatusHandler.log(status);
			setErrorMessage(status.getMessage());
		}
	}

	@Override
	public boolean canFlipToNextPage() {
		return getSelectedQuery()==null;
	}
	
	@Override
	public boolean isPageComplete() {
		return getSelectedQuery()!=null;
	}

	private void restoreQuery() {
		try {
			Query query = getRedmineQuery();
			if(query != null) {

				QueryFilter queryFilter = query.getQueryFilter(QueryField.STOREDQUERY);
				if(queryFilter!=null) {

					int storedQueryId = RedmineUtil.parseIntegerId(queryFilter.getValues().get(0));
					net.sf.redmine_mylyn.api.model.Query oldValue = getConfiguration().getQueries().getById(storedQueryId);

					if(oldValue != null) {
						titleText.setText(getQuery().getSummary());
						queryViewer.setSelection(new StructuredSelection(oldValue), true);
					}
				}
			}
		
		} catch (IndexOutOfBoundsException e) {
			IStatus status = RedmineUiPlugin.toStatus(e, Messages.ERRMSG_QUERY_RESTORING_FAILED);
			StatusHandler.log(status);
			setErrorMessage(status.getMessage());
		}

		getContainer().updateButtons();
	}

	private net.sf.redmine_mylyn.api.model.Query getSelectedQuery() {
		if(queryViewer.getSelection() instanceof StructuredSelection) {
			StructuredSelection selection  = (StructuredSelection)queryViewer.getSelection();
			Object selected = selection.getFirstElement();
			if(selected instanceof net.sf.redmine_mylyn.api.model.Query) {
				return (net.sf.redmine_mylyn.api.model.Query)selected;
			}
		}
		return null;
	}
}
