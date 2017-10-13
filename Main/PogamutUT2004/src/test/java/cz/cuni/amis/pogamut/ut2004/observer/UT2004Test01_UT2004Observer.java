package cz.cuni.amis.pogamut.ut2004.observer;

import org.junit.Test;

import cz.cuni.amis.pogamut.base.utils.Pogamut;
import cz.cuni.amis.pogamut.ut2004.factory.direct.remoteagent.UT2004ObserverFactory;
import cz.cuni.amis.pogamut.ut2004.test.UT2004Test;

/**
 * Tests direct factory of the observer.
 */
public class UT2004Test01_UT2004Observer extends UT2004Test {

    @Test
    public void test01_ObserverConnect() {
    	UT2004ObserverFactory factory = new UT2004ObserverFactory();    	
    	IUT2004Observer observer = startUTObserver(factory);
        observer.stop();

        System.out.println("---/// TEST OK ///---");

        Pogamut.getPlatform().close();
    }

}
