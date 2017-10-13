package cz.cuni.amis.pogamut.ut2004.communication.translator.shared.events;

import java.util.List;

import cz.cuni.amis.pogamut.base.communication.translator.event.WorldEventIdentityWrapper;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;

public class PlayerListObtained extends TranslatorEvent {

	private List<Player> players;

	public PlayerListObtained(List<Player> list, long simTime) {
		super(simTime);
		this.players = list;
	}

	public List<Player> getPlayers() {
		return players;
	}
	
	@Override
	public String toString() {
		return "PlayerListObtained[players.size() = " + players.size() + "]";
	}
	
	
}