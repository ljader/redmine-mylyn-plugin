package net.sf.redmine_mylyn.internal.ui.query;

import java.util.ArrayList;
import java.util.List;

import net.sf.redmine_mylyn.api.model.container.AbstractPropertyContainer;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;

public class RedmineContentProvider implements IStructuredContentProvider {

	String title;

	public RedmineContentProvider() {
		this(null);
	}

	public RedmineContentProvider(String title) {
		this.title = title;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof AbstractPropertyContainer<?>) {
			inputElement = ((AbstractPropertyContainer<?>)inputElement).getAll();
		}
		
		if (inputElement instanceof List) {
			List tmp = (List)inputElement;
			if (title!=null) {
				tmp = new ArrayList<Object>(tmp.size()+1);
				tmp.add(title);
				tmp.addAll((List)inputElement);
			}
			return tmp.toArray();
		} else if (inputElement instanceof String[]) {
			return (String[])inputElement;
		} else if (title!=null && inputElement.equals(title)) {
			return new String[]{title};
		}
		return null;
	}

	public void dispose() {
	}

	public void inputChanged(final Viewer viewer, Object oldInput, Object newInput) {
		if (oldInput==null || newInput==null || !viewer.getControl().isEnabled()) {
			return;
		}

		if(!viewer.getSelection().isEmpty()) {
			final ISelection selection = viewer.getSelection();
			
			Display.getCurrent().asyncExec(new Runnable() {
				public void run() {
					viewer.setSelection(selection, true);
				}
			});
		}
	}
	
}
