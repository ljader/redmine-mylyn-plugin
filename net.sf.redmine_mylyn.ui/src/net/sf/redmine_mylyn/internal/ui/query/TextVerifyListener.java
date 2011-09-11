package net.sf.redmine_mylyn.internal.ui.query;

import net.sf.redmine_mylyn.api.query.QueryField;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

final class TextVerifyListener implements Listener {
	
	private QueryField.IValidator validator = null;

	public TextVerifyListener(QueryField.IValidator validator) {
		this.validator = validator;
	}

	@Override
	public void handleEvent(Event event) {
		if (validator != null && !event.text.isEmpty() && event.widget!=null) {
			String current = ((Text)event.widget).getText();
			
			if(current.isEmpty()) {
				current = event.text;
			} else {
				StringBuilder sb = new StringBuilder(current.length() + event.text.length());
				sb.append(current.substring(0, event.start));
				sb.append(event.text);
				sb.append(current.substring(event.start));
				current = sb.toString();
			}
			
			event.doit = validator.isValid(current);
		}
	}
}
