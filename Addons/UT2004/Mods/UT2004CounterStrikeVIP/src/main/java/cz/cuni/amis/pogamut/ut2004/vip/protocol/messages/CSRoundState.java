package cz.cuni.amis.pogamut.ut2004.vip.protocol.messages;

import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.communication.messages.custom.ControlMessageField;
import cz.cuni.amis.pogamut.ut2004.communication.messages.custom.ControlMessageType;
import cz.cuni.amis.pogamut.ut2004.vip.protocol.VIPGameState;

/**
 * Broadcast to notify everybody about current round state.
 * 
 * Being periodically (or when important state change happens) between  
 * @author Jimmy
 *
 */
@ControlMessageType(type="CS_ROUND_STATE")
public class CSRoundState extends CSMessage {
	
	// INTS

	/**
	 * Maps to {@link VIPGameState}, use {@link VIPGameState#getGameState(int)} to resolve.
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
		
	// STRINGS
	
	@ControlMessageField(index=1)
	private UnrealId vipBotId;
		
	public CSRoundState() {
	}

	public Integer getGameState() {
		return gameState;
	}
	
	public VIPGameState getGameStateEnum() {
		return gameState == null ? null : VIPGameState.getGameState(gameState);
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

	public UnrealId getVIPBotId() {
		return vipBotId;
	}

	public void setVIPBotId(UnrealId seekerBotId) {
		this.vipBotId = seekerBotId;
	}
	
	@Override
	public String toString() {
		return "CSRoundState[...]";
	}


}
