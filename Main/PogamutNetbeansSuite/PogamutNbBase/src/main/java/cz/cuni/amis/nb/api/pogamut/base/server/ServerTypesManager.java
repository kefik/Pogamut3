/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.amis.nb.api.pogamut.base.server;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author ik
 */
public class ServerTypesManager {

    static Map<String, ServersManager> managersMap = new HashMap<String, ServersManager>();

    /**
     * Returns a servers manager for given type. The type should conform to the
     * protocol used by the server. Eg, "gb04" for Unreal Tournament 2004 servers.
     * @param serverType
     * @return
     */
    public static ServersManager getServersManager(String serverType) {
        ServersManager manager = managersMap.get(serverType);
        if (manager == null) {
            throw new RuntimeException("No server registered for type " + serverType);
            // TODO use SPI
          /*  manager = new DefaultServersManager(serverType);
            managersMap.put(serverType, manager);*/
        }
        return manager;
    }

    public static void registerServersManager(ServersManager manager) {
        managersMap.put(manager.getServerType(), manager);
    }

    /**
     * Saves all changes to all server managers.
     */
    public static void serializeAll() {
        for(ServersManager manager : managersMap.values()) {
            manager.serialize();
        }
    }
}
