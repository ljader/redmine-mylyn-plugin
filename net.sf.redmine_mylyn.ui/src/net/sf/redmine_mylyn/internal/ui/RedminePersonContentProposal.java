package net.sf.redmine_mylyn.internal.ui;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.swt.graphics.Image;

public class RedminePersonContentProposal implements IContentProposal, Comparable<RedminePersonContentProposal> {

	private final String address;

	private final boolean isCurrentUser;

	private final String replacementText;

	private final int cursorPosition;

	public RedminePersonContentProposal(String address, boolean isCurrentUser, String replacementText, int cursorPosition) {
		Assert.isNotNull(address);
		Assert.isNotNull(replacementText);
		this.address = address;
		this.isCurrentUser = isCurrentUser;
		this.replacementText = replacementText;
		this.cursorPosition = cursorPosition;
	}

	public RedminePersonContentProposal(String address, boolean isCurrentUser) {
		this(address, isCurrentUser, address, address.length());
	}

	public String getLabel() {
		return address;
	}

	public String getDescription() {
		return null;
	}

	public int getCursorPosition() {
		return cursorPosition;
	}

	public String getContent() {
		return replacementText;
	}

	public Image getImage() {
//		if (isCurrentUser) {
//			return CommonImages.getImage(CommonImages.PERSON_ME);
//		} else {
//			return CommonImages.getImage(CommonImages.PERSON);
//		}
		return null;
	}

	public int compareTo(RedminePersonContentProposal otherContentProposal) {
		if (isCurrentUser) {
			return -1;
		} else if (otherContentProposal.isCurrentUser) {
			return 1;
		}
		return address.compareToIgnoreCase(otherContentProposal.address);
	}

	public boolean isCurrentUser() {
		return isCurrentUser;
	}

}
