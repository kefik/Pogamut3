package cz.cuni.amis.pogamut.base.communication.parser.impl.yylex;

import java.io.Reader;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Inject;
import com.google.inject.internal.Nullable;

import cz.cuni.amis.pogamut.base.communication.connection.IWorldReaderProvider;
import cz.cuni.amis.pogamut.base.communication.exception.CommunicationException;
import cz.cuni.amis.pogamut.base.communication.messages.InfoMessage;
import cz.cuni.amis.pogamut.base.communication.parser.IWorldMessageParser;
import cz.cuni.amis.pogamut.base.communication.parser.exception.ParserException;
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
import cz.cuni.amis.utils.token.Token;
import cz.cuni.amis.utils.token.Tokens;

/**
 * TODO!:
 * <p><p>
 * Ignores {@link IComponentControlHelper#startPaused()}, performs {@link IComponentControlHelper#start()} in both start cases.
 * 
 * @author Jimmy
 */
@AgentScoped
public class YylexParser implements IWorldMessageParser {
	
	public static final Token COMPONENT_ID = Tokens.get("Parser");
	
	private IWorldReaderProvider readerProvider;
	
	private Reader reader = null;
	
	private IYylex yylex;
	
	private IAgentLogger agentLogger = null;
	
	private LogCategory log = null;
	
	private ComponentController<IComponent> controller = null;

	private IComponentBus eventBus;
	
	@Inject
	public YylexParser(IWorldReaderProvider readerProvider, IYylex yylex, @Nullable IYylexObserver yylexObserver, IComponentBus eventBus, IAgentLogger logger) throws CommunicationException {
		agentLogger = logger;
		log = agentLogger.getCategory(getComponentId().getToken());
		this.readerProvider = readerProvider;
		this.yylex = yylex;
		this.yylex.setObserver(yylexObserver == null ? new IYylexObserver.LogObserver(logger) : yylexObserver);
		
		this.eventBus = eventBus;
		this.controller = new ComponentController(this, control, eventBus, log, ComponentDependencyType.STARTS_AFTER, readerProvider);
	}
	
	private IComponentControlHelper control = new ComponentControlHelper() {
		
		@Override
		public void startPaused() {
			start();
		}
		
		@Override
		public void start() throws ParserException {
			Reader reader = readerProvider.getReader();
			if (reader == null) throw new ParserException("Can't get reader from " + readerProvider + ", can't start.", log, this); 
			yylex.setReader(reader);
		}
		
	};
	
	@Override
	public Token getComponentId() {
		return COMPONENT_ID;
	}
	
	public Logger getLog() {
		return log;
	}

	@Override
	public InfoMessage parse() throws ComponentNotRunningException, ComponentPausedException, ParserException {
		if (controller.isPaused()) throw new ComponentPausedException(controller.getState().getFlag(), this);
		if (!controller.isRunning()) throw new ComponentNotRunningException(controller.getState().getFlag(), this);
		try {
			InfoMessage parsed = yylex.yylex(); 
			if (log.isLoggable(Level.FINEST)) log.finest("Received: " + parsed);
			return parsed;
		} catch (ComponentPausedException cp) {
			throw cp;
		} catch (ComponentNotRunningException cnr) {
			throw cnr;
		} catch (ParserException p) {
			throw p;
		} catch (Exception e) {
			throw new ParserException("Can't parse next message: " + e.getMessage(), e, log, this);
		}
	}
	
	public String toString() {
		if (this == null) return "YylexParser";
		else return getClass().getSimpleName();
	}
	
}
