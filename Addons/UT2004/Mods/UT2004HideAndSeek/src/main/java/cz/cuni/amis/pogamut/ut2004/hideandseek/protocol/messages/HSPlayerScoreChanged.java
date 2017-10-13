package cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.messages;

import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.communication.messages.custom.ControlMessageField;
import cz.cuni.amis.pogamut.ut2004.communication.messages.custom.ControlMessageType;
import cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.HSScoreChangeReason;

@ControlMessageType(type="HS_PLAYER_SCORE_CHANGED")
public class HSPlayerScoreChanged extends HSMessage {

	@ControlMessageField(index=1)
	private UnrealId botId;
	
	@ControlMessageField(index=1)
	private Integer score;
	
	/**
	 * Maps to {@link HSScoreChangeReason}, use {@link HSScoreChangeReason#getScoreChangeReason(int)} to resolve.
	 */
	@ControlMessageField(index=2)
	private Integer scoreChangeReason;
	
	public HSPlayerScoreChanged() {
	}

	public UnrealId getBotId() {
		return botId;
	}

	public void setBotId(UnrealId botId) {
		this.botId = botId;
	}

	/**
	 * How many times was this bot TAGGED (negative score).
	 * @return
	 */
	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	public Integer getScoreChangeReason() {
		return scoreChangeReason;
	}

	public HSScoreChangeReason getScoreChangeReasonEnum() {
		return scoreChangeReason == null ? null : HSScoreChangeReason.getScoreChangeReason(scoreChangeReason);
	}
	
	public void setScoreChangeReason(Integer scoreChangeReason) {
		this.scoreChangeReason = scoreChangeReason;
	}
	
	@Override
	public String toString() {
		return "HSPlayerScoreChanged[botId=" + (botId == null ? "null" : botId.getStringId() + ", score=" + score + ", reason=" + getScoreChangeReasonEnum() + "]") ;
	}
	
}
