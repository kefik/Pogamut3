package cz.cuni.pogamut.ut2004.levelgeom;

import java.io.File;
import java.util.Iterator;
import java.util.logging.Logger;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;

import cz.cuni.amis.utils.simple_logging.SimpleLogging;

public class Main {
	
	// INITIALIZED EARLY IN MAIN()
	private static Logger log;
	
	private static JSAP jsap;

	private static boolean headerOutput = false;

	private static JSAPResult config;

	private static String jumppadFile;
	
	private static File jumppadFileFile;
	
	private static String envelopeFile;
	
	private static File envelopeFileFile;
	
	private static String sourceFile;
	
	private static File sourceFileFile;
	
	private static String outputDir;

	private static File outputDirFile;
	
	private static String ut2004Dir;
	
	private static File ut2004DirFile;
	
	private static String mapName;

	private static final char ARG_INPUT_FILE_SHORT = 'i';
	
	private static final String ARG_INPUT_FILE_LONG = "input-file";
	
	private static final char ARG_JUMPPAD_FILE_SHORT = 'j';
	
	private static final String ARG_JUMPPAD_FILE_LONG = "jumppad-file";
	
	private static final char ARG_ENVELOPE_FILE_SHORT = 'e';
	
	private static final String ARG_ENVELOPE_FILE_LONG = "envelope-file";
	
	private static final char ARG_OUTPUT_DIR_SHORT = 'o';
	
	private static final String ARG_OUTPUT_DIR_LONG = "output-dir";
	
	private static final char ARG_UT2004_DIR_SHORT = 'u';
	
	private static final String ARG_UT2004_DIR_LONG = "ut2004-dir";
	
	
		
	private static void fail(String errorMessage) {
		fail(errorMessage, null);
	}

	private static void fail(String errorMessage, Throwable e) {
		header();
		log.severe(errorMessage);
		if (e != null) {
			System.out.println("");
			e.printStackTrace();		
		}		
		System.out.println("");
		System.out.println("Used to preprocess data from UShock in order to be usable by Recast. It can run in 'offline' mode without UT2004 present, but it has a catch." 
		                 + " In order to have correct NavMesh you need to take into an account JumpPads that are present in some UT2004 maps, e.g., DM-Flux2 + the map envelope (min/max x,y,z). Either you can" 
				         + " supply jumppad/envelope files manually in the format of x;y;z per line specifying jumppad position, or you can supply path to UT2004 and the UShock2Recast"
				         + " will extract jumppads automatically for you. If you DO NOT SUPPLY neither jumppad file, nor UT2004 directory, the navmesh might not be usable"
				         + " if jumppads are present in the map. Envelope file is optional. Note that we're assuming that input file (without its extension) is the map name."						
		);
		System.out.println("");
		System.out.println("Usage: java -jar UShock2Recast.jar ");
		System.out.println("                " + jsap.getUsage());
        System.out.println("");
        System.out.println(jsap.getHelp());
        System.out.println("");
        throw new RuntimeException("FAILURE: " + errorMessage);
	}

	private static void header() {
		if (headerOutput) return;
		System.out.println("");
		System.out.println("=============");
		System.out.println("UShock2Recast");
		System.out.println("=============");
		System.out.println("");
		headerOutput = true;
	}
	
	private static void initJSAP() throws JSAPException {
		jsap = new JSAP();
		    	
    	FlaggedOption opt1 = new FlaggedOption(ARG_INPUT_FILE_LONG)
        	.setStringParser(JSAP.STRING_PARSER)
        	.setRequired(true) 
        	.setShortFlag(ARG_INPUT_FILE_SHORT)
        	.setLongFlag(ARG_INPUT_FILE_LONG);    
        opt1.setHelp("Source file to read (.xml output from UShock). We're assuming that the file name (without its extension) is the name of the map.");
        
        jsap.registerParameter(opt1);
        
        FlaggedOption opt2 = new FlaggedOption(ARG_OUTPUT_DIR_LONG)
	    	.setStringParser(JSAP.STRING_PARSER)
	    	.setRequired(false) 
	    	.setShortFlag(ARG_OUTPUT_DIR_SHORT)
	    	.setLongFlag(ARG_OUTPUT_DIR_LONG)
	    	.setDefault("./output");    
	    opt2.setHelp("Target dir where to output results (.obj, .scale, .center).");
	    
	    jsap.registerParameter(opt2);
	    
	    FlaggedOption opt3 = new FlaggedOption(ARG_JUMPPAD_FILE_LONG)
	    	.setStringParser(JSAP.STRING_PARSER)
	    	.setRequired(false) 
	    	.setShortFlag(ARG_JUMPPAD_FILE_SHORT)
	    	.setLongFlag(ARG_JUMPPAD_FILE_LONG);    
	    opt3.setHelp("Jumppad file containing jumppad per row in the form x;y;z e.g. 1,25;2,34;3,45 ... if not specified we will use INPUT_FILE.jumppads as default.");
	    
	    jsap.registerParameter(opt3);
	    
	    FlaggedOption opt5 = new FlaggedOption(ARG_ENVELOPE_FILE_LONG)
	    	.setStringParser(JSAP.STRING_PARSER)
	    	.setRequired(false) 
	    	.setShortFlag(ARG_ENVELOPE_FILE_SHORT)
	    	.setLongFlag(ARG_ENVELOPE_FILE_LONG);    
	    opt5.setHelp("Envelope file containing minX;minY;minZ;maxX;maxY;maxZ coordinates ... if not specified we will use INPUT_FILE.envelope as default.");
	    
	    jsap.registerParameter(opt5);
	    
	    FlaggedOption opt4 = new FlaggedOption(ARG_UT2004_DIR_LONG)
	    	.setStringParser(JSAP.STRING_PARSER)
	    	.setRequired(false) 
	    	.setShortFlag(ARG_UT2004_DIR_SHORT)
	    	.setLongFlag(ARG_UT2004_DIR_LONG)
	    	.setDefault("./output");    
	    opt4.setHelp("Path to UT2004 that should be used to extract jumppads for the map.");
	    
	    jsap.registerParameter(opt4);
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
		
		sourceFile = config.getString(ARG_INPUT_FILE_LONG);
		
		jumppadFile = config.getString(ARG_JUMPPAD_FILE_LONG);
		
		envelopeFile = config.getString(ARG_ENVELOPE_FILE_LONG);
		
		ut2004Dir = config.getString(ARG_UT2004_DIR_LONG);
		
		outputDir = config.getString(ARG_OUTPUT_DIR_LONG);
	}
	
