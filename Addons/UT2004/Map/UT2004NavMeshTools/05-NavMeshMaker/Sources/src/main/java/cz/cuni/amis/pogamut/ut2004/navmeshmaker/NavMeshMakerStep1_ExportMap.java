package cz.cuni.amis.pogamut.ut2004.navmeshmaker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import cz.cuni.amis.utils.process.ProcessExecution;
import cz.cuni.amis.utils.process.ProcessExecutionConfig;

public class NavMeshMakerStep1_ExportMap extends NavMeshMakerStep {

	private static final long EXPORT_MAP_TIMEOUT_MILLIS = 60 * 60 * 1000;
	
	private File outputDir;
	
	public NavMeshMakerStep1_ExportMap(UT2004NavMeshMaker owner) {
		super(owner);
		
		outputDir = new File("./output");
		
		outputFiles = new File[] { owner.getOutputFile(outputDir, ".xml") };
		targetFiles = new File[] { owner.getOutputFile(".xml") };
	}
	
	public void execute() {
		if (shouldSkip()) {
			info("STEP1: XML export already exists, skipping.");
			return;
		}
		
		if (anyOutputExist() && !owner.isOverwriteMode()) {
			fail("STEP1: XML file already exist and cannot be used or overwritten, use CONTINUE or OVERWRITE mode.");			
		}
		
		if (!outputDir.exists()) {
			info("STEP1: Output directory for UShock.exe does not exist, creating one at " + outputDir.getAbsolutePath());
			outputDir.mkdirs();
		}
		
		if (!outputDir.isDirectory()) {
			fail("STEP1: Output directory is not directory at " + outputDir.getAbsolutePath());
		}
		
		cleanAllExistingOutputs();
		
		ProcessExecutionConfig config = new ProcessExecutionConfig();
		
		config.setPathToProgram("./Tools/01-UShock/UShock.exe");
		
		List<String> args = new ArrayList<String>();
		args.add(owner.getUt2004Home().getAbsolutePath());
		args.add(owner.getMapName());		
		config.setArgs(args);
		
		config.setExecutionDir(".");
		
		config.setId("UShock");
		
		config.setRedirectStdErr(true);
		config.setRedirectStdOut(true);
		
		config.setTimeout(EXPORT_MAP_TIMEOUT_MILLIS);
		
		ProcessExecution process = new ProcessExecution(config, log);
		
		process.start();
		
		process.getRunning().waitFor(false);
		
		if (process.isFailed()) {
			fail("STEP1: Map export failed.");
		}
		if (process.isTimeout()) {
			fail("STEP1: Map export timeout.");
		}
		
		if (!allOutputExist()) {
			fail("STEP1: Map was not exported into " + outputFiles[0].getAbsolutePath() + " as expected...");
		}
		
		info("STEP1: Geometry exported into " + outputFiles[0].getAbsolutePath());
	}
	
}
