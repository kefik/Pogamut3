package cz.cuni.amis.pogamut.ut2004.vip.protocol.messages;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.communication.messages.custom.ControlMessageField;
import cz.cuni.amis.pogamut.ut2004.communication.messages.custom.ControlMessageType;
import cz.cuni.amis.pogamut.ut2004.vip.protocol.VIPGameConfig;

@ControlMessageType(type="VIP_GAME_START")
public class VIPGameStart extends CSMessage {

	/**
	 * How many rounds are going to be played. 
	 * <= 0 ~ infinite.
	 */
	@ControlMessageField(index=1)
	private Integer roundCount;
	
	@ControlMessageField(index=2)
	private Integer vipSafeAreaRadius;
	
	// DOUBLES
	
	/**
	 * Total number of seconds each round is going to be played for.
	 */
	@ControlMessageField(index=1)
	private Double roundTimeUT;
		
	// STRINGS
		
	@ControlMessageField(index=1)
	private String fixedVIPNamePrefix;
	
	/**
	 * Serialization of (mainly) scoring data, filled/read by {@link VIPGameConfig}.
	 */
	@ControlMessageField(index=2)
	private String stringData;
	
	// BOOLEANS
	
	@ControlMessageField(index=1)
	private Boolean fixedVIP;
	
	public VIPGameStart() {
	}
	
	public VIPGameStart(VIPGameConfig config) {
		this.roundCount = config.getRoundCount();
		this.roundTimeUT = config.getRoundTimeUT();
		this.vipSafeAreaRadius = config.getVipSafeAreaRadius();
		this.fixedVIP = config.isFixedVIP();
		this.fixedVIPNamePrefix = config.getFixedVIPNamePrefix();
		this.stringData = config.writeStringData();		
	}

	public Integer getRoundCount() {
		return roundCount;
	}

	public void setRoundCount(Integer roundCount) {
		this.roundCount = roundCount;
	}

	public Integer getVipSafeAreaRadius() {
		return vipSafeAreaRadius;
	}

	public void setVipSafeAreaRadius(Integer vipSafeAreaRadius) {
		this.vipSafeAreaRadius = vipSafeAreaRadius;
	}

	public Double getRoundTimeUT() {
		return roundTimeUT;
	}

	public void setRoundTimeUT(Double roundTimeUT) {
		this.roundTimeUT = roundTimeUT;
	}

	public String getFixedVIPNamePrefix() {
		return fixedVIPNamePrefix;
	}

	public void setFixedVIPNamePrefix(String fixedVIPNamePrefix) {
		this.fixedVIPNamePrefix = fixedVIPNamePrefix;
	}

	public String getStringData() {
		return stringData;
	}

	public void setStringData(String stringData) {
		this.stringData = stringData;
	}

	public Boolean getFixedVIP() {
		return fixedVIP;
	}

	public void setFixedVIP(Boolean fixedVIP) {
		this.fixedVIP = fixedVIP;
	}

	@Override
	public String toString() {
		return "VIPGameStart[...]";
	}
	
}
