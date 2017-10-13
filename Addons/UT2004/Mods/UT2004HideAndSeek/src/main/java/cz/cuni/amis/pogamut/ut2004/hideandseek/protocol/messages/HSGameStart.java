package cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.messages;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.communication.messages.custom.ControlMessageField;
import cz.cuni.amis.pogamut.ut2004.communication.messages.custom.ControlMessageType;
import cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.HSGameConfig;

@ControlMessageType(type="HS_GAME_START")
public class HSGameStart extends HSMessage {

	/**
	 * How many rounds are going to be played.
	 */
	@ControlMessageField(index=1)
	private Integer roundCount;
	
	@ControlMessageField(index=2)
	private Integer safeAreaRadius;
	
	@ControlMessageField(index=3)
	private Integer restrictedAreaRadius;
	
	// DOUBLES
	
	/**
	 * Total number of seconds the round is going to be played.
	 */
	@ControlMessageField(index=1)
	private Double roundTimeUT;
	
	/**
	 * How much time will runners get to hide.
	 */
	@ControlMessageField(index=2)
	private Double hideTimeUT;
	
	/**
	 * Time during which runners are not allowed to enter restricted area.
	 */
	@ControlMessageField(index=3)
	private Double restrictedAreaTimeUT;
	
	// STRINGS
		
	/**
	 * Position of the "safe" area.
	 */
	@ControlMessageField(index=1)
	private Location safeArea;
	
	/**
	 * Serialization of (mainly) scoring data, filled/read by {@link HSGameConfig}.
	 */
	@ControlMessageField(index=2)
	private String scoring;
	
	@ControlMessageField(index=3)
	private String fixedSeekerName;
	
	// BOOLEANS
	
	@ControlMessageField(index=1)
	private Boolean fixedSeeker;
	
	public HSGameStart() {
	}
	
	public HSGameStart(HSGameConfig config) {
		this.hideTimeUT = config.getHideTimeUT();
		this.restrictedAreaRadius = config.getRestrictedAreaRadius();
		this.restrictedAreaTimeUT = config.getRestrictedAreaTimeSecs();
		this.roundCount = config.getRoundCount();
		this.roundTimeUT = config.getRoundTimeUT();
		this.safeArea = config.getSafeArea();
		this.safeAreaRadius = config.getSafeAreaRadius();
		this.fixedSeeker = config.isFixedSeeker();
		this.fixedSeekerName = config.getFixedSeekerName();
		this.scoring = config.writeScoring();		
	}

	public Integer getRoundCount() {
		return roundCount;
	}

	public void setRoundCount(Integer roundCount) {
		this.roundCount = roundCount;
	}

	public Integer getSafeAreaRadius() {
		return safeAreaRadius;
	}

	public void setSafeAreaRadius(Integer safeAreaRadius) {
		this.safeAreaRadius = safeAreaRadius;
	}

	public Integer getRestrictedAreaRadius() {
		return restrictedAreaRadius;
	}

	public void setRestrictedAreaRadius(Integer restrictedAreaRadius) {
		this.restrictedAreaRadius = restrictedAreaRadius;
	}

	public Double getRoundTimeUT() {
		return roundTimeUT;
	}

	public void setRoundTimeUT(Double roundTimeUT) {
		this.roundTimeUT = roundTimeUT;
	}

	public Double getHideTimeUT() {
		return hideTimeUT;
	}

	public void setHideTimeUT(Double hideTimeUT) {
		this.hideTimeUT = hideTimeUT;
	}

	public Double getRestrictedAreaTimeUT() {
		return restrictedAreaTimeUT;
	}

	public void setRestrictedAreaTimeUT(Double restrictedAreaTimeUT) {
		this.restrictedAreaTimeUT = restrictedAreaTimeUT;
	}

	public Location getSafeArea() {
		return safeArea;
	}

	public void setSafeArea(Location safeArea) {
		this.safeArea = safeArea;
	}

	public String getScoring() {
		return scoring;
	}

	public void setScoring(String scoring) {
		this.scoring = scoring;
	}

	public String getFixedSeekerName() {
		return fixedSeekerName;
	}

	public void setFixedSeekerName(String fixedSeekerName) {
		this.fixedSeekerName = fixedSeekerName;
	}

	public Boolean getFixedSeeker() {
		return fixedSeeker;
	}

	public void setFixedSeeker(Boolean fixedSeeker) {
		this.fixedSeeker = fixedSeeker;
	}	
	
	@Override
	public String toString() {
		return "HSGameStart[...]";
	}
	
}
