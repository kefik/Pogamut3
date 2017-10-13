package cz.cuni.amis.netbeans.publicpkgs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

import cz.cuni.amis.utils.Const;
import cz.cuni.amis.utils.ExceptionToString;
import cz.cuni.amis.utils.FilePath;
import cz.cuni.amis.utils.simple_logging.SimpleLogging;

public class NetBeansPublicPackages {
	
	public static final Pattern PUBLIC_PACKAGES_BEGIN = 
		Pattern.compile("(\\s*)\\<\\s*publicPackages\\s*\\>");
	
	public static final Pattern PUBLIC_PACKAGES_END = 
		Pattern.compile("\\<\\s*\\/\\s*publicPackages\\s*\\>");
	
	public static final Pattern PUBLIC_PACKAGE = 
		Pattern.compile("\\<\\s*publicPackage\\s*\\>([^<]*)\\<\\s*\\/\\s*publicPackage\\s*\\>");
	
	private NetBeansPublicPackagesConfig config;

	private Logger log;
	
	private boolean failure = false;
	
	public NetBeansPublicPackages(NetBeansPublicPackagesConfig config) {
		if (config == null) throw new IllegalArgumentException("'config' can't be null!");
		this.config = config;		
	}
	
	public NetBeansPublicPackages(File xmlFile) {
		if (xmlFile == null) throw new IllegalArgumentException("'xmlFile' can't be null!");
		this.config = NetBeansPublicPackagesConfig.loadXML(xmlFile);
	}

	public Logger getLog() {
		return log;
	}

	public void setLog(Logger log) {
		this.log = log;
	}
	
	String pomBegin;
	String pomEnd;
	String packagesPrefix = "";
	List<String> pomPublicPackages = new ArrayList<String>();
	
	private boolean parsePublicPackages(File targetPom) {
		logInfo("Reading pom.xml from: " + targetPom.getAbsolutePath());
		
		String pom;
		try {
			pom = FileUtils.readFileToString(targetPom);
		} catch (IOException e) {
			logSevere(ExceptionToString.process("Could not read " + targetPom.getAbsolutePath(), e));
			return false;
		}
		
		Matcher m;
		
		m = PUBLIC_PACKAGES_BEGIN.matcher(pom);
		
		if (!m.find()) {
			logSevere("<publicPackages> not found using pattern: " + PUBLIC_PACKAGES_BEGIN.pattern());
			return false;
		}
		
		packagesPrefix = m.group(1);
		if (packagesPrefix.startsWith(Const.NEW_LINE)) packagesPrefix = packagesPrefix.substring(2);
		
		pomBegin = pom.substring(0, m.start());
		
		String pomRest = pom.substring(m.start());
		
		m = PUBLIC_PACKAGES_END.matcher(pomRest);
		
		if (!m.find()) {
			logSevere("</publicPackages> not found using pattern: " + PUBLIC_PACKAGES_END.pattern());
		}
		
		pomEnd = pom.substring(pomBegin.length() + m.end());
		
		String pomPkgs = pomRest.substring(0, m.end());
		
		logInfo("Parsed <publicPackages> section:" + Const.NEW_LINE + pomPkgs);
		logInfo("Locating respective <publicPackage> decalarations using pattern: " + PUBLIC_PACKAGE.pattern());
		
		m = PUBLIC_PACKAGE.matcher(pomPkgs);
		
		while (m.find()) {
			String pkg = m.group(1).trim();
			if (!pkg.endsWith(".*")) pkg += ".*";
			logInfo("Located: " + pkg);
			boolean match = false;
			for (String packagePrefix : config.getPackagePrefixes()) {
				if (pkg.startsWith(packagePrefix)) {
					logInfo("Matches package prefix (will be replaced): '" + packagePrefix + "'");
					match = true;
					break;
				}
			}
			if (!match) {
				logInfo("Does not match any package prefix, will be included in the result.");
				pomPublicPackages.add(pkg);
			}
		}
		
		return true;
	}
	
