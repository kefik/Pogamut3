package cz.cuni.amis.pogamut.ut2004.observer;

import java.util.Random;

import org.junit.Test;

import cz.cuni.amis.pogamut.ut2004.factory.direct.remoteagent.UT2004ObserverFactory;
import cz.cuni.amis.pogamut.ut2004.test.UT2004Test;
import cz.cuni.amis.utils.StopWatch;

public class UT2004Test02_UT2004Observer extends UT2004Test {
        
    @Test
    public void test01_ServerConnect() {
    	UT2004ObserverFactory factory = new UT2004ObserverFactory();    	
    	IUT2004Observer observer = startUTObserver(factory);
    	
    	StopWatch watches = new StopWatch();
    	for (int i = 0; i < 10; ++i) {	
    		if (observer == null) {
    			observer = startUTObserver(factory);
    		} else {
    			observer.start();
    		}
    		try {
    			int wait = new Random(System.currentTimeMillis()).nextInt(200);
    			System.out.println("Sleeping for a small period of time (" + wait + "ms)...");
				Thread.sleep(wait);
			} catch (InterruptedException e) {
			}
			watches.start();
    		observer.stop();
    		System.out.println("Stopping observer took " + watches.stopStr() + " ...");
    		if (watches.time() > 1000) {
    			System.out.println("!!! Stopping the observer took more then 1s...");
    		}
    		if (watches.time() > 10000) {
    			System.out.println("!!!!!! Stopping the observer took more then 10s...");
    			throw new RuntimeException("!!!!!! Stopping the observer took more then 10s...");
    		}
    	}
  
    	System.out.println("---/// TEST OK ///---");
    }

}
