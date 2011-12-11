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
		User user = null;
		
		if (person.getPersonId()!=null && !person.getPersonId().isEmpty()) {
			user = configuration.getUsers().getByLogin(person.getPersonId());
			if (user==null && person.getPersonId().matches(IRedmineConstants.REGEX_INTEGER)) {
				user = configuration.getUsers().getById(RedmineUtil.parseIntegerId(person.getPersonId()));
			}
		}
		
		if(user!=null) {
			setValue(taskAttribute, ""+ user.getId()); //$NON-NLS-1$
		} else {
			setValue(taskAttribute, ""); //$NON-NLS-1$
		}
	}

	@Override
	public IRepositoryPerson getRepositoryPerson(TaskAttribute taskAttribute) {
		User user = null;
		
		if (!taskAttribute.getValue().isEmpty()) {
			if(RedmineUtil.isInteger(taskAttribute.getValue())) {
				user = configuration.getUsers().getById(RedmineUtil.parseIntegerId(taskAttribute.getValue()));
			}
			
			if (user==null) {
				user = configuration.getUsers().getByLogin(taskAttribute.getValue());
			}
			
			if (user!=null) {
				IRepositoryPerson person = getTaskRepository().createPerson(user.getLogin());
				person.setName(user.getName());
				return person;
			}
		}
		
		IRepositoryPerson person = super.getRepositoryPerson(taskAttribute);
		if (person.getName()==null) {
			person.setName(""); //$NON-NLS-1$
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
	
@Override
public void setValue(TaskAttribute attribute, String value) {
	
	if (attribute.getMetaData().getKind()!=null && attribute.getMetaData().getKind().equals(TaskAttribute.KIND_PEOPLE)) {
		if (!value.isEmpty() && !value.matches(IRedmineConstants.REGEX_INTEGER)) {
			User user = configuration.getUsers().getByLogin(value);
			if(user!=null) {
				super.setValue(attribute, ""+user.getId()); //$NON-NLS-N$
				return;
			}
		}
	}
	
	super.setValue(attribute, value);
}
	
}
