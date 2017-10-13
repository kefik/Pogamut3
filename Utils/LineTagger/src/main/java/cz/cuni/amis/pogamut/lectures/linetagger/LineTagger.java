package cz.cuni.amis.pogamut.lectures.linetagger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import cz.cuni.amis.pogamut.lectures.linetagger.checker.Checker;
import cz.cuni.amis.pogamut.lectures.linetagger.tagger.Tagger;
import cz.cuni.amis.utils.rewrite.IncludeDirForSubstitutions;
import cz.cuni.amis.utils.rewrite.RewriteFilesConfig;
import cz.cuni.amis.utils.simple_logging.SimpleLogging;

/**
 * LineTagger
 * <p><p>
 * This tool will tag each line with javadoc multi-line comment and "unique" id or check existing ones.
 */
public class LineTagger {
		
	public static String fileSeparator = System.getProperty("file.separator");
	
	public static void printHelp() {
		System.out.println("");
		System.out.println("================");
		System.out.println("Line Tagger Tool");
		System.out.println("================");
		System.out.println("");
		System.out.println("This tool allows you to tag every line of the Java code or check tagged files how much they have changed.");
		System.out.println("");
		System.out.println("Usage: java -jar lineTagger.jar [-r] [-t] [-b] [-c <csv_file>] file1 [file2 file3 ...]");
		System.out.println("");
		System.out.println("       -t       ...       tag files");
		System.out.println("       -b       ...       backup files before tagging");
		System.out.println("       -c       ...       check files and produce statistic");
		System.out.println("       fileN    ...       path/to/file or path/to/directory to process");
		System.out.println("");
		System.out.println("Examples:");
		System.out.println("");
		System.out.println("java -jar lineTagger.jar -t MyClass.java");
		System.out.println("                ...       tags MyClass.java");
		System.out.println("");
		System.out.println("java -jar lineTagger.jar -c result.csv MyClass1.java MyClass2.java");
		System.out.println("                ...       checks MyClass1.java and MyClass2.java and writes result into result.csv");
		System.out.println("");
		System.out.println("java -jar lineTagger.jar -t src/cz/cuni");
		System.out.println("                ...       tags *.java files within src/cz/cuni directory");
		System.out.println("                ...       does not descend to subdirs");
		System.out.println("");
		System.out.println("java -jar lineTagger.jar -r -t src/cz/cuni");
		System.out.println("                ...       tags *.java files within src/cz/cuni directory");
		System.out.println("                ...       DOES descend to subdirs");
		System.out.println("");
		System.out.println("java -jar lineTagger.jar -r -c result.csv src/cz/cuni src/eu/cuni");
		System.out.println("                ...       checks *.java files within src/cz/cuni and src/eu/cuni directory");
		System.out.println("                ...       DOES descend to subdirs");
		System.out.println("                ...       results are written to result.csv");
		System.out.println("");
		System.out.println("java -jar lineTagger.jar -r -c result.csv src/cz/cuni src/eu/cuni MyClass1.java");
		System.out.println("                ...       checks *.java files within src/cz/cuni and src/eu/cuni directory");
		System.out.println("                ...       DOES descend to subdirs");
		System.out.println("                ...       also checks MyClass1.java");
		System.out.println("                ...       results are written to result.csv");
		System.out.println("");
		System.out.println("EXIT 1");
		System.out.println("");
	}
	
	public static void tag(RewriteFilesConfig config, boolean backup, Logger log) throws IOException {
		log.info("TAGGING!");
		Tagger tag = new Tagger(config, backup);
		tag.setLog(log);
		tag.tag();
	}

	public static void check(RewriteFilesConfig config, String resultFile, Logger log) {
		log.info("CHECKING!");
		Checker checker = new Checker(config, resultFile);
		checker.setLog(log);
		checker.check();
	}
	
	public static void main( String[] args ) throws IOException {
		
		if (args.length == 0) {
			printHelp();
			System.exit(1);
		}
		
		SimpleLogging.initLogging();
		Logger log = Logger.getAnonymousLogger();
		
		log.info("================");
		log.info("Line Tagger Tool");
		log.info("================");
		log.info("");
		log.info("Reading configuration...");
		
		boolean recursive = false;
		boolean check = false;
		boolean tag = false;
		boolean backup = false;
		String resultFile = null;
		List<String> inputs = new ArrayList<String>();
		
		for (int i = 0; i < args.length; ++i) {
			if ("-r".equals(args[i])) {
				log.info("'-r' paramter detected, will be recursively descending to subdirs.");
				recursive = true;
			} else
			if ("-t".equals(args[i])) {
				log.info("'-t' parameter detected, tagging mode.");
				tag = true;
			} else
			if ("-c".equals(args[i])) {
				log.info("'-c' parameter detected, checking mode.");
				check = true;
				if (i+1 >= args.length) {
					log.severe("No 'result.file.csv' specified after -c tag!");
					printHelp();
					System.exit(1);
				}
				resultFile = args[++i];
			} 
			if ("-b".equals(args[i])) {
				log.info("'-b' parameter detected, files will be backed up (.bak) before tagging.");
				backup = true;
			} else {
				if (args[i] != null) {
					inputs.add(args[i]);
				}
			}
		}
		
		if (!recursive) {
			log.info("'-r' parameter not specified, won't recursively descend to subdirs.");
		}
		
		if (check && tag) {
			log.severe("Both -c and -t tag specified, invalid!");
			printHelp();
			System.exit(1);
		}
		if (inputs.size() == 0) {
			log.severe("No input files/dirs specified, invalid!");
			System.exit(1);
		}
		
		RewriteFilesConfig config = new RewriteFilesConfig();
		
		int i = recursive ? 1 : 0;
		for (; i < args.length; ++i) {
			File file = new File(args[i]);
			if (!file.exists()) {
				log.warning("File/Dir (" + (i+1) + ". param) does not exist, ommiting: " + args[i] + " -> " + file.getAbsolutePath());
				continue;
			}
			
			IncludeDirForSubstitutions dir = new IncludeDirForSubstitutions();			
			if (file.isDirectory()) {
				log.info("Adding configuration for directory (" + (i+1) + ". param): " + args[i] + " -> " + file.getAbsolutePath());
				dir.setSubdirs(recursive);
				dir.setDir(file);
				dir.getIncludeFiles().add("*.java");
			} else {
				log.info("Adding configuration for file      (" + (i+1) + ". param): " + args[i] + " -> " + file.getAbsolutePath());
				dir.setSubdirs(false);
				dir.setDir(file.getParentFile());
				dir.getIncludeFiles().add(file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf(fileSeparator)+1));
			}		
			
			config.getDirs().add(dir);
		}
		
		if (check) {
			check(config, resultFile, log);
		} else {
			tag(config, backup, log);
		}
		
		log.info("");
		log.info("DONE!");
		
		System.exit(0);
    }

}
