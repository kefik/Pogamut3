package cz.cuni.amis.utils.flag;

import java.util.concurrent.CountDownLatch;

import org.junit.Test;

import cz.cuni.amis.utils.exception.PogamutInterruptedException;

public class Test02_RaceConditions {

	@Test
	public void test01() {
		System.out.println("Testing racing conditions for the flag...");
		
		final Flag<Boolean> flag = new Flag<Boolean>(true);
		
		final CountDownLatch latch = new CountDownLatch(2);        
        final CountDownLatch end = new CountDownLatch(2);
     
        Thread one = new Thread(new Runnable() {        	

			@Override
			public void run() {
				latch.countDown();
				try {
					latch.await();
				} catch (InterruptedException e) {
				}
				
				System.out.println("Thread 1: start");
				
				for (int i = 0; i < 100; ++i) {
					boolean value = i % 2 == 0;
					System.out.println("Thread 1: setting flag to " + value);
					flag.setFlag(value);
					Thread.yield();
				}
				
				end.countDown();
			}

        });
        
        Thread two = new Thread(new Runnable() {        	

			@Override
			public void run() {				
				latch.countDown();
				try {
					latch.await();
				} catch (InterruptedException e) {
				}
				
				System.out.println("Thread 2: start");
				
				for (int i = 0; i < 100; ++i) {
					boolean value = i % 2 == 0;
					System.out.println("Thread 2: setting flag to " + value);
					flag.setFlag(value);
					Thread.yield();
				}
				
				end.countDown();
			}
        	
        });
        
        
        one.start();
        two.start();
        
        try {
			Thread.sleep(10);
		} catch (InterruptedException e1) {
		}
        
        flag.freeze();
        System.out.println("flag freezed...");
        
        FlagListenerMock<Boolean> testListener = new FlagListenerMock<Boolean>();
        flag.addListener(testListener);
        
        try {
			Thread.sleep(5);
		} catch (InterruptedException e1) {
		}
        
        flag.setFlag(!flag.getFlag());
        
        try {
			Thread.sleep(5);
		} catch (InterruptedException e1) {
			throw new PogamutInterruptedException(e1, this);
		}
        
		// nothing is changed during freeze
        testListener.checkValuesInOrder("TestListener", new Boolean[0]);
        System.out.println("flag was not changed while freezed, good!");
        
        flag.removeListener(testListener);
        
        System.out.println("flag defreezed...");
        flag.defreeze();
        
        try {
			end.await();
		} catch (InterruptedException e) {
			throw new PogamutInterruptedException(e, this);
		}
		
		System.out.println("---/// TEST OK ///---");
	}
	
}
