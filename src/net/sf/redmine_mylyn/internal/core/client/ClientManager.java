package net.sf.redmine_mylyn.internal.core.client;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.redmine_mylyn.api.model.Configuration;
import net.sf.redmine_mylyn.core.RedmineCorePlugin;
import net.sf.redmine_mylyn.core.RedmineStatusException;
import net.sf.redmine_mylyn.core.client.ClientFactory;
import net.sf.redmine_mylyn.core.client.IClient;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.tasks.core.IRepositoryListener;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.TaskRepositoryLocationFactory;

public class ClientManager implements IRepositoryListener {

	private Map<String, IClient> clientByUrl = new HashMap<String, IClient>();

	private Map<String, Configuration> confByUrl = new HashMap<String, Configuration>();
	
	private final TaskRepositoryLocationFactory locationFactory;
	
	private final File cacheFile;
	
	public ClientManager(TaskRepositoryLocationFactory locationFactory, File cacheFile) {
		this.locationFactory = locationFactory;
		this.cacheFile = cacheFile;
		
		readCache();
	}
	
	public IClient getClient(TaskRepository repository) throws RedmineStatusException {
		
		synchronized(clientByUrl) {
			IClient client = clientByUrl.get(repository.getUrl());
			
			if(client==null) {
				
				Configuration conf = confByUrl.get(repository.getUrl());
				if(conf==null) {
					conf = new Configuration();
					confByUrl.put(repository.getUrl(), conf);
				}
				
				client = ClientFactory.createClient(repository, locationFactory.createWebLocation(repository), conf);
				clientByUrl.put(repository.getUrl(), client);
			}
			
			return client;
		}
	}
	
	@Override
	public void repositoryAdded(TaskRepository arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void repositoryRemoved(TaskRepository repository) {
		synchronized (clientByUrl) {
			clientByUrl.remove(repository.getRepositoryUrl());
			confByUrl.remove(repository.getRepositoryUrl());
		}
	}

	@Override
	public void repositorySettingsChanged(TaskRepository repository) {
		// TODO Auto-generated method stub
//		synchronized (clientByUrl) {
//			IClient client = clientByUrl.get(repository.getRepositoryUrl());
//			if (client!=null) {
//				AbstractWebLocation location = locationFactory.createWebLocation(repository);
//				client.refreshRepositorySettings(repository, location);
//			}
//		}
	}

	@Override
	public void repositoryUrlChanged(TaskRepository repository, String oldUrl) {
		synchronized (clientByUrl) {
			clientByUrl.put(repository.getRepositoryUrl(), clientByUrl.remove(oldUrl));
			confByUrl.put(repository.getRepositoryUrl(), confByUrl .remove(oldUrl));
		}
	}

	private void readCache() {
		if (cacheFile==null) {
			return;
		}
		
		ObjectInputStream in = null;
		try {
			 in = new ObjectInputStream(new FileInputStream(cacheFile));
			 for(int count=in.readInt();count>0;count--) {
				 confByUrl.put(in.readObject().toString(), (Configuration)in.readObject());
			 }
		} catch (Throwable e) {
			StatusHandler.log(new Status(IStatus.WARNING, RedmineCorePlugin.PLUGIN_ID,
					"The Redmine respository data cache could not be read", e));
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					//do nothing
				}
			}
		}
	}
	
	//TODO als xml abspeichern
	public void writeCache() {
		if (cacheFile==null) {
			return;
		}

		ObjectOutputStream out = null;
		try {
			out = new ObjectOutputStream(new FileOutputStream(cacheFile));
			
			out.writeInt(confByUrl.size());
			for(Entry<String, Configuration>  entry : confByUrl.entrySet()) {
				out.writeObject(entry.getKey());
				out.writeObject(entry.getValue());
			}
			
			out.flush();
		} catch (Throwable e) {
			StatusHandler.log(new Status(IStatus.WARNING, RedmineCorePlugin.PLUGIN_ID,
					"The Redmine respository data cache could not be written", e));
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e1) {
					//do nothing
				}
			}
		}
	}

}
