package cz.cuni.amis.pogamut.ut2004.communication.parser;


import com.google.inject.Inject;
import com.google.inject.internal.Nullable;

import cz.cuni.amis.pogamut.base.communication.connection.IWorldReaderProvider;
import cz.cuni.amis.pogamut.base.communication.exception.CommunicationException;
import cz.cuni.amis.pogamut.base.communication.parser.impl.yylex.IYylexObserver;
import cz.cuni.amis.pogamut.base.communication.parser.impl.yylex.YylexParser;
import cz.cuni.amis.pogamut.base.component.bus.IComponentBus;
import cz.cuni.amis.pogamut.base.utils.guice.AgentScoped;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.pogamut.multi.agent.ITeamId;
import cz.cuni.amis.pogamut.multi.agent.impl.TeamedAgentId;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemTypeTranslator;
import cz.cuni.amis.pogamut.ut2004.communication.messages.UnrealIdTranslator;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ItemPickedUp;
import cz.cuni.amis.pogamut.ut2004.communication.translator.itemdescriptor.ItemTranslator;
import cz.cuni.amis.utils.NullCheck;

@AgentScoped
public class UT2004Parser extends YylexParser {
	
	@Inject
	public UT2004Parser(UnrealIdTranslator unrealIdTranslator, ItemTranslator itemTranslator, ItemTypeTranslator itemTypeTranslator,
					    IWorldReaderProvider readerProvider, IUT2004Yylex yylex, @Nullable IYylexObserver yylexObserver, 
			            IComponentBus bus, IAgentLogger logger) throws CommunicationException {
		super(readerProvider, yylex, yylexObserver, bus, logger);
		
		NullCheck.check(unrealIdTranslator, "unrealIdTranslator");
		
		yylex.setTranslator(unrealIdTranslator);
		
		NullCheck.check(itemTranslator, "itemTranslator");
		
		yylex.setItemTranslator(itemTranslator);
		
		
		NullCheck.check(itemTypeTranslator, "itemTypeTranslator");
		
		yylex.setItemTypeTranslator(itemTypeTranslator);
		
		if (logger.getAgentId() instanceof TeamedAgentId) {
			yylex.setTeamId(((TeamedAgentId)logger.getAgentId()).getTeamId());
		}
	}
	
//	@Override
//	public InfoMessage parse() throws ParserException {
//		log.warning("going to parse message");
//		InfoMessage msg = super.parse();
//		log.warning("Message: " + msg);
//		return msg;
//	}

}
