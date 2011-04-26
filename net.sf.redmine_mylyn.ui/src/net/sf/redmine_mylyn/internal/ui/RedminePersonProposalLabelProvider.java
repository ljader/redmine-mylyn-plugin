package net.sf.redmine_mylyn.internal.ui;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class RedminePersonProposalLabelProvider extends LabelProvider {

	@Override
	public Image getImage(Object element) {
		if (element instanceof RedminePersonContentProposal) {
			return ((RedminePersonContentProposal) element).getImage();
		}
		return null;
	}

	@Override
	public String getText(Object element) {
		if (element instanceof RedminePersonContentProposal) {
			return ((RedminePersonContentProposal) element).getLabel();
		}
		return super.getText(element);
	}
}
