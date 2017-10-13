package cz.cuni.amis.pogamut.ut2004.communication.translator.shared.events;

import java.util.List;

import cz.cuni.amis.pogamut.base.communication.translator.event.WorldEventIdentityWrapper;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Mover;

public class MoverListObtained  extends TranslatorEvent {

	private List<Mover> movers;

	public MoverListObtained(List<Mover> list, long simTime) {
		super(simTime);
		this.movers = list;
	}

	public List<Mover> getMovers() {
		return movers;
	}

	public String toString() {
		return "MoverListObtained[movers.size() = " + movers.size() + "]";
	}

}
