package cz.cuni.amis.utils.rewrite;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import cz.cuni.amis.utils.rewrite.rewriter.Const;
import cz.cuni.amis.utils.rewrite.rewriter.ISubstitution;
import cz.cuni.amis.utils.rewrite.rewriter.Rewriter;
import cz.cuni.amis.utils.simple_logging.SimpleLogging;

public class RewriteFiles {
	
	private RewriteFilesConfig config;

	private Logger log;
	
	private boolean failure = false;
	
	public RewriteFiles(RewriteFilesConfig config) {
		if (config == null) throw new IllegalArgumentException("'config' can't be null!");
		this.config = config;		
	}
	
	public RewriteFiles(File xmlFile) {
		if (xmlFile == null) throw new IllegalArgumentException("'xmlFile' can't be null!");
		this.config = RewriteFilesConfig.loadXML(xmlFile);
	}

	public Logger getLog() {
		return log;
	}

	public void setLog(Logger log) {
		this.log = log;
	}
	
	
	/**
	 * Whether all files were rewritten ({@link RewriteFiles#rewrite()}) according to the configuration == all ok, no exceptions.
	 */
	public synchronized boolean isOk() {
		return !failure;
	}
	public synchronized boolean rewrite() {

		failure = false;
		
		logInfo("=================================================");
		logInfo("Configuration: " + Const.NEW_LINE + config.toString());
		logInfo("=================================================");
		logInfo("Initializing...");
		config.initialize();
		logInfo("Iterating directories...");
		
		if (config.getDirs() == null || config.getDirs().size() == 0) {
			logWarning("NO DIRECTORIES DEFINED VIA <include> TAGS! ARBOTING!");
			return false;
		}
		
		int i = 1;
		int ok = 0;
		int ko = 0;
		int totalFiles = 0;
		
		for (IncludeDirForSubstitutions origDir : config.getDirs()) {
			i = 1;
			IncludeDirForSubstitutions includeDir = new IncludeDirForSubstitutions(origDir, config.getGlobals());
			logInfo("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
			if (includeDir.getDir() == null) {
				logWarning("CONFIGURATION CONTAINS <includeDir> TAG WITHOUT dir ATTRIBUTE! OMITTING!");
				logWarning("IncludeDir config: ");
				logWarning(includeDir.toString("IncludeDir", "  "));
				continue;
			}
			logInfo("Directory: " + includeDir.getDir().getAbsolutePath());
			logInfo("Config:" + Const.NEW_LINE + includeDir.toString("Dir+Globals", "  "));
			logInfo("Initializing...");
			includeDir.initialize();
			Rewriter rewriter = null;
			if (includeDir.getSubstitutions() != null && includeDir.getSubstitutions().size() > 0) {
				logInfo("Initializing substitutions...");
				rewriter = new Rewriter((List<? extends ISubstitution>)includeDir.getSubstitutions());
				rewriter.setLog(log);
			} else {
				logInfo("No substitutions defined.");
			}
			if (includeDir.getLineEndings() != null) {
				logInfo("Using " + includeDir.getLineEndings() + ".");
			}
			logInfo("Initializing directory walker...");
			MyDirectoryWalker walker = includeDir.getDirectoryWalker();
			logInfo("Collecting files...");
			List<File> files = walker.walk();
			if (files == null || files.size() == 0) {
				logWarning("NO FILES FOUND FOR THIS INCLUDE!");
			} else {
				logInfo("Matched " + files.size() + " files.");
				logInfo("Rewriting!");
				for (File file : files) {				
					logInfo("-------------------------------------------------");
					logInfo("Rewriting (" + i + " / " + files.size() + "): " + file.getAbsolutePath());
					boolean exception = false;
					try {
						boolean rewritten = false;
						if (rewriter != null) {
							if (rewriter.rewriteFile(file, config.getEncoding())) {
								rewritten = true;
								logInfo("Rewritten.");
							} else {
								logInfo("No changes.");
							}
						}						
						if (includeDir.getLineEndings() != null) {
							if (!includeDir.getLineEndings().isFixOnlyIfRewritten() || rewritten) {
								if (includeDir.getLineEndings().fix(file)) {
									rewritten = true;
									logInfo("Line endings fixed.");
								} else {
									logInfo("Line endings are consistent.");
								}
							} else {
								if (includeDir.getLineEndings().isFixOnlyIfRewritten()) {
									logInfo("File was not modified, line endings are not checked.");
								}
							}
						}
						if (rewritten) {
							++ok;
						}
					} catch (IOException e) {
						++ko;
						logSevere("FAILED TO REWRITE: " + file.getAbsolutePath());
						e.printStackTrace();
						exception = true;
					}
					++i;
				}			
			}
			totalFiles += files.size();
		}
		

		logInfo("-------------------------------------------------");
		logInfo("STATISTICS:");
		log.info("Total files:  " + totalFiles);
		log.info("Rewritten:    " + ok);
		if (ko != 0) {
			log.info("Failed:       " + ko);
			failure = true;
		} else {
			if (ok == 0) {
				log.info("NO FILES WERE CHANGED, OK!");
			} else {
				log.info("FILES REWRITTEN, OK!");
			}
			failure = false;
		}	
		logInfo("FINISHED!");
		return !failure;
	}
	
	protected void logInfo(String msg) {
		if (log != null && log.isLoggable(Level.INFO)) log.info(msg); 
	}

	protected void logWarning(String msg) {
		if (log != null && log.isLoggable(Level.WARNING)) log.warning(msg); 
	}
	
	protected void logSevere(String msg) {
		if (log != null && log.isLoggable(Level.SEVERE)) log.severe(msg); 
	}
	
	protected static void example(Logger log) {
		log.info(              "<RewriteFilesConfig>"
			+ Const.NEW_LINE + "    <!-- GLOBAL DEFINITIONS THAT APPLIED TO ALL <include> TAGS -->"
			+ Const.NEW_LINE + "    <globals>"
			+ Const.NEW_LINE + "        <includeFile>pom.xml</includeFile> <!-- WILDCARDS ALLOWED! -->"
			+ Const.NEW_LINE + ""
			+ Const.NEW_LINE + "        <!--" 
			+ Const.NEW_LINE + "    	    <excludeFile>...</excludeFile>"
			+ Const.NEW_LINE + "    	    MAY BE USED TO GLOBALLY EXCLUDE SOME FILES" 
			+ Const.NEW_LINE + "         -->"
			+ Const.NEW_LINE + "        <excludeDir>.svn</excludeDir>      <!-- WILDCARDS NOT ALLOWED, RELATIVE PATH THAT IS BLOCKED == **/path/defined/in/exclude/dir -->"
			+ Const.NEW_LINE + "        <substitutions>"
			+ Const.NEW_LINE + "    	    <substitution>"
			+ Const.NEW_LINE + "    		    <match>3.2.0</match>"
			+ Const.NEW_LINE + "    		    <replace>3.2.0-SNAPSHOT</replace>"
			+ Const.NEW_LINE + "    		    <caseSensitive>false</caseSensitive> <!-- DEFAULT IS TRUE -->"
			+ Const.NEW_LINE + "    		    </substitution>"
			+ Const.NEW_LINE + "        </substitutions>"
			+ Const.NEW_LINE + "        <fixLineEndings windowsStyle=\"true\" />"			
			+ Const.NEW_LINE + "    </globals>"
			+ Const.NEW_LINE + ""
			+ Const.NEW_LINE + "    <!-- DEFINES DIRESTORIES TO WALK THROUGH, MAY ADD ADDITIONAL INCLUDEs/EXCLUDEs/SUBSTITUTIONS -->"
			+ Const.NEW_LINE + "    <include dir=\"./data1\" />"
			+ Const.NEW_LINE + "        <!-- JUST INHERITS ALL FROM <globals> -->"
			+ Const.NEW_LINE + ""
			+ Const.NEW_LINE + "    <include dir=\"./data2\">"
			+ Const.NEW_LINE + "        <!-- INCLUDES ALL *.java BUT SomeFile.java FILES -->"
			+ Const.NEW_LINE + "        <includeFile>*.java</includeFile>"
			+ Const.NEW_LINE + "        <excludeFile>SomeFile.java</excludeFile>"
			+ Const.NEW_LINE + "    </include>"
			+ Const.NEW_LINE + ""    			
			+ Const.NEW_LINE + "</RewriteFilesConfig>"
		);
	}
	
	public static void main(String[] args) {
		//String definition = "RewriteFiles.xml";
		String definition = "config.xml";
		if (args.length > 0) {
			definition = args[0];
			if (definition == null) definition = "RewriteFiles.xml";
		}
		
		SimpleLogging.initLogging();
		Logger log = Logger.getAnonymousLogger();
		log.info("---[[ REWRITE FILES ]]---");
		log.info("Loading definition from xml file: " + definition + " --> " + new File(definition).getAbsoluteFile());
		File file = new File(definition);
		if (!file.exists() || !file.isFile()) {
			log.severe("FAILED! Definition file not found at: " + file.getAbsolutePath());
			log.severe("Usage: java -jar RewriteFiles.jar [path-to-definition-xml-file]");
			example(log);
			log.info("---[[ END ]]---");
			System.exit(1);
			return;
		}
		RewriteFiles rewrite;
		try {
			rewrite = new RewriteFiles(file);
		} catch (Exception e) {
			e.printStackTrace();
			log.severe("Usage: java -jar RewriteFiles.jar [path-to-definition-xml-file]");
			example(log);
			log.info("---[[ END ]]---");
			return;
		}
		rewrite.setLog(log);
		log.info("Definition file loaded.");
		boolean result = rewrite.rewrite();
		
		
		if (result) {
			log.info("System.exit(0)");
		}
		else {
			log.warning("Failure! System.exit(1)!");
			System.exit(1);
		}
		
		log.info("---[[ END ]]---");
		
		if (result) {
			System.exit(0);
		} else {
			System.exit(1);
		}
		
	}

	
	
}
