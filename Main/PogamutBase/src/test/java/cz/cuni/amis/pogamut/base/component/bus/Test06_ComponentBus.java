package cz.cuni.amis.pogamut.base.component.bus;

import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Assert;
import org.junit.Test;

import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.agent.impl.AgentId;
import cz.cuni.amis.pogamut.base.component.IComponent;
import cz.cuni.amis.pogamut.base.component.bus.event.IStartedEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.impl.StartedEvent;
import cz.cuni.amis.pogamut.base.utils.logging.AgentLogger;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.utils.StopWatch;
import cz.cuni.amis.utils.maps.LazyMap;
import cz.cuni.amis.utils.sets.ConcurrentLinkedHashSet;
import cz.cuni.amis.utils.token.IToken;
import cz.cuni.amis.utils.token.Tokens;

import cz.cuni.amis.tests.BaseTest;
				
public class Test06_ComponentBus extends BaseTest {
	
	private static final IToken[] TOKENS = new IToken[] {
		Tokens.get("token1"), Tokens.get("token2"), Tokens.get("token3")
	};
	
	private CountDownLatch latch;
	
	private IAgentLogger logger;
	
	private class BusManager implements Runnable {
		
		private Random random = new Random(System.currentTimeMillis());
		private IComponentBus bus;
		private int num;
		
		private int notified = 0;
		
		private Map<IToken, Set<IComponentEventListener<IStartedEvent>>> listeners = new LazyMap<IToken, Set<IComponentEventListener<IStartedEvent>>>() {

			@Override
			protected Set<IComponentEventListener<IStartedEvent>> create(IToken key) {
				return new HashSet<IComponentEventListener<IStartedEvent>>();
			}
			
		};
		
		private void addListener() {
			IToken token = TOKENS[random.nextInt(TOKENS.length)];
			IComponentEventListener<IStartedEvent> listener = new IComponentEventListener<IStartedEvent>() {

				@Override
				public void notify(IStartedEvent event) {
					++notified;
				}
			};
			bus.addEventListener(IStartedEvent.class, listener);
			listeners.get(token).add(listener);
		}
		
		private void removeListener() {
			IToken token = TOKENS[random.nextInt(TOKENS.length)];
			Set<IComponentEventListener<IStartedEvent>> set = listeners.get(token);
			if (set.size() > 0) {
				IComponentEventListener<IStartedEvent> listener = set.iterator().next();
				bus.removeEventListener(IStartedEvent.class, listener);
				set.remove(listener);
			}
		}
		
		private void event() {
			final IToken token = TOKENS[random.nextInt(TOKENS.length)];
			bus.event(new StartedEvent(new IComponent() {
				@Override
				public IToken getComponentId() {
					return token;
				}
				
				public Logger getLog() {
					return logger.getCategory(token.getToken());
				}
				
			}));
		}
		
		public BusManager(IComponentBus bus, int num) {
			this.bus = bus;
			this.num = num;
		}

		@Override
		public void run() {
			StopWatch watch = new StopWatch();
			addListener();
			event();
			for (int i = 0; i < 1000; ++i) {
				switch(random.nextInt(6)) {
				case 0:
					try {
						Thread.sleep(2);
					} catch (InterruptedException e) {
					}
					break;
				case 1:
					addListener();
					break;
				case 2:
					addListener();
					break;
				case 3:
					removeListener();
					break;
				case 4: 	
					event();
					break;
				case 5: 	
					event();
					break;
				}
			}
			System.out.println("Thread "+ num + ": notified = " + notified + ".");
			System.out.println("Thread "+ num + ": 2000 operations took "+ watch.stopStr());
			latch.countDown();
			Assert.assertTrue("At least one notification of thread " + num + " listener must go through.", notified > 0);
		}
		
	}
	
	@Test
	public void test() {
		IAgentId agentId = new AgentId("Test06_ComponentBus");
		logger = new AgentLogger(agentId);
		logger.setLevel(Level.OFF);
		IComponentBus bus = new ComponentBus(logger);
		ConcurrentLinkedHashSet set = new ConcurrentLinkedHashSet<Integer>();
		Thread[] threads = new Thread[32];
		for (int i = 0; i < threads.length; ++i) {
			threads[i] = new Thread(new BusManager(bus,i+1));
		}
		latch = new CountDownLatch(threads.length);
		StopWatch watch = new StopWatch();
		
		for (int i = 0; i < threads.length; ++i) {
			threads[i].start();
		}
		
		try {
			latch.await();
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Exception waiting for threads...");
		}		
		System.out.println("Total time: " + watch.stopStr());
		
		System.out.println("---/// TEST OK ///---");
	}

	public static void main(String[] args) {
		Test06_ComponentBus test = new Test06_ComponentBus();
		test.test();
	}
	
}
