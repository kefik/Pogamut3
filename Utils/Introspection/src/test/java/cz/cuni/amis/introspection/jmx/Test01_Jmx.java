/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.amis.introspection.jmx;

import org.junit.Test;
import cz.cuni.amis.introspection.Folder;
import cz.cuni.amis.introspection.IntrospectionException;
import cz.cuni.amis.introspection.Test01_JavaIntrospection;
import cz.cuni.amis.introspection.java.Introspector;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerFactory;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import static org.junit.Assert.*;

/**
 *
 * @author Ik
 */
public class Test01_Jmx {

    public Test01_Jmx() {
    }
/*
    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
*/
   static  MBeanServer mbs = null;
   static  JMXConnectorServer cs = null;
    
    @BeforeClass
    public static void setUpClass() throws RemoteException, MalformedURLException, IOException {
        Registry r = LocateRegistry.createRegistry(9999);
        mbs = MBeanServerFactory.createMBeanServer();

        person = new Test01_JavaIntrospection.Person("Alice Aho", 23);
        person.knows = new Test01_JavaIntrospection.Person("Bob", 30);
        
        JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:9999/server");
        cs = JMXConnectorServerFactory.newJMXConnectorServer(url, null, mbs);
        if (cs == null) {
        	throw new RuntimeException("Could not setUpClass() for test! JMXConnectorServerFactory.newJMXConnectorServer FAILED (Returned null...)! Url: " + url + ", mbs: " + mbs);
        }
        cs.start();
        System.out.println("Registry created / JMX connector started.");
    }

    @AfterClass
    public static void tearDownClass() throws IOException {
    	if (cs != null) {    		
    		cs.stop();
    		System.out.println("JMX connector stopped");
    	}
    }

    static Test01_JavaIntrospection.Person person = null;
    
    @Test
    public void exportMBean() throws MalformedURLException, IOException, JMException, IntrospectionException {
       Folder folder = Introspector.getFolder("Alice", person);
        FolderMBean.exportFolderHierarchy(folder, mbs, "myDomain", "tutorial");
        System.out.println("---/// TEST OK ///---");
    }

    @Test
    public void changeExportedFolderRemotely() throws MalformedURLException, IOException, MalformedObjectNameException, IntrospectionException {
        // connect through RMI and get the proxy
        JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:9999/server");
        JMXConnector jmxc = JMXConnectorFactory.connect(url, null);
        MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
        ObjectName agentName = ObjectName.getInstance("myDomain:name=Alice,type=tutorial");
        /*DynamicMBean proxy = JMX.newMBeanProxy(mbs, 
                agentName,
                DynamicMBean.class);
        */
        DynamicProxy proxy = new DynamicProxy(agentName, mbsc);
         // convert the proxy to standard Folder
        
        Folder jmxFolder = new DynamicMBeanToFolderAdapter(proxy);
        //jmxFolder.getProperty("name").setValue("Cecil Corn");
        String newName = "Cecil Corn";
        jmxFolder.getFolder("knows").getProperty("name").setValue(newName);
        //mbsc.setAttribute(agentName, new Attribute("name", "Bob"));

       // waitForEnterPressed();
        assertTrue(person.knows.name.equals(newName));
        System.out.println("---/// TEST OK ///---");
    }

    private static void waitForEnterPressed() {
        try {
            System.out.println("\nPress <Enter> to continue...");
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}