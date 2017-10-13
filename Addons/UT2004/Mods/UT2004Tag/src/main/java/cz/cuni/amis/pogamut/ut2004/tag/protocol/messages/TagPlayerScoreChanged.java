package cz.cuni.amis.pogamut.ut2004.tag.protocol.messages;

import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.communication.messages.custom.ControlMessageField;
import cz.cuni.amis.pogamut.ut2004.communication.messages.custom.ControlMessageType;

@ControlMessageType(type="TAG_PLAYER_SCORE_CHANGED")
public class TagPlayerScoreChanged extends TagMessage {

	@ControlMessageField(index=1)
	private UnrealId botId;
	
	@ControlMessageField(index=1)
	private Integer score;
	
	public TagPlayerScoreChanged() {
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

}
