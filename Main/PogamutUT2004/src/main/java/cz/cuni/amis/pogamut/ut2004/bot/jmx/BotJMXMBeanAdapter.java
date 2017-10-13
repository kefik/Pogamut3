/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.amis.pogamut.ut2004.bot.jmx;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import cz.cuni.amis.pogamut.base3d.agent.jmx.Agent3DMBeanAdapter;
import cz.cuni.amis.pogamut.ut2004.bot.IUT2004Bot;
import cz.cuni.amis.pogamut.ut2004.bot.IUT2004Bot.BoolBotParam;
import cz.cuni.amis.utils.exception.PogamutException;

/**
 * This is the other side (the agent side) of BotJMXProxy (th eplugin side).
 * @author ik
 */
public class BotJMXMBeanAdapter<T extends IUT2004Bot> extends Agent3DMBeanAdapter<T> implements BotJMXMBeanAdapterMBean {

    public BotJMXMBeanAdapter(T agent, ObjectName objectName, MBeanServer mbs) throws MalformedObjectNameException, InstanceAlreadyExistsException, InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException {
        super(agent, objectName, mbs);
    }

    public void respawn() throws PogamutException  {
        getAgent().respawn();
    }

    public void boolConfigure(String param, boolean value) {
        getAgent().setBoolConfigure(BoolBotParam.valueOf(param), value);
    }

    public boolean retrieveBoolConfigure(String param) {
        return getAgent().getBoolConfigure(BoolBotParam.valueOf(param));
    }
}
