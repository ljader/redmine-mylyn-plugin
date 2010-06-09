package net.sf.redmine_mylyn.api.model.container;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import net.sf.redmine_mylyn.api.model.Project;

@XmlRootElement(name="projects")
@XmlAccessorType(XmlAccessType.NONE)
public class Projects extends AbstractPropertyContainer<Project> {

	protected List<Project> projects;
	
	@Override
	@XmlElement(name="project")
	protected List<Project> getModifiableList() {
		if(projects==null) {
			projects = new ArrayList<Project>();
		}
		return projects;
	}

}
