package net.sf.redmine_mylyn.ui;

import net.sf.redmine_mylyn.core.RedmineCorePlugin;
import net.sf.redmine_mylyn.internal.ui.query.RedmineRepositoryQueryPage;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.wizards.ITaskRepositoryPage;
import org.eclipse.mylyn.tasks.ui.wizards.NewTaskWizard;
import org.eclipse.mylyn.tasks.ui.wizards.RepositoryQueryWizard;

public class RedmineRepositoryConnectorUi extends AbstractRepositoryConnectorUi {

	@Override
	public String getConnectorKind() {
		return RedmineCorePlugin.REPOSITORY_KIND;
	}

	@Override
	public ITaskRepositoryPage getSettingsPage(TaskRepository repository) {
		return new RedmineRepositorySettingsPage(repository);
	}

	@Override
	public boolean hasSearchPage() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IWizard getNewTaskWizard(TaskRepository repository, ITaskMapping mapping) {
		return new NewTaskWizard(repository, mapping);
	}

	@Override
	public IWizard getQueryWizard(TaskRepository repository, IRepositoryQuery query) {
		RepositoryQueryWizard wizard = new RepositoryQueryWizard(repository);
		wizard.addPage(new RedmineRepositoryQueryPage(repository, query));
		return wizard;
	}

}
