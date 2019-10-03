package cz.cuni.amis.pogamut.ut2004.tournament.tdm.table;

import java.io.File;
import java.util.Iterator;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;

import cz.cuni.amis.pogamut.ut2004.tournament.tdm.table.report.summary.TDMMatchesExcelReport;

/**
 * Creates an excel with results out of respective matches.
 *
 * @author Jimmy
 */
public class MainExcel {
	
	private static final char ARG_RESULT_DIR_SHORT = 'r';
	
	private static final String ARG_RESULT_DIR_LONG = "result-directory";
	
	private static final char ARG_OUTPUT_DIR_SHORT = 'o';
	
	private static final String ARG_OUTPUT_DIR_LONG = "output-directory";
	
	private static final char ARG_OUTPUT_FILE_NAME_SHORT = 'f';
	
	private static final String ARG_OUTPUT_FILE_NAME_LONG = "output-file-name";
	
	private static JSAP jsap;

	private static JSAPResult config;
	
	private static boolean headerOutput = false;

	private static String resultDir;
	
	private static String outputDir;
	
	private static String outputFileName;
	
	private static File resultDirFile;
	
	private static File outputDirFile;
	
	private static File outputFile;

	
	private static void fail(String errorMessage) {
		fail(errorMessage, null);
	}

	private static void fail(String errorMessage, Throwable e) {
		header();
		System.out.println("ERROR: " + errorMessage);
		System.out.println();
		if (e != null) {
			e.printStackTrace();
			System.out.println("");
		}		
        System.out.println("Usage: java -jar ut2004-tournament-tdm-excel-onejar.jar ");
        System.out.println("                " + jsap.getUsage());
        System.out.println();
        System.out.println(jsap.getHelp());
        System.out.println();
        throw new RuntimeException("FAILURE: " + errorMessage);
	}

	private static void header() {
		if (headerOutput) return;
		System.out.println();
		System.out.println("====================================================");
		System.out.println("Pogamut UT2004 TeamDeathMatch Table Result Generator");
		System.out.println("====================================================");
		System.out.println();
		headerOutput = true;
	}
	
	private static void info(String msg) {
		System.out.println("[INFO]    " + msg);
	}
	
	private static void warning(String msg) {
		System.out.println("[WARNING] " + msg);
	}
	
	private static void severe(String msg) {
		System.out.println("[SEVERE]  " + msg);
	}
	
	private static void initJSAP() throws JSAPException {
		jsap = new JSAP();
		    	
	    FlaggedOption opt8 = new FlaggedOption(ARG_RESULT_DIR_LONG)
	    	.setStringParser(JSAP.STRING_PARSER)
	    	.setRequired(false) 
	    	.setShortFlag(ARG_RESULT_DIR_SHORT)
	    	.setLongFlag(ARG_RESULT_DIR_LONG)
	    	.setDefault(".");
	    opt8.setHelp("PATH/TO/directory where to search for results (output directory of TDMTable).");
	    
	    jsap.registerParameter(opt8);	
	    
	    FlaggedOption opt9 = new FlaggedOption(ARG_OUTPUT_DIR_LONG)
	    	.setStringParser(JSAP.STRING_PARSER)
	    	.setRequired(false) 
	    	.setShortFlag(ARG_OUTPUT_DIR_SHORT)
	    	.setLongFlag(ARG_OUTPUT_DIR_LONG)
	    	.setDefault(".");
	    opt9.setHelp("PATH/TO/directory where to output results (does not need to exist).");
	    
	    jsap.registerParameter(opt9);	
	    
	    FlaggedOption opt1 = new FlaggedOption(ARG_OUTPUT_FILE_NAME_LONG)
	    	.setStringParser(JSAP.STRING_PARSER)
	    	.setRequired(false) 
	    	.setShortFlag(ARG_OUTPUT_FILE_NAME_SHORT)
	    	.setLongFlag(ARG_OUTPUT_FILE_NAME_LONG)
	    	.setDefault("TDMTable-Results.xls");
	    opt1.setHelp("Name of the filename to produce. If it won't end with .xls, it will be extended with this suffix.");
	
	    jsap.registerParameter(opt1);	    	   
	}

	private static void readConfig(String[] args) {
		System.out.println("Parsing command arguments.");
		
		try {
	    	config = jsap.parse(args);
	    } catch (Exception e) {
	    	fail(e.getMessage());
	    	System.out.println("");
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
			
	    resultDir = config.getString(ARG_RESULT_DIR_LONG);
	    outputDir = config.getString(ARG_OUTPUT_DIR_LONG);	 
	    outputFileName = config.getString(ARG_OUTPUT_FILE_NAME_LONG);
	}
	
	private static void sanityChecks() {
		System.out.println("Sanity checks...");
		
		resultDirFile = new File(resultDir);
	    if (!resultDirFile.exists() || !resultDirFile.isDirectory()) {
	    	fail("Result directory was not found at '" + resultDirFile.getAbsolutePath() + "', path resolved from configuration read as '" + resultDir + "'.");
	    }
	    System.out.println("-- Result directory found at '" + resultDirFile.getAbsolutePath() + "'");
	    
	    outputDirFile = new File(outputDir);
	    boolean outputDirExist = true;
	    if (!outputDirFile.exists()) {
	    	outputDirExist = false;
	    	outputDirFile.mkdirs();
	    }
	    if (!outputDirFile.exists() || !outputDirFile.isDirectory()) {
	    	fail("Failed to create output directory at '" + outputDirFile.getAbsolutePath() + "', path resolved from configuration read as '" + outputDir + "'.");
	    }
	    System.out.println("-- Output directory " + (outputDirExist ? "found" : "created") + " at '" + outputDirFile.getAbsolutePath() + "'");
	    	    
	    if (!(outputFileName.toLowerCase().endsWith(".xls"))) outputFileName += ".xls";
	    outputFile = new File(outputDir, outputFileName);
	    
	    System.out.println("-- Going to write results into file '" + outputFile.getAbsolutePath() + "'");
	    
	    if (outputFile.exists()) {
	    	if (!outputFile.isFile()) {
	    		fail("Cannot overwrite output file as it exists and is not a file. Output file at '" + outputFile.getAbsolutePath() + "' resolved from configuration read as '" + outputFileName + "'.");
	    	} else {
	    		warning("Going to overwrite file '" + outputFile.getAbsolutePath() + "' resolved from configuration read as '" + outputFileName + "'.");
	    	}
	    }
	    
	    System.out.println("Sanity checks OK!");
	}
	
	private static void generate() {
		TDMMatchesExcelReport report = new TDMMatchesExcelReport(resultDirFile, outputFile);
		report.generate();
	}

	public static void main(String[] args) throws JSAPException {
//      FOR TESTING
		if (args == null || args.length == 0) {
			args = new String[] {
				"-r",
				"d:\\Workspaces\\MFF\\NAIL068-UmeleBytosti\\Lectures\\AB2019-Labs\\Lab-06-TDM\\Students\\_Results\\",
				"-o",
				"d:\\Workspaces\\MFF\\NAIL068-UmeleBytosti\\Lectures\\AB2019-Labs\\Lab-06-TDM\\Students\\_Results\\",
			};
		}
		
		initJSAP();
	    
	    header();
	    
	    readConfig(args);
	    
	    sanityChecks();
	    
	    try {
	    	generate();	    
	    } catch (Exception e) {
	    	fail("Failed to generate results.", e);
	    }
	    
	    System.out.println("DONE!");
	}
	
}
