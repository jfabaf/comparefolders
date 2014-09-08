package com.jorgefaba.comparefolders;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import org.apache.commons.cli.*;

public class Main {
	/*TODO: 
	 * Patrones de nombres de archivos
	 * Rsultados ordenados alfabéticamente
	 */
	public static final String NAME = "comparefolders";
	private static final Options options = new Options();
	private static CommandLine cml;
	public static void main(String[] args) {
		HashFolder hashFolder1;
		HashFolder hashFolder2;
		cml = parseArgs(args);
		if (!checkArgs()) {
			usage();
			System.exit(-1);
		}

		try {
			hashFolder1 = getHashFolder(1);
			hashFolder2 = getHashFolder(2);
			System.out.println("folder1: " + hashFolder1.getFolderPath());
			System.out.println("folder2: " + hashFolder2.getFolderPath());
			System.out.println("folder1:processedpaths " + hashFolder1.getProcessedPaths());
			System.out.println("folder2:processedpaths " + hashFolder2.getProcessedPaths());
			for (FileHash file: hashFolder1.getFiles()) {
				if (!hashFolder2.contains(file)) {
					System.out.println("File '" + file.getPath() + "' not found in folder2");
				}
			}
			
			for (FileHash file: hashFolder2.getFiles()) {
				if (!hashFolder1.contains(file)) {
					System.out.println("File '" + file.getPath() + "' not found in folder1");
				}
			}
			saveHashFolders(hashFolder1, hashFolder1);
		} catch (Exception e) {
			System.out.println("An error is ocurred: " + e.toString());
		}
	}
	
	private static void saveHashFolders(HashFolder ht1, HashFolder ht2) {
		if (cml.hasOption("hs1")) {
			saveHashFolderToFile(cml.getOptionValue("hs1"), ht1);
		}
		if (cml.hasOption("hs2")) {
			saveHashFolderToFile(cml.getOptionValue("hs2"), ht2);
		}
	}
	
	private static void saveHashFolderToFile(String pathToFile, HashFolder ht) {
		//serialize
	    try (
	      OutputStream file = new FileOutputStream(pathToFile);
	      OutputStream buffer = new BufferedOutputStream(file);
	      ObjectOutput output = new ObjectOutputStream(buffer);
	    ){
	      output.writeObject(ht);
	    }  
	    catch(IOException e){
	    	System.out.println("An error is ocurred while writing Hashfolder (" + pathToFile + ") : " + e.toString());
	    }
	}
	
	private static boolean checkArgs() {
		boolean dev = true;
		
		//folder1
		if (cml.hasOption("f1") && cml.hasOption("ht1")) {
			dev = false;
			System.err.println("Options f1 and ht1 couldn't be together");
		}
		
		if (cml.hasOption("ht1") && cml.hasOption("hs1")) {
			dev = false;
			System.err.println("Options ht1 and hs1 couldn't be together");
		}
		
		if (!cml.hasOption("f1") && !cml.hasOption("ht1")) {
			dev = false;
			System.err.println("You need f1 or ht1 option (one of them)");
		}
				
		//folder2
		if (cml.hasOption("f2") && cml.hasOption("ht2")) {
			dev = false;
			System.err.println("Options f2 and ht2 couldn't be together");
		}
		
		if (cml.hasOption("ht2") && cml.hasOption("hs2")) {
			dev = false;
			System.err.println("Options ht2 and hs2 couldn't be together");
		}
		
		if (!cml.hasOption("f2") && !cml.hasOption("ht2")) {
			dev = false;
			System.err.println("You need f2 or ht2 option (one of them)");
		}

		return dev;
			
	}
	
	private static CommandLine parseArgs(String[] args) {
		
		//folder1
		Option folder1 = new Option("f1","folder1", true,
	      "Path to folder 1");
		folder1.setArgName("folder1");
		options.addOption(folder1);
		
		//read hashtable1
		Option hashtable1 = new Option("ht1","hashtable1", true,
			      "Use hashtable1 file as a folder1");
		hashtable1.setArgName("file");
		options.addOption(hashtable1);

		//save hashtable1
		Option hashtable1save = new Option("hs1","hashtablesave1", true,
	      "save hashtable of folder1 to file");
		hashtable1save.setArgName("file");
		options.addOption(hashtable1save);

		//folder2
		Option folder2 = new Option("f2","folder2", true,
	      "Path to folder 2");
		folder2.setArgName("folder2");
		options.addOption(folder2);	
		
		//read hashtable2
		Option hashtable2 = new Option("ht2","hashtable2", true,
			      "Use hashtable2 file as a folder2");
		hashtable2.setArgName("file");
		options.addOption(hashtable2);

		//save hashtable2
		Option hashtable2save = new Option("hs2","hashtablesave2", true,
			      "save hashtable of folder2 to file");
		hashtable2save.setArgName("file");
		options.addOption(hashtable2save);

		//progress
		Option progress = new Option("sp","showprogress", false,
			      "Show percentaje of progress");
		options.addOption(progress);		

		// create the parser
	    CommandLineParser parser = new PosixParser();
	    CommandLine line = null;
	    try {
	        // parse the command line arguments
	        line = parser.parse( options, args );
	    }
	    catch( ParseException exp ) {
	        // oops, something went wrong
	        System.err.println( "Parsing failed.  Reason: " + exp.getMessage() );
	        
	        System.exit(-1);
	    }
	    return line;

	}
	
	private static void usage() {
		HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(NAME + " ", options, true);
	}
	
	private static HashFolder getHashFolder(int nfolder) {
		HashFolder hf = null;
		String htPath = null;
		try {
			if (cml.hasOption("f" + Integer.toString(nfolder))) {
				if (!cml.hasOption("sp"))
					hf = new HashFolder(cml.getOptionValue("f" + Integer.toString(nfolder)));
				else
					hf = new HashFolder(cml.getOptionValue("f" + Integer.toString(nfolder)),true);
			}

			if (cml.hasOption("ht" + Integer.toString(nfolder))) {
				htPath = cml.getOptionValue("ht" + Integer.toString(nfolder));
				hf = readHashFolderFromFile(htPath);
			}
			
			
		} catch (Exception e) {
			System.err.println("An error is ocurred: " + e.toString());
			System.exit(-1);
		}
		 
		return hf;
		
	}
	
	private static HashFolder readHashFolderFromFile(String pathToFile) {
		HashFolder hf = null;
		//deserialize
	    try(
	      InputStream file = new FileInputStream(pathToFile);
	      InputStream buffer = new BufferedInputStream(file);
	      ObjectInput input = new ObjectInputStream (buffer);
	    ){
	      //deserialize
	      hf = (HashFolder)input.readObject();
	    }
	    catch(ClassNotFoundException e){
	    	System.err.println("An error is ocurred: " + e.toString());
			System.exit(-1);
	    }
	    catch(IOException e){
	    	System.err.println("An error is ocurred: " + e.toString());
			System.exit(-1);
	    }		
		return hf;
	}
	

}
