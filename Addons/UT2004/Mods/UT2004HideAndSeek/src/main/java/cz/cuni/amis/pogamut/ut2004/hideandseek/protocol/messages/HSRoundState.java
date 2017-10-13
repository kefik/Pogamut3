package cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.messages;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.communication.messages.custom.ControlMessageField;
import cz.cuni.amis.pogamut.ut2004.communication.messages.custom.ControlMessageType;
import cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.HSGameState;

/**
 * Broadcast to notify everybody about current round state.
 * 
 * Being periodically (or when important state change happens) between  
 * @author Jimmy
 *
 */
@ControlMessageType(type="HS_ROUND_STATE")
public class HSRoundState extends HSMessage {
	
	// INTS

	/**
	 * Maps to {@link HSGameState}, use {@link HSGameState#getGameState(int)} to resolve.
	 */
	@ControlMessageField(index=1)
	private Integer gameState;
	
	/**
	 * Number of this round (0-based)
	 */
	@ControlMessageField(index=2)
	private Integer roundNumber;
	
	/**
	 * Number of rounds that are going to be played (additional to this one).
	 */
	@ControlMessageField(index=3)
	private Integer roundLeft;
	
	// DOUBLES
	
	@ControlMessageField(index=1)
	private Double roundTimeLeftUT;
	
	@ControlMessageField(index=2)
	private Double hideTimeLeftUT;
	
	@ControlMessageField(index=3)
	private Double restrictedAreaTimeLeftUT;
	
	// STRINGS
	
	@ControlMessageField(index=1)
	private Location safeArea;
	
	@ControlMessageField(index=2)
	private UnrealId seekerBotId;
		
	public HSRoundState() {
	}

	public Integer getGameState() {
		return gameState;
	}
	
	public HSGameState getGameStateEnum() {
		return gameState == null ? null : HSGameState.getGameState(gameState);
	}

	public void setGameState(Integer gameState) {
		this.gameState = gameState;
	}

	public Integer getRoundNumber() {
		return roundNumber;
	}

	public void setRoundNumber(Integer roundNumber) {
		this.roundNumber = roundNumber;
	}

	public Integer getRoundLeft() {
		return roundLeft;
	}

	public void setRoundLeft(Integer roundLeft) {
		this.roundLeft = roundLeft;
	}

	public Double getRoundTimeLeftUT() {
		return roundTimeLeftUT;
	}

	public void setRoundTimeLeftUT(Double roundTimeLeftUT) {
		this.roundTimeLeftUT = roundTimeLeftUT;
	}

	public Double getHideTimeLeftUT() {
		return hideTimeLeftUT;
	}

	public void setHideTimeLeftUT(Double hideTimeLeftUT) {
		this.hideTimeLeftUT = hideTimeLeftUT;
	}

	public Double getRestrictedAreaTimeLeftUT() {
		return restrictedAreaTimeLeftUT;
	}

	public void setRestrictedAreaTimeLeftUT(Double restrictedAreaTimeLeftUT) {
		this.restrictedAreaTimeLeftUT = restrictedAreaTimeLeftUT;
	}

	public Location getSafeArea() {
		return safeArea;
	}

	public void setSafeArea(Location safeArea) {
		this.safeArea = safeArea;
	}

	public UnrealId getSeekerBotId() {
		return seekerBotId;
	}

	public void setSeekerBotId(UnrealId seekerBotId) {
		this.seekerBotId = seekerBotId;
	}
	
	@Override
	public String toString() {
		return "HSRoundState[...]";
	}


}
