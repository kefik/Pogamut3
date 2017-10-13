package cz.cuni.amis.pogamut.base.utils;

import cz.cuni.amis.utils.exception.PogamutException;

import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.remote.JMXServiceURL;

public interface PogamutPlatform {
	
	/**
     * @return Connection to a remote MBeanServer
     * @throws cz.cuni.amis.utils.exception.PogamutException
     */
    public MBeanServerConnection getMBeanServerConnection() throws PogamutException;
    
    public String getProperty(String key);

    public int getIntProperty(String key);
    
    public boolean getBooleanProperty(String key);
    
    public String getProperty(String key, String def);
    
    /**
     * Used to shutdown the Pogamut platform - currently it only stops JMX.
     * @throws PogamutException
     */
    public void close() throws PogamutException;
    
    public MBeanServer getMBeanServer() throws PogamutException;

    public JMXServiceURL getMBeanServerURL() throws PogamutException;
}
