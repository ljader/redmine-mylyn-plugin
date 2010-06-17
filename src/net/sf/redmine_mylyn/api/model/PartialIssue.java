package net.sf.redmine_mylyn.api.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.sf.redmine_mylyn.internal.api.parser.PartialIssueParser;
import net.sf.redmine_mylyn.internal.api.parser.adapter.EmbededPropertyAdapter;

@XmlType(name="issue", namespace=PartialIssueParser.FAKE_NS)
@XmlAccessorType(XmlAccessType.FIELD)
public class PartialIssue implements IModel {

	private static final long serialVersionUID = 1L;

	private int id;
	
	private String subject;
	
	@XmlJavaTypeAdapter(value=EmbededPropertyAdapter.class)
	private Integer status;

	@XmlJavaTypeAdapter(value=EmbededPropertyAdapter.class)
	private Integer priority;

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
	
}
