package net.sf.redmine_mylyn.core.extension.internal.timesheetextension;

import java.util.LinkedHashMap;
import java.util.Map;

import net.sf.redmine_mylyn.core.IRedmineExtensionField;

import org.eclipse.mylyn.tasks.core.data.TaskAttribute;

public class EndTimeHourField implements IRedmineExtensionField {

	private final static Map<String, String> options = new LinkedHashMap<String, String>(24); 
	
	@Override
	public String getLabel() {
		return "End time (hour)";
	}

	@Override
	public String getSubmitKey() {
		return "end_hour";
	}

	@Override
	public String getTaskKey() {
		return "end_hour";
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
