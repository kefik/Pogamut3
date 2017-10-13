package cz.cuni.amis.pogamut.ut2004.test;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.junit.After;
import org.junit.Before;

import cz.cuni.amis.pogamut.base.agent.IAgent;
import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.agent.impl.AbstractAgent;
import cz.cuni.amis.pogamut.base.agent.impl.AgentId;
import cz.cuni.amis.pogamut.base.agent.params.IRemoteAgentParameters;
import cz.cuni.amis.pogamut.base.agent.state.WaitForAgentStateChange;
import cz.cuni.amis.pogamut.base.agent.state.level0.IAgentState;
import cz.cuni.amis.pogamut.base.agent.state.level1.IAgentStateDown;
import cz.cuni.amis.pogamut.base.agent.state.level1.IAgentStateUp;
import cz.cuni.amis.pogamut.base.factory.IAgentFactory;
import cz.cuni.amis.pogamut.base.utils.Pogamut;
import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.pogamut.base.utils.logging.LogPublisher;
import cz.cuni.amis.pogamut.ut2004.bot.IUT2004BotController;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.bot.params.UT2004BotParameters;
import cz.cuni.amis.pogamut.ut2004.factory.guice.remoteagent.UT2004BotFactory;
import cz.cuni.amis.pogamut.ut2004.factory.guice.remoteagent.UT2004BotModule;
import cz.cuni.amis.pogamut.ut2004.observer.IUT2004Observer;
import cz.cuni.amis.pogamut.ut2004.server.IUT2004Server;
import cz.cuni.amis.pogamut.ut2004.server.exception.UCCStartException;
import cz.cuni.amis.pogamut.ut2004.utils.PogamutUT2004Property;
import cz.cuni.amis.pogamut.ut2004.utils.UCCWrapper;
import cz.cuni.amis.pogamut.ut2004.utils.UCCWrapperConf;
import cz.cuni.amis.pogamut.ut2004.utils.UT2004BotRunner;
import cz.cuni.amis.pogamut.ut2004.utils.UT2004ObserverRunner;
import cz.cuni.amis.pogamut.ut2004.utils.UT2004ServerRunner;
import cz.cuni.amis.utils.exception.PogamutException;

/**
 * Base class for tests that need a running UCC instance. If you inherit from
 * this class then before the first @Test method will be called the UCC server
 * will be executed. The ports where it is listening can be obtained by
 * <code>ucc.getGbPort()</code> and <code>ucc.getControlPort()</code> calls.
 * Don't forget to specify pogamut.unreal.home and pogamut.unreal.serverexec
 * variables.
 * 
 * @author ik
 */
public class UT2004Test {
	
	public static final String[] CTF_MAPS = new String[] {
		"CTF-1on1-Joust",
		"CTF-AbsoluteZero",
		"CTF-Avaris",
		"CTF-BridgeOfFate",
		"CTF-Chrome",
		"CTF-Citadel",
		"CTF-Colossus",
		"CTF-December",
		"CTF-DE-ElecFields",
		"CTF-DoubleDammage",
		"CTF-Face3",
		"CTF-FaceClassic",
		"CTF-Geothermal",
		"CTF-Grassyknoll",
		"CTF-Grendelkeep",
		"CTF-January",
		"CTF-Lostfaith",
		"CTF-Magma",
		"CTF-Maul",
		"CTF-MoonDragon",
		"CTF-Orbital2",
		"CTF-Smote",
		"CTF-TwinTombs"
	};
	
