package net.sf.redmine_mylyn.internal.ui;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.sf.redmine_mylyn.api.model.Configuration;
import net.sf.redmine_mylyn.api.model.User;
import net.sf.redmine_mylyn.core.RedmineRepositoryConnector;
import net.sf.redmine_mylyn.core.RedmineStatusException;
import net.sf.redmine_mylyn.core.RedmineUtil;
import net.sf.redmine_mylyn.ui.RedmineUiPlugin;

import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.ui.TasksUi;

public class RedminePersonProposalProvider implements IContentProposalProvider {

	private String currentUser;

	private Set<String> addressSet = null;

	private String repositoryUrl;

	private String connectorKind;

	private Map<String, String> proposals;
	
	private Configuration configuration;

	public RedminePersonProposalProvider(ITask task, TaskData taskData) {
		if (task != null) {
			repositoryUrl = task.getRepositoryUrl();
			connectorKind = task.getConnectorKind();
		} else if (taskData != null) {
			repositoryUrl = taskData.getRepositoryUrl();
			connectorKind = taskData.getConnectorKind();
		}

		if (repositoryUrl != null && connectorKind != null) {

			TaskRepository repository = TasksUi.getRepositoryManager().getRepository(connectorKind, repositoryUrl);
			if (repository != null) {

				AbstractRepositoryConnector connector = TasksUi.getRepositoryConnector(connectorKind);
				if (connector!=null && connector instanceof RedmineRepositoryConnector) {
					try {
						configuration = ((RedmineRepositoryConnector)connector).getClientManager().getClient(repository).getConfiguration();
					} catch (RedmineStatusException e) {
						RedmineUiPlugin.getLogService(getClass()).error(e, "Can't fetch repository configuration"); //$NON-NLS-1$
					}
				}
				
				AuthenticationCredentials credentials = repository.getCredentials(AuthenticationType.REPOSITORY);
				if (credentials != null && credentials.getUserName().length() > 0) {
					currentUser = credentials.getUserName();
				}
			}
		}
	}

	public RedminePersonProposalProvider(ITask task, TaskData taskData, Map<String, String> proposals) {
		this(task, taskData);
		this.proposals = proposals;
		
	}
	
	public IContentProposal[] getProposals(String contents, int position) {
		if (contents == null) {
			throw new IllegalArgumentException();
		}


		String searchText = contents.toLowerCase();

		Set<String> addressSet = new HashSet<String>();
		
		for (String address : getAddressSet()) {
			if (address.toLowerCase().contains(searchText)) {
				addressSet.add(address);
			}
		}
		
		IContentProposal[] result = new IContentProposal[addressSet.size()];
		int i = 0;
		for (final String address : addressSet) {
			result[i++] =  new RedminePersonContentProposal(
					address,
					address.contains(currentUser), 
					address,
					address.length());
		}
		
		Arrays.sort(result);
		return result;
	}

	private Set<String> getAddressSet() {
		if (addressSet != null) {
			return addressSet;
		}

		addressSet = new HashSet<String>();
		
		if (proposals != null && !proposals.isEmpty()) {
			for (Entry<String, String> entry : proposals.entrySet()) {
				
				String name = entry.getValue();
				if (name!=null && !name.isEmpty()) {
					User user = null;
					
					if (configuration!=null && (user=configuration.getUsers().getById(RedmineUtil.parseIntegerId(entry.getKey())))!=null) {
						addressSet.add(RedmineUtil.formatUserPresentation(user.getLogin(), name));
					} else {
						addressSet.add(RedmineUtil.formatUserPresentation(entry.getKey(), name));
					}
				}
			}
		}

		return addressSet;
	}

}

