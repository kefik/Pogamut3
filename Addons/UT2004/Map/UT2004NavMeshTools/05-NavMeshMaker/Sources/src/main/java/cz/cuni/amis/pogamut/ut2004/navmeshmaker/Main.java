package cz.cuni.amis.pogamut.ut2004.navmeshmaker;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Switch;

import cz.cuni.amis.utils.simple_logging.SimpleLogging;

public class Main {
	
	// INITIALIZED EARLY IN MAIN()
	private static Logger log;
	
	private static JSAP jsap;

	private static boolean headerOutput = false;

	private static JSAPResult config;

	private static String ut2004HomeDir;

	private static boolean convertAll;

	private static String mapList;

	private static String[] maps;

	private static String outputDir;

	private static File ut2004HomeDirFile;

	private static File ut2004MapsDirFile;

	private static File outputDirFile;

	private static boolean rewrite;

	private static boolean continueJob;

	private static final char ARG_UT2004_HOME_DIR_SHORT = 'u';
	
	private static final String ARG_UT2004_HOME_DIR_LONG = "ut2004-home-dir";
	
	private static final char ARG_ALL_SHORT = 'a';
	
	private static final String ARG_ALL_LONG = "all";
	
	private static final char ARG_MAPS_SHORT = 'm';
	
	private static final String ARG_MAPS_LONG = "maps";
	
	private static final char ARG_OUTPUT_SHORT = 'o';
	
	private static final String ARG_OUTPUT_LONG = "output-dir";
	
	private static final char ARG_REWRITE_SHORT = 'w';
	
	private static final String ARG_REWRITE_LONG = "rewrite";
	
	private static final char ARG_CONTINUE_SHORT = 'c';
	
	private static final String ARG_CONTINUE_LONG = "continue";
	
	private static void fail(String errorMessage) {
		fail(errorMessage, null);
	}

	private static void fail(String errorMessage, Throwable e) {
		header();
		log.severe(errorMessage);
		if (e != null) {
			System.out.println();
			e.printStackTrace();			
		}		
		System.out.println();
		System.out.println("Usage: java -jar ut2004-navmesh-maker....jar ");
		System.out.println("                " + jsap.getUsage());
        System.out.println("");
        System.out.println(jsap.getHelp());
        System.out.println("");
        throw new RuntimeException("FAILURE: " + errorMessage);
	}

	private static void header() {
		if (headerOutput) return;
		System.out.println("");
		System.out.println("===========================");
		System.out.println("Pogamut UT2004 NavMeshMaker");
		System.out.println("===========================");
		System.out.println("");
		headerOutput = true;
	}
	
	private static void initJSAP() throws JSAPException {
		jsap = new JSAP();
		    	
    	FlaggedOption opt1 = new FlaggedOption(ARG_UT2004_HOME_DIR_LONG)
        	.setStringParser(JSAP.STRING_PARSER)
        	.setRequired(true) 
        	.setShortFlag(ARG_UT2004_HOME_DIR_SHORT)
        	.setLongFlag(ARG_UT2004_HOME_DIR_LONG);    
        opt1.setHelp("UT2004 home directory containing maps (within Maps folder).");
        
        jsap.registerParameter(opt1);
        
        FlaggedOption opt2 = new FlaggedOption(ARG_MAPS_LONG)
	    	.setStringParser(JSAP.STRING_PARSER)
	    	.setRequired(false) 
	    	.setShortFlag(ARG_MAPS_SHORT)
	    	.setLongFlag(ARG_MAPS_LONG);    
	    opt2.setHelp("Semicolon separated list of maps for which navmeshes should be generated, e.g.: DM-Flux2;DM-1on1-Trite");
	    
	    jsap.registerParameter(opt2);
	    
	    Switch opt3 = new Switch(ARG_ALL_LONG)
			.setShortFlag(ARG_ALL_SHORT)
			.setLongFlag(ARG_ALL_LONG);    
		opt3.setHelp("Create navmeshes for all maps (*.ut2) from Maps directory.");
	
	    jsap.registerParameter(opt3);
	    
	    FlaggedOption opt4 = new FlaggedOption(ARG_OUTPUT_LONG)
	    	.setStringParser(JSAP.STRING_PARSER)
	    	.setRequired(false) 
	    	.setShortFlag(ARG_OUTPUT_SHORT)
	    	.setLongFlag(ARG_OUTPUT_LONG)
	    	.setDefault("./output");    
	    opt4.setHelp("Target directory where to output navmeshes and other files.");
	    
	    jsap.registerParameter(opt4);
	    
	    Switch opt5 = new Switch(ARG_REWRITE_LONG)
			.setShortFlag(ARG_REWRITE_SHORT)
			.setLongFlag(ARG_REWRITE_LONG);    
	    opt5.setHelp("Rewrite any existing files when outputting navmesh(es).");
	    
	    jsap.registerParameter(opt5);
	    
	    Switch opt6 = new Switch(ARG_CONTINUE_LONG)
			.setShortFlag(ARG_CONTINUE_SHORT)
			.setLongFlag(ARG_CONTINUE_LONG);    
	    opt6.setHelp("Do not rewrite any existing files, take them as inputs (e.g. continue the job / create missing files) if possible. ");
    
    	jsap.registerParameter(opt6);
   	}

