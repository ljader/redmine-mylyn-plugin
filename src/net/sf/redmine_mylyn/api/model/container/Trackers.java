package net.sf.redmine_mylyn.api.model.container;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import net.sf.redmine_mylyn.api.model.Tracker;

@XmlRootElement(name="trackers")
@XmlAccessorType(XmlAccessType.NONE)
public class Trackers extends AbstractSortedPropertyContainer<Tracker> {

	private static final long serialVersionUID = 1L;

	protected List<Tracker> trackers;
	
	@Override
	@XmlElement(name="tracker")
	protected List<Tracker> getModifiableList() {
		if(trackers==null) {
			trackers = new ArrayList<Tracker>();
		}
		return trackers;
	}

}
