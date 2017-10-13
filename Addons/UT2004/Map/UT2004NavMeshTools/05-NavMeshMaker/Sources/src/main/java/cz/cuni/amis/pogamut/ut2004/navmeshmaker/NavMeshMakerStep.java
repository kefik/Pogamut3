package cz.cuni.amis.pogamut.ut2004.navmeshmaker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

public abstract class NavMeshMakerStep {

	protected static Logger log = Logger.getAnonymousLogger();
	
	protected UT2004NavMeshMaker owner;
	
	protected File[] outputFiles;
	protected File[] targetFiles;
	
	public NavMeshMakerStep(UT2004NavMeshMaker owner) {
		this.owner = owner;
	}
	
	protected void fail(String errorMessage, Exception e) {
		log.severe(errorMessage);
		if (e instanceof RuntimeException) throw (RuntimeException)e;
		throw new RuntimeException("Error", e);
	}
	
	protected void fail(String errorMessage) {
		fail(owner.getName() + " " + errorMessage, new RuntimeException(errorMessage));
	}
	
	protected void info(String msg) {
		log.info(owner.getName() + " " + msg);
	}
	
	protected void warning(String msg) {
		log.warning(owner.getName() + " " + msg);
	}
	
	protected void severe(String msg) {
		log.severe(owner.getName() + " " + msg);
	}
	
	public boolean anyOutputExist() {
		for (File outputFile : outputFiles) {
			if (outputFile.exists()) {
				return true;
			}
		}
		
		return false;
	}
	
	public boolean allOutputExist() {
		for (File outputFile : outputFiles) {
			if (!outputFile.exists()) {
				return false;
			}
		}
		
		return true;
	}
	
	public void cleanAllExistingOutputs() {
		for (File outputFile : outputFiles) {
			if (outputFile.exists()) {
				outputFile.delete();
			}
		}
	}
	
	public boolean shouldSkip() {
		if (!owner.shouldContinue()) return false;
		
		if (!allOutputExist()) return false;
		
		return true;
	}
	
	public boolean moveOutput() {
		File outputFile = null;
		File targetFile = null;
		try {
			for (int i = 0; i < outputFiles.length; ++i) {
				outputFile = outputFiles[i];
				targetFile = targetFiles[i];
				
				if (!outputFile.exists()) continue;
				String outputFileAbsolute = removeThisDir(outputFile.getAbsolutePath());
				String targetFileAbsolute = removeThisDir(targetFile.getAbsolutePath());
				if (outputFileAbsolute.equals(targetFileAbsolute)) continue;
				
				FileUtils.moveFile(outputFile, targetFile);
			}
			return true;
		} catch (Exception e) {
			fail("Failed to move " + outputFile.getAbsolutePath() + " to " + targetFile.getAbsolutePath(), e);
			return false;
		}
	}
	
	private String removeThisDir(String absolutePath) { 
		StringBuffer result = new StringBuffer(absolutePath.length());
		for (int i = 0; i < absolutePath.length(); ++i) {
			if (i < absolutePath.length()-1) {
				if (absolutePath.charAt(i) == '.' && (absolutePath.charAt(i+1) == '/' || absolutePath.charAt(i+1) == '\\')) {
					++i;
					continue;
				} else
				if (absolutePath.charAt(i) == '/' && absolutePath.charAt(i+1) == '/') {
					continue;
				} else 
				if (absolutePath.charAt(i) == '\\' && absolutePath.charAt(i+1) == '\\') {
					continue;
				} else
				{
					result.append(absolutePath.charAt(i));
				}
			} else {
				result.append(absolutePath.charAt(i));
			}
		}
		return result.toString();
	}
	
	
	
}
