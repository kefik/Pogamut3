package cz.cuni.amis.pogamut.ut2004.logging.jmx;

import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;

import cz.cuni.amis.pogamut.base.agent.IAgent;
import cz.cuni.amis.pogamut.ut2004.factory.direct.remoteagent.UT2004ServerFactory;
import cz.cuni.amis.pogamut.ut2004.server.IUT2004Server;
import cz.cuni.amis.pogamut.ut2004.test.UT2004Test;

/**
 * @author ik
 */
public class UT2004Test01_AgentJmxLogging extends UT2004Test {

    /**
     * Tests that log categories are exported through the JMX.
     */
    @Test
    public void testJmxLoggingCategory() {
        final String LOG_CAT_NAME = "testCategory";
        IAgent localBot = startUTBot(TestBotBase.class);
        // add the cathegory
        localBot.getLogger().getCategory(LOG_CAT_NAME);
        Set<String> catNames = localBot.getLogger().getCategories().keySet();

        IUT2004Server server = startUTServer(new UT2004ServerFactory());

        IAgent jmxBot = null;
        // find our agent
        for (IAgent bot : server.getAgents()) {
            if (bot.getComponentId().getToken().equals(localBot.getComponentId().getToken())) {
                jmxBot = bot;
                break;
            }
        }
        Assert.assertTrue("Bot wasn't found through JMX.", jmxBot != null);

        Assert.assertTrue("Some category wasn't present in the JMX proxy.",
                jmxBot.getLogger().getCategories().keySet().containsAll(catNames));
    }
}