	public static String[] DM_MAPS = new String[] {
		"DM-1on1-Albatross",
		"DM-1on1-Crash",
		"DM-1on1-Desolation",
		"DM-1on1-Idoma",
		"DM-1on1-Irondust",
		"DM-1on1-Mixer",
		"DM-1on1-Roughinery",
		"DM-1on1-Serpen-tine",
		"DM-1on1-Spirit",
		"DM-1on1-Squader",
		"DM-1on1-Trite",
		"DM-Antalus",
		"DM-Asbestos",
		"DM-Compressed",
		"DM-Corrugation",
		"DM-Curse4",
		"DM-Deck17",
		"DM-DE-Grendelkeep",
		"DM-DE-Ironic",
		"DM-DE-Osiris2",
		"DM-DesertIsle",
		"DM-Flux2",
		"DM-Gael",
		"DM-Gestalt",
		"DM-Goliath",
		"DM-HyperBlast2",
		"DM-Icetomb",
		"DM-Inferno",
		"DM-Injector",
		"DM-Insidious",
		"DM-IronDeity",
		"DM-Junkyard",
		"DM-Leviathan",
		"DM-Metallurgy",
		"DM-Morpheus3",
		"DM-Oceanic",
		"DM-Phobos2",
		"DM-Plunge",
		"DM-Rankin",
		"DM-Rrajigar",
		"DM-Rustatorium",
		"DM-Sulphur",
		"DM-TokaraForest",
		"DM-TrainingDay"
	};

	protected IAgentId testId;
	
	protected LogCategory log;

    protected UCCWrapper ucc = null;
    
    /**
     * TRUE == use ucc executed through uccwrapper
     * FALSE == use externaly executed instance
     */
    //protected boolean useInternalUcc = !Pogamut.getPlatform().getBooleanProperty(PogamutUT2004Property.POGAMUT_UNREAL_TEST_EXT_SERVER.getKey());
    protected boolean useInternalUcc = true;
    //protected boolean useInternalUcc = true;
    
    /**
     * If not null will be used.
     */
    protected String unrealHome = "C:\\Games\\UT";
    //protected String unrealHome = "D:/Games/UT2004-Devel";

    public UT2004Test() {
    	this.testId = new AgentId("Test");
    	this.log = new LogCategory("UT2004Test");
    	this.log.addHandler(new LogPublisher.ConsolePublisher(testId));
    }
        
    /**
     * Starts UCC server.
     *
     * @throws cz.cuni.amis.pogamut.ut2004.server.exceptions.UCCStartException
     */
    public void startUCC(UCCWrapperConf uccConf) throws UCCStartException {
    	if (unrealHome != null) {
    		uccConf.setUnrealHome(unrealHome);
    	}
        if (useInternalUcc) {            
            ucc = new UCCWrapper(uccConf);
        }
    }
    
    public void endUcc() {
    	if (useInternalUcc) {
            ucc.stop();
        }
    }

    /**
     * Initialize UCC server.
     * @throws UCCStartException
     */
    @Before
    public void beforeTest() throws UCCStartException {
    	startUCC(new UCCWrapperConf());    	
    }

    /**
     * Kills the UCC server and closes PogamutPlatform.
     */
    @After
    public void afterTest() {
    	endUcc();
        Pogamut.getPlatform().close();
    }    

    /**
     * Waits till 'agent' changes its state to {@link IAgentStateUp}.
     * <p><p>
     * 60s timeout.
     * 
     * @param agent
     * @return
     */
    protected boolean awaitAgentUp(AbstractAgent agent) {
    	System.out.println("Awaiting server UP(timeout 60s)...");
    	IAgentState state = new WaitForAgentStateChange(agent.getState(), IAgentStateUp.class).await(60000, TimeUnit.MILLISECONDS);
    	return state != null && state instanceof IAgentStateUp;
    }
    
    /**
     * Waits till 'agent' changes its state to {@link IAgentStateDown}.
     * <p><p>
     * 60s timeout.
     * 
     * @param agent
     * @return
     */
    protected boolean awaitAgentDown(AbstractAgent agent) {
    	System.out.println("Awaiting server DOWN (timeout 60s)...");
    	IAgentState state = new WaitForAgentStateChange(agent.getState(), IAgentStateDown.class).await(120000, TimeUnit.MILLISECONDS);
    	return state != null && state instanceof IAgentStateDown;
    }
    
    /**
     * Starts new bot in the environment.
     * @param <T>
     * @param controller controller that will be used for newly created bot
     * @return running bot with the given controller
     */
    protected <T extends IUT2004BotController> UT2004Bot startUTBot(Class<T> controller) {
    	return startUTBot(controller, null);
    }
    
