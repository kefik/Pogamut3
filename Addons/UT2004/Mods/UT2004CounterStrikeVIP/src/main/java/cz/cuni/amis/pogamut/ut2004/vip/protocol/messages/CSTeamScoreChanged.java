package cz.cuni.amis.pogamut.ut2004.vip.protocol.messages;

import cz.cuni.amis.pogamut.ut2004.communication.messages.custom.ControlMessageField;
import cz.cuni.amis.pogamut.ut2004.communication.messages.custom.ControlMessageType;
import cz.cuni.amis.pogamut.ut2004.vip.protocol.CSRoundResult;

@ControlMessageType(type="CS_TEAM_SCORE_CHANGED")
public class CSTeamScoreChanged extends CSMessage {

	@ControlMessageField(index=1)
	private Integer ut2004Team;
	
	@ControlMessageField(index=2)
	private Integer score;
	
	/**
	 * Maps to {@link CSRoundResult}, use {@link CSRoundResult#getRoundResult(int)} to resolve.
	 */
	@ControlMessageField(index=3)
	private Integer roundResult;
	
	public CSTeamScoreChanged() {
	}

	public Integer getUt2004Team() {
		return ut2004Team;
	}

	public void setUt2004Team(Integer ut2004Team) {
		this.ut2004Team = ut2004Team;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
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
		return "CSTeamScoreChanged[ut2004Team=" + (ut2004Team == null ? "null" : ut2004Team + ", score=" + score + ", result=" + getRoundResultEnum() + "]") ;
	}
	
}
