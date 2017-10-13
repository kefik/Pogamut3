package cz.cuni.amis.pogamut.ut2004.navmeshmaker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import cz.cuni.amis.utils.process.ProcessExecution;
import cz.cuni.amis.utils.process.ProcessExecutionConfig;

public class NavMeshMakerStep2_ExportForRecast extends NavMeshMakerStep {

	private static final long EXPORT_MAP_TIMEOUT_MILLIS = 60 * 60 * 1000;
	
	private File outputDir;
	private File sourceFile;
	
	public NavMeshMakerStep2_ExportForRecast(UT2004NavMeshMaker owner) {
		super(owner);
		outputDir = new File("./output");
		sourceFile = new File(outputDir, owner.getMapName() + ".xml");
		
		outputFiles = new File[]{ owner.getOutputFile(outputDir, ".obj"), owner.getOutputFile(outputDir, ".scale"), owner.getOutputFile(outputDir, ".centre") };
		targetFiles = new File[]{ owner.getOutputFile(".obj"),            owner.getOutputFile(".scale"),            owner.getOutputFile(outputDir, ".centre") };
	}
	
	public void execute() {
		if (shouldSkip()) {
			if (allOutputExist()) {
				info("STEP2: OBJ/SCALE/CENTRE files already exists, skipping the step");
				return;
			}
		}
		
		if (anyOutputExist() && !owner.isOverwriteMode()) {
			fail("STEP2: Some/All output/s already exist/s and cannot be used or overwritten, use CONTINUE or OVERWRITE mode.");			
		}
		
		cleanAllExistingOutputs();
		
		ProcessExecutionConfig config = new ProcessExecutionConfig();
		
		String javaCmd = JavaHome.getJavaCommand();
		if (javaCmd == null) {
			fail("Could not find java, do you have $JAVA_HOME set up?");
		}
				
		
		config.setPathToProgram(javaCmd);
		
		List<String> args = new ArrayList<String>();
		args.add("-jar");
		args.add("tools/02-UShock2Recast/ut2004-level-geom-3.6.2-SNAPSHOT.one-jar.jar");
		args.add("-i");
		args.add("./output/" + owner.getMapName() + ".xml");		
		args.add("-o");
		args.add("./output");
		args.add("-u");
		args.add(owner.getUt2004Home().getAbsolutePath());
		config.setArgs(args);
		
		config.setExecutionDir(".");
		
		config.setId("UShock2Recast");
		
		config.setRedirectStdErr(true);
		config.setRedirectStdOut(true);
		
		config.setTimeout(EXPORT_MAP_TIMEOUT_MILLIS);
		
		ProcessExecution process = new ProcessExecution(config, log);
		
		process.start();
		
		process.getRunning().waitFor(false);
		
		if (process.isFailed()) {
			fail("STEP2: Export for Recast failed.");
		}
		if (process.isTimeout()) {
			fail("STEP2: Export for Recast failed.");
		}
		
		if (!allOutputExist()) {
			fail("STEP2: Output files was not exported into ./output dir as expected...");
		}
	}
	
}
