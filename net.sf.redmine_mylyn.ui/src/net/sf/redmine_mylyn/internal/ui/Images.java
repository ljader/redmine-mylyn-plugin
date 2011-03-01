package net.sf.redmine_mylyn.internal.ui;

import net.sf.redmine_mylyn.ui.RedmineUiPlugin;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;

public class Images {

	private static ImageRegistry registry;
	
	public final static String CLEAR = "/icons/etool16/clear.gif";

	public final static String FIND_CLEAR = "/icons/etool16/find-clear.gif";
	
	public final static String FIND_CLEAR_DISABLED = "/icons/etool16/find-clear-disabled.gif";

	public final static String REPLY = "/icons/etool16/reply.gif";

	public final static String PERSON_NARROW = "/icons/etool16/person-narrow.gif";

	public static ImageDescriptor getImageDescriptor(String key) {
		ImageDescriptor imageDescriptor = RedmineUiPlugin.imageDescriptorFromPlugin("org.eclipse.mylyn.commons.ui", key);
		
		if(imageDescriptor==null) {
			imageDescriptor = RedmineUiPlugin.imageDescriptorFromPlugin("org.eclipse.mylyn.tasks.ui", key);
		}
		return imageDescriptor;
	}

	public static Image getImage(String key) {
		ImageDescriptor descriptor = getImageDescriptor(key);
		return descriptor==null ? null : getImage(descriptor);
	}
	
	public static Image getImage(ImageDescriptor descriptor) {
		if (descriptor==null) {
			return null;
		}
		
		Image image = getRegistry().get("" + descriptor.hashCode());
		if(image==null) {
			image = descriptor.createImage();
			getRegistry().put("" + descriptor.hashCode(), image);
		}
		
		return image;
	}
	
	private static ImageRegistry getRegistry() {
		if(registry==null) {
			registry = new ImageRegistry();
		}
		return registry;
	}
}
