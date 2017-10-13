package cz.cuni.amis.pogamut.release.archetype;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;

import cz.cuni.amis.utils.Const;
import cz.cuni.amis.utils.ExceptionToString;
import cz.cuni.amis.utils.NullCheck;
import cz.cuni.amis.utils.process.ProcessExecution;
import cz.cuni.amis.utils.process.ProcessExecutionConfig;
import cz.cuni.amis.utils.simple_logging.SimpleLogging;

public class ArchetypeRefresh {
	
	private ArchetypeRefreshConfig config;

	private Logger log;
	
	public ArchetypeRefresh() {
		this.config = new ArchetypeRefreshConfig();
	}
	
	public ArchetypeRefresh(ArchetypeRefreshConfig config) {
		this.config = config;
		NullCheck.check(this.config, "config");
	}

	public ArchetypeRefreshConfig getConfig() {
		return config;
	}

	public void setConfig(ArchetypeRefreshConfig config) {
		this.config = config;
	}
	
	public Logger getLog() {
		return log;
	}

	public void setLog(Logger log) {
		this.log = log;
	}

	public boolean validate() {
		if (config == null) {
			logWarning("Config is null! Use setConfig() first!");
			return false;
		}
		if (config.getSourceDir() == null) {
			logWarning("config.getSourceDir() is null!");
			return false;
		}
		if (config.getTargetDir() == null) {
			logWarning("config.getTargetDir() is null!");
			return false;
		}
		File sourceDir = new File(config.getSourceDir());
		File targetDir = new File(config.getTargetDir());
		
		if (!sourceDir.exists() || !sourceDir.isDirectory()) {
			logWarning("config.getSourceDir() does not point to a directory! SourceDir: " + sourceDir.getAbsolutePath());
			return false;
		} else {
			File pom = new File(sourceDir, "pom.xml");
			if (!pom.exists() || !pom.isFile()) {
				logWarning("There is no pom.xml file found in config.getSourceDir() --> " + sourceDir.getAbsolutePath());
				return false;
			}
		}
		
		if (!targetDir.exists() || !targetDir.isDirectory()) {
			logWarning("config.getTargetDir() does not point to a directory! TargetDir: " + targetDir.getAbsolutePath());
			return false;
		} else {
			File pom = new File(targetDir, "pom.xml");
			if (!pom.exists() || !pom.isFile()) {
				logWarning("There is no pom.xml file found in config.getTargetDir() --> " + targetDir.getAbsolutePath());
				return false;
			}
		}
		
		return true;
	}

    //
	// IMPLEMENTATION
	// 

	/**
	 * Deletes all except '.svn.' from 'dir'.
	 */
	protected boolean deleteAllIn(File dir) {
		if (!dir.exists() || !dir.isDirectory()) {
			// IGNORING
			return true;
		}
		try {
			for (File f : dir.listFiles()) {
				if (f.isDirectory() && f.getAbsolutePath().endsWith(".svn")) continue;
				if (f.isDirectory()) {
					logInfo("    +-- Deleting directory: " + f.getAbsolutePath());
					//svnDelete(f);
					FileUtils.deleteDirectory(f);
				} else {
					logInfo("    +-- Deleting file: " + f.getAbsolutePath());
					//svnDelete(f);
					if (!FileUtils.deleteQuietly(f)) {
						throw new IOException("Failed to delete file: " + f.getAbsolutePath());
					}
				}
			}
		} catch (Exception e) {
			logSevere(ExceptionToString.process(e));
			return false;
		}
		return true;
	}
	
