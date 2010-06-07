package net.sf.redmine_mylyn.api.model.container;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import net.sf.redmine_mylyn.api.model.Query;

@XmlRootElement(name="queries")
@XmlAccessorType(XmlAccessType.NONE)
public class Queries extends AbstractPropertyContainer<Query> {

	protected List<Query> queries;
	
	@Override
	@XmlElement(name="query")
	protected List<Query> getModifiableList() {
		if(queries==null) {
			queries = new ArrayList<Query>();
		}
		return queries;
	}
	
}
