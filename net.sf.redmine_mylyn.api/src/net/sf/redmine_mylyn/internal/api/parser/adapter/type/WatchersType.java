package net.sf.redmine_mylyn.internal.api.parser.adapter.type;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlValue;

public class WatchersType {

	@XmlAttribute
	public boolean viewAllowed;

	@XmlAttribute
	public boolean addAllowed;
	
	@XmlAttribute
	public boolean deleteAllowed;

	@XmlValue
	@XmlList
	public int[] watchers;

}
