package cz.cuni.amis.pogamut.ut2004.vip.protocol.messages;

import cz.cuni.amis.pogamut.ut2004.communication.messages.custom.ControlMessageField;
import cz.cuni.amis.pogamut.ut2004.communication.messages.custom.ControlMessageType;
import cz.cuni.amis.pogamut.ut2004.vip.protocol.CSRoundResult;

/**
 * Terrorist (red) team has won the round. 
 * <p><p>
 * Sent before new {@link CSRoundState} is sent and before {@link CSTeamScoreChanged} messages.
 * 
 * @author Jimmy
 *
 */
@ControlMessageType(type="CS_TERRORISTS_WIN")
public class CSTerroristsWin extends CSMessage {
	
	/**
	 * Maps to {@link CSRoundResult}, use {@link CSRoundResult#getRoundResult(int)} to resolve.
	 */
	@ControlMessageField(index=1)
	private Integer roundResult;
	
	public CSTerroristsWin() {
	}
	
	public Integer getRoundResult() {
		return roundResult;
	}

	public CSRoundResult getRoundResultEnum() {
		return roundResult == null ? null : CSRoundResult.getRoundResult(roundResult);
	}
	
	public void setRoundResult(Integer scoreChangeReason) {
		this.roundResult = scoreChangeReason;
	}
	
	public void setRoundResult(CSRoundResult roundResult) {
		this.roundResult = roundResult.number;
	}
	
	@Override
	public String toString() {
		return "CSTerroristsWin[result=" + getRoundResultEnum() + "]";
	}

}
