package cz.cuni.amis.pogamut.ut2004.hideandseek.protocol;

import java.util.Formatter;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.messages.HSGameStart;
import cz.cuni.amis.pogamut.ut2004.hideandseek.server.UT2004HSServer;

/**
 * Contains all configuration parameters for the Hide-and-Seek game.
 * <p><p>
 * WARNING: if you extend this, do not forget to alter {@link #getCSVHeader()} and {@link #formatCSVLine(Formatter)}.
 *  
 * @author Jimmy
 */
/**
 * @author Jimmy
 *
 */
public class HSGameConfig {
	
	// ======
	// UT2004
	// ======
	
	/**
	 * Relevant only for {@link UT2004HSServer}, not exported for hide&seek participants
	 */
	private int observerPort = 3002;
	
	// ===
	// MAP
	// ===
	
	private String targetMap;
	
	private Location safeArea;
	
	// ============
	// AREA & TIMES
	// ============
	
	private int roundCount;
	
	/**
	 * Total time for the round including {@link #hideTimeUT} and {@link #restrictedAreaTimeSecs}.
	 */
	private double roundTimeUT;
	
	private double hideTimeUT;
	
	private double restrictedAreaTimeSecs;
	
	private double restrictedAreaSeekerMaxTimeSecs = 7;
		
	private int safeAreaRadius;
	
	private int restrictedAreaRadius;
	
	private int spotTimeMillis = 600;
	
	/**
	 * How far from safe area should RUNNERS be spawned.
	 */
	private int spawnRadiusForRunners = 100;
	
	// =============
	// FIXED SEEKER?
	// =============
	
	private boolean fixedSeeker;
	
	private String fixedSeekerName;
	
	// =======
	// SCORING
	// =======
	
	private int runnerCaptured = -10;
	
	private int runnerFouled = -1000;
	
	private int runnerSafe = 100;
	
	private int runnerSpotted = 0;
	
	private int runnerSurvived = 50;
	
	private int seekerSpottedRunner = 20;
	
	private int seekerCapturedRunner = 100;
	
	private int seekerLetRunnerSurvive = -10;
	
	private int seekerLetRunnerEscape = -20;
	
	private int seekerFouled = -100;
	
	// ==============
	// IMPLEMENTATION
	// ==============
	
	public HSGameConfig() {
	}

	public HSGameConfig(HSGameStart msg) {
		this.hideTimeUT = msg.getHideTimeUT();
		this.restrictedAreaRadius = msg.getRestrictedAreaRadius();
		this.restrictedAreaTimeSecs = msg.getRestrictedAreaTimeUT();
		this.roundCount = msg.getRoundCount();
		this.roundTimeUT = msg.getRoundTimeUT();
		this.safeArea = msg.getSafeArea();
		this.safeAreaRadius = msg.getSafeAreaRadius();
		this.fixedSeeker = msg.getFixedSeeker();
		this.fixedSeekerName = msg.getFixedSeekerName();
		readScoring(msg.getScoring());
	}
	
	public int getObserverPort() {
		return observerPort;
	}

	public void setObserverPort(int observerPort) {
		this.observerPort = observerPort;
	}

	public String getTargetMap() {
		return targetMap;
	}

	public void setTargetMap(String targetMap) {
		this.targetMap = targetMap;
	}

	public int getRoundCount() {
		return roundCount;
	}

	public void setRoundCount(int roundCount) {
		this.roundCount = roundCount;
	}

	public double getRoundTimeUT() {
		return roundTimeUT;
	}

	public void setRoundTimeUT(double roundTimeUT) {
		this.roundTimeUT = roundTimeUT;
	}

	public double getHideTimeUT() {
		return hideTimeUT;
	}

	public void setHideTimeUT(double hideTimeUT) {
		this.hideTimeUT = hideTimeUT;
	}

	public double getRestrictedAreaTimeSecs() {
		return restrictedAreaTimeSecs;
	}

