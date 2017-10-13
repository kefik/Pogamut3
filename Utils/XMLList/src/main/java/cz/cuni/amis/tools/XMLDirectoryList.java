package cz.cuni.amis.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;


public class XMLDirectoryList {
	
	public static void printHelp() {
		
		System.out.println("");
		System.out.println("=====================");
		System.out.println("XML Directory Listing");
		System.out.println("=====================");
		System.out.println("");
		System.out.println("Simple program for creating list of all files from several directories.");
		System.out.println("");
		System.out.println("Usage:");
		System.out.println("");
		System.out.println("java XMLDirectoryList.jar [<directory>]+ <target_xml_file>");
		System.out.println("");
		System.out.println("Current directory is: " + new File(".").getAbsolutePath());
		System.out.println("");		
	}
	
	public static PrintWriter out = null;
	
	public static String backslashes(String str) {		
		StringBuffer sb = new StringBuffer(str.length()+str.length()/4);
		for (int i = 0; i < str.length(); ++i) {
			if (str.charAt(i) == '\\') {
				sb.append("/");
			} else {
				sb.append(str.charAt(i));
			}
		}		
		return sb.toString();		
	}
	
	public static void openOut(File targetFile) {
		try {
			out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(targetFile),"UTF8"));
		} catch (UnsupportedEncodingException e) {
			System.out.println("");
			System.out.println("ERROR: unsupported encoding UTF-8");
			System.out.println("");
			e.printStackTrace();
			System.exit(1);
		} catch (FileNotFoundException e) {
			System.out.println("");
			System.out.println("ERROR: can't create file " + targetFile.getAbsolutePath());
			System.out.println("");
			e.printStackTrace();
			System.exit(1);
		}
		out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		out.println("<files>");
	}
	
	public static void closeOut() {
		out.println("</files>");		
		out.close();
	}

	public static void main(String[] args) {
		
		if (args.length < 2) {
			printHelp();
			System.exit(1);			
		}
		
		File[] directories = new File[args.length-1];
		
		for (int i = 0; i < args.length-1; ++i) {
			directories[i] = new File(args[i]);
			if (!directories[i].isDirectory()) {
				System.out.println("");
				System.out.println("ERROR: "+args[i]);
				System.out.println("ERROR: is not directory");
				System.out.println("ERROR: base directory is: " + new File(".").getAbsolutePath());
				System.out.println("");
				printHelp();
				System.exit(1);
			}
		}
		
		openOut(new File(args[args.length-1]));
		
		for (File dir : directories) {
			File[] files = dir.listFiles();
			for (File f : files) {
				if (f.isFile()) {
					out.println("  <file name=\"file:///"+backslashes(f.getAbsolutePath())+"\"/>");
				}
			}
		}
		
		closeOut();
		
		System.out.println("DONE!");
		
	}
	
}