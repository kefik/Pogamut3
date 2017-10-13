package cz.cuni.amis.pogamut.ut2004.server;


import java.util.Random;

import org.junit.Test;

import cz.cuni.amis.pogamut.base.utils.Pogamut;
import cz.cuni.amis.pogamut.ut2004.factory.guice.remoteagent.UT2004ServerFactory;
import cz.cuni.amis.pogamut.ut2004.factory.guice.remoteagent.UT2004ServerModule;
import cz.cuni.amis.pogamut.ut2004.test.UT2004Test;
import cz.cuni.amis.utils.StopWatch;

public class UT2004Test04_UT2004Server extends UT2004Test {

    
    @Test
    public void test01_ServerConnect() {
    	UT2004ServerFactory factory = new UT2004ServerFactory(new UT2004ServerModule());
    	IUT2004Server tempServer = null;        
    	
    	StopWatch watches = new StopWatch();
    	for (int i = 0; i < 10; ++i) {	
    		if (tempServer == null) {
    			tempServer = startUTServer(factory);
    		} else {
    			tempServer.start();
    		}
    		try {
    			int wait = new Random(System.currentTimeMillis()).nextInt(200);
    			System.out.println("Sleeping for a small period of time (" + wait + "ms)...");
				Thread.sleep(wait);
			} catch (InterruptedException e) {
			}
			watches.start();
    		tempServer.stop();
    		System.out.println("Stopping server took " + watches.stopStr() + " ...");
    		if (watches.time() > 1000) {
    			System.out.println("!!! Stopping the server took more then 1s...");
    		}
    		if (watches.time() > 10000) {
    			System.out.println("!!!!!! Stopping the server took more then 10s !!!!!!");
    			throw new RuntimeException("Stopping the server took more then 10s!");
    		}
    	}
  
    	System.out.println("---/// TEST OK ///---");
    	
    	Pogamut.getPlatform().close();
    }

}
