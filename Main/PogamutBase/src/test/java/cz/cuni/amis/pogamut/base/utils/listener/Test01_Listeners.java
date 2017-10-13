package cz.cuni.amis.pogamut.base.utils.listener;

import java.util.EventListener;

import junit.framework.Assert;

import org.junit.Test;

import cz.cuni.amis.utils.listener.Event;
import cz.cuni.amis.utils.listener.IListener;
import cz.cuni.amis.utils.listener.Listeners;
import cz.cuni.amis.utils.listener.Listeners.ListenerRemover;

import cz.cuni.amis.tests.BaseTest;
				
public class Test01_Listeners extends BaseTest {
	
	Listeners<IListener> listeners = new Listeners<IListener>();
	
	int notified1 = 0;
	int notified2 = 0;
	
	private void event() {
		listeners.notify(new IListener.Notifier<IListener>(new Event(){}));
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
		
		listeners.addStrongListener(listener1);
		listeners.addWeakListener(listener2);
		
		event();
		event();
		
		Assert.assertTrue("strong-listener not notified", notified1 == 2);
		Assert.assertTrue("weak-listener not notified", notified2 == 2);
		
		listeners.removeListener(listener1);
		
		event();
		event();
		
		Assert.assertTrue("strong-listener notified (was removed)", notified1 == 2);
		Assert.assertTrue("weak-listener not notified", notified2 == 4);
		
		listeners.removeListener(listener2);
		
		event();
		event();
		
		Assert.assertTrue("strong-listener notified (was removed)", notified1 == 2);
		Assert.assertTrue("weak-listener notified (was removed)", notified2 == 4);
		
		listeners.addStrongListener(listener1);
		listeners.addWeakListener(listener2);

		event();
		event();
		
		Assert.assertTrue("strong-listener not notified", notified1 == 4);
		Assert.assertTrue("weak-listener not notified", notified2 == 6);
		
		listeners.remove(new ListenerRemover() {

			@Override
			public boolean remove(EventListener listener) {
				return listener == listener2;
			}
			
		});
		
		event();
		event();
		
		Assert.assertTrue("strong-listener not notified", notified1 == 6);
		Assert.assertTrue("weak-listener notified (was removed)", notified2 == 6);
		
		System.out.println("---/// TEST OK ///---");
		
		
	}
	
}