	private static void sanityChecks() {
		log.info("Sanity checks...");
		
		sourceFileFile = new File(sourceFile);
		if (!sourceFileFile.exists() || !sourceFileFile.isFile()) {
			fail("-- UShock generated source file does not exist at '" + sourceFileFile.getAbsolutePath() + "' creating...");
		}
		log.info("-- UShock generated source file resolved to: " + sourceFileFile.getAbsolutePath());
		
		
		mapName = sourceFileFile.getName().substring(0, sourceFileFile.getName().lastIndexOf("."));
		log.info("-- map name parsed as: '" + mapName + "' (without quotes)");
			    
	    outputDirFile = new File(outputDir);
	    if (!outputDirFile.exists()) {
	    	log.info("-- output directory does not exist at: " + outputDirFile.getAbsolutePath() + " creating...");
	    	if (!outputDirFile.mkdirs()) {
	    		fail("Failed to create directory '" + outputDirFile.getAbsolutePath() + "'!");
	    	}
	    } else {
	    	if (!outputDirFile.isDirectory()) {
	    		fail("output directory at '" + outputDirFile.getAbsolutePath() + "' is not a directory!");
	    	}
	    }
	    log.info("-- result directory resolved to '" + outputDirFile.getAbsolutePath() + "'");
	    
	    if (jumppadFile == null) {
	    	jumppadFileFile = new File(new File(sourceFileFile.getParent()), mapName + "." + "jumppads");
	    	log.info("-- no jumppad file specified, will try to use: " + jumppadFileFile.getAbsolutePath());
	    } else {
	    	jumppadFileFile = new File(jumppadFile);
	    	log.info("-- jumppad file resolved to: " + jumppadFileFile.getAbsolutePath());
	    }
	    
	    if (jumppadFileFile.exists()) {
    		log.info("-- jumppad file exists, will be used as a source of jumppads for the level, UT2004 won't be used even if specified neither we will fallback to it in case of errors");
    	}
	    
	    if (envelopeFile == null) {
	    	envelopeFileFile = new File(new File(sourceFileFile.getParent()), mapName + "." + "envelope");
	    	log.info("-- no envelope file specified, will try to use: " + envelopeFileFile.getAbsolutePath());
	    } else {
	    	envelopeFileFile = new File(envelopeFile);
	    	log.info("-- envelope file resolved to: " + envelopeFileFile.getAbsolutePath());
	    }
	    
	    if (envelopeFileFile.exists()) {
    		log.info("-- envelope file exists, will be used as a source of envelope for the level, UT2004 won't be used even if specified neither we will fallback to it in case of errors");
    	}
	   
	    if (ut2004Dir == null) {
	    	if (!jumppadFileFile.exists()) {
	    		log.warning("!!! JUMPPAD FILE DOES NOT EXIST and UT2004 HOME NOT SPECIFIED => if the level is containing jumppads, the resulting NavMesh won't be usable.");
	    	}
	    } else {
	    	ut2004DirFile = new File(ut2004Dir);
	    	
	    	log.info("-- UT2004 home directory resolved to: " + ut2004DirFile.getAbsolutePath());
	    	
	    	if (!ut2004DirFile.exists()) {
	    		fail("UT2004 directory does not exist at: " + ut2004DirFile.getAbsolutePath());
	    	}
	    	
	    	File ut2004MapsDirFile = new File(ut2004DirFile, "Maps");
	    	if (!ut2004MapsDirFile.exists()) {
	    		fail("UT2004 directory does not contain Maps subfolder at: " + ut2004MapsDirFile.getAbsolutePath());
	    	}
	    	
	    	File ut2004MapFile = new File(ut2004MapsDirFile, mapName + ".ut2");
	    	if (!ut2004MapFile.exists()) {
	    		log.warning("!!! UT2004 map file does not found at: " + ut2004MapFile);
	    	}
	    }
    	
	    log.info("Sanity checks OK!");
	}
	
	public static void transform() {
		try {
			UShock2Recast transform = new UShock2Recast(sourceFileFile, jumppadFileFile, envelopeFileFile, ut2004DirFile, outputDirFile);
			transform.perform();
		} catch (Exception e) {			
			fail("Failed to export OBJ for Recast.", e);
		}
	}
	
	public static void main(String[] args) throws JSAPException {
		SimpleLogging.initLogging();
		
		log = Logger.getAnonymousLogger();
		
		initJSAP();
	    
	    header();
	    
	    readConfig(args);
	    
	    sanityChecks();
	    
	    transform();
	}

}
