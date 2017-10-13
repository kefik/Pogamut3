package cz.cuni.amis.pogamut.ut2004.communication.translator.shared.events;

import java.util.List;

import cz.cuni.amis.pogamut.base.communication.translator.event.WorldEventIdentityWrapper;

/**
 * Event notifying that list of some objects/events was received.
 * @author ik
 */
public class ListObtained<T> extends TranslatorEvent {

	private List<T> list;

	public ListObtained(List<T> list, long simTime) {
		super(simTime);
		this.list = list;
	}

    /**
     * Returns list of received objects.
     * @return
     */
	public List<T> getList() {
		return list;
	}

	@Override
	public String toString() {
		return "ListObtained[list.size() = " + list.size() + "]";
	}

}
