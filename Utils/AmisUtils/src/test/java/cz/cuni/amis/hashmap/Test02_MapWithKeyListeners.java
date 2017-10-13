package cz.cuni.amis.hashmap;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import cz.cuni.amis.tests.BaseTest;
import cz.cuni.amis.utils.maps.MapWithKeyListeners;
import cz.cuni.amis.utils.maps.MapWithKeyListeners.IKeyCreatedListener;
import cz.cuni.amis.utils.maps.MapWithKeyListeners.KeyCreatedEvent;

public class Test02_MapWithKeyListeners extends BaseTest {
	
	@Test
	public void test() {
		
		MapWithKeyListeners<Integer, Integer> map = new MapWithKeyListeners<Integer, Integer>();
		
		final CountDownLatch latch = new CountDownLatch(1);
		
		IKeyCreatedListener<Integer, Integer> listener = new IKeyCreatedListener<Integer, Integer>() {

			@Override
			public void notify(KeyCreatedEvent<Integer, Integer> event) {
				log.info("SENSED " + event.getKey() + " -> " + event.getValue());
				latch.countDown();
			}
			
		};
		
		map.addWeakListener(1, listener);
		
		map.put(1, 2);
		
		try {
			latch.await(1000, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			throw new RuntimeException("Interrupted while on latch.", e);
		}
		if (latch.getCount() > 0) {
			testFailed("Failed to sense map.put(1, 2);");
		}
		
		testOk();
	}
	

}
