package cz.cuni.amis.pogamut.base.utils.logging;

import java.util.logging.Level;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import com.google.inject.Inject;

import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.agent.exceptions.CantStartJMXException;
import cz.cuni.amis.pogamut.base.agent.exceptions.JMXAlreadyEnabledException;
import cz.cuni.amis.pogamut.base.utils.Pogamut;
import cz.cuni.amis.pogamut.base.utils.PogamutProperty;
import cz.cuni.amis.pogamut.base.utils.guice.AgentScoped;
import cz.cuni.amis.pogamut.base.utils.jmx.PogamutJMX;
import cz.cuni.amis.pogamut.base.utils.logging.jmx.JMXLogCategories;
import cz.cuni.amis.utils.ExceptionToString;

/**
 * All logging apis are fine ... but we don't want to have 
 * loggers for classes but for instances - therefore we've created
 * our wrapper allowing you to do two things quickly:
 * <ol>
 * <li>log things</li>
 * <li>create new logger categories</li>
 * </ol>
 * 1) that's obvious - it should be easy
 * <p>
 * 2) this may prove crucial for your debugging to have own logger
 * for planner and another for emotions of your agents, etc.
 * <p><p>
 * Simply - every Agent instance (starting with the first abstract class
 * AbstractAgent) has instance of this class (which is java.logging.Logger(s) wrapper).
 * <p>
 * Every agent's component has own {@link LogCategory} and you may obtain your own via getCategory() method.
 * <p><p>
 * {@link LogCategory} serves as a gateway for your log messages, it contains methods as you
 * know them from java.logging API (things like fine(), info(), severe(), log(Level, msg), etc.).
 * <p><p>
 * Plus it allows you to obtain new {@link LogHandler} instances for that category (if you need to 
 * publish log messages from that category somewhere else).
 * <p>
 * Every {@link LogHandler} serves for filtering messages for one category and publishing them
 * into one end (console, file, memory, whatever...).
 * <p><p>
 * Additionally every {@link LogCategory} has {@link AgentLogger} as its parent.
 * 
 * @author Jimmy
 */
@AgentScoped
public class AgentLogger extends AbstractAgentLogger {

    private ILogCategories categories;
    
    /**
     * Name of the logger inside MBean server, initialized when {@link AgentLogger#enableJMX(MBeanServer, ObjectName)} is called.
     */
	private ObjectName objectName;
	
    @Inject
    public AgentLogger(IAgentId agentId) {
        super(agentId);
        this.categories = new LogCategories();
        String level = Pogamut.getPlatform().getProperty(PogamutProperty.POGAMUT_LOGGER_LEVEL_DEFAULT.getKey());
        Level logLevel;
        if (level == null) {
        	System.err.println("No default level for AgentLogger is specified! Setting WARNING.");
        	logLevel = Level.WARNING;
        } else {
        	try {
        		logLevel = Level.parse(level);
        	} catch (Exception e) {
        		System.err.println("Default AgentLogger level is malformed, could not par log level from: " + level);
        		System.err.println("Setting log level to WARNING.");
        		logLevel = Level.WARNING;
        	}
        }
        if (logLevel != null) {
        	setLevel(logLevel);
        } else {
        	setLevel(Level.WARNING);
        }
    }
    
    public static ObjectName getJMXAgentLoggerName(ObjectName parent) {
    	return PogamutJMX.getObjectName(parent, PogamutJMX.AGENT_LOGGER_SUBTYPE);
    }

    @Override
    public void enableJMX(MBeanServer mBeanServer, ObjectName parent) throws JMXAlreadyEnabledException, CantStartJMXException {    
        if (getCategories() instanceof JMXLogCategories) {
            throw new JMXAlreadyEnabledException("AgentLogger has already JMX turned on.", this);
        }
        try {
        	objectName = ObjectName.getInstance(getJMXAgentLoggerName(parent));
    		mBeanServer.registerMBean(this, objectName);
            categories = new JMXLogCategories(getLogCategories(), mBeanServer, parent);
        } catch (Exception e) {
            throw new CantStartJMXException(ExceptionToString.process("Can't start JMX for agent logger", e), this);
        }
    }

    @Override
    protected ILogCategories getLogCategories() {
        return categories;
    }

}
