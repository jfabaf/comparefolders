package com.jorgefaba.comparefolders;

import java.io.FileInputStream;

import org.apache.commons.cli.*;

public class Main {
	/*TODO: 
	 * Patrones de nombres de archivos
	 * Rsultados ordenados alfabéticamente
	 * parámetros con apache commons cli
	 * grabar el hashtable en un fichero y elegir pasar un path o un fichero con el árbol hash
	 * calcular el tamaño total al principio para ofrecer % de progreso
	 */
	public static final String NAME = "comparefolders";
	private static final Options options = new Options();
	private static CommandLine cml;
	public static void main(String[] args) {
		String folder1;
		String folder2;
		HashFolder hashFolder1;
		HashFolder hashFolder2;
		String[] folders;
		cml = parseArgs(args);
		folders = cml.getArgs();
		
		//get folder1
		
		
		
		checkArgs(args);
		folder1 = args[0];
		folder2 = args[1]; 
		try {
			hashFolder1 = new HashFolder(folder1);
			hashFolder2 = new HashFolder(folder2);
			System.out.println("folder1: " + folder1);
			System.out.println("folder2: " + folder2);
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
		} catch (Exception e) {
			System.out.println("An error is ocurred: " + e.toString());
		}
	}
	
	private static void checkArgs(String[] args) {
		if (args.length != 2) {
			usage();
			System.exit(-1);
		}
	}
	
	private static CommandLine parseArgs(String[] args) {
		
		//save hashtable1
		Option hashtable1save = new Option("hs1","hashtablesave1", true,
	      "save hashtable of folder1 to file");
		hashtable1save.setArgName("file");
		options.addOption(hashtable1save);

		//save hashtable2
		Option hashtable2save = new Option("hs2","hashtablesave2", true,
			      "save hashtable of folder2 to file");
		hashtable2save.setArgName("file");
		options.addOption(hashtable2save);
		
		//read hashtable1
		Option hashtable1 = new Option("ht1","hashtable1", true,
			      "Use hashtable1 file as a folder1");
		hashtable1.setArgName("file");
		options.addOption(hashtable1);

		//read hashtable1
		Option hashtable2 = new Option("ht2","hashtable2", true,
			      "Use hashtable2 file as a folder1");
		hashtable1.setArgName("file");
		options.addOption(hashtable2);
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
	
	private static HashFolder getHashFolder(String filebin, String pathfolder) {
		
		
	}
	

}
