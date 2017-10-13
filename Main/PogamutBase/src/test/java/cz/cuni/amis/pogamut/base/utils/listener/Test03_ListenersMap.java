package cz.cuni.amis.pogamut.base.utils.listener;

import java.util.EventListener;

import junit.framework.Assert;

import org.junit.Test;

import cz.cuni.amis.utils.listener.Event;
import cz.cuni.amis.utils.listener.IListener;
import cz.cuni.amis.utils.listener.Listeners;
import cz.cuni.amis.utils.listener.ListenersMap;
import cz.cuni.amis.utils.listener.Listeners.ListenerRemover;

import cz.cuni.amis.tests.BaseTest;
				
public class Test03_ListenersMap extends BaseTest {
	
	ListenersMap<Integer> listeners = new ListenersMap<Integer>();
	
	int notified1 = 0;
	int notified2 = 0;
	
	private void event(int key) {
		listeners.notify(key, new IListener.Notifier<IListener>(new Event(){}));
	}
	
	@Test
	public void test() {
		final IListener<Event> listener1 = new IListener<Event>() {
			@Override
			public void notify(Event event) {
				++notified1;
			}
		};
		final IListener<Event> listener2 = new IListener<Event>() {
			@Override
			public void notify(Event event) {
				++notified2;
			}
		};
		
		listeners.add(1, listener1);
		listeners.add(2, listener2);
		
		event(1);
		event(2);
		
		Assert.assertTrue("1-listener not notified", notified1 == 1);
		Assert.assertTrue("2-listener not notified", notified2 == 1);
		
		listeners.remove(1, listener1);
		listeners.remove(1, listener2);
		
		event(1);
		event(2);
		
		Assert.assertTrue("1-listener notified (was removed)", notified1 == 1);
		Assert.assertTrue("2-listener not notified", notified2 == 2);
		
		listeners.remove(2, listener1);
		listeners.remove(2, listener2);
		
		event(1);
		event(2);
		
		Assert.assertTrue("1-listener notified (was removed)", notified1 == 1);
		Assert.assertTrue("2-listener notified (was removed)", notified2 == 2);
		
		listeners.add(1, listener1);
		listeners.add(2, listener2);

		event(1);
		event(2);
		
		Assert.assertTrue("1-listener not notified", notified1 == 2);
		Assert.assertTrue("2-listener not notified", notified2 == 3);
		
		System.out.println("---/// TEST OK ///---");
		
		
	}
	
}
