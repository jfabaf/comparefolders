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
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;

public class HashFolder implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4098355192873388828L;
	Hashtable<String,FileHash>	hash = new Hashtable<String,FileHash>();
	HashSet<String> processedPaths = new HashSet<String>();
	File			folder;
	boolean			progress;
	BigInteger 		sizeFolder;
	BigInteger		sizeProcessed = BigInteger.ZERO;
	int				percentajeDone = 0;
	long			init = 0;

	public HashFolder(String folderstr) throws Exception {
		this(folderstr,false);
	}
	
	public HashFolder(String folderstr, boolean progress) throws Exception {
		super();
		folder = new File(folderstr);
		if (!folder.isDirectory())
			throw new Exception("Folder " + folderstr + " is not a folder");
		this.progress = progress;
		if (progress) {
			System.out.printf("Processing folder '%s':\n",folder.getAbsolutePath());
			sizeFolder = FileUtils.sizeOfDirectoryAsBigInteger(folder);
			init = System.currentTimeMillis();
		}
		generateHash(folder);
		
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
		return String.format("%02d:%02d:%02d ET", 
			    TimeUnit.MILLISECONDS.toHours(estimatedTime),
			    TimeUnit.MILLISECONDS.toMinutes(estimatedTime) - 
			    TimeUnit.MINUTES.toMinutes(TimeUnit.MILLISECONDS.toHours(estimatedTime)),
			    TimeUnit.MILLISECONDS.toSeconds(estimatedTime) - 
			    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(estimatedTime)));
		
	}
	
	private void generateHash(File file) throws IOException {
		//int				percentajeDoneAux;
		if (file.isFile()) {
			String md5 = getMD5(file);
			hash.put(md5, new FileHash(file.getPath(),md5));
			if (progress) {
				sizeProcessed = sizeProcessed.add(FileUtils.sizeOfAsBigInteger(file));
				percentajeDone =  sizeProcessed.multiply(BigInteger.valueOf(100)).divide(sizeFolder).intValue();
				System.out.printf("[%s] %d%% done (%s)%s",generateBar(percentajeDone), percentajeDone,generateET(System.currentTimeMillis() - init,sizeProcessed),percentajeDone >= 100 ? "\n" : "\r");
				if (percentajeDone >= 100)
					progress = false;
			}
		} else if (!processedPaths.contains(file.getCanonicalPath())){
			processedPaths.add(file.getCanonicalPath());
			File files[] = file.listFiles();
			for(File fileIt: files ) {
				generateHash(fileIt);
		}
		}
	}
	
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
	
	

}
