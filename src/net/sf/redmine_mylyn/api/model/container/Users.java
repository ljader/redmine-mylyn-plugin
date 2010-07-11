package net.sf.redmine_mylyn.api.model.container;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import net.sf.redmine_mylyn.api.model.User;

@XmlRootElement(name="users")
@XmlAccessorType(XmlAccessType.NONE)
public class Users extends AbstractPropertyContainer<User> {

	private static final long serialVersionUID = 1L;

	protected List<User> users;
	
	@Override
	@XmlElement(name="user")
	protected List<User> getModifiableList() {
		if(users==null) {
			users = new ArrayList<User>();
		}
		return users;
	}

}