	public void setRestrictedAreaTimeSecs(double restrictedAreaTimeSecs) {
		this.restrictedAreaTimeSecs = restrictedAreaTimeSecs;
	}

	public Location getSafeArea() {
		return safeArea;
	}

	public void setSafeArea(Location safeArea) {
		this.safeArea = safeArea;
	}

	public int getSafeAreaRadius() {
		return safeAreaRadius;
	}

	public void setSafeAreaRadius(int safeAreaRadius) {
		this.safeAreaRadius = safeAreaRadius;
	}

	public int getRestrictedAreaRadius() {
		return restrictedAreaRadius;
	}

	public void setRestrictedAreaRadius(int restrictedAreaRadius) {
		this.restrictedAreaRadius = restrictedAreaRadius;
	}

	public boolean isFixedSeeker() {
		return fixedSeeker;
	}

	public void setFixedSeeker(boolean fixedSeeker) {
		this.fixedSeeker = fixedSeeker;
	}

	public String getFixedSeekerName() {
		return fixedSeekerName;
	}

	public void setFixedSeekerName(String fixedSeekerName) {
		this.fixedSeekerName = fixedSeekerName;
	}
	
	public int getSpotTimeMillis() {
		return spotTimeMillis;
	}

	public void setSpotTimeMillis(int spotTimeMillis) {
		this.spotTimeMillis = spotTimeMillis;
	}

	public int getSpawnRadiusForRunners() {
		return spawnRadiusForRunners;
	}

	public void setSpawnRadiusForRunners(int spawnRadiusForRunners) {
		this.spawnRadiusForRunners = spawnRadiusForRunners;
	}

	public int getRunnerCaptured() {
		return runnerCaptured;
	}

	public void setRunnerCaptured(int runnerCaptured) {
		this.runnerCaptured = runnerCaptured;
	}

	public int getRunnerFouled() {
		return runnerFouled;
	}

	public void setRunnerFouled(int runnerFouled) {
		this.runnerFouled = runnerFouled;
	}

	public int getRunnerSafe() {
		return runnerSafe;
	}

	public void setRunnerSafe(int runnerSafe) {
		this.runnerSafe = runnerSafe;
	}

	public int getRunnerSpotted() {
		return runnerSpotted;
	}

	public void setRunnerSpotted(int runnerSpotted) {
		this.runnerSpotted = runnerSpotted;
	}

	public int getRunnerSurvived() {
		return runnerSurvived;
	}

	public void setRunnerSurvived(int runnerSurvived) {
		this.runnerSurvived = runnerSurvived;
	}

	public int getSeekerSpottedRunner() {
		return seekerSpottedRunner;
	}

	public void setSeekerSpottedRunner(int seekerSpottedRunner) {
		this.seekerSpottedRunner = seekerSpottedRunner;
	}

	public int getSeekerCapturedRunner() {
		return seekerCapturedRunner;
	}

	public void setSeekerCapturedRunner(int seekerCapturedRunner) {
		this.seekerCapturedRunner = seekerCapturedRunner;
	}

	public int getSeekerLetRunnerSurvive() {
		return seekerLetRunnerSurvive;
	}

	public void setSeekerLetRunnerSurvive(int seekerLetRunnerSurvive) {
		this.seekerLetRunnerSurvive = seekerLetRunnerSurvive;
	}
	
	public int getSeekerLetRunnerEscape() {
		return seekerLetRunnerEscape;
	}

	public void setSeekerLetRunnerEscape(int seekerLetRunnerEscape) {
		this.seekerLetRunnerEscape = seekerLetRunnerEscape;
	}
	
	public int getSeekerFouled() {
		return seekerFouled;
	}

	public void setSeekerFouled(int seekerFouled) {
		this.seekerFouled = seekerFouled;
	}

	public double getRestrictedAreaSeekerMaxTimeSecs() {
		return restrictedAreaSeekerMaxTimeSecs;
	}

