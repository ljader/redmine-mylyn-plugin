package net.sf.redmine_mylyn.internal.core;

import net.sf.redmine_mylyn.api.model.Configuration;
import net.sf.redmine_mylyn.api.model.IssuePriority;
import net.sf.redmine_mylyn.api.model.container.IssuePriorities;
import net.sf.redmine_mylyn.core.RedmineAttribute;
import net.sf.redmine_mylyn.core.RedmineUtil;

import org.eclipse.core.runtime.Assert;
import org.eclipse.mylyn.tasks.core.ITask.PriorityLevel;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskMapper;

public class RedmineTaskMapper extends TaskMapper {

	private final Configuration configuration;
	
	public RedmineTaskMapper(TaskData taskData, Configuration configuration) {
		super(taskData);
		Assert.isNotNull(configuration);
		this.configuration = configuration;
	}
	
	@Override
	public PriorityLevel getPriorityLevel() {
		PriorityLevel level =  super.getPriorityLevel();

		//TODO repositoryAttributes (via aspect)
		if (configuration!=null) {
			IssuePriorities priorities = configuration.getIssuePriorities();
			
			TaskAttribute attribute = getTaskData().getRoot().getAttribute(RedmineAttribute.PRIORITY.getTaskKey());
			IssuePriority priority = priorities.getById(RedmineUtil.parseIntegerId(attribute.getValue()));
			
			//some tickets references a non existing priority ?!
			if (priority==null) {
				priority = priorities.getDefault();
				
				if(priority==null && priorities.getAll().size()>0) {
					priority = priorities.getAll().get(0); 
				}
			}
			
			if (priority==null) {
				PriorityLevel.fromLevel(1);
			} else {
				int pos = priority.getPosition();
				level = PriorityLevel.fromLevel(pos>5 ? 1 : 6-pos);
			}
		}

		return level;
	}
}
