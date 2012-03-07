package net.sf.redmine_mylyn.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.redmine_mylyn.core.RedmineCorePlugin;
import net.sf.redmine_mylyn.internal.ui.RedmineRevisionHyperlink;
import net.sf.redmine_mylyn.internal.ui.query.RedmineRepositoryQueryWizard;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.TaskHyperlink;
import org.eclipse.mylyn.tasks.ui.wizards.ITaskRepositoryPage;
import org.eclipse.mylyn.tasks.ui.wizards.NewTaskWizard;

public class RedmineRepositoryConnectorUi extends AbstractRepositoryConnectorUi {

    private static Pattern revisionPattern = Pattern.compile("r(\\d+)");
    private static Pattern commitPattern = Pattern.compile("commit:([0-9a-z]{8,})\\b");
    private static Pattern issuePattern = Pattern.compile("#(\\d+)");

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
		// TODO not implemented yet
		return false;
	}

	@Override
	public IWizard getNewTaskWizard(TaskRepository repository, ITaskMapping mapping) {
		return new NewTaskWizard(repository, mapping);
	}

	@Override
	public IWizard getQueryWizard(TaskRepository repository, IRepositoryQuery query) {
		return new RedmineRepositoryQueryWizard(repository, query);
	}

	@Override
	public IHyperlink[] findHyperlinks(TaskRepository repository, ITask task, String text, int textOffset, int lineOffset) {
		List<IHyperlink> links = new ArrayList<IHyperlink>();
		Matcher m = null;

		m = revisionPattern.matcher(text);
		while (m.find()) {
			if (m.start() <= textOffset && textOffset <= m.end()) {
				links.add(new RedmineRevisionHyperlink(buildRegion(lineOffset, m.start(), m.end()), repository, task, m.group(1)));
			}
		}
		
		m = commitPattern.matcher(text);
		while (m.find()) {
			if (m.start() <= textOffset && textOffset <= m.end()) {
				links.add(new RedmineRevisionHyperlink(buildRegion(lineOffset, m.start(), m.end()), repository, task, m.group(1)));
			}
		}

		m = issuePattern.matcher(text);
		while (m.find()) {
			if (m.start() <= textOffset && textOffset <= m.end()) {
				links.add(new TaskHyperlink(buildRegion(lineOffset, m.start(),
						m.end()), repository, m.group(1)));
			}
		}

		return links.toArray(new IHyperlink[links.size()]);
	};

	protected IRegion buildRegion(int lineOffset, int matcherStart, int matcherEnd) {
		return new Region(lineOffset + matcherStart, matcherEnd - matcherStart);
	}

}
