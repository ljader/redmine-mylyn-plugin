package net.sf.redmine_mylyn.internal.ui.query;

import java.lang.reflect.InvocationTargetException;

import net.sf.redmine_mylyn.api.exception.RedmineApiErrorException;
import net.sf.redmine_mylyn.api.model.Configuration;
import net.sf.redmine_mylyn.api.query.Query;
import net.sf.redmine_mylyn.core.RedmineRepositoryConnector;
import net.sf.redmine_mylyn.ui.RedmineUiPlugin;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositoryQueryPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;

public abstract class AbstractRedmineRepositoryQueryPage extends AbstractRepositoryQueryPage {

	private final RedmineRepositoryConnector connector;
	
	private final Configuration configuration;
	
	public AbstractRedmineRepositoryQueryPage(String title, TaskRepository repository, IRepositoryQuery query, RedmineRepositoryConnector connector, Configuration configuration) {
		super(title, repository, query);

		this.connector = connector;
		this.configuration = configuration;
	}

	abstract protected void configurationChanged();
	
	public Configuration getConfiguration() {
		return configuration;
	}

	protected void updateRepositoryConfiguration(final boolean force) {
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
			((AbstractRedmineRepositoryQueryPage)getContainer().getCurrentPage()).setErrorMessage(e.getMessage());
			return;
		} catch (InterruptedException e) {
			return;
		}
		
		configurationChanged();
	}

	protected Query getRedmineQuery() {
		IRepositoryQuery repositoryQuery = getQuery();
		if(repositoryQuery!=null ) {
			try {
				return Query.fromUrl(repositoryQuery.getUrl(), getTaskRepository().getCharacterEncoding(), getConfiguration());
			} catch (RedmineApiErrorException e) {
				IStatus status = RedmineUiPlugin.toStatus(e, "Restore of Query failed");
				StatusHandler.log(status);
				setErrorMessage(status.getMessage());
			}
		}
		
		return null;
	}
}
