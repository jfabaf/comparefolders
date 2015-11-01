package com.jorgefaba.comparefolders;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.io.FileUtils;

public class HashFolder implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4098355192873388829L;
	//private static final int multiplierThreads = 20; 
	private static final int millisSleepMainThread = 4000;
	Hashtable<String,FileHash>	hash = new Hashtable<String,FileHash>();
	Hashtable<String,FileHash>  hashByPath = new Hashtable<String,FileHash>();
	HashSet<String> processedPaths = new HashSet<String>();
	transient ExecutorService executor = Executors.newWorkStealingPool(); // newFixedThreadPool(Runtime.getRuntime().availableProcessors()*multiplierThreads);
	File			folder;
	boolean			progress;
	boolean			debug;
	BigInteger 		sizeFolder;
	BigInteger		sizeProcessed = BigInteger.ZERO;
	int				percentajeDone = 0;
	long			init = 0;
	transient List<FutureTask<FileHash>> taskList = new ArrayList<FutureTask<FileHash>>();

	public HashFolder(String folderstr) throws Exception {
		this(folderstr,null,false,false);
	}
	
	public HashFolder(String folderstr,HashFolder hf) throws Exception {
		this(folderstr,hf,false,false);
	}
	
	public HashFolder(String folderstr, HashFolder hf, boolean progress, boolean debug) throws Exception {
		super();
		folder = new File(folderstr);
		if (!folder.isDirectory())
			throw new Exception("Folder " + folderstr + " is not a folder");
		this.progress = progress;
		this.debug = debug;
		if (progress || debug) {
			System.out.printf("Processing folder '%s':\n",folder.getAbsolutePath());
			sizeFolder = FileUtils.sizeOfDirectoryAsBigInteger(folder);
			init = System.currentTimeMillis();
		}
		generateHash(folder, hf);
		checkFutures();
	}
	
	private void checkFutures() throws InterruptedException {
		while (!taskList.isEmpty()) {
			for (Iterator<FutureTask<FileHash>> iterator = taskList.iterator(); iterator.hasNext(); ) {
				FutureTask<FileHash> future = iterator.next();
				if (future.isDone()) {
					try {
						FileHash fh = future.get();
						if (debug)
							System.out.println("[DEBUG] Getted MD5 of future of file " + fh.getPath() + "is " + fh.getHashFile());
						insertIntoHash(fh);
						if (debug)
							System.out.println("[DEBUG] Inserted hash of file " + fh.getPath());
					}
					catch (Exception e) {
						System.out.println("[ERROR] Unable to calculate hash of file:" + e.getMessage());
					}
					iterator.remove();
				}
			}
			Thread.sleep(millisSleepMainThread);
		}
		executor.shutdown();
	}
	
	private void close(Closeable c) throws IOException {
	     if (c == null) return; 
	     c.close();
	}
	
	private String generateBar(int percentage) {
		String bar = "";
		for (int i = 1 ; i <= 10 ; i++) {
			if (i <= percentage/10)
				bar = bar+"#";
			else
				bar = bar + " ";
		}
		return bar;
			
	}
	
	private String generateET(long milis, BigInteger size) {
		long estimatedTime = sizeFolder.multiply(BigInteger.valueOf(milis)).divide(size).longValue() - (System.currentTimeMillis() - init);
		long hours, minutes, seconds;
		hours = TimeUnit.MILLISECONDS.toHours(estimatedTime);
		minutes = TimeUnit.MILLISECONDS.toMinutes(estimatedTime) -TimeUnit.HOURS.toMinutes(hours);
		seconds = TimeUnit.MILLISECONDS.toSeconds(estimatedTime) - TimeUnit.MINUTES.toSeconds(minutes) - TimeUnit.HOURS.toSeconds(hours);
		return String.format("%02d:%02d:%02d ET",hours,minutes,seconds);
	}
	
	private void generateHash(File file, HashFolder hf) throws IOException {
		//int				percentajeDoneAux;
		if (file.isFile()) {
			
			String md5 = null;
			if (hf != null)
				md5 = hf.getMd5FromHash(file);
			if (md5 == null)
			{
				FutureTask<FileHash> future = this.getFuture(file);
				taskList.add(future);
				executor.execute(future);
				if (debug)
					System.out.println("[DEBUG] Start computing md5 of file " + file.getCanonicalPath());
			} else {
				if (progress || debug) {
					System.out.printf("[INFO] Skipping file found in hastable: '%s':\n",file.getCanonicalPath());
				}
				insertIntoHash(new FileHash(file.getCanonicalPath(),md5));
			}
			
		} else if (!processedPaths.contains(file.getCanonicalPath())){
			processedPaths.add(file.getCanonicalPath());
			File files[] = file.listFiles();
			for(File fileIt: files ) {
				generateHash(fileIt, hf);
		}
		}
	}
	
	private void insertIntoHash(FileHash fh) {
		hash.put(fh.getHashFile(), fh);
		hashByPath.put(fh.getPath(), fh);
		if (progress) {
			File file = new File(fh.getPath());
			sizeProcessed = sizeProcessed.add(FileUtils.sizeOfAsBigInteger(file));
			percentajeDone =  sizeProcessed.multiply(BigInteger.valueOf(100)).divide(sizeFolder).intValue();
			System.out.printf("[%s] %d%% done (%s)%s",generateBar(percentajeDone), percentajeDone,generateET(System.currentTimeMillis() - init,sizeProcessed),percentajeDone >= 100 ? "\n" : "\r");
			if (percentajeDone >= 100)
				progress = false;
		}
	}
	
	private FutureTask<FileHash> getFuture(File file) {
		FutureTask<FileHash> future =
				   new FutureTask<FileHash>(new Callable<FileHash>() {

				   private String getMD5(File file) throws IOException {
						FileInputStream fis = null;
						String md5 = null;
						try {
							fis = new FileInputStream(file);
							md5 =  org.apache.commons.codec.digest.DigestUtils.md5Hex(fis);
						}
						finally {
							close(fis);
						}
						return md5;
					}					   
					   
					   
					public FileHash call() throws Exception {
						String md5 = null;
						try {
							md5 = this.getMD5(file);
							
							if (debug)
								System.out.println("[DEBUG] End of MD5 calculation of file " + file.getCanonicalPath());
							
							return new FileHash(file.getCanonicalPath(),md5);
						} 
						catch (Exception e) {
							throw new Exception("[ERROR] An exception has ocurred: " + e.getMessage());
						}
							
				   }});
		return future;
	}
	
	public Collection<FileHash> getFiles() {
		ArrayList<FileHash> files = new ArrayList<FileHash>(hash.values()); 
		Collections.sort(files);
		return files;
	}
	
	public boolean contains(FileHash fileHash) {
		return hash.containsKey(fileHash.getHashFile());
	}
	
	public String getProcessedPaths() {
		return processedPaths.toString();
	}
	
	
	public String getFolderPath() {
		return folder.getAbsolutePath();
	}
	
	public String getMd5FromHash(File file) throws IOException {
		FileHash fh = hashByPath.get(file.getCanonicalPath());
		if (fh != null)
			return fh.getHashFile();
		else
			return null;
	}
	
	

}