	public synchronized boolean rewrite() {

		failure = false;
		
		logInfo("=================================================");
		logInfo("Configuration: " + Const.NEW_LINE + config.toString());
		logInfo("=================================================");
		logInfo("Initializing...");
		logInfo("-------------------------------------------------");
		config.initialize();
		
		if (config.getTargetPom() == null) {
			logSevere("config.getTargetPom() is null!");
			logSevere("FAILURE!");
			return false;
		}
		
		File targetPom = new File(config.getTargetPom());
		if (!targetPom.isFile() || !targetPom.exists()) {
			logSevere("Target pom is not a valid file, path: " + targetPom.getAbsolutePath());
			logSevere("FAILURE!");
			return false;
		}
		
		if (!parsePublicPackages(targetPom)) {
			logSevere("FAILURE!");
			return false;
		}
		
		logInfo("POM PACKAGES: " + pomPublicPackages.size());
		
		List<String> pkgs = new ArrayList<String>();
		
		for (IncludeDir inc : config.getDirs()) {
			logInfo("Crawling:" + Const.NEW_LINE + inc.toString("IncludeDir", " "));
			inc.initialize();
			if (inc.getDir() == null) {
				logSevere("Invalid directory: null");				
				return false;
			}
			if (!inc.getDir().isDirectory() || !inc.getDir().exists()) {
				logSevere("Invalid directory: " + inc.getDir().getAbsolutePath());
				return false;
			}
			List<File> files = inc.getDirectoryWalker().walk(inc.getDir());
			logInfo("Found:");
			String prefix = FilePath.makeUniform(inc.getDir().getAbsolutePath());
			for (File file : files) {
				String path = FilePath.makeUniform(file.getAbsolutePath());
				path = path.substring(prefix.length()).trim();
				if (path.length() == 0) continue;
				logInfo("  Dir: " + path);
				String pkg = path.replaceAll("\\/", ".");
				if (pkg.endsWith(".")) pkg += "*";
				else pkg += ".*";
				pkg = pkg.replaceAll("\\.\\.", ".");
				if (pkg.startsWith(".")) pkg = pkg.substring(1);
				logInfo("  Pkg: " + pkg);
				pkgs.add(pkg);
			}
			
		}

		int allPak = (pkgs.size() + pomPublicPackages.size());
		
		logInfo("FOUND PACKAGES: " + pkgs.size());
		logInfo("POM+FOUND PACKAGES: " + allPak);
		
		pkgs.addAll(pomPublicPackages);
		
		logInfo("Removing duplicities...");
		
		Collections.sort(pkgs);
		for (int i = 1; i < pkgs.size(); ) {
			if (pkgs.get(i).equals(pkgs.get(i-1))) {
				pkgs.remove(i);
			} else {
				++i;
			}
		}
		
		logInfo("TOTAL PACKAGES: " + pkgs.size());
		
		// RESULT
		
		String result = Const.NEW_LINE + packagesPrefix + "<publicPackages>";
		
		for (String newPkg : pkgs) {
			result += Const.NEW_LINE + packagesPrefix + "    <publicPackage>" + newPkg + "</publicPackage>";
		}
		result += Const.NEW_LINE + packagesPrefix + "</publicPackages>";
		
		logInfo("Combined public packages:" + Const.NEW_LINE + result);
		
		String pom = pomBegin + result + pomEnd;
		
		logInfo("Rewriting " + targetPom.getAbsolutePath() + " ...");
		
		try {
			FileUtils.writeStringToFile(targetPom, pom);
		} catch (IOException e) {
			logSevere(ExceptionToString.process("Failed to rewrite " + targetPom.getAbsolutePath(), e));
			return false;
		}
		
		logInfo("FINISHED!");
		
		return true;		
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
		log.info(              "<NetBeansPublicPackages>"
			+ Const.NEW_LINE + ""
			+ Const.NEW_LINE + "    <targetPom>Main/PogamutNetbeansSuiteBase/PogamutNbBase/pom.xml</targetPom>"
			+ Const.NEW_LINE + ""
			+ Const.NEW_LINE + "    <!-- THESE PACKAGES WILL BE REMOVED FROM NetBeansModule pom.xml -->"
			+ Const.NEW_LINE + "    <packagePrefix>cz.cuni.amis.base</packagePrefix>"
			+ Const.NEW_LINE + "    <packagePrefix>cz.cuni.amis.utils</packagePrefix>"
			+ Const.NEW_LINE + ""
			+ Const.NEW_LINE + "    <!-- GLOBAL DEFINITIONS THAT APPLIED TO ALL <include> TAGS -->"
			+ Const.NEW_LINE + "    <excludeDir>.svn</excludeDir>      <!-- WILDCARDS NOT ALLOWED, RELATIVE PATH THAT IS BLOCKED == **/path/defined/in/exclude/dir -->"
			+ Const.NEW_LINE + "    <excludeDir>.cvs</excludeDir>      <!-- WILDCARDS NOT ALLOWED, RELATIVE PATH THAT IS BLOCKED == **/path/defined/in/exclude/dir -->"
			+ Const.NEW_LINE + ""
			+ Const.NEW_LINE + "    <!-- FOLLOWING DIRECTORIES WILL BE INTERPRETED AS JAVA-SOURCES DIRs CONTAINING PACKAGES THAT SHOULD BE ALL BE PUBLIC  -->"
			+ Const.NEW_LINE + "    <!-- DEFINES DIRESTORIES TO WALK THROUGH, MAY ADD ADDITIONAL EXCLUDEs -->"
			+ Const.NEW_LINE + "    <include dir=\"Main/PogamutBase/src/main/java\" />"
			+ Const.NEW_LINE + "    <!-- excludeDir are inherited here -->"
			+ Const.NEW_LINE + ""
			+ Const.NEW_LINE + "    <include dir=\"Utils/AmisUtils/src/main/java\">"
			+ Const.NEW_LINE + "        <!-- INCLUDES ALL *.java BUT SomeFile.java FILES -->"
			+ Const.NEW_LINE + "        <excludeDir>some/dir</excludeDir>"
			+ Const.NEW_LINE + "    </include>"
			+ Const.NEW_LINE + ""    			
			+ Const.NEW_LINE + "</NetBeansPublicPackages>"
		);
	}
	
