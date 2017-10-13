package cz.cuni.amis.pogamut.base.agent.module;

import java.util.logging.Level;

import junit.framework.Assert;

import org.junit.Test;

import cz.cuni.amis.pogamut.base.agent.IAgent;
import cz.cuni.amis.pogamut.base.agent.impl.AbstractAgent;
import cz.cuni.amis.pogamut.base.agent.impl.AgentId;
import cz.cuni.amis.pogamut.base.component.bus.ComponentBus;
import cz.cuni.amis.pogamut.base.component.controller.ComponentDependencies;
import cz.cuni.amis.pogamut.base.component.controller.ComponentDependencyType;
import cz.cuni.amis.pogamut.base.component.stub.component.ComponentStub;
import cz.cuni.amis.pogamut.base.utils.logging.AgentLogger;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.utils.flag.Flag;
import cz.cuni.amis.utils.flag.FlagListener;

import cz.cuni.amis.tests.BaseTest;
				
public class Test01_LogicModule extends BaseTest {
	
	private Flag<Boolean> shutdown = new Flag<Boolean>(false);
	private boolean init = false;
	private int logicCalled = 0;
	private Flag<Boolean> logic = new Flag<Boolean>(false);
	private Flag<Boolean> beforeLogic = new Flag<Boolean>(false);
	
	@Test
	public void test() {
		AgentId agentId = new AgentId("Test01_LogicModule");
		final IAgentLogger logger = new AgentLogger(agentId);
		logger.addDefaultConsoleHandler();
		logger.setLevel(Level.ALL);
		ComponentBus bus = new ComponentBus(logger);
		final ComponentStub starter = new ComponentStub(logger, bus);
		final LogCategory log = logger.getCategory("Logic");
		
		FlagListener<Boolean> logicListener = new FlagListener<Boolean>() {

			@Override
			public void flagChanged(Boolean changedValue) {
				if (changedValue) {
					starter.getController().manualStop("Logic sensed.");
				}
			}
			
		};
		logic.addListener(logicListener);
		
		IAgentLogic agentLogic = new IAgentLogic() {			
			
			
			@Override
			public long getLogicInitializeTime() {
				return 0;
			}

			@Override
			public long getLogicShutdownTime() {
				return 0;
			}

			@Override
			public void logic() {
				++logicCalled;
				if (log.isLoggable(Level.INFO)) log.info("Logic called: " + logicCalled + "x");				
				if (logicCalled == 5) {
					logic.setFlag(true);
					return;
				}
				if (logicCalled > 5) {
					Assert.fail("logic should be called only 5x");
				}				
			}

			@Override
			public void logicInitialize(LogicModule logicModule) {
				if (log.isLoggable(Level.INFO)) log.info("Logic initialize.");
				if (init) {
					Assert.fail("logicInitialize could not be called twice");
				}
			}

			@Override
			public void logicShutdown() {
				if (log.isLoggable(Level.INFO)) log.info("Logic shutdown.");
				if (shutdown.getFlag()) {
					Assert.fail("logicShutdown() could not be called twice");
				}
				shutdown.setFlag(true);
			}

			@Override
			public void beforeFirstLogic() {
				if (log.isLoggable(Level.INFO)) log.info("BeforeLogic.");
				if (beforeLogic.getFlag()) {
					Assert.fail("beforeFirstLogic() could not be called twice");
				}
				beforeLogic.setFlag(true);
			}
			
		};
		
		LogicModule logicModule = new LogicModule<IAgent>(new AbstractAgent(agentId, bus, logger){}, agentLogic, log, new ComponentDependencies(ComponentDependencyType.STARTS_WITH).add(starter));
		
		for (int i = 0; i < 20; ++i) {
			System.out.println("---((( Iteration " + (i+1) + " / 20 )))---" );
			init = false;
			logic.setFlag(false);
			shutdown.setFlag(false);
			beforeLogic.setFlag(false);
			logicCalled = 0;
			
			starter.getController().manualStart("Starting the logic.");
			Boolean result = shutdown.waitFor(10000, true);
			if (result == null || !result) {
				testFailed("logic should have terminated");
			}
			if (!beforeLogic.getFlag()) {
				testFailed("beforeFirstLogic() not called!");
			}
			if (!logic.getFlag()) {
				testFailed("logic() not called!");
			}
			if (logicCalled != 5) {
				testFailed("logic() should have been called only 5x not " + logicCalled + "x !");	
			}
			
		}
		
		System.out.println("---/// TEST OK ///---");
		
	}
	
	public static void main(String[] args) {
		Test01_LogicModule test = new Test01_LogicModule();
		test.test();
	}

}
