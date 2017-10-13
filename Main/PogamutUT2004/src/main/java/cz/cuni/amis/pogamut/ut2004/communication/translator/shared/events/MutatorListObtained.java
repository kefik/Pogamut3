package cz.cuni.amis.pogamut.ut2004.communication.translator.shared.events;

import java.util.List;

import cz.cuni.amis.pogamut.base.communication.translator.event.WorldEventIdentityWrapper;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Mutator;

public class MutatorListObtained  extends TranslatorEvent {

	private List<Mutator> mutators;

	public MutatorListObtained(List<Mutator> list, long simTime) {
		super(simTime);
		this.mutators = list;
	}

	public List<Mutator> getMutators() {
		return mutators;
	}
	
	@Override
	public String toString() {
		return "MutatorListObtained[mutators# = " + mutators.size() + "]";
	}
	
}
