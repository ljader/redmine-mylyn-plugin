package net.sf.redmine_mylyn.internal.ui.query;

import java.util.ArrayList;
import java.util.List;

import net.sf.redmine_mylyn.api.model.container.AbstractPropertyContainer;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

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

	//TODO
	public void inputChanged(final Viewer viewer, Object oldInput, Object newInput) {
//		if (oldInput==null || newInput==null) {
//			return;
//		}
//
//		IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();
//		Object o = selection.getFirstElement();
//		
//		if (o instanceof RedmineTicketAttribute) {
//			if (o instanceof RedmineStoredQuery || o instanceof RedmineProject) {
//				selectLastOrDefault(viewer, o);
//			} else {
//				reselect(viewer, selection);
//			}
//		} else if (o instanceof String) {
//			reselect(viewer, selection);
//		} else if (title!=null) {
//			selectLastOrDefault(viewer, title);
//		}
//		
	}
	
//	private void selectLastOrDefault(final Viewer viewer, final Object item) {
//		Display.getCurrent().asyncExec(new Runnable() {
//			public void run() {
//				viewer.setSelection(new StructuredSelection(item), true);
//			}
//		});
//	}
//
//	private void reselect(final Viewer viewer, final IStructuredSelection selection) {
//		Display.getCurrent().asyncExec(new Runnable() {
//			public void run() {
//				viewer.setSelection(selection, true);
//			}
//		});
//	}
}
