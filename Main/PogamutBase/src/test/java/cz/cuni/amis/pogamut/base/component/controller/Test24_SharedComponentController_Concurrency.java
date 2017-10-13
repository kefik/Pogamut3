package cz.cuni.amis.pogamut.base.component.controller;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.junit.Test;

import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.agent.impl.AgentId;
import cz.cuni.amis.pogamut.base.component.ISharedComponent;
import cz.cuni.amis.pogamut.base.component.bus.event.impl.StartedEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.impl.StartingEvent;
import cz.cuni.amis.pogamut.base.component.lifecyclebus.ILifecycleBus;
import cz.cuni.amis.pogamut.base.component.lifecyclebus.LifecycleBus;
import cz.cuni.amis.pogamut.base.component.stub.component.ManualCheckComponent;
import cz.cuni.amis.pogamut.base.component.stub.sharedcomponent.AutoCheckSharedComponent;
import cz.cuni.amis.pogamut.base.component.stub.sharedcomponent.EventToString;
import cz.cuni.amis.pogamut.base.component.stub.sharedcomponent.MethodToString;
import cz.cuni.amis.pogamut.base.utils.logging.AgentLogger;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.utils.StopWatch;

/**
 * Tests: many agents using the component, starting/pausing/resuming/stopping
 * 
 * Does not checks events/methods as eagerly as previous tests, but at least we're checking whether it survives the concurrency
 * and whether the component is running when it should.
 * 
 * @author Jimmy
 */
import cz.cuni.amis.tests.BaseTest;
				
public class Test24_SharedComponentController_Concurrency extends BaseTest {
	
	private static final Level loggingLevel = Level.WARNING;
	
	private static void checkState(AutoCheckSharedComponent sharedComp, ComponentState state) {
		if (sharedComp.getController().notInState(state)) {
			String msg = "[ERROR] " + sharedComp.getComponentId().getToken() + " is not in state " + state + " but in state " + sharedComp.getController().getState().getFlag() + ", INVALID!";
			System.out.println(msg);
			exception = new RuntimeException(msg);
			while (latch != null && latch.getCount() > 0) latch.countDown();
			throw exception;
		}
	}
	
	private static class OneAgent implements Runnable {

		private static int COUNTER = 1;
		private AgentId agentId;
		private AgentLogger logger;
		private LifecycleBus bus;
		private ManualCheckComponent manualComp0;
		private ManualCheckComponent manualComp1;
		private ComponentController manualCompCtrl1;
		private int operations;
		private Random random;
		private AutoCheckSharedComponent sharedComp;
		
		public OneAgent(AutoCheckSharedComponent sharedComp, ComponentDependencyType type, int operations) {
			this.sharedComp = sharedComp;
			agentId = new AgentId("Agent" + (COUNTER++));
			logger = new AgentLogger(agentId);
			logger.addDefaultConsoleHandler();
			logger.setLevel(loggingLevel);
			bus = new LifecycleBus(logger);
			manualComp0 = new ManualCheckComponent(logger, bus);
			manualComp1 = new ManualCheckComponent(logger, bus);
			
			manualCompCtrl1 = 
				new ComponentController(
						manualComp1, new ComponentControlHelper(), bus, logger.getCategory(manualComp1.getComponentId().getToken()),
						new ComponentDependencies(ComponentDependencyType.STARTS_WITH).add(manualComp0)
				);
			
			sharedComp.addComponentBus(agentId, bus, new ComponentDependencies(type).add(manualComp1));
			
			this.operations = operations;
			this.random = new Random(System.currentTimeMillis());
		}
		
