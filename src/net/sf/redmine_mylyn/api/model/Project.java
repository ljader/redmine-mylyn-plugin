package net.sf.redmine_mylyn.api.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.sf.redmine_mylyn.internal.api.parser.adapter.CFbyTrackerAdapter;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="project")
public class Project extends Property {

	private static final long serialVersionUID = 1L;

	@XmlAttribute(required=true)
	private boolean newIssueAllowed;

	@XmlAttribute(required=true)
	private boolean moveIssueAllowed;
	
	private String identifier;
	
	@XmlElement(name="trackers")
	@XmlList
	private int[] trackerIds;
	
	@XmlElement(name="versions")
	@XmlList
	private int[] versionIds;
	
	@XmlElement(name="issueCategories")
	@XmlList
	private int[] issueCategoryIds;
	
	@XmlElementWrapper(name="members")
	@XmlElement(name="member")
	private List<Member> members;
	
	@XmlElement(name="issueCustomFields")
	@XmlJavaTypeAdapter(CFbyTrackerAdapter.class)
	public Map<Integer, int[]> customFieldIdsByTrackerId;

	public boolean isNewIssueAllowed() {
		return newIssueAllowed;
	}

	public void setNewIssueAllowed(boolean newIssueAllowed) {
		this.newIssueAllowed = newIssueAllowed;
	}

	public boolean isMoveIssueAllowed() {
		return moveIssueAllowed;
	}

	public void setMoveIssueAllowed(boolean moveIssueAllowed) {
		this.moveIssueAllowed = moveIssueAllowed;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public int[] getTrackerIds() {
		return trackerIds;
	}
	
	public void setTrackerIds(int[] trackerIds) {
		this.trackerIds = trackerIds;
	}

	public int[] getVersionIds() {
		if(versionIds==null) {
			return new int[0];
		}
		return versionIds;
	}

	public void setVersionIds(int[] versionIds) {
		this.versionIds = versionIds;
	}
	
	public List<Member> getMembers() {
		return members;
	}

	public void setMembers(List<Member> members) {
		this.members = members;
	}

	public int[] getIssueCategoryIds() {
		return issueCategoryIds;
	}

	public void setIssueCategoryIds(int[] issueCategoryIds) {
		this.issueCategoryIds = issueCategoryIds;
	}

	//TODO should not be public
	public Map<Integer, int[]> getCustomFieldIdsByTrackerId() {
		if(customFieldIdsByTrackerId==null) {
			customFieldIdsByTrackerId = new HashMap<Integer, int[]>();
		}
		return customFieldIdsByTrackerId;
	}

	public int[] getCustomFieldIdsByTrackerId(int trackerId) {
		return getCustomFieldIdsByTrackerId().get(trackerId);
	}

	public int[] getAssignableMemberIds() {
		if(members==null) {
			return new int[0];
		}
		
		int[] ids = new int[members.size()];
		for (int i = 0; i < members.size(); i++) {
			if(members.get(i).isAssignable()) {
				ids[i] = members.get(i).getUserId();
			}
		}
		return ids;
	}
	
}
