package net.sf.redmine_mylyn.internal.core.client;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import net.sf.redmine_mylyn.api.model.Configuration;
import net.sf.redmine_mylyn.core.IRedmineClientManager;
import net.sf.redmine_mylyn.core.RedmineCorePlugin;
import net.sf.redmine_mylyn.core.RedmineStatusException;
import net.sf.redmine_mylyn.core.client.ClientFactory;
import net.sf.redmine_mylyn.core.client.IClient;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.TaskRepositoryLocationFactory;

public class ClientManager implements IRedmineClientManager {

	private final static char[] ILLEGAL_ZIP_ENTRY_CHARS = "/*\\".toCharArray();
	
	private Map<String, IClient> clientByUrl = new HashMap<String, IClient>();

	private Map<String, Configuration> confByUrl = new HashMap<String, Configuration>();
	
	private final TaskRepositoryLocationFactory locationFactory;
	
	private final File cacheFile;
	
	private final File zipedCacheFile;
	
	public ClientManager(TaskRepositoryLocationFactory locationFactory, File cacheFile, File zipedCacheFile) {
		this.locationFactory = locationFactory;
		this.cacheFile = cacheFile;
		this.zipedCacheFile = zipedCacheFile;
		readCache();
	}
	
	@Override
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
		if (zipedCacheFile!=null && zipedCacheFile.exists()) {
			FileInputStream fileInput = null;
			try {
				try {
					fileInput = new FileInputStream(zipedCacheFile);
					
					ZipInputStream zip = new ZipInputStream(fileInput) {
						public void close() throws IOException {};
					};
					
					ZipEntry zipEntry;
					while((zipEntry=zip.getNextEntry())!=null) {
						String name = zipEntry.getName();
						name = name.substring(0, name.length()-4); //.xml
						for (char chr : ILLEGAL_ZIP_ENTRY_CHARS) {
							name = name.replace("0x"+Integer.toHexString(chr), ""+chr);
						}
						
						confByUrl.put(name, Configuration.fromStream(zip));
						zip.closeEntry();
					}
				} finally {
					if(fileInput!=null) {
						fileInput.close();
					}
				}
			} catch (Exception e) {
				IStatus status = new Status(IStatus.ERROR, RedmineCorePlugin.PLUGIN_ID, "The Redmine respository data cache could not be read", e);
				RedmineCorePlugin.getDefault().getLog().log(status);

				confByUrl.clear();
			}
			
		}
		
		if (confByUrl.isEmpty() && cacheFile!=null && cacheFile.exists()) {
			//DEPRECATED
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
		
	}
	
	public void writeCache() {
		if (zipedCacheFile!=null) {
			if(zipedCacheFile.exists()) {
				zipedCacheFile.delete();
			}
			
			ZipOutputStream zip = null;
			try {
				try {
					zip = new ZipOutputStream(new FileOutputStream(zipedCacheFile));
					
					for(Entry<String, Configuration>  entry : confByUrl.entrySet()) {
						String name = entry.getKey();
						for (char chr : ILLEGAL_ZIP_ENTRY_CHARS) {
							name = name.replace(""+chr, "0x"+Integer.toHexString(chr));
						}
						zip.putNextEntry(new ZipEntry(name + ".xml"));
						entry.getValue().write(zip);
						zip.closeEntry();
					}
					
				} finally {
					if(zip!=null) {
						zip.close();
					}
				}
			} catch (Exception e) {
				IStatus status = new Status(IStatus.ERROR, RedmineCorePlugin.PLUGIN_ID, "The Redmine respository data cache could not be written", e);
				RedmineCorePlugin.getDefault().getLog().log(status);
				
				if(zipedCacheFile.exists()) {
					zipedCacheFile.delete();
				}
			}
		}
	}

}
