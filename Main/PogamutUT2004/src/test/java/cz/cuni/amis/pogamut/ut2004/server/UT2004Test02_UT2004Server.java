package cz.cuni.amis.pogamut.ut2004.server;



import org.junit.Test;

import cz.cuni.amis.pogamut.base.utils.Pogamut;
import cz.cuni.amis.pogamut.ut2004.factory.direct.remoteagent.UT2004ServerFactory;
import cz.cuni.amis.pogamut.ut2004.test.UT2004Test;

/**
 * Tests direct factory.
 */
public class UT2004Test02_UT2004Server extends UT2004Test {

    @Test
    public void test01_ServerConnect() {
        IUT2004Server tempServer = startUTServer(new UT2004ServerFactory());
        tempServer.stop();
    	
    	System.out.println("---/// TEST OK ///---");
    	
    	Pogamut.getPlatform().close();
    }

}
