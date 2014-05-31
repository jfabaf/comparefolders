package com.jorgefaba.comparefolders;

import java.io.Serializable;

public class FileHash implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3936313859574080298L;
	String hash;
	String path;
	public FileHash(String path, String hash) {
		super();
		this.hash = hash;
		this.path = path;
	}
	public String getHashFile() {
		return hash;
	}
	public String getPath() {
		return path;
	}
	
	
	
}
