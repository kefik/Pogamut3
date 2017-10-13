package cz.cuni.amis.pogamut.ut2004.analyzer;

import java.util.logging.Level;

import org.junit.After;
import org.junit.Test;

import cz.cuni.amis.pogamut.base.agent.state.level1.IAgentStateDown;
import cz.cuni.amis.pogamut.base.utils.Pogamut;
import cz.cuni.amis.pogamut.ut2004.agent.params.UT2004AgentParameters;
import cz.cuni.amis.pogamut.ut2004.bot.IUT2004Bot;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotController;
import cz.cuni.amis.pogamut.ut2004.bot.params.UT2004BotParameters;
import cz.cuni.amis.pogamut.ut2004.factory.guice.remoteagent.UT2004AnalyzerFactory;
import cz.cuni.amis.pogamut.ut2004.utils.UT2004AnalyzerRunner;
import cz.cuni.amis.pogamut.ut2004.utils.UT2004BotRunner;
import cz.cuni.amis.utils.exception.PogamutInterruptedException;

public class UT2004Test02_UT2004AnalyzerWithBot {

	IUT2004Analyzer analyzer;
	IUT2004Bot bot;
	
	@After
	public void setUp() {
		if (analyzer != null && !(analyzer.getState().getFlag() instanceof IAgentStateDown)) {
			analyzer.stop();
		}
		if (bot != null && !(bot.getState().getFlag() instanceof IAgentStateDown)) {
			bot.stop();
		}
		Pogamut.getPlatform().close();
	}
	
	@Test
    public void test01_AnalyzerWithBot() {
    	UT2004AnalyzerRunner<IUT2004Analyzer, UT2004AnalyzerParameters> analyzerRunner = new UT2004AnalyzerRunner<IUT2004Analyzer, UT2004AnalyzerParameters>(
    		new UT2004AnalyzerFactory(
    			new UT2004AnalyzerModule()
    		)
    	);
    	analyzerRunner.setLogLevel(Level.INFO);
    	UT2004BotRunner<IUT2004Bot, UT2004BotParameters> botRunner = new UT2004BotRunner<IUT2004Bot, UT2004BotParameters>(UT2004BotController.class);
    	
    	
    	analyzer = analyzerRunner.startAgent();
    	System.out.println("Analyzer is running...");
    	        
    	bot = botRunner.startAgent();
    	System.out.println("Bot is running...");    	    	    
    	
    	try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			throw new PogamutInterruptedException(e, this);
		}
		
		if (analyzer.getObservers().size() != 1) {
			System.out.println("ERROR! Observer has not been launched for started bot...");
			throw new RuntimeException("Observer has not been launched for started bot...");
		}
		System.out.println("Observer is present...");
		
		System.out.println("Stopping the bot...");
		try {
			bot.stop();
		} catch (Exception e) {
			System.out.println("ERROR! Could not stop the bot...");
			throw new RuntimeException("Could not stop the bot...", e);
		}
		System.out.println("Bot has been stopped...");
		
		int i = 0;
		while (analyzer.getObservers().size() > 0) {			
			System.out.println("Waiting for the observer to be detached (" + (i+1) + " / 30)...");
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				throw new PogamutInterruptedException(e, this);
			}
			++i;
			if (i > 30) { // 3secs timeout
				break;
			}
		}
			
		if (analyzer.getObservers().size() != 0) {
			System.out.println("ERROR! Observer has not been stopped for the bot...");
			throw new RuntimeException("Observer has not been stopped for the bot...");
		}
		System.out.println("Observer has been detached...");
		
    	System.out.println("---/// TEST OK ///---");        
    }
	
}
