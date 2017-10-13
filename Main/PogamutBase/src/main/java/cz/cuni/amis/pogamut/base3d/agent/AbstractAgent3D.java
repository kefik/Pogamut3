package cz.cuni.amis.pogamut.base3d.agent;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import com.google.inject.Inject;

import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.agent.impl.AbstractEmbodiedAgent;
import cz.cuni.amis.pogamut.base.agent.jmx.AgentJMXComponents;
import cz.cuni.amis.pogamut.base.agent.jmx.adapter.AgentMBeanAdapter;
import cz.cuni.amis.pogamut.base.communication.command.IAct;
import cz.cuni.amis.pogamut.base.component.bus.IComponentBus;
import cz.cuni.amis.pogamut.base.utils.guice.AgentScoped;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.pogamut.base3d.agent.jmx.Agent3DMBeanAdapter;
import cz.cuni.amis.pogamut.base3d.worldview.IVisionWorldView;

/**
 * Adds Agent3D special JMX abilities.
 * @author ik
 */
@AgentScoped
public abstract class AbstractAgent3D<WORLD_VIEW extends IVisionWorldView, ACT extends IAct> extends AbstractEmbodiedAgent<WORLD_VIEW, ACT> implements IAgent3D {

    @Inject
    public AbstractAgent3D(IAgentId agentId, IComponentBus eventBus, IAgentLogger logger, WORLD_VIEW worldView, ACT act) {
        super(agentId, eventBus, logger, worldView, act);
    }

    @Override
    protected AgentJMXComponents createAgentJMX() {
        return new AgentJMXComponents<IAgent3D>(this) {

            @Override
            protected AgentMBeanAdapter createAgentMBean(ObjectName objectName, MBeanServer mbs) throws MalformedObjectNameException, InstanceAlreadyExistsException, InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException {
                return new Agent3DMBeanAdapter(AbstractAgent3D.this, objectName, mbs);
            }
        };
    }
}
