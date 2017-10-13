package cz.cuni.amis.pogamut.base.communication.mediator;

import cz.cuni.amis.pogamut.base.communication.worldview.IWorldChangeEventInput;
import cz.cuni.amis.pogamut.base.component.IComponent;

public interface IMediator extends IComponent {
	
	public void setConsumer(IWorldChangeEventInput consumer);

}