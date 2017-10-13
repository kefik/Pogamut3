package cz.cuni.amis.pogamut.ut2004.communication.parser;

import java.util.logging.Level;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;

import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.agent.impl.AgentId;
import cz.cuni.amis.pogamut.base.communication.parser.exception.ParserEOFException;
import cz.cuni.amis.pogamut.base.component.bus.ComponentBus;
import cz.cuni.amis.pogamut.base.component.bus.IComponentBus;
import cz.cuni.amis.pogamut.base.utils.logging.AgentLogger;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemTypeTranslator;
import cz.cuni.amis.pogamut.ut2004.communication.messages.UT2004ItemTypeTranslator;
import cz.cuni.amis.pogamut.ut2004.communication.messages.UnrealIdTranslator;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InfoMessages;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Yylex;
import cz.cuni.amis.pogamut.ut2004.communication.translator.itemdescriptor.ItemTranslator;
import cz.cuni.amis.tests.BaseTest;
				
@Ignore
public class Test01_UT2004Parser extends BaseTest {

	@Test
	public void test01_Parser() {
		
		IAgentId agentId = new AgentId("Test01_UT2004Parser");
		IAgentLogger logger = new AgentLogger(agentId);
		logger.addDefaultConsoleHandler();
		logger.setLevel(Level.ALL);
		IComponentBus bus = new ComponentBus(logger);
		LogCategory log = logger.getCategory("Test");
				
		ReaderProvider readerProvider = new ReaderProvider(logger, bus);
		
		YylexObserver yylexObserver = new YylexObserver();
		
        ItemTypeTranslator itemTypeTranslator  = new UT2004ItemTypeTranslator();
        ItemTranslator itemTranslator = new ItemTranslator(itemTypeTranslator);
        
		UT2004Parser parser = new UT2004Parser(new UnrealIdTranslator(), itemTranslator, itemTypeTranslator, readerProvider, new Yylex(), yylexObserver, bus, logger);
		
		readerProvider.getController().manualStart("Starting test...");
		
		for (int i = 0; i < InfoMessages.PROTOTYPES.length-1; ++i) {
			if (log.isLoggable(Level.INFO)) log.info("Parsed: " + parser.parse().toString());
		}
		
		boolean exception = false;
		try {
			parser.parse();
		} catch (ParserEOFException e) {
			exception = true;
		}
		
		Assert.assertTrue("parser should throw exception when EOF is encountered", exception);
		
		readerProvider.getController().manualStop("Stopping test...");
		
		System.out.println("---/// TEST OK ///---");
		
	}
	
	
	

}
