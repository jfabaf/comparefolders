package com.jorgefaba.comparefolders;

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
	int				percentajeDone;

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
		}
		generateHash(folder);
		
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
	
	private void generateHash(File file) throws IOException {
		int				percentajeDoneAux;
		if (file.isFile()) {
			String md5 = getMD5(file);
			hash.put(md5, new FileHash(file.getPath(),md5));
			if (progress) {
				sizeProcessed = sizeProcessed.add(FileUtils.sizeOfAsBigInteger(file));
				percentajeDoneAux =  sizeProcessed.multiply(BigInteger.valueOf(100)).divide(sizeFolder).intValue();
				if (percentajeDoneAux > percentajeDone) {
					percentajeDone = percentajeDoneAux;
					System.out.printf("[%s] %d%% done%s",generateBar(percentajeDone), percentajeDone,percentajeDone >= 100 ? "\n" : "\r");
					if (percentajeDone >= 100)
						progress = false;
				}
				
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
		FileInputStream fis = new FileInputStream(file);
		return org.apache.commons.codec.digest.DigestUtils.md5Hex(fis);
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
