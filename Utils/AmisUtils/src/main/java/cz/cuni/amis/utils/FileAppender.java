package cz.cuni.amis.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public class FileAppender {
	
	private File file;

	private PrintWriter writer;
	
	public FileAppender(File file) {
		this.file = file;
	}
	
	public synchronized void append(String line) {
		if (writer == null) {
			try {
				writer = new PrintWriter(new FileOutputStream(file, true));
			} catch (Exception e) {
				throw new RuntimeException("Failed to open file for appending at: " + file.getAbsolutePath());
			}
		}
		writer.append(line);
		writer.append(Const.NEW_LINE);
		writer.flush();
	}
	
	public synchronized void appendIfNotExists(String line) {
		if (!file.exists()) append(line);
	}
	
	public synchronized void close() {
		if (writer != null) {
			try {
				writer.flush();
			} catch (Exception e) {				
			}
			try {
				writer.close();
			} catch (Exception e) {				
			}
			writer = null;
		}
	}
	
	public void appendAndClose(String line) {
		append(line);
		close();
	}
	
	public void appendIfNotExistsAndClose(String line) {
		appendIfNotExists(line);
		close();
	}
	
	
	public static void appendToFile(File file, String line) {
		new FileAppender(file).appendAndClose(line);
	}
	
	public static void appendToFileIfNotExists(File file, String line) {
		new FileAppender(file).appendIfNotExistsAndClose(line);
	}

}