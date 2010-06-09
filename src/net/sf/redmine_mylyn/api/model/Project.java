package net.sf.redmine_mylyn.api.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
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

	private String identifier;
	
	@XmlElement(name="trackers")
	@XmlList
	private List<Integer> trackerIds;
	
	@XmlElement(name="versions")
	@XmlList
	private List<Integer> versionIds;
	
	@XmlElement(name="issueCategories")
	@XmlList
	private List<Integer> issueCategoryIds;
	
	@XmlElementWrapper(name="members")
	@XmlElement(name="member")
	private List<Member> members;
	
	@XmlElement(name="issueCustomFields")
	@XmlJavaTypeAdapter(CFbyTrackerAdapter.class)
	public Map<Integer, List<Integer>> customFieldIdsByTrackerId;

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public List<Integer> getTrackerIds() {
		if(trackerIds==null) {
			trackerIds = new ArrayList<Integer>();
		}
		return trackerIds;
	}

	public void setTrackerIds(List<Integer> trackerIds) {
		this.trackerIds = trackerIds;
	}

	public List<Integer> getVersionIds() {
		if (versionIds==null) {
			versionIds = new ArrayList<Integer>();
		}
		return versionIds;
	}

	public void setVersionIds(List<Integer> versionIds) {
		this.versionIds = versionIds;
	}

	public List<Member> getMembers() {
		if(members==null) {
			members = new ArrayList<Member>();
		}
		return members;
	}

	public void setMembers(List<Member> members) {
		this.members = members;
	}

	public List<Integer> getIssueCategoryIds() {
		if (issueCategoryIds==null) {
			issueCategoryIds = new ArrayList<Integer>();
		}
		return issueCategoryIds;
	}

	public void setIssueCategoryIds(List<Integer> issueCategoryIds) {
		this.issueCategoryIds = issueCategoryIds;
	}

	
	public Map<Integer, List<Integer>> getCustomFieldIdsByTrackerId() {
		if(customFieldIdsByTrackerId==null) {
			customFieldIdsByTrackerId = new HashMap<Integer, List<Integer>>();
		}
		return customFieldIdsByTrackerId;
	}

	public void setCustomFieldIdsByTrackerId(
			Map<Integer, List<Integer>> customFieldIdsByTrackerId) {
		this.customFieldIdsByTrackerId = customFieldIdsByTrackerId;
	}

	public List<Integer> getCustomFieldIdsByTrackerId(int trackerId) {
		return getCustomFieldIdsByTrackerId().get(trackerId);
	}

}