    /**
     * Starts new bot in the environment with specified 'params'.
     * @param <T>
     * @param controller controller that will be used for newly created bot
     * @return running bot with the given controller
     */
    protected <T extends IUT2004BotController> UT2004Bot startUTBot(Class<T> controller, UT2004BotParameters params) {

        UT2004BotFactory factory = new UT2004BotFactory(new UT2004BotModule(controller));

        String host = Pogamut.getPlatform().getProperty(PogamutUT2004Property.POGAMUT_UT2004_BOT_HOST.getKey());
        int port = Pogamut.getPlatform().getIntProperty(PogamutUT2004Property.POGAMUT_UT2004_BOT_PORT.getKey());
        if (useInternalUcc) {
            host = ucc.getHost();
            port = ucc.getBotPort();
        }
        UT2004BotRunner botRunner = new UT2004BotRunner(factory, "TestBot", host, port);
        UT2004Bot bot = 
        	params == null ? (UT2004Bot) botRunner.startAgent()
                           : (UT2004Bot) botRunner.startAgents(params).get(0);
        return bot;
    }
    
    protected <T extends IUT2004BotController> List<UT2004Bot> startAllUTBots(Class<T> controller, UT2004BotParameters... params) {

        UT2004BotFactory factory = new UT2004BotFactory(new UT2004BotModule(controller));

        String host = Pogamut.getPlatform().getProperty(PogamutUT2004Property.POGAMUT_UT2004_BOT_HOST.getKey());
        int port = Pogamut.getPlatform().getIntProperty(PogamutUT2004Property.POGAMUT_UT2004_BOT_PORT.getKey());
        if (host == null) host = "localhost"; 
        if (port == 0) port = 3000;
        
        if (useInternalUcc) {
            host = ucc.getHost();
            port = ucc.getBotPort();
        }
        UT2004BotRunner botRunner = new UT2004BotRunner(factory, "TestBot", host, port);
        botRunner.setPausing(true);
        return botRunner.startAgents(params);
    }
    
    /**
     * Starts new UTServer.
     * @param <T>
     * @return running server connected to UCC instance.
     */
    protected IUT2004Server startUTServer(IAgentFactory<IUT2004Server, IRemoteAgentParameters> factory) {
        String host = Pogamut.getPlatform().getProperty(PogamutUT2004Property.POGAMUT_UT2004_SERVER_HOST.getKey());
        int port = Pogamut.getPlatform().getIntProperty(PogamutUT2004Property.POGAMUT_UT2004_SERVER_PORT.getKey());
        if (useInternalUcc) {
            host = ucc.getHost();
            port = ucc.getControlPort();
        }

        UT2004ServerRunner runner = new UT2004ServerRunner(factory,
                "TEST server",
                host, port) {
            @Override
            protected void preStartHook(IAgent agent) throws PogamutException {
            	super.preStartHook(agent);
            	agent.getLogger().setLevel(Level.ALL);                
            }
        };
        return runner.startAgent();
    }
    
    /**
     * Starts new UTServer.
     * @param <T>
     * @return running server connected to UCC instance.
     */
    protected IUT2004Observer startUTObserver(IAgentFactory<IUT2004Observer, IRemoteAgentParameters> factory) {
        String host = Pogamut.getPlatform().getProperty(PogamutUT2004Property.POGAMUT_UT2004_SERVER_HOST.getKey());
        int port = Pogamut.getPlatform().getIntProperty(PogamutUT2004Property.POGAMUT_UT2004_SERVER_PORT.getKey());
        if (useInternalUcc) {
            host = ucc.getHost();
            port = ucc.getObserverPort();
        }

        UT2004ObserverRunner runner = new UT2004ObserverRunner(factory,
                "TEST observer",
                host, port) {
            @Override
            protected void preStartHook(IAgent agent) throws PogamutException {
            	super.preStartHook(agent);
            	agent.getLogger().setLevel(Level.ALL);
            }
        };
        return runner.startAgent();
    }

}