	public void setRestrictedAreaSeekerMaxTimeSecs(double restrictedAreaSeekerMaxTimeSecs) {
		this.restrictedAreaSeekerMaxTimeSecs = restrictedAreaSeekerMaxTimeSecs;
	}
	
	// =======================
	// SERIALIZATION UTILITIES
	// =======================
		
	public String writeScoring() {
		return 
				runnerCaptured + ";"
			+   runnerFouled + ";"
			+   runnerSafe + ";"
			+   runnerSpotted + ";"
			+   runnerSurvived + ";"
			+   seekerSpottedRunner + ";"
			+   seekerCapturedRunner + ";"
			+   seekerLetRunnerSurvive + ";"
			+   seekerLetRunnerEscape + ";"
			+   seekerFouled + ";"
			+   spotTimeMillis + ";"
			+   spawnRadiusForRunners + ";"
			+   targetMap;
	}
	
	public void readScoring(String scoring) {
		String[] scores = scoring.split(";");
		if (scores.length != 13) throw new RuntimeException("Invalid scoring string, does not contain 8 numbers separated by ';', string: '" + scoring + "'.");
		runnerCaptured = Integer.parseInt(scores[0]);
		runnerFouled = Integer.parseInt(scores[1]);
		runnerSafe = Integer.parseInt(scores[2]);
		runnerSpotted = Integer.parseInt(scores[3]);
		runnerSurvived = Integer.parseInt(scores[4]);
		seekerSpottedRunner = Integer.parseInt(scores[5]);
		seekerCapturedRunner = Integer.parseInt(scores[6]);
		seekerLetRunnerSurvive = Integer.parseInt(scores[7]);
		seekerLetRunnerEscape = Integer.parseInt(scores[8]);
		seekerFouled = Integer.parseInt(scores[9]);
		spotTimeMillis = Integer.parseInt(scores[10]);
		spawnRadiusForRunners = Integer.parseInt(scores[11]);
		targetMap = scores[12];
	}
	
	public String getCSVHeader() {
		return     "Map;FixedSeeker;FixedSeekerName;RoundCount;RoundTimeSecs;HideTimeSecs;RestrictedAreaTimeSecs;SafeArea;SafeAreaRadius;RestrictedAreaRadius;SpotTimeMillis;SpawnRadiusForRunners"
			   + ";ScoreRunnerCaptured;ScoreRunnerSpotted;ScoreRunnerSafe;ScoreRunnerSurvived;ScoreRunnerFouled"
			   + ";ScoreSeekerCapturedRunner;ScoreSeekerSpottedRunner;ScoreSeekerLetRunnerSurvive;ScoreSeekerLetRunnerEscape;ScoreSeekerFouled";
	}
	
	public void formatCSVLine(Formatter writer) {
		writer.format("%s;", targetMap);
		writer.format("%s;", String.valueOf(fixedSeeker));
		writer.format("%s;", String.valueOf(fixedSeekerName));
		writer.format("%d;", roundCount);
		writer.format("%.3f;", roundTimeUT);
		writer.format("%.3f;", hideTimeUT);
		writer.format("%.3f;", restrictedAreaTimeSecs);
		writer.format("%s;", (safeArea == null ? "null" : safeArea.toString().replaceAll(";", "|")));
		writer.format("%d;", safeAreaRadius);
		writer.format("%d;", restrictedAreaRadius);
		writer.format("%d;", spotTimeMillis);
		writer.format("%d;", spawnRadiusForRunners);
		
		writer.format("%d;", runnerCaptured);
		writer.format("%d;", runnerSpotted);
		writer.format("%d;", runnerSafe);
		writer.format("%d;", runnerSurvived);
		writer.format("%d;", runnerFouled);
		
		writer.format("%d;", seekerCapturedRunner);
		writer.format("%d;", seekerSpottedRunner);
		writer.format("%d;", seekerLetRunnerSurvive);
		writer.format("%d;", seekerLetRunnerEscape);
		writer.format("%d",  seekerFouled);
	} 
	

}
