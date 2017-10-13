package cz.cuni.amis.pogamut.ut2004.bot.jmx;

import java.io.IOException;
import java.net.MalformedURLException;

import javax.management.MalformedObjectNameException;

import cz.cuni.amis.pogamut.base3d.agent.jmx.Agent3DJMXProxy;
import cz.cuni.amis.pogamut.ut2004.bot.IUT2004Bot;
import cz.cuni.amis.utils.exception.PogamutException;

/**
 *
 * @author ik
 */
public class BotJMXProxy extends Agent3DJMXProxy implements IUT2004Bot {

    public BotJMXProxy(String agentJMXAddress) throws MalformedURLException, IOException, MalformedObjectNameException {
        super(agentJMXAddress);
    }

    public void respawn() throws PogamutException {
        call("respawn");
    }

    public void setBoolConfigure(BoolBotParam param, boolean value) {
        callNoException("boolConfigure", new Object[]{param.toString(), value},
                new String[] {String.class.getName(), Boolean.TYPE.getName()});
    }

    public boolean getBoolConfigure(BoolBotParam param) {
        return (Boolean)callNoException("retrieveBoolConfigure", new Object[]{param.toString()},
                new String[] {String.class.getName()});

    }

}
