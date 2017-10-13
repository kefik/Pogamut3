package cz.cuni.amis.pogamut.base.utils;

import cz.cuni.amis.utils.exception.PogamutException;

import java.util.HashMap;
import java.util.Map;
import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.remote.JMXServiceURL;

/**
 * Used for programatical substitution of properties.
 * @author ik
 */
public abstract class PogamutPlatformProxy implements PogamutPlatform {

    PogamutPlatform platform;
    Map<String, String> internalProps = new HashMap<String, String>();

    public PogamutPlatformProxy(PogamutPlatform platform) {
        this.platform = platform;
    }

    @Override
    public MBeanServerConnection getMBeanServerConnection() throws PogamutException {
        return platform.getMBeanServerConnection();
    }

    public void setProperty(String key, String value) {
        internalProps.put(key, value);
    }

    @Override
    public String getProperty(String key) {
        String val = internalProps.get(key);
        if (val == null) {
            val = platform.getProperty(key);
        }
        return val;
    }

    @Override
    public String getProperty(String key, String def) {
        String val = internalProps.get(key);
        if (val == null) {
            val = platform.getProperty(key);
        }
        if (val == null) {
            val = def;
        }

        return val;
    }

    @Override
    public void close() throws PogamutException {
        platform.close();
    }

    @Override
    public MBeanServer getMBeanServer() throws PogamutException {
        return platform.getMBeanServer();
    }

    @Override
    public JMXServiceURL getMBeanServerURL() throws PogamutException {
        return getMBeanServerURL();
    }
}