		@Override
		public void run() {
			try {				
				ComponentState lastState = ComponentState.STOPPED;
				int operation = 0;
				for (int i = 0; i < operations; ++i) {
					switch(lastState) {
					case STOPPED:
						System.out.println(agentId.getName().getFlag() + " - STARTING (operation " + (i+1) + " / " + operations + ")");
						manualComp0.manualStart();						
						lastState = ComponentState.RUNNING;
						checkState(sharedComp, ComponentState.RUNNING);
						System.out.println(agentId.getName().getFlag() + " - RUNNING");
						break;
						
					case RUNNING:
						operation = random.nextInt(2);
						switch(operation) {
						case 0:
							System.out.println(agentId.getName().getFlag() + " - STOPPING (operation " + (i+1) + " / " + operations + ")");
							manualComp0.manualStop();
							lastState = ComponentState.STOPPED;
							System.out.println(agentId.getName().getFlag() + " - STOPPED");
							break;
						case 1:
							System.out.println(agentId.getName().getFlag() + " - PAUSING (operation " + (i+1) + " / " + operations + ")");
							manualComp0.manualPause();
							lastState = ComponentState.PAUSED;
							System.out.println(agentId.getName().getFlag() + " - PAUSED");
							break;
						}
						break;
						
					case PAUSED:
						operation = random.nextInt(2);
						switch(operation) {
						case 0:
							System.out.println(agentId.getName().getFlag() + " - STOPPING (operation " + (i+1) + " / " + operations + ")");
							manualComp0.manualStop();
							lastState = ComponentState.STOPPED;
							System.out.println(agentId.getName().getFlag() + " - STOPPED");
							break;
						case 1:
							System.out.println(agentId.getName().getFlag() + " - RESUMING (operation " + (i+1) + " / " + operations + ")");
							manualComp0.manualResume();
							lastState = ComponentState.RUNNING;
							checkState(sharedComp, ComponentState.RUNNING);
							System.out.println(agentId.getName().getFlag() + " - RUNNING");
							break;
						}
						break;
						
					default:
						throw (exception = new RuntimeException("INVALID SWITCH CASE = " + lastState));
					}
					
				}
				
				if (lastState != ComponentState.STOPPED) {
					manualComp0.manualStop();
				}
			} catch (Exception e) {
				exception = new RuntimeException("EXCEPTION!", e);
				while (latch != null && latch.getCount() > 0) latch.countDown();
			} finally {
				latch.countDown();
			}
		}
	}
	
	private static RuntimeException exception = null;
	
	private static CountDownLatch latch;
	
