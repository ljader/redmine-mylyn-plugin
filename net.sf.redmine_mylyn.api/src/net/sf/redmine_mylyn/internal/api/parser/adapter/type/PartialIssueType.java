package net.sf.redmine_mylyn.internal.api.parser.adapter.type;

import java.lang.reflect.Field;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.sf.redmine_mylyn.api.model.IModel;
import net.sf.redmine_mylyn.api.model.Issue;
import net.sf.redmine_mylyn.internal.api.parser.adapter.DateAdapter;
import net.sf.redmine_mylyn.internal.api.parser.adapter.EmbededPropertyAdapter;

@XmlRootElement(name="issue")
@XmlType(name="issue", namespace="http://redmin-mylyncon.sf.net/api")
@XmlAccessorType(XmlAccessType.FIELD)
public class PartialIssueType implements IModel {

	private static Field idField = getIdField();
	
	private static final long serialVersionUID = 1L;

	private int id;
	
	private String subject;
	
	@XmlJavaTypeAdapter(value=EmbededPropertyAdapter.class)
	private Integer status;

	@XmlJavaTypeAdapter(value=EmbededPropertyAdapter.class)
	private Integer priority;

	@XmlJavaTypeAdapter(value=EmbededPropertyAdapter.class)
	private Integer project;
	
	@XmlElement(name="updated_on")
	@XmlJavaTypeAdapter(value=DateAdapter.class)
	private Date updatedOn;
	
	public int getId() {
		return id;
	}

	public String getSubject() {
		return subject;
	}

	public int getStatusId() {
		return status;
	}

	public int getPriorityId() {
		return priority;
	}
	
	public int getProjectId() {
		return project;
	}
	
	public Date getUpdatedOn() {
		return updatedOn;
	}
	
	public Issue toIssue() {
		try {
			Issue issue = new Issue();
			idField.setInt(issue, id);
			issue.setSubject(subject);
			issue.setProjectId(project);
			issue.setStatusId(status);
			issue.setPriorityId(priority);
			issue.setUpdatedOn(updatedOn);
			return issue;
		} catch (Exception e) {
			//should never happens
		}
		return null;
	}
	
	private static Field getIdField() {
		try {
			Field field = Issue.class.getDeclaredField("id"); //$NON-NLS-1$
			field.setAccessible(true);
			return field;
		} catch (Exception e) {
			//should never happens
		}
		
		return null;
	}
}
