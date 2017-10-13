package cz.cuni.amis.pogamut.base.communication.mediator;

import cz.cuni.amis.pogamut.base.communication.exception.CommunicationException;
import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldChangeEvent;
import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldChangeEventOutput;
import cz.cuni.amis.pogamut.base.component.bus.IComponentBus;
import cz.cuni.amis.pogamut.base.component.bus.exception.ComponentNotRunningException;
import cz.cuni.amis.pogamut.base.component.stub.component.ComponentStub;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;

public class MockMediatorOutput extends ComponentStub implements IWorldChangeEventOutput {

	private IWorldChangeEventOutput mock;

	public MockMediatorOutput(IAgentLogger logger, IComponentBus bus, IWorldChangeEventOutput mock) {
		super(logger, bus);
		this.mock = mock;
	}

	@Override
	public IWorldChangeEvent getEvent() throws CommunicationException, ComponentNotRunningException {
		return mock.getEvent();
	}

}
