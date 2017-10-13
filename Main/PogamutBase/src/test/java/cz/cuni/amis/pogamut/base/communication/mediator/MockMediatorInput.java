package cz.cuni.amis.pogamut.base.communication.mediator;

import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldChangeEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.IWorldChangeEventInput;
import cz.cuni.amis.pogamut.base.component.bus.IComponentBus;
import cz.cuni.amis.pogamut.base.component.bus.exception.ComponentNotRunningException;
import cz.cuni.amis.pogamut.base.component.bus.exception.ComponentPausedException;
import cz.cuni.amis.pogamut.base.component.stub.component.ComponentStub;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;

public class MockMediatorInput extends ComponentStub implements IWorldChangeEventInput {

	private IWorldChangeEventInput mock;


	public MockMediatorInput(IAgentLogger logger, IComponentBus bus, IWorldChangeEventInput mock) {
		super(logger, bus);
		this.mock = mock;
	}
	
	
	@Override
	public void notify(IWorldChangeEvent event)
			throws ComponentNotRunningException {
		mock.notify(event);
	}


	@Override
	public void notifyImmediately(IWorldChangeEvent event)
			throws ComponentNotRunningException, ComponentPausedException {
		mock.notifyImmediately(event);
	}


	@Override
	public void notifyAfterPropagation(IWorldChangeEvent event)
			throws ComponentNotRunningException, ComponentPausedException {
		mock.notifyAfterPropagation(event);
	}

}
