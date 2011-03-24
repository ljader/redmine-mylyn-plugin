package net.sf.redmine_mylyn.internal.core;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sf.redmine_mylyn.api.model.Property;

public class ProgressValues extends Property {

	private static List<ProgressValues> availableValues;
	
	private ProgressValues(int value) {
		id = value;
		name = value + " %"; //$NON-NLS-1$
	}

	private static final long serialVersionUID = 1L;

	public static List<ProgressValues> availableValues() {
		if (availableValues==null) {
			availableValues = new ArrayList<ProgressValues>(10);
			for(int i=0; i<=10; i++) {
				availableValues.add(new ProgressValues(i*10));
			}
			availableValues = Collections.unmodifiableList(availableValues);
		}
		return availableValues;
	}
	
	
}
