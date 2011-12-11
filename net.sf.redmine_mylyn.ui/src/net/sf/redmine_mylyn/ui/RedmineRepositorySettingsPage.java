package net.sf.redmine_mylyn.ui;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.redmine_mylyn.api.client.RedmineServerVersion;
import net.sf.redmine_mylyn.api.client.RedmineServerVersion.Release;
import net.sf.redmine_mylyn.api.exception.RedmineApiAuthenticationException;
import net.sf.redmine_mylyn.core.IRedmineConstants;
import net.sf.redmine_mylyn.core.IRedmineExtensionManager;
import net.sf.redmine_mylyn.core.RedmineCorePlugin;
import net.sf.redmine_mylyn.core.RedmineStatusException;
import net.sf.redmine_mylyn.core.client.ClientFactory;
import net.sf.redmine_mylyn.core.client.IClient;
import net.sf.redmine_mylyn.internal.ui.Messages;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositorySettingsPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;


public class RedmineRepositorySettingsPage extends AbstractRepositorySettingsPage {

//	private static final String EXTENSION_ID_TEXTILE = "org.eclipse.mylyn.wikitext.tasks.ui.editor.textileTaskEditorExtension"; //$NON-NLS-1$
//	private static final String EXTENSION_ID_PLAIN = "none"; //$NON-NLS-1$
//	private static final String EXTENSION_POINT_CLIENT = "org.svenk.redmine.core.clientInterface"; //$NON-NLS-1$
	
	private String checkedUrl;
	
	private RedmineServerVersion requiredVersion;

	private String detectedVersionString = null;
	
	private Text apiKeyText;

	private Label apiKeyLabel;
	
	private Button apiKeyEnableButton;
	
	private HashMap<String, Button> redmineExtensions;
	
