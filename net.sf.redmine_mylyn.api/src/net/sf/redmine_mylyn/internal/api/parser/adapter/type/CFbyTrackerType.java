package net.sf.redmine_mylyn.internal.api.parser.adapter.type;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class CFbyTrackerType {

	@XmlElement(name="issueCustomFieldsByTracker")
	public List<CFbyTrackerEntryType> entrys;

}