	private static void performTest(int agents, int operations, AutoCheckSharedComponent sharedComp) {
		
		OneAgent.COUNTER = 1;
		exception = null;
		
		latch = new CountDownLatch(agents);
		
		OneAgent[] agent = new OneAgent[agents];
		
		for (int i = 0; i < agents; ++i) {
			System.out.println("CREATING AGENT " + (i+1) + " / " + agents);
			agent[i] = new OneAgent(sharedComp, i % 2 == 0 ? ComponentDependencyType.STARTS_WITH : ComponentDependencyType.STARTS_AFTER, operations);
		}
		
		for (int i = 0; i < agents; ++i) {
			System.out.println("STARTING AGENT " + (i+1) + " / " + agents);
			new Thread(agent[i], "CheckThread-" + i).start();
		}
		
		try {
			latch.await(agents * operations * 1000, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			if (exception != null) throw exception;
			throw new RuntimeException("Interrupted on the latch!", e);
		}
		
		if (exception != null) throw exception;
		
		checkState(sharedComp, ComponentState.STOPPED);
	}
	
	@Test
	public void test01() {
		int agents = 1;
		int operations = 10;
		System.out.println("--- TEST: agents = " + agents + ", operations = " + operations + " ---");
		
		System.out.println("In case of exception, set static field loggingLevel to Level.ALL and rerun!");
		
		IAgentId id = new AgentId("SharedComponents");
		IAgentLogger logger = new AgentLogger(id);
		AutoCheckSharedComponent sharedComp = new AutoCheckSharedComponent(logger);
		sharedComp.setShouldBeChecking(false);
		
		StopWatch watch = new StopWatch();		
		performTest(agents, operations, sharedComp);
		System.out.println(agents*operations + " operations finished in " + watch.stopStr());
		
		System.out.println("---/// TEST OK (agents = " + agents + ", operations = " + operations + ") ///---");
	}
	
	@Test
	public void test02() {
		int agents = 2;
		int operations = 10;
		System.out.println("--- TEST: agents = " + agents + ", operations = " + operations + " ---");
		
		System.out.println("In case of exception, set static field loggingLevel to Level.ALL and rerun!");
		
		IAgentId id = new AgentId("Shared");
		IAgentLogger logger = new AgentLogger(id);
		logger.addDefaultConsoleHandler();
		logger.setLevel(loggingLevel);
		AutoCheckSharedComponent sharedComp = new AutoCheckSharedComponent(logger);
		sharedComp.setShouldBeChecking(false);
		
		StopWatch watch = new StopWatch();		
		performTest(agents, operations, sharedComp);
		System.out.println(agents*operations + " operations finished in " + watch.stopStr());
		
		System.out.println("---/// TEST OK (agents = " + agents + ", operations = " + operations + ") ///---");
	}
	
	@Test
	public void test03() {
		int agents = 3;
		int operations = 10;
		System.out.println("--- TEST: agents = " + agents + ", operations = " + operations + " ---");
		
		System.out.println("In case of exception, set static field loggingLevel to Level.ALL and rerun!");
		
		IAgentId id = new AgentId("Shared");
		IAgentLogger logger = new AgentLogger(id);
		logger.addDefaultConsoleHandler();
		logger.setLevel(loggingLevel);
		AutoCheckSharedComponent sharedComp = new AutoCheckSharedComponent(logger);
		sharedComp.setShouldBeChecking(false);
		
		StopWatch watch = new StopWatch();		
		performTest(agents, operations, sharedComp);
		System.out.println(agents*operations + " operations finished in " + watch.stopStr());
		
		System.out.println("---/// TEST OK (agents = " + agents + ", operations = " + operations + ") ///---");
	}
	
	// STRESS TESTING
	
	@Test
	public void test04() {
		int agents = 10;
		int operations = 100;
		System.out.println("--- TEST: agents = " + agents + ", operations = " + operations + " ---");
		
		System.out.println("In case of exception, set static field loggingLevel to Level.ALL and rerun!");
		
		IAgentId id = new AgentId("Shared");
		IAgentLogger logger = new AgentLogger(id);
		logger.addDefaultConsoleHandler();
		logger.setLevel(loggingLevel);
		AutoCheckSharedComponent sharedComp = new AutoCheckSharedComponent(logger);
		sharedComp.setShouldBeChecking(false);
		
		StopWatch watch = new StopWatch();		
		performTest(agents, operations, sharedComp);
		System.out.println(agents*operations + " operations finished in " + watch.stopStr());
		
		System.out.println("---/// TEST OK (agents = " + agents + ", operations = " + operations + ") ///---");
	}

	@Test
	public void test05() {
		int agents = 10;
		int operations = 1000;
		System.out.println("--- TEST: agents = " + agents + ", operations = " + operations + " ---");
		
		System.out.println("In case of exception, set static field loggingLevel to Level.ALL and rerun!");
		
		IAgentId id = new AgentId("Shared");
		IAgentLogger logger = new AgentLogger(id);
		logger.addDefaultConsoleHandler();
		logger.setLevel(loggingLevel);
		AutoCheckSharedComponent sharedComp = new AutoCheckSharedComponent(logger);
		sharedComp.setShouldBeChecking(false);
		
		StopWatch watch = new StopWatch();		
		performTest(agents, operations, sharedComp);
		System.out.println(agents*operations + " operations finished in " + watch.stopStr());
		
		System.out.println("---/// TEST OK (agents = " + agents + ", operations = " + operations + ") ///---");
	}

	@Test
	public void test06() {
		int agents = 100;
		int operations = 100;
		System.out.println("--- TEST: agents = " + agents + ", operations = " + operations + " ---");
		
		System.out.println("In case of exception, set static field loggingLevel to Level.ALL and rerun!");
		
		IAgentId id = new AgentId("Shared");
		IAgentLogger logger = new AgentLogger(id);
		logger.addDefaultConsoleHandler();
		logger.setLevel(loggingLevel);
		AutoCheckSharedComponent sharedComp = new AutoCheckSharedComponent(logger);
		sharedComp.setShouldBeChecking(false);
		
		StopWatch watch = new StopWatch();		
		performTest(agents, operations, sharedComp);
		System.out.println(agents*operations + " operations finished in " + watch.stopStr());
		
		System.out.println("---/// TEST OK (agents = " + agents + ", operations = " + operations + ") ///---");
	}
	
	@Test
	public void test07() {
		
		
		int agents = 100;
		int operations = 1000;
		System.out.println("--- TEST: agents = " + agents + ", operations = " + operations + " ---");
		
		System.out.println("In case of exception, set static field loggingLevel to Level.ALL and rerun!");
		
		IAgentId id = new AgentId("Shared");
		IAgentLogger logger = new AgentLogger(id);
		logger.addDefaultConsoleHandler();
		logger.setLevel(loggingLevel);
		AutoCheckSharedComponent sharedComp = new AutoCheckSharedComponent(logger);
		sharedComp.setShouldBeChecking(false);
		
		StopWatch watch = new StopWatch();		
		performTest(agents, operations, sharedComp);
		System.out.println(agents*operations + " operations finished in " + watch.stopStr());
		
		System.out.println("---/// TEST OK (agents = " + agents + ", operations = " + operations + ") ///---");
	}

	
}
