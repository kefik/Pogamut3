package cz.cuni.amis.pogamut.ut2004.utils;

import cz.cuni.amis.pogamut.base.utils.Pogamut;
import cz.cuni.amis.utils.exception.PogamutIOException;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

/**
 * Wrapper of the UT2004 instance. Can be used for launching the game in spectate mode.
 * @author ik
 */
public class UT2004Wrapper {

    public static void launchSpectate(URI serverUri) throws IOException {
        String utHomeProp = Pogamut.getPlatform().getProperty(PogamutUT2004Property.POGAMUT_UNREAL_HOME.getKey());
        if(utHomeProp == null) throw new PogamutIOException(
                "Property " + PogamutUT2004Property.POGAMUT_UNREAL_HOME.getKey() + " not set. Set it to point to the UT2004 home directory. You can do this in environments variables.", null);
        String path = utHomeProp + File.separator + "System" + File.separator;
        
        List<String> cmds = null;
        if (!System.getProperty("os.name").contains("Windows")) {
            // TODO how to start in UNIX?
        } else {
            path += "UT2004.exe";
            // TODO deal with multiple uccs on one host
            // get IP
            InetAddress adr = InetAddress.getByName(serverUri.getHost());
            cmds = Arrays.asList("cmd.exe",
                    "/c",
                    "start \"UT2004 Spectate\" /low \"" + path + "\" " + adr.getHostAddress());
        }

        ProcessBuilder builder = new ProcessBuilder(cmds);
        Process ut = builder.start();
    }
}
