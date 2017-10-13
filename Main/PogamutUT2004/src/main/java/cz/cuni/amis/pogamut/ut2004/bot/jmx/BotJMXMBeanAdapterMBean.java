package cz.cuni.amis.pogamut.ut2004.bot.jmx;

import cz.cuni.amis.pogamut.base3d.agent.jmx.Agent3DMBeanAdapterMBean;
import cz.cuni.amis.utils.exception.PogamutException;

/**
 *
 * @author ik
 */
public interface BotJMXMBeanAdapterMBean extends Agent3DMBeanAdapterMBean {

    public void respawn() throws PogamutException;

    /**
     * Configures bot property.
     * @param param see BoolBotParam
     * @param value
     */
    public void boolConfigure(String param, boolean value);

    /**
     * Get configuration parameter value.
     * @param param see BoolBotParam
     * @return
     */
    public boolean retrieveBoolConfigure(String param);
}
