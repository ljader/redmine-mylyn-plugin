package net.sf.redmine_mylyn.core;

import net.sf.redmine_mylyn.api.model.Configuration;
import net.sf.redmine_mylyn.api.model.User;

import org.eclipse.mylyn.tasks.core.IRepositoryPerson;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;

public class RedmineTaskAttributeMapper extends TaskAttributeMapper {

	private Configuration configuration;
	
	public RedmineTaskAttributeMapper(TaskRepository taskRepository, Configuration configuration) {
		super(taskRepository);
		this.configuration = configuration;
	}
	
	@Override
	public void setRepositoryPerson(TaskAttribute taskAttribute, IRepositoryPerson person) {
		if(person.getName()==null || person.getName().isEmpty()) {
			User user = configuration.getUsers().getById(RedmineUtil.parseIntegerId(person.getPersonId()));
			if(user!=null) {
				person.setName(user.getName());
			}
		}
		
		super.setRepositoryPerson(taskAttribute, person);
	}

}
