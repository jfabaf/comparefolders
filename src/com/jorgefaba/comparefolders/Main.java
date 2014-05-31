package com.jorgefaba.comparefolders;

public class Main {
	/*TODO: 
	 * Patrones de nombres de archivos
	 * Rsultados ordenados alfabéticamente
	 * parámetros con apache commons cli
	 * grabar el hashtable en un fichero y elegir pasar un path o un fichero con el árbol hash
	 * calcular el tamaño total al principio para ofrecer % de progreso
	 */

	public static void main(String[] args) {
		String folder1;
		String folder2;
		checkArgs(args);
		folder1 = args[0];
		folder2 = args[1]; 
		try {
			HashFolder hashFolder1 = new HashFolder(folder1);
			HashFolder hashFolder2 = new HashFolder(folder2);
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
	
	private static void usage() {
		System.out.println("Usage: comparefolder folder1 folder2");
	}
	

}
