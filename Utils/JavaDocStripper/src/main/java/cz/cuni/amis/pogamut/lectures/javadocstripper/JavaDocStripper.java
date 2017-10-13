package cz.cuni.amis.pogamut.lectures.javadocstripper;

import java.io.File;
import java.util.logging.Logger;

import cz.cuni.amis.utils.rewrite.IncludeDirForSubstitutions;
import cz.cuni.amis.utils.rewrite.RewriteFiles;
import cz.cuni.amis.utils.rewrite.RewriteFilesConfig;
import cz.cuni.amis.utils.rewrite.rewriter.Substitution;
import cz.cuni.amis.utils.simple_logging.SimpleLogging;

/**
 * JavaDocStripper.
 * <p><p>
 * This tool allows you to strip comments from Java source files.
 */
public class JavaDocStripper {
	
	public static String singleLineCommentPattern = "//.*";
	
	/**
	 * See: http://ostermiller.org/findcomment.html
	 */ 
	public static String multiLineCommentPattern = "(?:/\\*(?:[^*]|(?:\\*+[^*/]))*\\*+/)|(?://.*)";
	
	public static String fileSeparator = System.getProperty("file.separator");
	
	public static void printHelp() {
		System.out.println("");
		System.out.println("=====================");
		System.out.println("JavaDoc Stripper Tool");
		System.out.println("=====================");
		System.out.println("");
		System.out.println("This tool allows you to strip comments from Java source files.");
		System.out.println("It allows you to strip single files or recursively dig through directories (using -r).");
		System.out.println("Warning, no backups are made! Files are rewritten!");
		System.out.println("");
		System.out.println("Usage: java -jar javaDocStripper.jar [-r] file1 [file2 file3 ...]");
		System.out.println("");
		System.out.println("       -r       ...       recursively crawl through directories");
		System.out.println("       fileN    ...       path/to/file or path/to/directory to process");
		System.out.println("");
		System.out.println("Examples:");
		System.out.println("");
		System.out.println("java -jar javaDocStripper.jar MyClass.java");
		System.out.println("                ...       strips JavaDoc from MyClass.java");
		System.out.println("");
		System.out.println("java -jar javaDocStripper.jar MyClass1.java MyClass2.java");
		System.out.println("                ...       strips JavaDoc from MyClass1.java and MyClass2.java");
		System.out.println("");
		System.out.println("java -jar javaDocStripper.jar src/cz/cuni");
		System.out.println("                ...       strips JavaDoc from *.java files within src/cz/cuni directory");
		System.out.println("                ...       does not descend to subdirs");
		System.out.println("");
		System.out.println("java -jar javaDocStripper.jar -r src/cz/cuni");
		System.out.println("                ...       strips JavaDoc from *.java files within src/cz/cuni directory");
		System.out.println("                ...       DOES descend to subdirs");
		System.out.println("");
		System.out.println("java -jar javaDocStripper.jar -r src/cz/cuni src/eu/cuni");
		System.out.println("                ...       strips JavaDoc from *.java files within src/cz/cuni and src/eu/cuni directory");
		System.out.println("                ...       DOES descend to subdirs");
		System.out.println("");
		System.out.println("java -jar javaDocStripper.jar -r src/cz/cuni src/eu/cuni MyClass1.java");
		System.out.println("                ...       strips JavaDoc from *.java files within src/cz/cuni and src/eu/cuni directory");
		System.out.println("                ...       DOES descend to subdirs");
		System.out.println("                ...       also strips JavaDoc from MyClass1.java");
		System.out.println("");
		System.out.println("EXIT 1");
		System.out.println("");
	}
	
	public static void main( String[] args ) {
		
		if (args.length == 0) {
			printHelp();
			System.exit(1);
		}
		
		SimpleLogging.initLogging();
		Logger log = Logger.getAnonymousLogger();
		
		log.info("=====================");
		log.info("JavaDoc Stripper Tool");
		log.info("=====================");
		log.info("");
		log.info("Reading configuration...");
		
		Substitution multiLineCommentSubst = new Substitution();
		multiLineCommentSubst.setCaseSensitive(false);
		multiLineCommentSubst.setMultiLine(true);
		multiLineCommentSubst.setReMatch(multiLineCommentPattern);
		multiLineCommentSubst.setReSubst("");
		
		Substitution singleLineCommentSubst = new Substitution();
		singleLineCommentSubst.setCaseSensitive(false);
		singleLineCommentSubst.setMultiLine(false);
		singleLineCommentSubst.setReMatch(singleLineCommentPattern);
		singleLineCommentSubst.setReSubst("");
		
		boolean recursive = "-r".equals(args[0]);
		
		if (recursive) {
			log.info("'-r' paramter detected, will be recursively descending to subdirs.");
		} else {
			log.info("'-r' parameter not specified, won't recursively descend to subdirs.");
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
				dir.getSubstitutions().add(singleLineCommentSubst);
				dir.getSubstitutions().add(multiLineCommentSubst);								
			} else {
				log.info("Adding configuration for file      (" + (i+1) + ". param): " + args[i] + " -> " + file.getAbsolutePath());
				dir.setSubdirs(false);
				dir.setDir(file.getParentFile());
				dir.getIncludeFiles().add(file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf(fileSeparator)+1));
				dir.getSubstitutions().add(singleLineCommentSubst);
				dir.getSubstitutions().add(multiLineCommentSubst);
			}		
			
			config.getDirs().add(dir);
		}
		
		RewriteFiles rewriter = new RewriteFiles(config);
		rewriter.setLog(log);
		log.info("STRIPPING!");
		rewriter.rewrite();
		
		log.info("");
		log.info("DONE!");
		
		System.exit(0);
    }
}
