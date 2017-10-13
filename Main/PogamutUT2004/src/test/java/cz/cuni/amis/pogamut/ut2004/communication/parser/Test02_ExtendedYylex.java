/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.amis.pogamut.ut2004.communication.parser;

import java.io.Reader;
import java.io.StringReader;
import java.util.logging.Level;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;

import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.agent.impl.AgentId;
import cz.cuni.amis.pogamut.base.communication.connection.IWorldReaderProvider;
import cz.cuni.amis.pogamut.base.communication.connection.WorldReader;
import cz.cuni.amis.pogamut.base.communication.exception.CommunicationException;
import cz.cuni.amis.pogamut.base.communication.messages.InfoMessage;
import cz.cuni.amis.pogamut.base.component.bus.ComponentBus;
import cz.cuni.amis.pogamut.base.component.bus.IComponentBus;
import cz.cuni.amis.pogamut.base.utils.logging.AgentLogger;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemTypeTranslator;
import cz.cuni.amis.pogamut.ut2004.communication.messages.UT2004ItemTypeTranslator;
import cz.cuni.amis.pogamut.ut2004.communication.messages.UnrealIdTranslator;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Yylex;
import cz.cuni.amis.pogamut.ut2004.communication.translator.itemdescriptor.ItemTranslator;
import cz.cuni.amis.pogamut.ut2004.component.ComponentStub;
import cz.cuni.amis.tests.BaseTest;
import cz.cuni.amis.utils.Const;
import cz.cuni.amis.utils.token.IToken;
import cz.cuni.amis.utils.token.Tokens;


/**
 *
 * @author Martin Cerny
 */
@Ignore
public class Test02_ExtendedYylex extends BaseTest{
    
    private static final int intParamValue = 123;
    
    @Test
    public void test01_ExtendedYylex() {

        IAgentId agentId = new AgentId("Test01_ExtendedYylex");
        IAgentLogger logger = new AgentLogger(agentId);
        logger.addDefaultConsoleHandler();
        logger.setLevel(Level.ALL);
        IComponentBus bus = new ComponentBus(logger);
        LogCategory log = logger.getCategory("Test");

        AdditionalMessageReaderProvider readerProvider = new AdditionalMessageReaderProvider(logger, bus);

        YylexObserver yylexObserver = new YylexObserver();
        ItemTypeTranslator itemTypeTranslator  = new UT2004ItemTypeTranslator();
        ItemTranslator itemTranslator = new ItemTranslator(itemTypeTranslator);
        UT2004Parser parser = new UT2004Parser(new UnrealIdTranslator(), itemTranslator, itemTypeTranslator, readerProvider, new ExtendedYylex(), yylexObserver, bus, logger);

        readerProvider.getController().manualStart("Starting test...");


        AdditionalMessage msg = (AdditionalMessage)parser.parse();
        
        Assert.assertTrue("Param value was not parsed correctly", msg.getIntParam() == intParamValue);


        readerProvider.getController().manualStop("Stopping test...");

        System.out.println("---/// TEST OK ///---");

    }
    
    
    private class ExtendedYylex extends Yylex {

        @Override
        protected InfoMessage tryParsingUnprocessedMessage(String messageName) {
            if(messageName.equals("AdditionalMessage")){
                return new AdditionalMessage();
            }
            return super.tryParsingUnprocessedMessage(messageName);
        }

        @Override
        protected boolean tryParsingUnprocessedMessageParameter(String paramName, String wholeParamText) {
            if ( actObj instanceof AdditionalMessage){
                if(paramName.equals("intParam")){
                    ((AdditionalMessage)actObj).setIntParam(intValue(wholeParamText));
                    return true;
                }
            }
            return super.tryParsingUnprocessedMessageParameter(paramName, wholeParamText);
        }
        
    }
    
    private class AdditionalMessage extends InfoMessage {

        private int intParam;

        public int getIntParam() {
            return intParam;
        }

        public void setIntParam(int intParam) {
            this.intParam = intParam;
        }

        public AdditionalMessage() {
        }

        @Override
        public String toString() {
            return "AdditionalMessage{" + "intParam=" + intParam + '}';
        }
    }

public class AdditionalMessageReaderProvider extends ComponentStub implements IWorldReaderProvider {

	private Reader reader;
	
	public AdditionalMessageReaderProvider(IAgentLogger logger, IComponentBus bus) {
		super(logger, bus);
		this.reader = new StringReader("AdditionalMessage {intParam " + intParamValue + "}" + Const.NEW_LINE);
	}
	
	@Override
	public WorldReader getReader() throws CommunicationException {
		return new WorldReader.WorldReaderWrapper(reader);	}

	@Override
	public IToken getComponentId() {
		return Tokens.get("ReaderProvider");
	}

}
    
        
}