	public static void main(String[] args) {
		String definition = "NetBeansPublicPackages.xml";
		if (args.length > 0) {
			definition = args[0];
			if (definition == null) definition = "NetBeansPublicPackages.xml";
		}
		
		SimpleLogging.initLogging();
		Logger log = Logger.getAnonymousLogger();
		log.info("---[[ PUBLIC PACKAGES ]]---");
		log.info("Loading definition from xml file: " + definition + " --> " + new File(definition).getAbsoluteFile());
		File file = new File(definition);
		if (!file.exists() || !file.isFile()) {
			log.severe("FAILED! Definition file not found at: " + file.getAbsolutePath());
			log.severe("Usage: java -jar NetBeansPublicPackages.jar [path-to-definition-xml-file]");
			example(log);
			log.info("---[[ END ]]---");
			System.exit(1);
			return;
		}
		NetBeansPublicPackages publicPackages;
		try {
			publicPackages = new NetBeansPublicPackages(file);
		} catch (Exception e) {
			e.printStackTrace();
			log.severe("Usage: java -jar RewriteFiles.jar [path-to-definition-xml-file]");
			example(log);
			log.info("---[[ END ]]---");
			return;
		}
		publicPackages.setLog(log);
		log.info("Definition file loaded.");
		
		boolean result = publicPackages.rewrite();
		
		
		if (result) {
			log.info("System.exit(0)");
		} else {
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
