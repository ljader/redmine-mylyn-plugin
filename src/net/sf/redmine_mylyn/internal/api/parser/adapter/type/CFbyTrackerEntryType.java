package net.sf.redmine_mylyn.internal.api.parser.adapter.type;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

public class CFbyTrackerEntryType {
	
	@XmlAttribute
	public int trackerId;
	
	@XmlValue
	public List<Integer> idList;

}
