package cz.cuni.amis.pogamut.base.agent.jmx.proxy;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.logging.Logger;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import cz.cuni.amis.pogamut.base.agent.IGhostAgent;
import cz.cuni.amis.pogamut.base.communication.command.IAct;
import cz.cuni.amis.pogamut.base.communication.command.ICommandListener;
import cz.cuni.amis.pogamut.base.communication.exception.CommunicationException;
import cz.cuni.amis.pogamut.base.communication.messages.CommandMessage;
import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.base.utils.jmx.PogamutJMX;
import cz.cuni.amis.utils.token.Token;
import cz.cuni.amis.utils.token.Tokens;

/**
 *
 * @author ik
 */
public class GhostAgentJMXProxy extends AgentJMXProxy implements IGhostAgent {

    IAct act = null;

    public GhostAgentJMXProxy(String agentJMXAddress) throws MalformedURLException, IOException, MalformedObjectNameException {
        super(agentJMXAddress);
        final ObjectName actMBeanName = PogamutJMX.getObjectName(getObjectName(), PogamutJMX.ACT_NAME);
        act = new IAct() {

        	Token componentId = Tokens.get(PogamutJMX.ACT_NAME);
        	
            @Override
            public void act(CommandMessage command) throws CommunicationException {
                try {
                    getMBeanServerConnection().invoke(actMBeanName, "act", new Object[]{command}, null);
                } catch (Exception ex) {
                    throw  new CommunicationException("JMX error sending command.", ex, this);
                }
            }

            @Override
            public void addCommandListener(Class commandClass, ICommandListener listener) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void removeCommandListener(Class commandClass, ICommandListener listener) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

			@Override
			public Token getComponentId() {
				return componentId;
			}
			
			@Override
			public boolean isCommandListening(Class commandClass,
					ICommandListener listener) {
				throw new UnsupportedOperationException("Not supported yet.");
			}
			
        };
    }

    @Override
    public IAct getAct() {
        return act;
    }

	@Override
	public IWorldView getWorldView() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

}
