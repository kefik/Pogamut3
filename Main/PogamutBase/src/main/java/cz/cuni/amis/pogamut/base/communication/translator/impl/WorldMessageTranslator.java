package cz.cuni.amis.pogamut.base.communication.translator.impl;

import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Logger;

import com.google.inject.Inject;

import cz.cuni.amis.pogamut.base.communication.messages.InfoMessage;
import cz.cuni.amis.pogamut.base.communication.parser.IWorldMessageParser;
import cz.cuni.amis.pogamut.base.communication.translator.IWorldMessageTranslator;
import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldChangeEvent;
import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldChangeEventOutput;
import cz.cuni.amis.pogamut.base.communication.translator.exception.TranslatorException;
import cz.cuni.amis.pogamut.base.component.IComponent;
import cz.cuni.amis.pogamut.base.component.bus.IComponentBus;
import cz.cuni.amis.pogamut.base.component.bus.exception.ComponentNotRunningException;
import cz.cuni.amis.pogamut.base.component.bus.exception.ComponentPausedException;
import cz.cuni.amis.pogamut.base.component.controller.ComponentControlHelper;
import cz.cuni.amis.pogamut.base.component.controller.ComponentController;
import cz.cuni.amis.pogamut.base.component.controller.ComponentDependencyType;
import cz.cuni.amis.pogamut.base.component.controller.IComponentControlHelper;
import cz.cuni.amis.pogamut.base.utils.guice.AgentScoped;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.utils.NullCheck;
import cz.cuni.amis.utils.token.Token;
import cz.cuni.amis.utils.token.Tokens;

/**
 * Abstract class for translators between world messages (InfoObjects) and IWorldEvents. 
 * It implements IWorldEventOutput interface meaning
 * it can be given to the mediator as the source for IWorldEvents. It is constructed
 * with parser that should provide parsed messages (translated text messages from the world
 * to the java object wrappers) + IWorldMessageHandler that is given those parsed messages
 * to produce IWorldEvents. It should process those messages into IWorldEvents and return them.
 * <p><p>
 * Ignores {@link IComponentControlHelper#startPaused()}, performs {@link IComponentControlHelper#start()} in both start cases.
 * 
 * @author Jimmy
 */
@AgentScoped
public class WorldMessageTranslator implements IWorldChangeEventOutput {
	
	public static final Token COMPONENT_ID = Tokens.get("WorldMessageTranslator");

	/**
	 * Parser of the world messages, may be used to get additional messages.
	 */
	protected IWorldMessageParser parser = null;
	
	protected LogCategory log = null;
	
	protected IWorldMessageTranslator handler = null;

	private IComponentBus eventBus;

	private ComponentController<IComponent> controller;
	
	@Inject
	public WorldMessageTranslator(IWorldMessageParser parser, IWorldMessageTranslator messageHandler, IComponentBus eventBus, IAgentLogger logger) {
		log = logger.getCategory(getComponentId().getToken());				
		this.parser = parser;
		NullCheck.check(this.parser, "parser");
		this.handler = messageHandler;
		NullCheck.check(this.handler, "handler");
		
		this.eventBus = eventBus;
		this.controller = new ComponentController(this, control, eventBus, log, ComponentDependencyType.STARTS_AFTER, parser);
	}
	
	private IComponentControlHelper control = new ComponentControlHelper() {

		@Override
		public void startPaused() {
			start();
		}
		
		@Override
		public void start() {
			handler.reset();
		}
	};

	@Override
	public Token getComponentId() {
		return COMPONENT_ID;
	}
	
	public Logger getLog() {
		return log;
	}
	
	/**
	 * Method for translating messages into events.
	 * 
	 * @param message
	 * @return
	 * @throws WorldMessageHandlerException
	 */
	protected IWorldChangeEvent[] processMessage(InfoMessage message) throws TranslatorException {
		return handler.processMessage(message);
	}
	
	/**
	 * Method processMessage() may produce more events per message - if it does so, we will store those events
	 * in this queue. If queue is not empty - getEvent() will return events from this queue.
	 */
	private Queue<IWorldChangeEvent> worldEventQueue = new LinkedList<IWorldChangeEvent>();

	@Override
	public synchronized IWorldChangeEvent getEvent() throws ComponentNotRunningException, ComponentPausedException {
		if (controller.isPaused()) throw new ComponentPausedException(controller.getState().getFlag(), this);
		if (!controller.isRunning()) throw new ComponentNotRunningException(controller.getState().getFlag(), this);
		if (worldEventQueue.size() > 0) return worldEventQueue.poll();
		InfoMessage message;
		IWorldChangeEvent[] worldEvents = null;
		while (worldEvents == null || worldEvents.length == 0) {
			message = null;
			while (message == null) {
				message = parser.parse();
			}
			worldEvents = processMessage(message);			
		}
		for (int i = 1; i < worldEvents.length; ++i) {
			worldEventQueue.add(worldEvents[i]);
		}
		return worldEvents[0];
	}
	
	public String toString() {
		if (this == null) return "WorldMessageParser";
		else return getClass().getSimpleName() + "[parser=" + parser + ", handler=" + handler + "]";
	}
	
}
