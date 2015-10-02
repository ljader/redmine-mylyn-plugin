package net.sf.redmine_mylyn.core;

import net.sf.redmine_mylyn.api.model.Configuration;
import net.sf.redmine_mylyn.api.model.User;

import org.eclipse.mylyn.tasks.core.IRepositoryPerson;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;

public class RedmineTaskAttributeMapper extends TaskAttributeMapper {

	private final Configuration configuration;

	public RedmineTaskAttributeMapper(final TaskRepository taskRepository, final Configuration configuration) {
		super(taskRepository);
		this.configuration = configuration;
	}

	@Override
	public void setRepositoryPerson(final TaskAttribute taskAttribute, final IRepositoryPerson person) {
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
	public IRepositoryPerson getRepositoryPerson(final TaskAttribute taskAttribute) {
		User user = null;

		if (!taskAttribute.getValue().isEmpty()) {
			if(RedmineUtil.isInteger(taskAttribute.getValue())) {
				user = configuration.getUsers().getById(RedmineUtil.parseIntegerId(taskAttribute.getValue()));
			}

			if (user==null) {
				user = configuration.getUsers().getByLogin(taskAttribute.getValue());
			}

			if (user!=null) {
				final String login = user.getLogin();
				final String id;
				if (login.isEmpty()) {
					id = user.getName();
				} else {
					id = login;
				}
				final IRepositoryPerson person = getTaskRepository().createPerson(id);
				person.setName(user.getName());
				return person;
			}
		}

		final IRepositoryPerson person = super.getRepositoryPerson(taskAttribute);
		if (person.getName()==null) {
			person.setName(""); //$NON-NLS-1$
		}
		return person;
	}

	@Override
	public boolean getBooleanValue(final TaskAttribute attribute) {
		final String value = attribute.getValue();
		if (value.equals(IRedmineConstants.BOOLEAN_TRUE_SUBMIT_VALUE)) {
			return true;
		}
		return super.getBooleanValue(attribute);
	}

	@Override
	public void setValue(final TaskAttribute attribute, final String value) {

		if (attribute.getMetaData().getKind()!=null && attribute.getMetaData().getKind().equals(TaskAttribute.KIND_PEOPLE)) {
			if (!value.isEmpty() && !value.matches(IRedmineConstants.REGEX_INTEGER)) {
				final User user = configuration.getUsers().getByLogin(value);
				if(user!=null) {
					super.setValue(attribute, ""+user.getId());
					return;
				}
			}
		}

		super.setValue(attribute, value);
	}

}
