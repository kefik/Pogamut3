package cz.cuni.amis.pogamut.ut2004.navmeshmaker;

import java.io.File;
import java.util.logging.Logger;

public class UT2004NavMeshMaker {
	
	private static Logger log = Logger.getAnonymousLogger();
	
	private File ut2004Home;
	private File ut2004Maps;
	private String mapName;
	private String mapFileName;
	private File ut2004Map;
	
	private File outputDir;
	private boolean overwriteMode;
	private boolean continueMode;
	
	private String name;
	
	public UT2004NavMeshMaker(String name, File ut2004Home, File outputDir, String mapName, boolean overwriteMode, boolean continueMode) {
		this.ut2004Home = ut2004Home;
		this.ut2004Maps = new File(ut2004Home, "Maps");
		this.mapName = mapName;
		
		this.mapFileName = mapName;		
		if (!mapFileName.endsWith(".ut2")) mapFileName = mapFileName + ".ut2";
		
		this.ut2004Map = new File(ut2004Maps, mapFileName);
		
		this.outputDir = outputDir;
		this.overwriteMode = overwriteMode;
		this.continueMode = continueMode;
		
		this.name = name;
		
		sanityChecks();
	}
	
	private void sanityChecks() {
		if (!ut2004Home.exists() && !ut2004Home.isDirectory()) {
			fail("UT2004 directory does not exist at: " + ut2004Home.getAbsolutePath());
		}
		if (!ut2004Maps.exists() && !ut2004Maps.isDirectory()) {
			fail("UT2004/Maps directory does not exist at: " + ut2004Home.getAbsolutePath());
		} 
		if (!ut2004Map.exists() && !ut2004Map.isFile()) {
			fail("UT2004/Maps/" + mapFileName + " file does not exist at: " + ut2004Map.getAbsolutePath());
		}
	}

	// =======
	// GETTERS
	// =======
	
	protected String getName() {
		return name;
	}
	
	public File getUt2004Home() {
		return ut2004Home;
	}
	
	public File getUt2004Maps() {
		return ut2004Maps;
	}

	public File getUt2004Map() {
		return ut2004Map;
	}
	
	public String getMapName() {
		return mapName;
	}
	
	public File getOutputDir() {
		return outputDir;
	}

	public boolean isOverwriteMode() {
		return overwriteMode;
	}

	public boolean isContinueMode() {
		return continueMode;
	}
	
	protected boolean mayContinue() {
		return mayContinue;
	}
	
	protected boolean shouldContinue() {
		return continueMode && mayContinue;
	}
	
	// =====
	// UTILS
	// =====

	private static void fail(String errorMessage, RuntimeException e) {
		log.severe(errorMessage);
        throw e;
	}
	
	private static void fail(String errorMessage) {
		fail(errorMessage, new RuntimeException(errorMessage));
	}
	
	protected void info(String msg) {
		log.info(getName() + " " + msg);
	}
	
	protected void warning(String msg) {
		log.warning(getName() + " " + msg);
	}
	
	protected void severe(String msg) {
		log.severe(getName() + " " + msg);
	}
	
	protected File getOutputFile(File outputDir, String extension) {
		String name = mapName;		
		if (!extension.startsWith(".")) extension = "." + extension;		
		name += extension;		
		return new File(outputDir, name);		
	}
	
	protected File getOutputFile(String extension) {
		return getOutputFile(outputDir, extension);
	}
	
	// ===========
	// MAIN METHOD
	// ===========
	
	private boolean mayContinue = false;
	
	public void createMesh() {
		
		info("CREATING NAVMESH" + (overwriteMode ? " [OVERWRITE]" : "") + (continueMode ? " [CONTINUE]" : ""));
		
		// 0. MAP HASH
		
		NavMeshMakerStep_ExportMapHash hash = new NavMeshMakerStep_ExportMapHash(this);		
		mayContinue = hash.hashNotExistOrEquals();
		hash.backupHash();
		
		if (continueMode) {
			if (!mayContinue) {
				warning("Could not 'continue' as the map has changes since the last time (hashes mismatch).");
			}
		}
		
		// 1. EXPORT MAP
		
		NavMeshMakerStep1_ExportMap exportMap = new NavMeshMakerStep1_ExportMap(this);
		exportMap.execute();
		
		// 2. TRANSFORM FOR RECAST
		
		NavMeshMakerStep2_ExportForRecast transformForRecast = new NavMeshMakerStep2_ExportForRecast(this);
		transformForRecast.execute();
		
		// 3. RECAST - CREATE NAV MESH
		
		NavMeshMakerStep3_Recast recast = new NavMeshMakerStep3_Recast(this);
		recast.execute();
		
		// 4. MOVE OUTPUTS
		exportMap.moveOutput();
		transformForRecast.moveOutput();
		recast.moveOutput();
		
		info("FINISHED!");
	}	
}
