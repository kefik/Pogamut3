package cz.cuni.amis.pogamut.ut2004.bot.impl.test;

import java.util.logging.Level;

import cz.cuni.amis.pogamut.base.communication.command.IAct;
import cz.cuni.amis.pogamut.base.communication.command.ICommandSerializer;
import cz.cuni.amis.pogamut.base.communication.command.impl.Act;
import cz.cuni.amis.pogamut.base.communication.command.impl.StringCommandSerializer;
import cz.cuni.amis.pogamut.base.communication.connection.impl.socket.ISocketConnectionAddress;
import cz.cuni.amis.pogamut.base.communication.connection.impl.socket.SocketConnection;
import cz.cuni.amis.pogamut.base.communication.mediator.IMediator;
import cz.cuni.amis.pogamut.base.communication.mediator.impl.Mediator;
import cz.cuni.amis.pogamut.base.communication.parser.IWorldMessageParser;
import cz.cuni.amis.pogamut.base.communication.parser.impl.yylex.IYylexObserver;
import cz.cuni.amis.pogamut.base.communication.translator.IWorldMessageTranslator;
import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldChangeEventOutput;
import cz.cuni.amis.pogamut.base.communication.translator.impl.WorldMessageTranslator;
import cz.cuni.amis.pogamut.base.component.bus.ComponentBus;
import cz.cuni.amis.pogamut.base.component.bus.IComponentBus;
import cz.cuni.amis.pogamut.base.component.controller.ComponentDependencies;
import cz.cuni.amis.pogamut.base.component.controller.ComponentDependencyType;
import cz.cuni.amis.pogamut.base.factory.IAgentFactory;
import cz.cuni.amis.pogamut.base.utils.logging.AgentLogger;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotLogicController;
import cz.cuni.amis.pogamut.ut2004.bot.params.UT2004BotParameters;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemTypeTranslator;
import cz.cuni.amis.pogamut.ut2004.communication.messages.UT2004ItemTypeTranslator;
import cz.cuni.amis.pogamut.ut2004.communication.messages.UnrealIdTranslator;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ItemPickedUp;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Yylex;
import cz.cuni.amis.pogamut.ut2004.communication.parser.UT2004Parser;
import cz.cuni.amis.pogamut.ut2004.communication.translator.bot.BotFSM;
import cz.cuni.amis.pogamut.ut2004.communication.translator.itemdescriptor.ItemTranslator;
import cz.cuni.amis.pogamut.ut2004.communication.worldview.UT2004SyncLockableWorldView;
import cz.cuni.amis.utils.exception.PogamutException;

public class LeakTestBotFactory implements IAgentFactory<LeakTestBot, UT2004BotParameters> {

	@Override
	public LeakTestBot newAgent(UT2004BotParameters agentParameters) throws PogamutException {
		// setup loger
        AgentLogger logger = new AgentLogger(agentParameters.getAgentId());

        // Since default agent logger doesn't write anything, platform can be exposed to silent fail
        // i.e. RuntimeException that is thrown, something happens and whole thing stops working
        // without user knowing what the error message was.
        logger.setLevel(Level.SEVERE);
        logger.addDefaultConsoleHandler();
        
        IComponentBus eventBus = new ComponentBus(logger);

        ///////////////////////////////
        ///      WORLD -> AGENT     ///
        ///////////////////////////////
        
        // create connection to the world
        SocketConnection socketConnection = new SocketConnection((ISocketConnectionAddress)agentParameters.getWorldAddress(), new ComponentDependencies(ComponentDependencyType.STARTS_WITH).add(agentParameters.getAgentId()), eventBus, logger);
        
        UnrealIdTranslator unrealIdTranslator = new UnrealIdTranslator();
        ItemTypeTranslator itemTypeTranslator  = new UT2004ItemTypeTranslator();
        ItemTranslator itemTranslator = new ItemTranslator(itemTypeTranslator);
        
        // parser for translating text messages into Java objects
        IWorldMessageParser parser = new UT2004Parser(unrealIdTranslator, itemTranslator,itemTypeTranslator, socketConnection, new Yylex(), new IYylexObserver.LogObserver(logger), eventBus, logger);

        // translates sets of messages wrapped in Java objects into agregared messages
        IWorldMessageTranslator messageTranslator = new BotFSM(itemTranslator, logger);
        IWorldChangeEventOutput producer = new WorldMessageTranslator(parser, messageTranslator, eventBus, logger);

        IMediator mediator = new Mediator(producer, eventBus, logger);
        
        UT2004SyncLockableWorldView worldView = new UT2004SyncLockableWorldView(new ComponentDependencies(ComponentDependencyType.STARTS_WITH).add(agentParameters.getAgentId()), mediator, eventBus, logger);
        
        ///////////////////////////////
        ///      AGENT -> WORLD     ///
        ///////////////////////////////

        ICommandSerializer<String> commandSerializer = new StringCommandSerializer();
        IAct act = new Act(socketConnection, commandSerializer, eventBus, logger);
        
        ///////////////////////////////
        ///   AGENT INITIALIZATION  ///
        ///////////////////////////////
        
        LeakTestBot bot = new LeakTestBot(agentParameters, eventBus, logger, worldView, act, new UT2004BotLogicController());        
        return bot;
	}

}
