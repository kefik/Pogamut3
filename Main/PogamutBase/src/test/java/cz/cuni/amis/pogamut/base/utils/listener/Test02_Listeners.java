package cz.cuni.amis.pogamut.base.utils.listener;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;

import org.junit.Assert;
import org.junit.Test;

import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.agent.impl.AgentId;
import cz.cuni.amis.pogamut.base.component.bus.ComponentBus;
import cz.cuni.amis.pogamut.base.component.bus.IComponentBus;
import cz.cuni.amis.pogamut.base.utils.logging.AgentLogger;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.utils.StopWatch;
import cz.cuni.amis.utils.listener.Event;
import cz.cuni.amis.utils.listener.IListener;
import cz.cuni.amis.utils.listener.Listeners;
import cz.cuni.amis.utils.sets.ConcurrentLinkedHashSet;
import cz.cuni.amis.utils.token.IToken;
import cz.cuni.amis.utils.token.Tokens;

import cz.cuni.amis.tests.BaseTest;
				
public class Test02_Listeners extends BaseTest {
	
	private static class Event1 implements Event {
		
	}
	
	private static class Event2 implements Event {
		
	}
	
	
	private static final IToken[] TOKENS = new IToken[] {
		Tokens.get("token1"), Tokens.get("token2"), Tokens.get("token3")
	};
	
	private CountDownLatch latch;
	
	private boolean failure = false;
	
	private class ListenersManager implements Runnable {
		
		private Random random = new Random(System.currentTimeMillis());

		private int num;
		
		private int listenerNum = 0;
		
		private int notified = 0;
		
		private Listeners<IListener<Event>> listeners;
		
		private Set<IListener> myListeners = new HashSet<IListener>();
		
		private void addListener() {
			IListener<Event> listener = new IListener<Event>() {

				int lNum = ++listenerNum;
				
				@Override
				public void notify(Event event) {
					notified++;
				}
				
				@Override
				public String toString() {
					return "Listener " + num + "-" + lNum;
				}
				
			};
			myListeners.add(listener);
			
			switch(random.nextInt(2)) {
			case 0: listeners.addStrongListener(listener); break;
			case 1: listeners.addWeakListener(listener); break;
			}			
			
			if (!listeners.isListening(listener)) {
				throw new RuntimeException("listener not added");
			}
			if (!listeners.isEqualListening(listener)) {
				throw new RuntimeException("listeners has leaked the listener (not found)");
			}
		}
		
		private void removeListener() {
			if (myListeners.size() == 0) return;
			Iterator<IListener> iter = myListeners.iterator();
			IListener listener = iter.next();
			if (!listeners.isListening(listener)) {
				throw new RuntimeException("listeners has leaked the listener (not found)");
			}
			if (!listeners.isEqualListening(listener)) {
				throw new RuntimeException("listeners has leaked the listener (not found)");
			}
			listeners.removeListener(listener);
			iter.remove();
			if (listeners.isListening(listener)) {
				throw new RuntimeException("listener not removed");
			}
			if (listeners.isEqualListening(listener)) {
				throw new RuntimeException("listener not removed");
			}
		}
		
		private void event() {
			switch(random.nextInt(2)) {
			case 0: listeners.notify(new IListener.Notifier(new Event1())); break;
			case 1: listeners.notify(new IListener.Notifier(new Event2())); break;
			}
		}
		
		public ListenersManager(Listeners listeners, int num) {
			this.listeners = listeners;
			this.num = num;
		}

		@Override
		public void run() {			
			try {
				StopWatch watch = new StopWatch();
				addListener();
				event();
				for (int i = 0; i < 200; ++i) {
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
				Assert.assertTrue("At least one notification of thread " + num + " listener must go through.", notified > 0);
				
				while(myListeners.size() > 0) {
					removeListener();
				}
			
			} catch (Exception e) {
				System.out.println("Thread "+ num + ": "+ e.getMessage());
				e.printStackTrace();
				failure = true;
			} finally {
				latch.countDown();	
			}
			
		}
		
	}
	
	@Test
	public void test() {
		IAgentId agentId = new AgentId("Test06_ComponentBus");
		IAgentLogger logger = new AgentLogger(agentId);
		logger.setLevel(Level.OFF);
		Listeners<IListener> listeners = new Listeners<IListener>();
		Thread[] threads = new Thread[32];
		for (int i = 0; i < threads.length; ++i) {
			threads[i] = new Thread(new ListenersManager(listeners,i+1));
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
		if (failure) Assert.fail("Test failed due to previous exceptions.");
		System.out.println("---/// TEST OK ///---");
	}

	public static void main(String[] args) {
		Test02_Listeners test = new Test02_Listeners();
		test.test();
	}
	
}