	/**
	 * Deletes all .svn dirs from directory recursively.
	 * @param dir
	 */
	protected boolean wipeSVNs(File dir) {
		logInfo("      +--  Wiping .svn dirs from: " + dir.getAbsolutePath());
		if (dir.exists() && dir.isDirectory()) {
			for (File f : dir.listFiles()) {
				if (f.isDirectory() && f.getAbsolutePath().endsWith(".svn")) {
					try {
						logInfo("        +--  Deleting: " + f.getAbsolutePath());
						FileUtils.deleteDirectory(f);
					} catch (IOException e) {
						logSevere(ExceptionToString.process(e));
						return false;
					}
					continue;
				}
				if (f.isDirectory()) {
					if (!wipeSVNs(f)) return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * Walks recursively over 'targetDir' deleting ALL dirs that are not in 'sourceDir' + wiping out all files + sustaining .svn dirs.
	 * @param sourceDir
	 * @param targetDir
	 * @return
	 */
	protected boolean deleteMissing(File sourceDir, File targetDir) {
		if (!sourceDir.exists() || !sourceDir.isDirectory()) {
			// IGNORING
			return true;
		}
		if (!targetDir.exists()) {
			// IGNORING
			return true;
		}
		
		for (File f : targetDir.listFiles()) {
			if (f.isDirectory() && f.getAbsolutePath().endsWith(".svn")) continue;
			File sourceFile = new File(sourceDir, f.getName());
			if (f.isDirectory()) {
				if (!sourceFile.exists()) {
					// DELETE THE DIRECTORY
					logInfo("    +--  Directory in TARGET does not exist in SOURCE anymore, deleting: " + f.getAbsolutePath());
					try {
					  // svnDelete(f);
					  FileUtils.deleteDirectory(f);
					} catch (Exception e) {
						logSevere("    +--  FAILED TO DELETE DIRECTORY!");
						logSevere(ExceptionToString.process(e));
						return false;
					}					
				} else
				if (sourceFile.isFile()) {
					// IT IS NOT A DIRECTORY ANYMORE, IT IS A FILE
					logInfo("    +--  Directory in TARGET is now file in SOURCE, deleting: " + f.getAbsolutePath());
					try {
						// svnDelete(f);
						if (!FileUtils.deleteQuietly(f)) {
							throw new IOException("Failed to delete directory.");
						}
					} catch (Exception e) {
						logSevere("    +--  FAILED TO DELETE DIRECTORY!");
						logSevere(ExceptionToString.process(e));
						return false;
					}
				} else {
					// BOTH ARE DIRECTORIES, RECURSION
					deleteMissing(f, sourceFile);
				}
			} else 
			if (f.isFile()) {
				if (!sourceFile.exists()) {
					// FILE DOES NOT EXIST IN SOURCE ANYMORE
					logInfo("    +--  File in TARGET does not exist in SOURCE anymore, deleting: " + f.getAbsolutePath());
					try {
					  // svnDelete(f);
					  if (!FileUtils.deleteQuietly(f)) {
						  throw new IOException("Failed to delete file.");
					  }
					} catch (Exception e) {
						logSevere("    +--  FAILED TO DELETE FILE!");
						logSevere(ExceptionToString.process(e));
						return false;
					}	
				} else
				if (sourceFile.isDirectory()) {
					// IT IS NOT A DIRECTORY ANYMORE, IT IS A FILE
					logInfo("    +--  File in TARGET is now directory in SOURCE, deleting: " + f.getAbsolutePath());
					try {
						// svnDelete(f);
						if (!FileUtils.deleteQuietly(f)) {
							throw new IOException("Failed to delete directory.");
						}
					} catch (Exception e) {
						logSevere("    +--  FAILED TO DELETE FILE!");
						logSevere(ExceptionToString.process(e));
						return false;
					}
				} else {
					// BOTH ARE FILES
					logInfo("    +--  File in TARGET is still a file in SOURCE, will be refreshed later: " + f.getAbsolutePath());					
				}
			} else {
				logSevere(ExceptionToString.process(new IOException("File is neither DIRECTORY nor FILE: " + f.getAbsolutePath())));
				return false;
			}			
		}
		return true;
	}
	
	protected boolean copyExisting(File sourceDir, File targetDir) {
		try {			
			if (targetDir.exists() && targetDir.isDirectory()) {
				for (File f : sourceDir.listFiles()) {					
					if (f.isDirectory() && f.getAbsolutePath().endsWith(".svn")) continue;
					File targetFile = new File(targetDir, f.getName());
					if (f.isDirectory()) {
						if (!copyExisting(f, targetFile)) return false;						
					} else {
						if (targetFile.isDirectory()) {
							throw new IOException("TARGET file is still directory, previous delete has failed! Offensive file: " + targetFile.getAbsolutePath());
						}
						if (targetFile.exists()) {
							logInfo("    +--  Refreshing file: " + f.getAbsolutePath() + " --> " + targetFile.getAbsolutePath());
							FileUtils.copyFile(f, new File(targetDir, f.getName()));
						} else {
							logInfo("    +--  Copying new file: " + f.getAbsolutePath() + " --> " + targetFile.getAbsolutePath());
							FileUtils.copyFile(f, targetFile);
							// svnAdd(targetFile);
						}
					}
				}
			} else {
				if (targetDir.exists()) {
					throw new IOException("TARGET is not a directory! Could not copy dir (SOURCE) " + sourceDir.getAbsolutePath() + " into (TARGET) " + targetDir.getAbsolutePath());
				}
				logInfo("    +--  Copying new directory: " + sourceDir.getAbsolutePath() + " --> " + targetDir);
				FileUtils.copyDirectory(sourceDir, targetDir);
				wipeSVNs(targetDir);
				// svnAdd(targetDir);
			}
		} catch (IOException e) {
			logSevere(ExceptionToString.process(e));
			return false;
		}
		return true;
	}
	
	protected boolean syncDirs(File sourceDir, File targetDir) {
		logInfo("Syncing archetype dirs: (SOURCE) " + sourceDir.getAbsolutePath() + " --> (TARGET) " + targetDir.getAbsolutePath());
		if (!targetDir.exists()) {
			logInfo("  +-- TARGET dir does not exist, creating");
			if (!targetDir.mkdirs()) {
				logSevere("Failed to create missing directory: " + targetDir.getAbsolutePath());
				return false;
			}
			logInfo("    +-- Done");
			//svnAdd(targetDir);
		}	
		if (!sourceDir.exists() || !sourceDir.isDirectory()) {
			logInfo("  +-- SOURCE does not exist, cleaning files inside TARGET");
			if (!deleteAllIn(targetDir)) return false;
			logInfo("    +-- Done");
			return true;
		}
		
		logInfo("  +--  Wiping out files / non-existing dirs from TARGET");
		if (!deleteMissing(sourceDir, targetDir)) return false;
		logInfo("    +--  Done");
		
		logInfo("  +--  Copying fresh archetype files/dirs from SOURCE to TARGET");
		if (!copyExisting(sourceDir, targetDir)) return false;
		logInfo("    +--  Done");
		
		logInfo("Directories synced.");
		return true;
	}
	
	/**
	 * Main work is done here.
	 */
	public boolean refresh() {
		logInfo("------------------------");
		logWarning("Starting archetype-refresh with config: " + Const.NEW_LINE + config);
		if (!validate()) {
			logWarning("Validation error.");
			return false;
		}
		logInfo("Starting archetype creation...");
		
		ProcessExecutionConfig processConfig = new ProcessExecutionConfig();
		processConfig.setExecutionDir(config.getSourceDir());
		if (System.getProperty("os.name") != null && System.getProperty("os.name").toLowerCase().contains("linux")) {
			processConfig.setPathToProgram("${MAVEN_HOME}/bin/mvn.sh");
		} else {
			processConfig.setPathToProgram("${MAVEN_HOME}/bin/mvn.bat");
		}
		processConfig.setArgs(new ArrayList<String>(2));
		processConfig.getArgs().add("clean");
		processConfig.getArgs().add("archetype:create-from-project");
		processConfig.setId("Create archetype");
		processConfig.setRedirectStdErr(true);
		processConfig.setRedirectStdOut(true);
		processConfig.setTimeout(900000); // 15 mins
		ProcessExecution process = new ProcessExecution(processConfig, log);
		
		process.start();
		process.getRunning().waitFor(false);
		if (process.isFailed(true)) {
			logWarning("COULD NOT GENERATE FRESH ARCHETYPE!");
			return false;
		}
		
		logInfo("Archetype generated.");
		
		File sourceDir = new File(config.getSourceDir());
		File targetDir = new File(config.getTargetDir());
		
		logInfo("Deleting/recreating old src directories in target dir...");
		
		File sourceFile;
		File targetFile;
		
		String sourcePrefix = "target/generated-sources/archetype/src/main/resources/archetype-resources/";
		String targetPrefix = "src/main/resources/archetype-resources/";
		
		logInfo("Syncing files from SOURCE archetype to TARGET...");
		
		{
			sourceFile = new File(sourceDir, sourcePrefix + "src/main/java");
			targetFile = new File(targetDir, targetPrefix + "src/main/java");
			if (!syncDirs(sourceFile, targetFile)) {
				return false;
			}
		}
		
		{
			sourceFile = new File(sourceDir, sourcePrefix + "src/main/resources");
			targetFile = new File(targetDir, targetPrefix + "src/main/resources");
			if (!syncDirs(sourceFile, targetFile)) {
				return false;
			}
		}
		
		{
			sourceFile = new File(sourceDir, sourcePrefix + "src/test/java");
			targetFile = new File(targetDir, targetPrefix + "src/test/java");
			if (!syncDirs(sourceFile, targetFile)) {
				return false;
			}
		}
		
		{
			sourceFile = new File(sourceDir, sourcePrefix + "src/test/resources");
			targetFile = new File(targetDir, targetPrefix + "src/test/resources");
			if (!syncDirs(sourceFile, targetFile)) {
				return false;
			}
		}
	
		logInfo("Archetype refreshed OK!");
	
		return true;
	}
	
	protected void logInfo(String msg) {
		if (log != null && log.isLoggable(Level.INFO)) {
			log.info(msg);
		}
	}
	
	protected void logWarning(String msg) {
		if (log != null && log.isLoggable(Level.WARNING)) {
			log.warning(msg);
		}
	}
	
	protected void logSevere(String msg) {
		if (log != null && log.isLoggable(Level.SEVERE)) {
			log.severe(msg);
		}
	}
	
//	public static void main(String[] args) {
//		SimpleLogging.initLogging();
//		//SimpleLogging.addConsoleLogging();
//		ArchetypeRefreshConfig config = new ArchetypeRefreshConfig();		
//		config.setSourceDir("d:\\Workspaces\\Pogamut-Trunk\\Main\\PogamutUT3Examples\\04-UT3-HunterBot\\");
//		config.setTargetDir("d:\\Workspaces\\Pogamut-Trunk\\Archetypes\\UT3\\04-UT3-HunterBot\\");
//		ArchetypeRefresh refresh = new ArchetypeRefresh(config);
//		refresh.setLog(Logger.getAnonymousLogger());
//		refresh.refresh();
//	}
	
}
