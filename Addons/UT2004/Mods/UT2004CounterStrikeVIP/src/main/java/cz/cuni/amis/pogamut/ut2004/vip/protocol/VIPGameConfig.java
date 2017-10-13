package cz.cuni.amis.pogamut.ut2004.vip.protocol;

import java.util.Formatter;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.vip.protocol.messages.VIPGameStart;
import cz.cuni.amis.pogamut.ut2004.vip.server.UT2004VIPServer;

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
public class VIPGameConfig {
	
	// ======
	// UT2004
	// ======
	
	/**
	 * Relevant only for {@link UT2004VIPServer}, not exported for VIP game mode participants.
	 */
	private int observerPort = 3002;
	
	// ===
	// MAP
	// ===
	
	private String targetMap;

	/**
	 * Where counter-terrorist forces may appear. List of locations, every round a random location from this array will be chosen.
	 */
	private Location[] ctsSpawnAreas;
	
	/**
	 * Where terrorist forces may appear. List of locations, every round a random location from this array will be chosen.
	 */
	private Location[] tsSpawnAreas;
	
	/**
	 * Where the VIP needs to get. List of locations, every round a random location from this array will be chosen.
	 */
	private Location[] vipSafeAreas;
	
	private int vipSafeAreaRadius;
	
	// ============
	// ROUND TIMES
	// ============
	
	/**
	 * <= 0 ~ infinite
	 */
	private int roundCount;
	
	/**
	 * Total time for the round.
	 */
	private double roundTimeUT;
	
			
	// ==========
	// FIXED VIP?
	// ==========
	
	/**
	 * If false, VIP will be assigned randomly.
	 */
	private boolean fixedVIP;
	
	/**
	 * If such a bot cannot be found in CTs team, server will tail till it gets connected...
	 */
	private String fixedVIPNamePrefix;
	
	// =======
	// SCORING
	// =======
	
	private int vipKilledTsScore = 100;
	
	private int vipKilledCTsScore = 0;
	
	private int vipSafeTsScore = 0;
	
	private int vipSafeCTsScore = 100;
	
	// ==============
	// IMPLEMENTATION
	// ==============
	
	public VIPGameConfig() {
	}

	public VIPGameConfig(VIPGameStart msg) {
		this.roundCount = msg.getRoundCount();
		this.roundTimeUT = msg.getRoundTimeUT();
		this.vipSafeAreaRadius = msg.getVipSafeAreaRadius();
		this.fixedVIP = msg.getFixedVIP();
		this.fixedVIPNamePrefix = msg.getFixedVIPNamePrefix();
		
		readStringData(msg.getStringData());
	}
	
	// =======================
	// SERIALIZATION UTILITIES
	// =======================
		
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

	public Location[] getCtsSpawnAreas() {
		return ctsSpawnAreas;
	}

	public void setCtsSpawnAreas(Location[] ctsSpawnAreas) {
		this.ctsSpawnAreas = ctsSpawnAreas;
	}

	public Location[] getTsSpawnAreas() {
		return tsSpawnAreas;
	}

	public void setTsSpawnAreas(Location[] tsSpawnAreas) {
		this.tsSpawnAreas = tsSpawnAreas;
	}

	public Location[] getVipSafeAreas() {
		return vipSafeAreas;
	}

	public void setVipSafeAreas(Location[] vipSafeAreas) {
		this.vipSafeAreas = vipSafeAreas;
	}

	public int getVipSafeAreaRadius() {
		return vipSafeAreaRadius;
	}

