package cz.cuni.amis.pogamut.ut2004.communication.translator.shared.events;

import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Spawn;

public class BotFirstSpawned extends Spawn {
	
	public String toString() {
		return "BotFirstSpawned[The bot was spawned for the first time in the environment as a result of INIT command!]";
	}

}

