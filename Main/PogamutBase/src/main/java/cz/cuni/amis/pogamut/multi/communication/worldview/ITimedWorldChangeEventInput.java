package cz.cuni.amis.pogamut.multi.communication.worldview;

import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldChangeEvent;
import cz.cuni.amis.pogamut.base.component.IComponent;
import cz.cuni.amis.pogamut.base.component.bus.exception.ComponentNotRunningException;
import cz.cuni.amis.pogamut.base.component.bus.exception.ComponentPausedException;

public interface ITimedWorldChangeEventInput extends IComponent {
	
	public void notify(IWorldChangeEvent event ) throws ComponentNotRunningException, ComponentPausedException;
	
}
