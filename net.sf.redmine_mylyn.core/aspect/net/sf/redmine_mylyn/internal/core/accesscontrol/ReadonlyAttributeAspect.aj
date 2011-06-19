package net.sf.redmine_mylyn.internal.core.accesscontrol;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import net.sf.redmine_mylyn.api.model.Configuration;
import net.sf.redmine_mylyn.api.model.CustomField;
import net.sf.redmine_mylyn.api.model.Issue;
import net.sf.redmine_mylyn.core.IRedmineExtensionField;
import net.sf.redmine_mylyn.core.RedmineAttribute;
import net.sf.redmine_mylyn.core.RedmineTaskDataHandler;

import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;

public aspect ReadonlyAttributeAspect {
	
	/**
	 * @see redmine issue_controller - UPDATABLE_ATTRS_ON_TRANSITION
	 */
	private EnumSet<RedmineAttribute> updatableNoTransition;
	
	private Map<Issue, Boolean> multipleStatus = new HashMap<Issue, Boolean>();

	public ReadonlyAttributeAspect() {
		updatableNoTransition = EnumSet.noneOf(RedmineAttribute.class);
		updatableNoTransition.add(RedmineAttribute.ASSIGNED_TO);
		updatableNoTransition.add(RedmineAttribute.VERSION);
		updatableNoTransition.add(RedmineAttribute.STATUS);
		updatableNoTransition.add(RedmineAttribute.PROGRESS);
	}
	
	private void checkForReadonly(TaskAttribute taskAttribute, RedmineAttribute redmineAttribute, Issue issue) {
		//new ticket - no change
		if (issue.getId()<1) {
			return;
		}
		
		//read only per default - no change
		if (redmineAttribute.isReadOnly()) {
			return;
		}
		
		//reopen possible?
		if(redmineAttribute==RedmineAttribute.STATUS_CHG && taskAttribute.getOptions().size()>1) {
			return;
		}
		
		//comments allowed - no change
		if (redmineAttribute==RedmineAttribute.COMMENT) {
			return;
		}

		//closed Ticket - set all attributes to readonly
		if (issue.isClosed()) {
			taskAttribute.getMetaData().setReadOnly(true);
			return;
		}
		
		//edit global allow - no change
		if (issue.isEditAllowed()) {
			return;
		}
		
		//attribute in updatableNoTransition and changing of status possible - no change
		if (multipleStatus.get(issue).booleanValue() && updatableNoTransition.contains(redmineAttribute)) {
			return;
		}
		
		//set all other to read only)
		taskAttribute.getMetaData().setReadOnly(true);
	}

	/* Default Attributes */
	pointcut createDefaultAttributes(Issue issue, Configuration configuration) : 
		execution(private static void RedmineTaskDataHandler.createDefaultAttributes(TaskRepository, TaskData, Issue, Configuration))
		&& args(TaskRepository, TaskData, issue, configuration);
	
	pointcut createDefaultAttribute(RedmineAttribute redmineAttribute, Issue issue) :
		call(private static TaskAttribute RedmineTaskDataHandler.createAttribute(..))
		&& withincode(private static void RedmineTaskDataHandler.createDefaultAttributes(..))
		&& args(TaskData, redmineAttribute, ..)
		&& cflow(createDefaultAttributes(issue, Configuration));
	
	before(Issue issue, Configuration configuration) : createDefaultAttributes(issue, configuration) {
		synchronized (issue) {
			multipleStatus.put(issue, (issue.getAvailableStatusId()!=null && issue.getAvailableStatusId().length>1));
		}
	};
	
	after(RedmineAttribute redmineAttribute, Issue issue) returning(TaskAttribute attr) : createDefaultAttribute(redmineAttribute, issue) {
		checkForReadonly(attr, redmineAttribute, issue);
	};

	/* Extension Attributes */
	pointcut createExtensionAttribute(TaskData taskData) :
		call(private static TaskAttribute RedmineTaskDataHandler.createAttribute(TaskData, IRedmineExtensionField, String))
		&& withincode(private static void RedmineTaskDataHandler.createDefaultAttributes(..))
		&& args(taskData, IRedmineExtensionField, String);

	after(TaskData taskData) returning(TaskAttribute attr) : createExtensionAttribute(taskData) {
		TaskAttribute referenceAttribute = taskData.getRoot().getMappedAttribute(RedmineAttribute.CATEGORY.getTaskKey());
		attr.getMetaData().setReadOnly(referenceAttribute.getMetaData().isReadOnly());
	}

	/* Custom Attributes */
	pointcut createCustomAttribute(TaskData taskData) :
		call(private static TaskAttribute RedmineTaskDataHandler.createAttribute(TaskData, CustomField, String))
		&& withincode(private static void RedmineTaskDataHandler.createCustomAttributes(..))
		&& args(taskData, CustomField, String);
	
	after(TaskData taskData) returning(TaskAttribute attr) : createCustomAttribute(taskData) {
		TaskAttribute referenceAttribute = taskData.getRoot().getMappedAttribute(RedmineAttribute.CATEGORY.getTaskKey());
		attr.getMetaData().setReadOnly(referenceAttribute.getMetaData().isReadOnly());
	}

}
