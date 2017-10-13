package cz.cuni.amis.pogamut.ut2004.tag.protocol.messages;

import cz.cuni.amis.pogamut.ut2004.communication.messages.custom.ControlMessageField;
import cz.cuni.amis.pogamut.ut2004.communication.messages.custom.ControlMessageType;

@ControlMessageType(type="TAG_GAME_RUNNING")
public class TagGameRunning extends TagMessage {

	@ControlMessageField(index=1)
	private Integer gameTimeUT;
	
	@ControlMessageField(index=2)
	private Integer gameMaxScore;
	
	@ControlMessageField(index=1)
	private Double tagPassDistance;
	
	public TagGameRunning() {
	}

	public Integer getGameTimeUT() {
		return gameTimeUT;
	}

	public void setGameTimeUT(Integer gameTimeUT) {
		this.gameTimeUT = gameTimeUT;
	}
	
	public Integer getGameMaxScore() {
		return gameMaxScore;
	}

	public void setGameMaxScore(Integer gameMaxScore) {
		this.gameMaxScore = gameMaxScore;
	}

	public Double getTagPassDistance() {
		return tagPassDistance;
	}

	public void setTagPassDistance(Double tagPassDistance) {
		this.tagPassDistance = tagPassDistance;
	}
	
}
