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

	private final String repositoryUrl;

	private final String connectorKind;

	private Map<String, String> proposals;

	private Configuration configuration;

	public RedminePersonProposalProvider(final ITask task, final TaskData taskData) {
		repositoryUrl = taskData.getRepositoryUrl();
		connectorKind = taskData.getConnectorKind();

		if (repositoryUrl != null && connectorKind != null) {

			final TaskRepository repository = TasksUi.getRepositoryManager().getRepository(connectorKind, repositoryUrl);
			if (repository != null) {

				final AbstractRepositoryConnector connector = TasksUi.getRepositoryConnector(connectorKind);
				if (connector!=null && connector instanceof RedmineRepositoryConnector) {
					try {
						configuration = ((RedmineRepositoryConnector)connector).getClientManager().getClient(repository).getConfiguration();
					} catch (final RedmineStatusException e) {
						RedmineUiPlugin.getLogService(getClass()).error(e, "Can't fetch repository configuration"); //$NON-NLS-1$
					}
				}

				final AuthenticationCredentials credentials = repository.getCredentials(AuthenticationType.REPOSITORY);
				if (credentials != null && credentials.getUserName().length() > 0) {
					currentUser = credentials.getUserName();
				}
			}
		}
	}

	public RedminePersonProposalProvider(final ITask task, final TaskData taskData, final Map<String, String> proposals) {
		this(task, taskData);
		this.proposals = proposals;

	}

	public void setProposals( final Map<String, String> proposals ) {
		this.proposals = proposals;
		addressSet = null;
	}

	@Override
	public IContentProposal[] getProposals(final String contents, final int position) {
		if (contents == null) {
			throw new IllegalArgumentException();
		}


		final String searchText = contents.toLowerCase();

		final Set<String> addressSet = new HashSet<String>();

		for (final String address : getAddressSet()) {
			if (address.toLowerCase().contains(searchText)) {
				addressSet.add(address);
			}
		}

		final IContentProposal[] result = new IContentProposal[addressSet.size()];
		int i = 0;
		for (final String address : addressSet) {
			result[i++] =  new RedminePersonContentProposal(
					address,
					currentUser != null && address.contains(currentUser),
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
			for (final Entry<String, String> entry : proposals.entrySet()) {

				final String name = entry.getValue();
				if (name!=null && !name.isEmpty()) {
					User user = null;

					if (configuration!=null && (user=configuration.getUsers().getById(RedmineUtil.parseIntegerId(entry.getKey())))!=null) {
						final String login = user.getLogin();
						if (login.isEmpty()) {
							addressSet.add(RedmineUtil.formatUserPresentation(name, name));
						} else {
							addressSet.add(RedmineUtil.formatUserPresentation(user.getLogin(), name));
						}
					} else {
						addressSet.add(RedmineUtil.formatUserPresentation(entry.getKey(), name));
					}
				}
			}
		}

		return addressSet;
	}

}