	public RedmineRepositorySettingsPage(TaskRepository taskRepository) {
		
		super(Messages.SETTINGS_PAGE_TITLE, Messages.SETTINGS_PAGE_EXAMPLE_URL, taskRepository);

		//TODO configure
		requiredVersion = new RedmineServerVersion(Release.REDMINE_1_0, Release.PLUGIN_2_7);

		setNeedsAnonymousLogin(false);
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
	public void applyTo(TaskRepository repository) {
		super.applyTo(repository);
		repository.setVersion(detectedVersionString);
		if(useApiKey()) {
			repository.setProperty(IRedmineConstants.REPOSITORY_SETTING_API_KEY, apiKeyText.getText().trim());
		} else {
			repository.removeProperty(IRedmineConstants.REPOSITORY_SETTING_API_KEY);
		}
		
		if (redmineExtensions!=null) {
			for (Entry<String, Button> entry : redmineExtensions.entrySet()) {
				repository.setProperty(entry.getKey(), Boolean.toString(entry.getValue().getSelection()));
			}
		}
		
	}
	
	@Override
	protected Validator getValidator(final TaskRepository repository) {
		return new Validator() {
			@Override
			public void run(IProgressMonitor monitor) throws CoreException {
				if (!isValidUrl(repository.getUrl())) {
					throw new CoreException(new Status(IStatus.ERROR, RedmineCorePlugin.PLUGIN_ID, Messages.INVALID_SERVERURL));
				}
				
				detectedVersionString = null;
				
				RedmineServerVersion detectedVersion = null;
				try {
					IClient client = ClientFactory.createClient(repository);
					detectedVersion = client.checkClientConnection(monitor);
				} catch (RedmineStatusException e) {
					if(e.getCause() instanceof RedmineApiAuthenticationException) {
						throw new CoreException(new Status(IStatus.ERROR, RedmineCorePlugin.PLUGIN_ID, Messages.INVALID_CREDENTIALS));
					}
					throw new CoreException(e.getStatus());
				}
				checkedUrl = repository.getRepositoryUrl();
				
				validateVersion(requiredVersion, detectedVersion);
//				validateEditorExtension(repository);

				detectedVersionString = detectedVersion.toString();

				String msg = Messages.SUCCESSFUL_CONNECTION_TEST_X_X;
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
					throw new CoreException(new Status(IStatus.ERROR, RedmineCorePlugin.PLUGIN_ID, Messages.ERRMSG_CONNECTION_TEST_FAILED_UNKNOWN_VERSION));
				} else if (detected.redmine.compareTo(required.redmine)<0 || detected.plugin.compareTo(required.plugin)<0) {
					String msg = Messages.ERRMSG_CONNECTION_TEST_FAILED_WRONG_VERSION;
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
	protected void createSettingControls(Composite parent) {
		super.createSettingControls(parent);
		
		//oldApiKey
		String apiKey = repository==null ? null : repository.getProperty(IRedmineConstants.REPOSITORY_SETTING_API_KEY);
		boolean useApiKey = apiKey!=null && !apiKey.isEmpty();

		//REPOSITORY_SETTING_API_KEY
		apiKeyLabel = new Label(parent, SWT.NONE);
		apiKeyLabel.setText(Messages.LBL_APIKEY);

		apiKeyText = new Text(parent, SWT.BORDER);
		apiKeyText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		apiKeyText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				isPageComplete();
				
			}
		});
		
		if(apiKey!=null) {
			apiKeyText.setText(apiKey);
		}
		
		apiKeyEnableButton = new Button(parent, SWT.CHECK);
		apiKeyEnableButton.setText(Messages.LBL_ENABLE);
		apiKeyEnableButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setApiKeyUsage(apiKeyEnableButton.getSelection());
				isPageComplete();
			}
		});
		
		apiKeyLabel.moveBelow(savePasswordButton);
		apiKeyText.moveBelow(apiKeyLabel);
		apiKeyEnableButton.moveBelow(apiKeyText);
		
		setApiKeyUsage(useApiKey);
	}

	@Override
	protected void createAdditionalControls(Composite parent) {
		IRedmineExtensionManager extMgr = RedmineCorePlugin.getDefault().getExtensionManager();
		Map<String, String> extensions = extMgr.getExtensions();
		
		if(extensions.size()>0) {
			
			Label lbl = new Label(parent, SWT.NONE);
			lbl.setText("Installed Redmine Plugins:");
			
			
			redmineExtensions = new HashMap<String, Button>(extensions.size());
			
			boolean first = true;
			for (Entry<String, String> entry : extensions.entrySet()) {
				
				if (first) {
					first = false;
				} else {
					new Label(parent, SWT.NONE);
				}
				
				Button btn = new Button(parent, SWT.CHECK);
				btn.setText(entry.getValue());
				
				if (repository!=null) {
					String oldValue = repository.getProperty(entry.getKey()); 
					btn.setSelection(oldValue!=null && Boolean.parseBoolean(oldValue));
				}
				
				redmineExtensions.put(entry.getKey(), btn);
			}
			
		}
	}

	@Override
	public boolean isPageComplete() {
		String errorMessage = null;
		
		if(isMissingApiKey()) {
			errorMessage = Messages.ERRMSG_INVALID_APIKEY;
		}
		
		if(isMissingApiKeyUsage()) {
			errorMessage = Messages.ERRMSG_HTTPAUTH_CREDENTIALS_MISMATCH;
		}
		
		if(errorMessage!=null) {
			setMessage(errorMessage, IMessageProvider.ERROR);
			return false;
		} else {
			return super.isPageComplete() && checkedUrl!= null && detectedVersionString != null && checkedUrl.equals(getRepositoryUrl());
		}
	}

	@Override
	protected boolean isValidUrl(String url) {
		return url.matches("^https?://.+"); //$NON-NLS-1$
	}
	
	@Override
	protected boolean isMissingCredentials() {
		return !useApiKey() && super.isMissingCredentials();
	}
	
	private boolean isMissingApiKey() {
		return useApiKey() && apiKeyText.getText().trim().isEmpty();
	}
	
	private boolean useApiKey() {
		return apiKeyEnableButton!=null && apiKeyEnableButton.getSelection();
	}
	
	protected boolean isMissingApiKeyUsage() {
		try {
			return !useApiKey() && getHttpAuth();
		} catch (NullPointerException e) {
			return false;
		}
	}

	private void setApiKeyUsage(boolean use) {
		Composite parent = apiKeyEnableButton.getParent();
		
		repositoryUserNameEditor.setEnabled(!use, parent);
		repositoryPasswordEditor.setEnabled(!use, parent);
		
		apiKeyEnableButton.setSelection(use);
		apiKeyText.setEnabled(use);
		
	}
	
	@Override
	protected boolean isMissingCredentials() {
		return !useApiKey() && super.isMissingCredentials();
	}
	
	private boolean isMissingApiKey() {
		return useApiKey() && apiKeyText.getText().trim().isEmpty();
	}
	
	private boolean useApiKey() {
		return apiKeyEnableButton!=null && apiKeyEnableButton.getSelection();
	}
	
	protected boolean isMissingApiKeyUsage() {
		try {
			return !useApiKey() && getHttpAuth();
		} catch (NullPointerException e) {
			return false;
		}
	}

	private void setApiKeyUsage(boolean use) {
		Composite parent = apiKeyEnableButton.getParent();
		
		repositoryUserNameEditor.setEnabled(!use, parent);
		repositoryPasswordEditor.setEnabled(!use, parent);
		
		apiKeyEnableButton.setSelection(use);
		apiKeyText.setEnabled(use);
		
	}

}
