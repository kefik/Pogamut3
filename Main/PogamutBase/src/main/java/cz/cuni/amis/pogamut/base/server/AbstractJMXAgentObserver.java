package cz.cuni.amis.pogamut.base.server;

import java.io.IOException;
import java.util.ArrayList;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import cz.cuni.amis.pogamut.base.agent.IAgent;
import cz.cuni.amis.utils.collections.ObservableCollection;
import cz.cuni.amis.utils.collections.ObservableList;


class AbstractJMXAgentObserver implements IWorldAgentsObserver<IAgent> {

       
    protected ObservableCollection<IAgent> agents = 
            new ObservableList<IAgent>(new ArrayList<IAgent>());
    /**
     * Creates JMX wrapper for agent on specified adress and adds it to the list
     * of all connected agents.
     * @param serviceUrl URL of the JMX service where remote agent resides eg. service:jmx:rmi:///jndi/rmi://localhost:9999/server
     * @param objectName name of the MBean representing agent eg. myDomain:name=MyAgent1
     */
    protected void addJMXAgentFromAdress(String serviceUrl, ObjectName objectName) throws IOException {
        JMXServiceURL url = new JMXServiceURL(serviceUrl);
        JMXConnector jmxc = JMXConnectorFactory.connect(url, null);
        MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
        
        IAgent agent = JMX.newMXBeanProxy(mbsc, objectName, IAgent.class);
        
        agents.add(agent);
    }

    public ObservableCollection<IAgent> getAgents() {
            return agents;
    }
    
}