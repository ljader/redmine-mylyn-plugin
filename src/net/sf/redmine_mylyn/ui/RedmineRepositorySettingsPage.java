package net.sf.redmine_mylyn.ui;

import net.sf.redmine_mylyn.api.client.RedmineServerVersion;
import net.sf.redmine_mylyn.api.client.RedmineServerVersion.Release;
import net.sf.redmine_mylyn.core.RedmineCorePlugin;
import net.sf.redmine_mylyn.core.RedmineStatusException;
import net.sf.redmine_mylyn.core.client.ClientFactory;
import net.sf.redmine_mylyn.core.client.IClient;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.net.UnsupportedRequestException;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositorySettingsPage;
import org.eclipse.swt.widgets.Composite;


public class RedmineRepositorySettingsPage extends AbstractRepositorySettingsPage {

//	private static final String EXTENSION_ID_TEXTILE = "org.eclipse.mylyn.wikitext.tasks.ui.editor.textileTaskEditorExtension"; //$NON-NLS-1$
//	private static final String EXTENSION_ID_PLAIN = "none"; //$NON-NLS-1$
//	private static final String EXTENSION_POINT_CLIENT = "org.svenk.redmine.core.clientInterface"; //$NON-NLS-1$
	
	private String checkedUrl;
	
	private RedmineServerVersion requiredVersion;

	private String detectedVersionString = null;

	public RedmineRepositorySettingsPage(TaskRepository taskRepository) {
		
		super("Redmine Repository Settings", "Example: http://www.your-domain.de/redmine", taskRepository);

		//TODO configure
//		requiredVersion = new RedmineServerVersion(Release.REDMINE_1_0, Release.PLUGIN_2_7);
		requiredVersion = new RedmineServerVersion(Release.REDMINE_0_9_DEVEL, Release.PLUGIN_2_7);

		setNeedsAnonymousLogin(true);
		setNeedsValidation(true);
		setNeedsHttpAuth(true);
	}
	
	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
		checkedUrl = getRepositoryUrl();

		//Set Default Encoding
		if (getRepository()==null) {
			setEncoding("UTF-8"); //$NON-NLS-1$
		}
	}
	
	@Override
	public boolean isPageComplete() {
		return super.isPageComplete() && checkedUrl!= null && detectedVersionString != null && checkedUrl.equals(getRepositoryUrl());
	}

	@Override
	public void applyTo(TaskRepository repository) {
		super.applyTo(repository);
		repository.setVersion(detectedVersionString);
	}
	
	@Override
	protected Validator getValidator(final TaskRepository repository) {
		return new Validator() {
			@Override
			public void run(IProgressMonitor monitor) throws CoreException {
				detectedVersionString = null;
				
				RedmineServerVersion detectedVersion = null;
				try {
					IClient client = ClientFactory.createClient(repository);
					detectedVersion = client.checkClientConnection(monitor);
				} catch (RedmineStatusException e) {
					if(e.getCause() instanceof UnsupportedRequestException) {
						throw new CoreException(new Status(IStatus.ERROR, RedmineCorePlugin.PLUGIN_ID, "Invalid credentials"));
					}
					throw new CoreException(e.getStatus());
				}
				checkedUrl = repository.getRepositoryUrl();
				
				validateVersion(requiredVersion, detectedVersion);
//				validateEditorExtension(repository);

				detectedVersionString = detectedVersion.toString();

				String msg = "Test of connection was successful - Redmine %s with Mylyn-Plugin %s";
				msg = String.format(msg, detectedVersion.redmine.toString(), detectedVersion.plugin.toString());
				this.setStatus(new Status(IStatus.OK, RedmineCorePlugin.PLUGIN_ID, msg));
			}
			
//			@SuppressWarnings("restriction")
//			protected void validateEditorExtension(TaskRepository repository) throws CoreException {
//				String editorExtension = repository.getProperty(TaskEditorExtensions.REPOSITORY_PROPERTY_EDITOR_EXTENSION);
//				if (!(editorExtension==null || editorExtension.equals(EXTENSION_ID_PLAIN) || editorExtension.equals(EXTENSION_ID_TEXTILE))) {
//					throw new CoreException(new Status(IStatus.WARNING, RedmineCorePlugin.PLUGIN_ID, Messages.RedmineRepositorySettingsPage_MESSAGE_WIKI_WARNING));
//				}
//			}
			
			protected void validateVersion(RedmineServerVersion required, RedmineServerVersion detected) throws CoreException {
				if (detected==null || detected.redmine==null || detected.plugin==null) {
					throw new CoreException(new Status(IStatus.ERROR, RedmineCorePlugin.PLUGIN_ID, "Can't detect the version of Redmine"));
				} else if (detected.redmine.compareTo(required.redmine)<0 || detected.plugin.compareTo(required.plugin)<0) {
					String msg = "Redmine %s with Mylyn-Plugin %s is required, found Version %s with %s";
					msg = String.format(msg, required.redmine.toString(), required.plugin.toString(), detected.redmine.toString(), detected.plugin.toString());
					throw new CoreException(new Status(IStatus.ERROR, RedmineCorePlugin.PLUGIN_ID, msg));
				}
			}
		};
	}

	@Override
	public String getVersion() {
		return detectedVersionString;
	}
	
	@Override
	public String getConnectorKind() {
		return RedmineCorePlugin.REPOSITORY_KIND;
	}

	@Override
	protected void createAdditionalControls(Composite arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected boolean isValidUrl(String arg0) {
		return true;
	}

}