	private static void readConfig(String[] args) {
		log.info("Parsing command arguments.");
		
		try {
	    	config = jsap.parse(args);
	    } catch (Exception e) {
	    	fail(e.getMessage());
	    	System.out.println();
	    	e.printStackTrace();
	    	throw new RuntimeException("FAILURE!");
	    }
		
		if (!config.success()) {
			String error = "Invalid arguments specified.";
			Iterator errorIter = config.getErrorMessageIterator();
			if (!errorIter.hasNext()) {
				error += "\n-- No details given.";
			} else {
				while (errorIter.hasNext()) {
					error += "\n-- " + errorIter.next();
				}
			}
			fail(error);
    	}
		
		ut2004HomeDir = config.getString(ARG_UT2004_HOME_DIR_LONG);
		convertAll = config.getBoolean(ARG_ALL_LONG);
		mapList = config.getString(ARG_MAPS_LONG);
		if (mapList != null && mapList.length() > 0) {
			maps = mapList.split(";");
		}
		
		outputDir = config.getString(ARG_OUTPUT_LONG);
		while (outputDir.startsWith("./")) outputDir = outputDir.substring(2);
		while (outputDir.startsWith(".\\")) outputDir = outputDir.substring(2);
		
	}
	
	private static void sanityChecks() {
		log.info("Sanity checks...");
		
	    ut2004HomeDirFile = new File(ut2004HomeDir);
	    if (!ut2004HomeDirFile.exists() || !ut2004HomeDirFile.isDirectory()) {
	    	fail("UT2004 directory was not found at '" + ut2004HomeDirFile.getAbsolutePath() + "', path resolved from configuration read as '" + ut2004HomeDir + "'.");
	    }
	    log.info("-- UT2004 directory found at '" + ut2004HomeDirFile.getAbsolutePath() + "'");
	    
	    ut2004MapsDirFile = new File(ut2004HomeDirFile, "Maps");
	    if (!ut2004MapsDirFile.exists() || !ut2004MapsDirFile.isDirectory()) {
	    	fail("UT2004/Maps directory was not found at '" + ut2004MapsDirFile.getAbsolutePath() + "', invalid UT2004 installation.");
	    }
	    log.info("-- UT2004/Maps directory found at '" + ut2004MapsDirFile.getAbsolutePath() + "'");
	    
	    outputDirFile = new File(outputDir);
	    if (!outputDirFile.exists()) {
	    	log.info("-- result directory does not exist at '" + outputDirFile.getAbsolutePath() + "' creating...");
	    	if (!outputDirFile.mkdirs()) {
	    		fail("Failed to create directory '" + outputDirFile.getAbsolutePath() + "'!");
	    	}
	    } else {
	    	if (!outputDirFile.isDirectory()) {
	    		fail("Result directory at '" + outputDirFile.getAbsolutePath() + "' is not a directory!");
	    	}
	    }
	    
	    log.info("-- result directory resolved to '" + outputDirFile.getAbsolutePath() + "'");
	    
	    rewrite = config.getBoolean(ARG_REWRITE_LONG);
	    continueJob = config.getBoolean(ARG_CONTINUE_LONG);
	    if (rewrite) {
	    	log.info("-- going to rewrite any existing files");
	    	if (continueJob) {
	    		continueJob = false;
	    		log.warning("-- ignoring '-c' as we are going to rewrite everything!");	    		
	    	}	    	
	    }
	    if (continueJob) {
	    	log.info("-- going to continue the job (if possible, existing files are going to be reused)");
	    }
	    
	    log.info("Sanity checks OK!");
	}
	
	private static void transformOne(String mapName) {
		UT2004NavMeshMaker maker = new UT2004NavMeshMaker("[" + mapName + "]", ut2004HomeDirFile, outputDirFile, mapName, rewrite, continueJob);
		maker.createMesh();
	}
	
	private static void transformAll() {
		
		List<String> mapList = new ArrayList<String>();
		
		for (File file : ut2004MapsDirFile.listFiles()) {
			if (file.isFile() && !file.getAbsolutePath().endsWith(".ut2")) continue;
			String name = file.getAbsolutePath();
			name = name.substring(name.lastIndexOf(System.getProperty("file.separator"))+1);
			name = name.substring(0, name.lastIndexOf("."));
			mapList.add(name);
		}
		
		maps = mapList.toArray(new String[0]);
		
		transformMaps(maps);		
	}
	
	private static void transformMaps(String[] maps) {
		for (String map : maps) {
			transformOne(map);
		}
	}
	
	public static void main(String[] args) throws JSAPException {
//      FOR TESTING		
//		args = new String[] {
//			"-u",
//			"D:\\Games\\UT2004-Devel",
//			"-m",
//			"DM-TrainingDay",
//			"-c"			
//		};
		
		SimpleLogging.initLogging(true);
		
		log = Logger.getAnonymousLogger();
		
		initJSAP();
	    
	    header();
	    
	    readConfig(args);
	    
	    sanityChecks();
	    
	    if (convertAll) {
	    	transformAll();
	    } else {
	    	transformMaps(maps);
	    }
	    	
	}

	
}
