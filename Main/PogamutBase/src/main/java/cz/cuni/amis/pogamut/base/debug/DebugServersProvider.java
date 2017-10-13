/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.amis.pogamut.base.debug;


import java.util.HashMap;
import java.util.Map;

/**
 * Here agents register/deregister servers that they are running on. This information
 * can be used later for debugging. The debugger can pause/unpause the server to ease
 * the debugging.
 * 
 * @author Ik
 */
public class DebugServersProvider {

    public static final String DELIMITER = ", ";
    protected static Map<String, Integer> servers = new HashMap<String, Integer>();

    /**
     * 
     * @return Comma separated list of servers' adresses
     */
    public static String getServersList() {
        String ret = "";
        for (String server : servers.keySet()) {
            ret += server.toString() + DELIMITER;
        }
        return ret;
    }

    /**
     *
     * @param server uri of server
     */
    public static void registerServer(String server) {
        Integer count = servers.get(server);
        if (count == null) {
            servers.put(server, 1);
        } else {
            count++;
        }
    }

    public static void deregisterServer(String server) {
        Integer count = servers.get(server);
        if (count == null) {
            throw new IllegalArgumentException("Deregistering server that wasn't previously registered.");
        } else {
            if(count == 0) {
                servers.remove(server);
            } else {
                count--;
            }
        }
        // TODO should resume the server in case that it was paused 
    }
}
