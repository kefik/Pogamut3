package cz.cuni.amis.pogamut.ut2004.navmeshmaker;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cz.cuni.amis.utils.process.ProcessExecution;
import cz.cuni.amis.utils.process.ProcessExecutionConfig;

public class NavMeshMakerStep3_Recast extends NavMeshMakerStep {

	private static final long EXPORT_MAP_TIMEOUT_MILLIS = 60 * 60 * 1000;
	
	private File outputDir;
	
	private File recastDir;
	private File outputRecastDir;
	
	private File sourceFile;
	private File scaleFile;
	private File centreFile;
	private File outputFile;
	

	public NavMeshMakerStep3_Recast(UT2004NavMeshMaker owner) {
		super(owner);
		
		outputDir = owner.getOutputDir();
		
		sourceFile = owner.getOutputFile(".obj");
		scaleFile = owner.getOutputFile(".scale");
		centreFile = owner.getOutputFile(".centre");
		outputFile = owner.getOutputFile(".navmesh");
				
		outputFiles = new File[]{ outputFile };
		targetFiles = new File[]{ outputFile };
	}
	
	public void execute() {
		if (shouldSkip()) {
			File file = owner.getOutputFile(".navmesh");
			if (file.exists()) {
				info("STEP3: NavMesh file already exists, skipping the step");
				return;
			}
		}
		
		if (anyOutputExist() && !owner.isOverwriteMode()) {
			fail("STEP3: NevMesh file already exists and cannot be used or overwritten, use CONTINUE or OVERWRITE mode.");			
		}
		
		cleanAllExistingOutputs();
		
		ProcessExecutionConfig config = new ProcessExecutionConfig();
		
		config.setPathToProgram("./tools/03-Recast/RecastDemo.exe");
		
		List<String> args = new ArrayList<String>();
		
		args.add(sourceFile.getAbsolutePath());
		args.add(scaleFile.getAbsolutePath());
		args.add(centreFile.getAbsolutePath());
		args.add(outputFile.getAbsolutePath());
		
		config.setArgs(args);
		
		config.setExecutionDir("./tools/03-Recast");
		
		config.setId("Recast");
		
		config.setRedirectStdErr(true);
		config.setRedirectStdOut(true);
		
		config.setTimeout(EXPORT_MAP_TIMEOUT_MILLIS);
		
		ProcessExecution process = new ProcessExecution(config, log);
		
		process.start();
		
		process.getRunning().waitFor(false);
		
		if (process.isFailed()) {
			fail("STEP3: Recast failed.");
		}
		if (process.isTimeout()) {
			fail("STEP3: Recast timed out.");
		}
		
		if (!allOutputExist()) {
			fail("STEP3: NavMesh file was not exported into ./output dir as expected...");
		}
	}
	
}
