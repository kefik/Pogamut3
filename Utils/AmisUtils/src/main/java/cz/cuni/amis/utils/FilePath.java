package cz.cuni.amis.utils;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import cz.cuni.amis.utils.exception.PogamutException;

public class FilePath {
	/**
	 * Seperator of the classpath entries.
	 */
	public static final String CLASSPATH_SEPARATOR = ";";
	
	private static final String ALLOWED_RELATIVE_PATH_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789._-/";
	
	private static final String ALLOWED_FILE_NAME_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789._-";
	
	private static final Set<String> ALLOWED_RELATIVE_PATH_CHARS_SET = new HashSet<String>();
	
	static {
		for (int i = 0; i < ALLOWED_RELATIVE_PATH_CHARS.length(); ++i) {
			ALLOWED_RELATIVE_PATH_CHARS_SET.add(ALLOWED_RELATIVE_PATH_CHARS.substring(i, i+1));
		}
	}
	
	public static String getValidFileName(String fileName) {
		if (fileName == null) return null;
		StringBuffer sb = new StringBuffer(fileName.length());
		for (int i = 0; i < fileName.length(); ++i) {
			if (ALLOWED_FILE_NAME_CHARS.contains(String.valueOf(fileName.charAt(i)))) sb.append(fileName.charAt(i));
			else sb.append("_");
		}
		return sb.toString();
	}
	
	/**
	 * Checks the 'path' for the presence of ".." + can't start with "/" + enforces use of the {@link FilePath#ALLOWED_RELATIVE_PATH_CHARS}.
	 * @param path
	 */
	public static void checkRelativePath(String path) {
		if (path.contains("..")) throw new PogamutException("Path '" + path + "' contains '..' at position " + path.indexOf("..") + " which is forbidden!", FilePath.class);
		if (path.startsWith("/")) throw new PogamutException("Path '" + path + "' can't start with '/'.", FilePath.class);
		for (int i = 0; i < path.length(); ++i) {
			if (!ALLOWED_RELATIVE_PATH_CHARS_SET.contains(path.substring(i, i+1))) {
				throw new PogamutException("Path '" + path + "' contains forbidden character at index " + i + " (0-based). Allowed chars are limited to: " + ALLOWED_RELATIVE_PATH_CHARS, FilePath.class);
			}
		}
	}
	
	/**
	 * Treats 'file' as something that points to the file and creates all parent dirs.
	 * @param file
	 */
	public static void makeDirsToFile(File file) {
		String parent = file.getParent();
		if (parent != null) {
			new File(parent).mkdirs();
		}
	}
	
	/**
	 * Concats all paths sequentially together replacing all backslashes with slashes and watches out for ending of the path1 and beginning of path2 solving
	 * "//", "/./", etc. Does not resolves "..".
	 * @param paths
	 * @return
	 */
	public static String concatPaths(String... paths) {
		if (paths == null) return null;
		if (paths.length == 0) return null;
		String result = paths[0];
		for (int i = 1; i < paths.length; ++i) {
			result = concatPaths(result, paths[i]);
		}
		return result;
	}	
		
	
	/**
	 * Concats path1 and path2 replacing all backslashes with slashes and watches out for ending of the path1 and beginning of path2 solving
	 * "//", "/./", etc. Does not resolves "..".
	 * 
	 * @param path1
	 * @param path2
	 * @return
	 */
	public static String concatPaths(String path1, String path2) {
		path1.replace("\\", "/");
		path2.replace("\\", "/");
		if (path1 == null) {
			return path2;
		} else
		if (path1.equals("./")) {
			if (path2 == null) {
				return path1;
			} else
			if (path2.startsWith("/")){
				throw new PogamutException("Can't contact path '" + path1 + "' with '" + path2 + "' as path2 starts with '/'.", FilePath.class);
			} else {
				return path2;
			}
		} else
		if (path1.endsWith("/")) {
			if (path2 == null) {
				return path1;
			} else
			if (path2.startsWith("./")) {
				return path1 + path2.substring(2);
			} else
			if (path2.startsWith("/")){
				throw new PogamutException("Can't contact path '" + path1 + "' with '" + path2 + "' as path2 starts with '/'.", FilePath.class);
			} else {
				return path1 + path2;
			}
		} else {
			if (path2 == null) {
				return path1;
			} else
			if (path2.startsWith("./")) {
				return path1 + "/" + path2.substring(2);
			} else 
			if (path2.startsWith("/")){
				throw new PogamutException("Can't contact path '" + path1 + "' with '" + path2 + "' as path2 starts with '/'.", FilePath.class);
			} else {
				return path1 + "/" + path2;
			}
		}
	}
	
	public static String makeUniform(String file) {
		if (file == null) return null;
		for (int i = 0; i < file.length(); ++i) {
			if (file.charAt(i) == '\\') file = file.substring(0, i) + "/" + file.substring(i+1);
		}
		file = file.replaceAll("//", "/");
		while (file.length() > 0 && file.endsWith("/")) file = file.substring(0, file.length()-1);
		while (file.startsWith("./")) file = file.substring(2);
		if (file.length() == 0) return null;
		
		return file;
	}

}
