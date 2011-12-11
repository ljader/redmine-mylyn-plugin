package net.sf.redmine_mylyn.internal.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.redmine_mylyn.core.IRedmineExtensionField;
import net.sf.redmine_mylyn.core.IRedmineExtensionManager;
import net.sf.redmine_mylyn.core.RedmineCorePlugin;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.mylyn.tasks.core.TaskRepository;

public class RedmineExtensionManager implements IRedmineExtensionManager {

	private final static String EXTENSION_ID = "net.sf.redmine_mylyn.core.redmineextension";

	private final static RedmineExtensionManager instance = new RedmineExtensionManager();

	private final IConfigurationElement configurationElements[];

	private Map<String, String> extensionNames;
	
	private Map<String, IRedmineExtensionField[]> timeEntryFieldsByRepository = new HashMap<String, IRedmineExtensionField[]>();

	private RedmineExtensionManager() {
		configurationElements = Platform.getExtensionRegistry()
				.getConfigurationElementsFor(EXTENSION_ID);
	}

	public static RedmineExtensionManager getInstance() {
		return instance;
	}

	@Override
	public Map<String, String> getExtensions() {

		if (extensionNames == null) {
			synchronized (this) {

				if (extensionNames == null) {
					extensionNames = new HashMap<String, String>(
							configurationElements.length);
					for (IConfigurationElement confElement : configurationElements) {
						extensionNames.put(confElement.getDeclaringExtension()
								.getUniqueIdentifier(), confElement
								.getDeclaringExtension().getLabel());
					}
				}

			}
		}

		return extensionNames;
	}

	@Override
	public IRedmineExtensionField[] getAdditionalTimeEntryFields(TaskRepository repository) {
		//TODO ignore issue fields
		
		String repositoryUrl = repository.getRepositoryUrl();
		if (!timeEntryFieldsByRepository.containsKey(repositoryUrl)) {
			
			synchronized (this) {
				
				if (!timeEntryFieldsByRepository.containsKey(repositoryUrl)) {
					
					List<IRedmineExtensionField> fields =  new ArrayList<IRedmineExtensionField>();
					
					for (IConfigurationElement confElement : configurationElements) {
						
						//Extension active?
						String identifier = confElement.getDeclaringExtension().getUniqueIdentifier();
						if (Boolean.parseBoolean(repository.getProperty(identifier))) {
							try {
								for (IConfigurationElement childElem : confElement.getChildren()) {
									
									Object obj = childElem.createExecutableExtension("fieldDescriptor");
									if (obj instanceof IRedmineExtensionField) {
										fields.add((IRedmineExtensionField)obj);
										
									}
								}
							} catch (CoreException e) {
								RedmineCorePlugin.getLogService(this.getClass()).error(e, "Instantiation of IRedmineExtensionField failed");
							}
						}
					}
					
					timeEntryFieldsByRepository.put(repositoryUrl, fields.toArray(new IRedmineExtensionField[fields.size()]));
				}
			}
			
		}
		
		return timeEntryFieldsByRepository.get(repositoryUrl);
	}

	@Override
	public void repositoryAdded(TaskRepository repository) {
	}

	@Override
	public void repositoryRemoved(TaskRepository repository) {
		synchronized (this) {
			timeEntryFieldsByRepository.remove(repository.getRepositoryUrl());
		}
	}

	@Override
	public void repositorySettingsChanged(TaskRepository repository) {
		synchronized (this) {
			timeEntryFieldsByRepository.remove(repository.getRepositoryUrl());
		}
	}

	@Override
	public void repositoryUrlChanged(TaskRepository repository, String oldUrl) {
	}

}
