package net.sf.redmine_mylyn.internal.ui.query;

import net.sf.redmine_mylyn.api.model.Configuration;
import net.sf.redmine_mylyn.core.RedmineCorePlugin;
import net.sf.redmine_mylyn.core.RedmineRepositoryConnector;
import net.sf.redmine_mylyn.internal.ui.Messages;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.wizards.RepositoryQueryWizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class RedmineRepositoryQueryWizard extends RepositoryQueryWizard {

	private final RedmineRepositoryConnector connector;
	
	private final Configuration configuration;

	private Button updateButton;
	
	private final RedmineRepositoryStoredQueryPage page1;

	private final RedmineRepositoryQueryPage page2;

	public RedmineRepositoryQueryWizard(TaskRepository repository, IRepositoryQuery query) {
		super(repository);

		connector = (RedmineRepositoryConnector) TasksUi.getRepositoryManager().getRepositoryConnector(RedmineCorePlugin.REPOSITORY_KIND);
		Assert.isNotNull(connector);
		configuration = connector.getRepositoryConfiguration(repository);
		Assert.isNotNull(configuration);
		
		addPage(page1 = new RedmineRepositoryStoredQueryPage(repository, query, connector, configuration));
		addPage(page2 = new RedmineRepositoryQueryPage(repository, query, connector, configuration));
	}
	
	@Override
	public IWizardPage getStartingPage() {
		if(page1.getRedmineQuery()==null || page1.getRedmineQuery().isStoredQuery()) {
			return page1;
		}
		return page2;
	}

	@Override
	public void createPageControls(Composite pageContainer) {
		super.createPageControls(pageContainer);
		
		if(getContainer() instanceof WizardDialog) {
			createUpdateButton((WizardDialog)getContainer());
		}
	}
	
	
	private void createUpdateButton(WizardDialog wizardDialog) {
		if (wizardDialog.buttonBar instanceof Composite) {
			Composite buttonBar = (Composite) wizardDialog.buttonBar;
			((GridLayout) buttonBar.getLayout()).numColumns++;

			updateButton = new Button(buttonBar, SWT.PUSH);
			updateButton.setText(Messages.UPDATE_ATTRIBUTES);
			updateButton.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
			updateButton.moveAbove(buttonBar.getChildren()[buttonBar.getChildren().length - 2]);
			updateButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					if (getTaskRepository() != null) {
						getCurrentPage().updateRepositoryConfiguration(true);
					} else {
						MessageDialog.openInformation(Display.getCurrent()
							.getActiveShell(),
							Messages.ERRMSG_UPDATING_ATTRIBUTES_FAILED,
							Messages.ERRMSG_NO_REPOSITORY_AVAILABLE);
					}
				}
			});
		}
	}

	private AbstractRedmineRepositoryQueryPage getCurrentPage() {
		if(getContainer().getCurrentPage()!=null) {
			return (AbstractRedmineRepositoryQueryPage)getContainer().getCurrentPage();
		}
		return null;
	}
}
