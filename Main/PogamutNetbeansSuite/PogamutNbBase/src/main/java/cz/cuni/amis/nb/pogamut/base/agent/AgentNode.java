package cz.cuni.amis.nb.pogamut.base.agent;

import cz.cuni.amis.introspection.IntrospectionException;
import cz.cuni.amis.nb.pogamut.base.introspection.FolderNode;
import cz.cuni.amis.nb.util.Updater;
import cz.cuni.amis.nb.util.collections.ObservableCollectionNode;
import cz.cuni.amis.pogamut.base.agent.IAgent;
import cz.cuni.amis.pogamut.base.agent.state.level0.IAgentState;
import cz.cuni.amis.pogamut.base.agent.state.level1.IAgentStateDown;
import cz.cuni.amis.pogamut.base.agent.state.level2.IAgentStateFailed;
import cz.cuni.amis.pogamut.base.agent.state.level2.IAgentStatePaused;
import cz.cuni.amis.pogamut.base.agent.state.level2.IAgentStatePausing;
import cz.cuni.amis.pogamut.base.agent.state.level2.IAgentStateResuming;
import cz.cuni.amis.pogamut.base.agent.state.level2.IAgentStateRunning;
import cz.cuni.amis.pogamut.base.agent.state.level2.IAgentStateStopped;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.pogamut.base.utils.logging.NetworkLogClient;
import cz.cuni.amis.pogamut.base.utils.logging.NetworkLogClient.LogRead;
import cz.cuni.amis.pogamut.base.utils.logging.NetworkLogEnvelope;
import cz.cuni.amis.utils.collections.ObservableList;
import cz.cuni.amis.utils.flag.FlagListener;
import java.awt.Image;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.util.LinkedList;
import java.util.List;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;

/**
 * Provides 
 * <ul>
 * <li> basic actions for controlling agents in the GUI.
 * <li> timer for updating node
 * </ul>
 * @author ik
 */
public abstract class AgentNode<T extends IAgent> extends ObservableCollectionNode<Node> implements Updater {

    protected List<Runnable> updateSubtasks = new LinkedList<Runnable>();
    /**
     * Used for icon badging.
     */
    FlagListener<IAgentState> agentStateListener = new FlagListener<IAgentState>() {

        @Override
        public void flagChanged(IAgentState changedValue) {
            if (changedValue instanceof IAgentStateDown) {
                logClient.stop();
            }
            fireIconChange();
        }
    };
    /**
     * Agent represented by this class
     */
    protected T agent = null;

    /**
     * Client that is used for receiving logs.
     */
    protected NetworkLogClient logClient = null;

    public AgentNode(final T agent) {
        super(new ObservableList<Node>(new LinkedList<Node>()));
        this.agent = agent;
        setName(agent.getName());
        agent.getState().addListener(agentStateListener);

        // enable network logger on the agent
        agent.getLogger().addDefaultNetworkHandler();

        // add children nodes
        try {
            getChildrenCollection().add(new LogsNode(agent.getLogger()));
        } catch (UnsupportedOperationException ex) {
            // nothing happens, not all IAgents must have loggers
        }
        try {
            getChildrenCollection().add(new FolderNode.Root(agent, (Updater) this));
        } catch (IntrospectionException ex) {
            Exceptions.printStackTrace(ex);
            throw new RuntimeException(ex);
        } catch (UnsupportedOperationException ex) {
            // nothing happens, not all IAgents must have introspection
        }

        // start log client and receive logs
        IAgentLogger agentLogger = agent.getLogger();
        String networkLogHost = agentLogger.getNetworkLoggerHost();
        Integer networkLogPort = agentLogger.getNetworkLoggerPort();
        String agentId = agent.getComponentId().getToken();
        logClient = new NetworkLogClient(agent.getLogger().getNetworkLoggerHost(), agent.getLogger().getNetworkLoggerPort(), agent.getComponentId().getToken());
        logClient.addListener(new NetworkLogClient.ILogReadListener() {
            @Override
            public void notify(LogRead event) {
                NetworkLogEnvelope record = event.getRecord();
                LogCategory category = agent.getLogger().getCategory(record.getCategory());
                category.log(record.asLogRecord());
            }
        });
        logClient.start();

        if (logClient.getConnected().getFlag() == null || !logClient.getConnected().getFlag()) {
            System.out.println("[WARNING] Could not connect to the network logger of agent " + agent.getComponentId().getToken() + " at " + agent.getLogger().getNetworkLoggerHost() + ":" + agent.getLogger().getNetworkLoggerPort());
        }
    }

    /**
     * Adds task that will be periodically called. It could be GUI updating or
     * logging etc.
     * @param task
     */
    @Override
    public void addUpdateTask(Runnable task) {
        updateSubtasks.add(task);
    }

    protected boolean isEndState(IAgentState state) {
        return state.isState(IAgentStateStopped.class, IAgentStateFailed.class);
    }

    public T getAgent() {
        return agent;
    }

    Image agentIcon = null;

    @Override
    public Image getIcon(int type) {
        if(agentIcon == null) {
            agentIcon = loadAgentIcon();
        }

        IAgentState agentState = agent.getState().getFlag();

        if (isEndState(agentState)) {
            // return grayscale icon
            ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
            ColorConvertOp op = new ColorConvertOp(cs, null);

            return op.filter((BufferedImage) agentIcon, null);
        } else {
            // use badge to indicate state of the agent
            Image badge = getBadgeIcon(agentState);

            return ImageUtilities.mergeImages(agentIcon, badge, 4, 4);
        }
    }

    private boolean isOKState(IAgentState state) {
        return state.isState(IAgentStateRunning.class, IAgentStateResuming.class);
    }

    protected Image getBadgeIcon(IAgentState state) {
        if (state.isState(IAgentStatePaused.class, IAgentStatePausing.class)) {
            // add paused badge when the agent isn't running
            return getBadgeIcon("Paused");
        }

        if (isOKState(state)) {
            // add running badge when everything is OK
            return getBadgeIcon("Running");
        }

        return getBadgeIcon("Error");
    }

    private Image getBadgeIcon(String badge) {
        return ImageUtilities.loadImage("cz/cuni/amis/nb/pogamut/base/icons/" + badge + "BadgeIcon.png");
    }

    @Override
    public Image getOpenedIcon(int arg0) {
        return getIcon(arg0);
    }

    /**
     * Used to get the iconic representation of the agent.
     * @return
     */
    public abstract Image loadAgentIcon();
}
