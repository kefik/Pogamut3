package cz.cuni.amis.pogamut.lectures.linetagger.tagger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;

import cz.cuni.amis.utils.process.ProcessExecution;
import cz.cuni.amis.utils.process.ProcessExecutionConfig;
import cz.cuni.amis.utils.rewrite.IncludeDir;
import cz.cuni.amis.utils.rewrite.RewriteFiles;
import cz.cuni.amis.utils.rewrite.RewriteFilesConfig;

public class Tagger {

	private Logger log = Logger.getLogger(getClass().getSimpleName());
	private RewriteFilesConfig config;
	private boolean backup;

	public Tagger(RewriteFilesConfig config, boolean backup) {
		this.config = config;
		this.config.getGlobals().getSubstitutions().add(new TagSubstitution());
		this.backup = backup;
	}
	
	public static boolean isWindows() {
		return System.getProperty("os.name").contains("Windows");
	}
	
	public static boolean isLinux() {
		return System.getProperty("os.name").contains("Linux");
	}

	public void setLog(Logger log) {
		this.log = log;
	}

	public synchronized void tag() throws IOException {
		try {
			step1_UnpackJacobe();
			step2_Prettify();
			step3_Tag();
		} finally {
			step4_DeleteJacobe();
		}
	}
	
	private void step1_UnpackJacobe() throws IOException {
		if (isWindows()) {
			step1_UnpackJacobe_Windows();
		} else 
		if (isLinux()) {
			step1_UnpackJacobe_Linux();
		} else {
			throw new RuntimeException("Can't run on non-windows, non-linus system!");
		}
	}
	
	private void step1_UnpackJacobe_Windows() throws IOException {
		InputStream stream = Tagger.class.getClassLoader().getResourceAsStream("jacobe/win32.zip");
		unpack(stream);
		if (!new File("temp_jacobe_unpacked/jacobe.exe").exists()) {
			throw new RuntimeException("Failed to unpack Jacobe prettifier!");
		}
		if (!new File("temp_jacobe_unpacked/sun.cfg").exists()) {
			throw new RuntimeException("Failed to unpack Jacobe prettifier!");
		}
		log.info("Exctracted.");
	}
	
	private void step1_UnpackJacobe_Linux() throws IOException {
		log.warning("Going to use Jacobe 7.3.14, see http://www.tiobe.com/index.php/content/products/jacobe/Jacobe.html, it requires Kernel (at least) v2.2.");
		InputStream stream = Tagger.class.getClassLoader().getResourceAsStream("jacobe/linux.zip");
		unpack(stream);
		if (!new File("temp_jacobe_unpacked/jacobe").exists()) {
			throw new RuntimeException("Failed to unpack Jacobe prettifier!");
		}
		if (!new File("temp_jacobe_unpacked/sun.cfg").exists()) {
			throw new RuntimeException("Failed to unpack Jacobe prettifier!");
		}
		log.info("Exctracted.");
	}
	
	private void unpack(InputStream stream) throws IOException {
		log.info("Exctracting Jacobe...");
		FileUtils.copyInputStreamToFile(stream, new File("temp_jacobe.zip"));
		try {
			File dir = new File("temp_jacobe_unpacked");
			if (!dir.mkdir()) {
				log.warning("Deleting " + dir.getAbsolutePath() + " ...");
				FileUtils.deleteQuietly(dir);
				if (!dir.mkdir()) {
					throw new IOException("Failed to create dir 'temp_jacobe_unpacked'!");
				}
			}
			
			ZipFile zipFile = new ZipFile(new File("temp_jacobe.zip"));
			try {
				Enumeration entries = zipFile.entries();
	
				while (entries.hasMoreElements()) {
					ZipEntry entry = (ZipEntry) entries.nextElement();
	
					if (entry.isDirectory()) {
						throw new RuntimeException("Jacobe's zip file should not contain directories!");
	
					}
					log.info("Extracting file: " + entry.getName());
					FileUtils.copyInputStreamToFile(zipFile.getInputStream(entry), new File("temp_jacobe_unpacked/" + entry.getName()));
				}
			} finally {
				zipFile.close();
			}
		} finally {
			new File("temp_jacobe.zip").delete();
		}
	}

	private void step2_Prettify() throws IOException {
		log.info("Prettifying files...");
		for (IncludeDir nextDir : config.getDirs()) {
			IncludeDir dir = new IncludeDir(nextDir, config.getGlobals());
			dir.initialize();
			List<File> files = dir.getDirectoryWalker().walk();
			
			for (File file : files) {
				ProcessExecutionConfig processConfig = new ProcessExecutionConfig();
				if (isWindows()) {
					processConfig.setPathToProgram("temp_jacobe_unpacked/jacobe.exe");
				} else 
				if (isLinux()) {
					processConfig.setPathToProgram("temp_jacobe_unpacked/jacobe");
				} else {
					throw new RuntimeException("Can't run on non-windows, non-linus system!");
				}
				processConfig.setRedirectStdErr(true);
				processConfig.setRedirectStdOut(true);
				processConfig.setExecutionDir("temp_jacobe_unpacked");
				processConfig.setId(file.getName());
				List<String> args = new ArrayList<String>();
				args.add(file.getAbsolutePath());
				processConfig.setArgs(args);
				ProcessExecution process = new ProcessExecution(processConfig, log);				
				log.info("Prettifying " + file.getAbsolutePath());
				process.start();
				process.getRunning().waitFor(false);
				if (process.getExitValue() != 0) {
					throw new RuntimeException("Prettifying of file filed: " + file.getAbsolutePath());
				}
				if (backup) {
					File backup = new File(file.getAbsolutePath() + ".bak");
					if (backup.exists()) FileUtils.deleteQuietly(backup);
					log.info("Backing up file...");
					FileUtils.moveFile(file, backup);
				} else {
					FileUtils.deleteQuietly(file);
				}
				
				log.info("Renaming prettified file...");
				FileUtils.moveFile(new File(file.getAbsolutePath() + ".jacobe"), file);
				new File(file.getAbsolutePath() + ".jacobe").delete();
				log.info("Prettified OK.");
			}
		}
	}
	
	private void step3_Tag() {
		RewriteFiles rewriter = new RewriteFiles(config);
		rewriter.setLog(log);
		rewriter.rewrite();
	}
	
	private void step4_DeleteJacobe() {
		FileUtils.deleteQuietly(new File("temp_jacobe_unpacked"));
	}

}
