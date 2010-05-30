package net.sf.redmine_mylyn.api.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name="sortedProperty")
@XmlAccessorType(XmlAccessType.FIELD)
public class SortedProperty extends Property implements Comparable<SortedProperty> {

	private static final long serialVersionUID = 1L;

	@XmlElement(required=true)
	protected int position;

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	@Override
	public int compareTo(SortedProperty o) {
		if (o.getPosition()<getPosition()) {
			return 1;
		}
		if (o.getPosition()>getPosition()) {
			return -1;
		}
		return 0;
	}
}
