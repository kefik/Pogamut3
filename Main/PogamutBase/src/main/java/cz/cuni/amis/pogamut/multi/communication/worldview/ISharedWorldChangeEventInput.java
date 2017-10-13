package cz.cuni.amis.pogamut.multi.communication.worldview;

import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldChangeEvent;
import cz.cuni.amis.pogamut.base.component.ISharedComponent;
import cz.cuni.amis.pogamut.base.component.bus.exception.ComponentNotRunningException;
import cz.cuni.amis.pogamut.base.component.bus.exception.ComponentPausedException;

/**
 * General interface for shared components capable of processing events.
 * @author srlok
 *
 */
public interface ISharedWorldChangeEventInput extends ISharedComponent{
	
	public void notify(IWorldChangeEvent event ) throws ComponentNotRunningException, ComponentPausedException;


}
