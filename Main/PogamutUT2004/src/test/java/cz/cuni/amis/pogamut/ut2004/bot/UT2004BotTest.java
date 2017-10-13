package cz.cuni.amis.pogamut.ut2004.bot;

import java.util.concurrent.TimeUnit;

import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.bot.params.UT2004BotParameters;
import cz.cuni.amis.pogamut.ut2004.server.exception.UCCStartException;
import cz.cuni.amis.pogamut.ut2004.test.UT2004Test;
import cz.cuni.amis.pogamut.ut2004.utils.UCCWrapperConf;
import cz.cuni.amis.utils.StopWatch;

/**
 * UT2004Bot test - it allows you to easily specify the game type and map name the bot should run 
 * at + specify a timeout for the test.
 * <p><p>
 * Just use one of the {@link UT2004BotTest#startTest(Class)} methods 
 * 
 * @author Jimmy
 */
public abstract class UT2004BotTest extends UT2004Test {

	/**
     * Starts UCC server according to params obtained from {@link UT2004BotTest#getGameType()} and
     * {@link UT2004BotTest#getMapName()}.
     *
     * @throws cz.cuni.amis.pogamut.ut2004.server.exceptions.UCCStartException
     */
    @Override
	public void startUCC(UCCWrapperConf uccConf) throws UCCStartException {
        uccConf.setMapName(getMapName());
        uccConf.setGameType(getGameType());
        super.startUCC(uccConf);
    }
    
    /**
     * @return game type that the UCC should start for the test
     */
    protected String getGameType() {
    	return "BotDeathMatch";
    }
    
    /**
     * @return the map the UCC should load for the test
     */
    protected String getMapName() {
    	return "DM-Flux2";
    }
    
    /**
     * Starts the test.
     * <p><p>
     * Test bot will be controlled by 'controllerClass'.
     * <p><p>
     * The test will timeout after 1 minutes.
     * 
     * @param controllerClass
     */
    protected void startTest(Class<? extends UT2004BotTestController> controllerClass) {
		startTest(controllerClass, 1);
    }
        
    /**
     * Starts the test.
     * <p><p>
     * Test bot will be controlled by 'controllerClass'.
     * <p><p>
     * The test will timeout after 'latchWaitMinutes'.
     * 
     * @param controllerClass
     * @param latchWaitMinutes
     */
	protected void startTest(Class<? extends UT2004BotTestController> controllerClass, double latchWaitMinutes) {
    	startTest(controllerClass, latchWaitMinutes, null);
	}
	
	/**
     * Starts the test.
     * <p><p>
     * Test bot will be controlled by 'controllerClass'.
     * <p><p>
     * The test will timeout after 'latchWaitMinutes'.
     * <p><p>
     * The test will lauch 'agentsCount' of bots of 'controllerClass' creating empty {@link UT2004BotParameters} for every one of them.
     * 
     * @param controllerClass
     * @param latchWaitMinutes
     * @param agentsCount
     */
	protected void startTest(Class<? extends UT2004BotTestController> controllerClass, double latchWaitMinutes, int agentsCount) {
		UT2004BotParameters[] params = new UT2004BotParameters[agentsCount];
		for (int i = 0; i < agentsCount; ++i) {
			params[i] = new UT2004BotParameters();
		}
    	startTest(controllerClass, latchWaitMinutes, params);
	}
	
	/**
	 * Starts the test.
     * <p><p>
     * Test bot will be controlled by 'controllerClass' and will be configured with 'params.
     * <p><p>
     * The test will timeout after 'latchWaitMinutes'.
     * 
     * TODO: [Jimmy] in the case of more bots, we should hook listeners to failure flags of bots to end the test case as early as possible
     * 
	 * @param controllerClass
	 * @param params
	 * @param latchWaitMinutes
	 */
    protected void startTest(Class<? extends UT2004BotTestController> controllerClass, double latchWaitMinutes, UT2004BotParameters... params) {
    	int numParams = (params == null || params.length == 0 ? 0 : params.length);
    	int numBots = numParams == 0 ? 1 : numParams;
    	
    	UT2004Bot bots[];
    	
    	if (params == null || params.length == 0) {
    		bots = new UT2004Bot[1];
    		bots[0] = startUTBot(controllerClass);
    	} else {
    		bots = startAllUTBots(controllerClass, params).toArray(new UT2004Bot[0]);
    	}
    	
    	long timeoutInMillis = (long)(latchWaitMinutes * 60 * 1000);
    	
    	StopWatch watches = new StopWatch();
    	
    	for (int i = 0; i < numBots; ++i) {
    		UT2004BotTestController controller = (UT2004BotTestController) bots[i].getController();
    		try {
    			controller.getTestLatch().await((long)(timeoutInMillis - watches.check()), TimeUnit.MILLISECONDS);
    		} catch (Exception ex) {
    			ex.printStackTrace();
    			for (int j = i; j < numBots; ++j) bots[j].kill();
    			Throwable throwable = controller.getLogicModule().getLogicException();
    			throw new RuntimeException("Test failed, bot did not finished.", throwable);
    		}
    		if (timeoutInMillis - watches.check() < 0) {
    			controller.timeout();
    		}
    		bots[i].kill();
    		if (controller.isFailure()) {
    			for (int j = i+1; j < numBots; ++j) bots[j].kill();
    			if (controller.getMessage() != null) {
    				System.out.println("[ERROR] " + controller.getMessage());
    			}
    			
				String exceptionMessage = "Test has failed!";
    			if(controller.getMessage() == null)
    				exceptionMessage += " " + controller.getMessage();
				throw new RuntimeException(exceptionMessage, controller.getCause());
    			
    		} else {
    			if (controller.getMessage() != null) {
    				System.out.println("[OK] " + controller.getMessage());
    			}
    		}
    	}
    	
    	System.out.println("---/// TEST OK ///---");    	
	}
 
}