package cz.cuni.amis.pogamut.base.utils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.rmi.registry.LocateRegistry;
import java.util.Properties;
import java.util.logging.Level;

import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

import cz.cuni.amis.pogamut.base.agent.module.comm.PogamutJVMComm;
import cz.cuni.amis.pogamut.base.utils.jmx.PogamutMBeanServer;
import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.pogamut.base.utils.logging.NetworkLogManager;
import cz.cuni.amis.utils.configuration.PropertiesManager;
import cz.cuni.amis.utils.exception.PogamutException;

/**
 * Singleton for platform wide settings like: JMX, properties loading.
 * @author Ik
 */
public class DefaultPogamutPlatform implements PogamutPlatform {

    LogCategory log = new LogCategory("DefaultPogamutPlatform");
    //public static PogamutPlatform
    static PogamutMBeanServer mBeanServer = null;
    static JMXConnectorServer cs = null;
    JMXServiceURL jmxServiceURL = null;
    PropertiesManager propertiesManager = new PropertiesManager();
    
    public DefaultPogamutPlatform() {
    	log.addConsoleHandler();
    }

    @Override
    public JMXServiceURL getMBeanServerURL() throws PogamutException {
        try {
            String hostNameProp = getProperty(PogamutProperty.POGAMUT_JMX_SERVER_ADDRESS.getKey());
            String hostName = hostNameProp != null ? hostNameProp : InetAddress.getLocalHost().getHostName();
            return new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + hostName + ":" + getRMIPort() + "/server");
        } catch (Exception ex) {
            throw new PogamutException("Error creating the JMX service URL.", ex, this);
        }
    }

    protected int getRMIPort() throws PogamutException {
        if (assignedPort == null) {
            try {
                // TODO not working now
                // find free port, start with the desired port
                //assignedPort = Integer.parseInt(getProperty(PogamutProperty.POGAMUT_JMX_SERVER_RMI_PORT.getKey()));
                // find free port            	
                ServerSocket socket = new ServerSocket(0);
                assignedPort = socket.getLocalPort();
                socket.close();
            } catch (IOException ex) {
                throw new PogamutException("Error while getting a port for RMI.", ex);
            }
        }
        return assignedPort;
    }
    Integer assignedPort = null;
    private boolean registryCreated = false;

    /**
     * Returns MBeans server for the Pogamut Platform. All MBeans connected from
     * the platform should be registered in this server. There is also default
     * RMI connector for this server running on service:jmx:rmi:///jndi/rmi://localhost:9999/server
     * @return
     * @throws cz.cuni.amis.utils.exception.PogamutException
     */
    @Override
    public synchronized MBeanServer getMBeanServer() throws PogamutException {
        try {
            if (!registryCreated) {
                if (log.isLoggable(Level.WARNING)) log.warning("Creating registry at " + getRMIPort() + " ...");
                LocateRegistry.createRegistry(getRMIPort());
                registryCreated = true;
            }
            if (mBeanServer == null) {
                if (log.isLoggable(Level.WARNING)) log.warning("Starting MBean server.");
                //start a RMI registry                
                mBeanServer = new PogamutMBeanServer();

                // also create connector for this server, server without connector
                // would be unreachable outside this JVM
                cs = JMXConnectorServerFactory.newJMXConnectorServer(getMBeanServerURL(), null, mBeanServer);
                cs.start();

            }
            return mBeanServer;
        } catch (Exception ex) {
            throw new PogamutException("Error during JMX initialization.", ex);
        }
    }

    @Override
    public synchronized void close() throws PogamutException {
        if (log.isLoggable(Level.WARNING)) log.warning("Closing the platform.");
        try {
            if (cs != null) {
            	cs.stop();
            }
            cs = null;
            if (mBeanServer != null) {
            	mBeanServer.unregisterAll();
            	mBeanServer.clearSaved();
            }
            mBeanServer = null;
        } catch (Exception ex) {
            throw new PogamutException("Could not shutdown the mBeanServer!", ex, log);
        } finally {
		    try {
		    	NetworkLogManager.getNetworkLogManager().kill();
		    } catch (Exception ex2) {
		    	throw new PogamutException("Could not shutdown the log manager!", ex2, log);
		    } finally {
		    	PogamutJVMComm.platformClose();
		    }
        }
    }
    /** Properties loaded from the platform property file. */
    Properties platformProperties = null;

    /**
     * @see DefaultPogamutPlatform.getProperty(String)
     * @param key
     * @param def
     * @return
     */
    @Override
    public String getProperty(String key, String def) {
        String val = getProperty(key);
        return val != null ? val : def;

    }

    /**
     * Returns property value. The search order for finding the value is determined dynamicaly by the SPI.
     * There are allways these sources of properties that are accesed in this order:
     * <ol>
     * <li>Get property from -D option supplied when running the JVM</li>
     * <li>Get the property from system properties (eg. specified by <code>set MY_PROP=hello</code>)</li>
     * <li>Load property from PogamutPlatformCustom.properties file in processe's working directory</li>
     * <li>Load property from default platform property file inside Gavialib</li>
     * </ol>
     * However other sources may be added through SPI.
     * @param key
     * @return null if no such property was found
     */
    public String getProperty(String key) {
        return propertiesManager.getProperty(key);
    }

    protected MBeanServerConnection mbsc = null;

    /**
     * @return Connection to a remote MBeanServer
     * @throws cz.cuni.amis.utils.exception.PogamutException
     */
    public MBeanServerConnection getMBeanServerConnection() throws PogamutException {
        // connect through RMI and get the proxy
        try {
            if (mbsc == null) {
                JMXServiceURL url = getMBeanServerURL();
                JMXConnector jmxc = JMXConnectorFactory.connect(url, null);
                mbsc = jmxc.getMBeanServerConnection();
            }
            return mbsc;
        } catch (IOException iOException) {
            throw new PogamutException("IO exception occured while creating remote MBeanServer connector.",
                    iOException);
        }
    }

    @Override
    public int getIntProperty(String key) {
    	String s = getProperty(key);
    	if(s == null) return 0;
        return Integer.parseInt(s);
    }

	@Override
	public boolean getBooleanProperty(String key) {
		String value = getProperty(key);
		if (value == null) return false;
		return value.equalsIgnoreCase("true");
	}
}
