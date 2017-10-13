package cz.cuni.amis.pogamut.base.communication.translator.event;

import cz.cuni.amis.pogamut.base.communication.translator.IWorldMessageTranslator;
import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.utils.listener.Event;

/**
 * Marker interface for every world event that can be sensed from the world.
 * <p><p>
 * That means the events between {@link IWorldMessageTranslator} (it outputs them) and {@link IWorldView} (it accepts them).
 * 
 * @author Jimmy4
 * @author srlok
 */
public interface IWorldChangeEvent extends Event {

	/**
	 * Time when the change has happened inside the environment.
	 *  
	 * @return timestamp
	 */
	public long getSimTime();
	
}
