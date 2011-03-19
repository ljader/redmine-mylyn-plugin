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

	@Override
	public IRepositoryPerson getRepositoryPerson(TaskAttribute taskAttribute) {
		IRepositoryPerson person =  super.getRepositoryPerson(taskAttribute);
		
		if (configuration!=null || person.getPersonId()!=null) {
			User user = configuration.getUsers().getById(RedmineUtil.parseIntegerId(person.getPersonId()));
			if(user!=null) {
				person.setName(user.getName());
			}
		}
		
		return person;
	}

	@Override
	public boolean getBooleanValue(TaskAttribute attribute) {
		String value = attribute.getValue();
		if (value.equals(IRedmineConstants.BOOLEAN_TRUE_SUBMIT_VALUE)) {
			return true;
		}
		return super.getBooleanValue(attribute);
	}
	
}
