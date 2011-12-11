package net.sf.redmine_mylyn.core.extension.internal.timesheetextension;

import java.util.LinkedHashMap;
import java.util.Map;

import net.sf.redmine_mylyn.core.IRedmineExtensionField;

import org.eclipse.mylyn.tasks.core.data.TaskAttribute;

public class StartTimeHourField implements IRedmineExtensionField {

	private final static Map<String, String> options = new LinkedHashMap<String, String>(24); 
	
	@Override
	public String getLabel() {
		return "Start time (hour)";
	}

	@Override
	public String getSubmitKey() {
		return "start_hour";
	}

	@Override
	public String getTaskKey() {
		return "start_hour";
	}
	
	@Override
	public boolean isRequired() {
		return true;
	}

	@Override
	public String getEditorType() {
		return TaskAttribute.TYPE_SINGLE_SELECT;
	}

	@Override
	public Map<String, String> getOptions() {
		if (options.size()==0) {
			synchronized (options) {
				if (options.size()==0) {
					for(int i=0;i<24;i++) {
						options.put("" +i , "" +i); //$NON-NLS-1$ //$NON-NLS-2$
					}
				}				
			}
		}
		return options;
	}

}