	public void setVipSafeAreaRadius(int vipSafeAreaRadius) {
		this.vipSafeAreaRadius = vipSafeAreaRadius;
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

	public boolean isFixedVIP() {
		return fixedVIP;
	}

	public void setFixedVIP(boolean fixedVIP) {
		this.fixedVIP = fixedVIP;
	}

	public String getFixedVIPNamePrefix() {
		return fixedVIPNamePrefix;
	}

	public void setFixedVIPNamePrefix(String fixedVIPNamePrefix) {
		this.fixedVIPNamePrefix = fixedVIPNamePrefix;
	}

	public int getVipKilledTsScore() {
		return vipKilledTsScore;
	}

	public void setVipKilledTsScore(int vipKilledTsScore) {
		this.vipKilledTsScore = vipKilledTsScore;
	}

	public int getVipKilledCTsScore() {
		return vipKilledCTsScore;
	}

	public void setVipKilledCTsScore(int vipKilledCTsScore) {
		this.vipKilledCTsScore = vipKilledCTsScore;
	}

	public int getVipSafeTsScore() {
		return vipSafeTsScore;
	}

	public void setVipSafeTsScore(int vipSafeTsScore) {
		this.vipSafeTsScore = vipSafeTsScore;
	}

	public int getVipSafeCTsScore() {
		return vipSafeCTsScore;
	}

	public void setVipSafeCTsScore(int vipSafeCTsScore) {
		this.vipSafeCTsScore = vipSafeCTsScore;
	}

	public String writeStringData() {
		String data =  
				vipKilledTsScore + ";"
			+   vipKilledCTsScore + ";"
			+   vipSafeTsScore + ";"
			+   vipSafeCTsScore + ";"
			+   targetMap;
		
		String areas;
		
		areas = "";
		for (Location area : ctsSpawnAreas) {
			if (areas.length() != 0) areas += "#";
			areas += area.toString().replaceAll(";", "|");
		}		
		data += ";" + areas;
		
		areas = "";
		for (Location area : tsSpawnAreas) {
			if (areas.length() != 0) areas += "#";
			areas += area.toString().replaceAll(";", "|");
		}		
		data += ";" + areas;
		
		areas = "";
		for (Location area : vipSafeAreas) {
			if (areas.length() != 0) areas += "#";
			areas += area.toString().replaceAll(";", "|");
		}		
		data += ";" + areas;
		
		return data;
	}
	
	public void readStringData(String stringData) {
		String[] data = stringData.split(";");
		if (data.length != 8) throw new RuntimeException("Invalid string data, does not contain 8 field separated by ';', string: '" + stringData + "'.");
		vipKilledTsScore = Integer.parseInt(data[0]);
		vipKilledCTsScore = Integer.parseInt(data[1]);
		vipSafeTsScore = Integer.parseInt(data[2]);
		vipSafeCTsScore = Integer.parseInt(data[3]);	
		targetMap = data[4];
		
		String[] areas;
		
		areas = data[5].split("#");
		ctsSpawnAreas = new Location[areas.length];	
		for (int i = 0; i < areas.length; ++i) {
			areas[i] = areas[i].replaceAll("\\|", ";");
			ctsSpawnAreas[i] = new Location(areas[i]);
		}
		
		areas = data[6].split("#");
		tsSpawnAreas = new Location[areas.length];	
		for (int i = 0; i < areas.length; ++i) {
			areas[i] = areas[i].replaceAll("\\|", ";");
			tsSpawnAreas[i] = new Location(areas[i]);
		}
		
		areas = data[7].split("#");
		vipSafeAreas = new Location[areas.length];	
		for (int i = 0; i < areas.length; ++i) {
			areas[i] = areas[i].replaceAll("\\|", ";");
			vipSafeAreas[i] = new Location(areas[i]);
		}
	}
	
	public String getCSVHeader() {
		return     "Map;FixedSeeker;FixedSeekerNamePrefix;RoundCount;RoundTimeSecs;CTsSpawnAreas;TsSpawnAreas;VipSafeAreas;VipSafeAreaRadius"
			   + ";VipKilledTsScore;VipKilledCTsScore;VipSafeTsScore;VipSafeCTsScore";
	}
	
	public void formatCSVLine(Formatter writer) {
		writer.format("%s;", targetMap);
		writer.format("%s;", String.valueOf(fixedVIP));
		writer.format("%s;", String.valueOf(fixedVIPNamePrefix));
		writer.format("%d;", roundCount);
		writer.format("%.3f;", roundTimeUT);
		
		String areas;
		
		areas = "";
		for (Location area : ctsSpawnAreas) {
			if (areas.length() != 0) areas += ",";
			areas += area.toString().replaceAll(";", "|");
		}		
		writer.format("%s;", areas);
		
		areas = "";
		for (Location area : tsSpawnAreas) {
			if (areas.length() != 0) areas += ",";
			areas += area.toString().replaceAll(";", "|");
		}		
		writer.format("%s;", areas);
		
		areas = "";
		for (Location area : vipSafeAreas) {
			if (areas.length() != 0) areas += ",";
			areas += area.toString().replaceAll(";", "|");
		}		
		writer.format("%s;", areas);
		
		writer.format("%d;", vipSafeAreaRadius);
		
		writer.format("%d;", vipKilledTsScore);
		writer.format("%d;", vipKilledCTsScore);
		writer.format("%d;", vipSafeTsScore);
		writer.format("%d;", vipSafeCTsScore);		
	} 

}
