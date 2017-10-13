package cz.cuni.amis.pogamut.ut2004.communication.translator.server;

import java.util.logging.Logger;

import com.google.inject.Inject;

import cz.cuni.amis.fsm.FSM;
import cz.cuni.amis.pogamut.base.communication.messages.InfoMessage;
import cz.cuni.amis.pogamut.base.communication.translator.IWorldMessageTranslator;
import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldChangeEvent;
import cz.cuni.amis.pogamut.base.communication.translator.exception.TranslatorException;
import cz.cuni.amis.pogamut.base.utils.logging.AgentLogger;
import cz.cuni.amis.pogamut.ut2004.communication.translator.IWorldEventQueue;
import cz.cuni.amis.pogamut.ut2004.communication.translator.TranslatorContext;
import cz.cuni.amis.pogamut.ut2004.communication.translator.IWorldEventQueue.Queue;
import cz.cuni.amis.pogamut.ut2004.communication.translator.itemdescriptor.ItemTranslator;
import cz.cuni.amis.pogamut.ut2004.communication.translator.server.state.HelloControlServerExpectedState;

public class ServerFSM implements IWorldMessageTranslator {
	
	private IWorldEventQueue eventQueue = new IWorldEventQueue.Queue();
	
	private TranslatorContext context = null;
	
	private FSM<InfoMessage, TranslatorContext> fsm = null;

	private Logger log;
		
	@Inject
	public ServerFSM(ItemTranslator translator, AgentLogger logger) {
		this.log = logger.getCategory(getClass().getSimpleName());
		context = new TranslatorContext(eventQueue, translator, log);
		fsm = new FSM<InfoMessage, TranslatorContext>(context, HelloControlServerExpectedState.class, log);
	}

	@Override
	public IWorldChangeEvent[] processMessage(InfoMessage message) throws TranslatorException {
		fsm.push(context, message);
		return eventQueue.popEvents();
	}

	@Override
	public void reset() {
		context.reset();
		this.fsm.restart(context);
	}
	
	@Override
	public String toString() {
		return "ServerFSM";
	}
	
}