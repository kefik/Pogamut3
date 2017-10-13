package cz.cuni.amis.pogamut.base.utils.listener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;

import org.junit.Assert;
import org.junit.Test;

import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.agent.impl.AgentId;
import cz.cuni.amis.pogamut.base.utils.logging.AgentLogger;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.utils.StopWatch;
import cz.cuni.amis.utils.listener.Event;
import cz.cuni.amis.utils.listener.IListener;
import cz.cuni.amis.utils.listener.Listeners;
import cz.cuni.amis.utils.listener.ListenersMap;
import cz.cuni.amis.utils.token.IToken;
import cz.cuni.amis.utils.token.Tokens;

import cz.cuni.amis.tests.BaseTest;
				
public class Test04_ListenersMap extends BaseTest {
	
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
		
		private ListenersMap<Integer> listeners;
		
		private Map<IListener, Integer> myListeners = new HashMap<IListener, Integer>();
		
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
			
			int key = random.nextInt(10);
			myListeners.put(listener, key);
			listeners.add(key, listener);
						
			if (!listeners.isListening(key, listener)) {
				throw new RuntimeException("listener not added");
			}
			if (listeners.isListening(key+1, listener)) {
				throw new RuntimeException("listener added under wrong key");
			}
			if (listeners.isListening(key-1, listener)) {
				throw new RuntimeException("listener added under wrong key");
			}
		}
		
		private void removeListener() {
			if (myListeners.size() == 0) return;
			Iterator<Entry<IListener, Integer>> iter = myListeners.entrySet().iterator();
			Entry<IListener, Integer> entry = iter.next();
			IListener listener = entry.getKey();
			int key = entry.getValue();
			if (!listeners.isListening(listener)) {
				throw new RuntimeException("listeners has leaked the listener (not found)");
			}			
			listeners.remove(key, listener);
			iter.remove();
			if (listeners.isListening(key, listener)) {
				throw new RuntimeException("listener not removed");
			}
		}
		
		private void event() {
			listeners.notify(random.nextInt(10), new IListener.Notifier(new Event1()));
		}
		
		public ListenersManager(ListenersMap<Integer> listeners, int num) {
			this.listeners = listeners;
			this.num = num;
		}

		@Override
		public void run() {			
			try {
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
		ListenersMap<Integer> listeners = new ListenersMap<Integer>();
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
		Test04_ListenersMap test = new Test04_ListenersMap();
		test.test();
	}
	
}
