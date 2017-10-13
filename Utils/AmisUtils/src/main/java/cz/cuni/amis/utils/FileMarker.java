package cz.cuni.amis.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.regex.Pattern;

public class FileMarker {

	public static final String FILE_SEPARATOR = System.getProperty("file.separator");
	
	private String prefix;
	private File directory;

	public FileMarker(String prefix) {
		this(prefix, new File("."));
	}
	
	public FileMarker(String prefix, File directory) {
		this.prefix = StringIdifier.idify(prefix);		
		this.directory = directory;
		if (this.directory.exists()) {
			if (!this.directory.isDirectory()) {
				throw new RuntimeException("'directory' exists but it is not directory: " + directory.getAbsolutePath());
			}
		}
	}
	
	public FileMarker(String prefix, String pathToDirectory) {
		this(prefix, new File(pathToDirectory));
	}
	
	public Pattern getMarkFileNamePattern() {
		return Pattern.compile("^" + prefix + "\\-.*\\.mark$");
	}

	public File getFileMark(String mark) {
		return new File(directory, prefix + "-" + StringIdifier.idify(mark) + ".mark");
	}
	
	public File getFileMark(String mark, int index) {
		return new File(directory, prefix + "-" + StringIdifier.idify(mark) + "." + index + ".mark");
	}
	
	protected boolean isExists(File markFile) {
		return markFile.exists() && markFile.isFile();
	}
	
	protected void touch(File markFile) {
		if (!directory.exists()) {
			directory.mkdirs();
		}
		if (markFile.exists()) {
			if (markFile.isFile()) {
				if (!markFile.delete()) {
					throw new RuntimeException("Failed to touch the mark, could not delete old mark: " + markFile.getAbsolutePath());
				}
			} else {
				throw new RuntimeException("Cannot touch the mark, because it already exists and it is not file: " + markFile.getAbsolutePath());
			}
		}
		
		try {
			new FileOutputStream(markFile).close();
		} catch (Exception e) {
			throw new RuntimeException("Failed to touch the mark: " + markFile.getAbsolutePath(), e);
		}		
	}
	
	public void remove(File markFile) {		
		if (markFile.exists() && markFile.isFile()) {
			if (!markFile.delete()) {
				throw new RuntimeException("Failed to delete the mark: " + markFile.getAbsolutePath());
			}
		}
	}
	
	public boolean isExists(String mark) {
		File markFile = getFileMark(mark);
		return isExists(markFile);
	}
	
	public void touch(String mark) {
		File markFile = getFileMark(mark);
		touch(markFile);
	}
	
	public void remove(String mark) {
		File markFile = getFileMark(mark);
		remove(markFile);
	}
	
	public boolean isExists(String mark, int index) {
		File markFile = getFileMark(mark, index);
		return isExists(markFile);
	}
	
	public void touch(String mark, int index) {
		File markFile = getFileMark(mark, index);
		touch(markFile);
	}
	
	public void remove(String mark, int index) {
		File markFile = getFileMark(mark, index);
		remove(markFile);
	}

	public void removeAllMarks() {
		if (!directory.exists()) return;
		Pattern pattern = getMarkFileNamePattern();
		for (File file : directory.listFiles()) {
			String fileName = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf(FILE_SEPARATOR)+1);
			if (pattern.matcher(fileName).matches()) {
				if (!file.delete()) {
					throw new RuntimeException("Failed to delete mark: " + file.getAbsolutePath());
				}
			}
		}
	}
	
}
