package cz.cuni.amis.pogamut.ut2004.server;

import org.junit.Test;

import cz.cuni.amis.pogamut.base.utils.Pogamut;
import cz.cuni.amis.pogamut.ut2004.factory.guice.remoteagent.UT2004ServerFactory;
import cz.cuni.amis.pogamut.ut2004.factory.guice.remoteagent.UT2004ServerModule;
import cz.cuni.amis.pogamut.ut2004.test.UT2004Test;

/**
 * Tests guice factory.
 */
public class UT2004Test01_UT2004Server extends UT2004Test {

    @Test
    public void test01_ServerConnect() {
    	UT2004ServerFactory factory = new UT2004ServerFactory(new UT2004ServerModule());    	
    	IUT2004Server server = startUTServer(factory);
        server.stop();

        System.out.println("---/// TEST OK ///---");

        Pogamut.getPlatform().close();
    }

}
