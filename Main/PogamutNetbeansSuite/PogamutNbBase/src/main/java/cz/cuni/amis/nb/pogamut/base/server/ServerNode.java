package cz.cuni.amis.nb.pogamut.base.server;

import cz.cuni.amis.nb.api.pogamut.base.server.ServerDefinition;
import cz.cuni.amis.nb.api.pogamut.base.server.ServersManager;
import cz.cuni.amis.nb.pogamut.base.NamedAction;
import cz.cuni.amis.nb.util.NodeFactory;
import cz.cuni.amis.pogamut.base.agent.IAgent;
import cz.cuni.amis.pogamut.base.agent.state.level0.IAgentState;
import cz.cuni.amis.pogamut.base.server.IWorldServer;
import cz.cuni.amis.utils.exception.PogamutException;
import cz.cuni.amis.utils.flag.Flag;
import cz.cuni.amis.utils.flag.FlagListener;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.beans.PropertyEditor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import javax.swing.Action;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.ImageUtilities;

/**
 * Viewer for server instance.
 * @author ik
 */
public abstract class ServerNode<T extends ServerDefinition> extends AbstractNode {

    ServersManager<T> serversManager = null;
    protected T serverDef = null;
    protected IWorldServer actualServer = null;
    protected FlagListener<IAgentState> serverStateListener = new FlagListener<IAgentState>() {

        @Override
        public void flagChanged(IAgentState changedValue) {
            serverStateChangeHandler(changedValue);
        }
    };
    /**
     * Used for icon badging and name update.
     */
    FlagListener serverListener = new FlagListener() {

        @Override
        public void flagChanged(Object changedValue) {
            serverChangedHandler((IWorldServer) changedValue);
        }
    };

    public void serverChangedHandler(IWorldServer newServer) {
        registerServerStateListener(newServer);
        setDisplayName(createName());
        fireIconChange();
    }

    public void registerServerStateListener(IWorldServer newServer) {
        if (actualServer != null) {
            actualServer.getState().removeListener(serverStateListener);
        }
        actualServer = newServer;
        if (actualServer != null) {
            newServer.getState().addListener(serverStateListener);
        }
    }

    public void serverStateChangeHandler(IAgentState serverState) {
        setDisplayName(createName());
    }

    /**
     * Default constructor, Pogamut agents are directly under the Server node.
     * @param serverDef
     * @param serversManager
     * @param nodeFactory
     */
    public ServerNode(final T serverDef, ServersManager<T> serversManager, final NodeFactory<IAgent> nodeFactory) {
        super(new ServerDefAgents(serverDef) {

            @Override
            protected Node[] createNodes(IAgent arg0) {

                return nodeFactory.create(arg0);
            }
        });
        init(serverDef);
        this.serversManager = serversManager;

    }

    public ServerNode(final T serverDef, ServersManager<T> serversManager, Children children) {
        super(children);

        init(serverDef);
        this.serversManager = serversManager;

        serverDef.getServerFlag().addListener(serverListener);
    }

    protected void init(T serverDef) {
        this.serverDef = serverDef;
        setDisplayName(createName());
        if (serverDef.getServerFlag().getFlag() != null) {
            registerServerStateListener((IWorldServer) serverDef.getServerFlag().getFlag());
        }
        serverDef.getServerNameFlag().addListener(new FlagListener<String>() {

            @Override
            public void flagChanged(String changedValue) {
                setDisplayName(createName());
            }
        });

    }

    /**
     * @return server definition represented by this node
     */
    protected T getServerDefinition() {
        return serverDef;
    }

    @Override
    public Action[] getActions(boolean context) {
        return new Action[]{
                    /*                    new NamedAction("ACT_SetAsDefault") {

                    public void action(ActionEvent e) throws PogamutException {
                    serversManager.getDefaultServer().setFlag(serverDef);
                    }

                    @Override
                    public boolean isEnabled() {
                    return !isDefaultServer();
                    }
                    },
                     */new NamedAction("ACT_RemoveServer") {

                public void action(ActionEvent e) throws PogamutException {
                    serversManager.removeServer(serverDef);
                }
            },};
    }

    protected String createName() {
        return serverDef.getServerNameFlag().getFlag() + " [" + serverDef.getUriFlag().getFlag().toString() + "]";
    }

    void updateNodeName() {
        String name = createName();
        if (isDefaultServer()) {
            name = "<b>" + name + "</b>";
        }
        setDisplayName(name);
    }

    boolean isDefaultServer() {
        return serversManager.getDefaultServer().getFlag() == serverDef;
    }

    /**
     * @return Custom class of editor used for editing server URI. Should handle protocol specifics like default port etc.
     */
    protected abstract Class<? extends PropertyEditor> getURIPropEditorClass();

    @Override
    protected Sheet createSheet() {
        Sheet sheet = super.createSheet();
        Sheet.Set props = sheet.get(Sheet.PROPERTIES);
        if (props == null) {
            props = Sheet.createPropertiesSet();
            sheet.put(props);
        }
        try {
            PropertySupport.Reflection prop = new ReflProp(serverDef, URI.class, "uri", "URI");
            prop.setPropertyEditorClass(getURIPropEditorClass());
            props.put(new Property[]{
                        new ReflProp(serverDef, String.class, "serverName", "Server name"), // TODO not working
                        prop,});

        } catch (NoSuchMethodException ex) {
            ex.printStackTrace();
        }

        return sheet;
    }

    public static class ReflProp extends PropertySupport.Reflection {

        String name = null;

        public ReflProp(Object o, Class c, String prop, String dispName) throws NoSuchMethodException {
            super(o, c, prop);
            setName(prop);
            this.name = dispName;
        }

        @Override
        public String getDisplayName() {
            return name;
        }
    }

    public static class FlagProp extends PropertySupport.ReadWrite<String> {

        Flag<String> flag;

        public FlagProp(Flag<String> flag, String propName, String shortDescr) {
            super(flag.toString(), String.class, propName, shortDescr);
            this.flag = flag;
        }

        @Override
        public String getValue() throws IllegalAccessException, InvocationTargetException {
            return flag.getFlag();
        }

        @Override
        public void setValue(String arg0) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            flag.setFlag(arg0);
        }
    }

    /**
     * Action that is enabled only when the server is not null.
     */
    public abstract static class ServerUpAction<T extends IWorldServer> extends NamedAction {

        ServerDefinition<T> def = null;

        public ServerUpAction(String key, Class cls, ServerDefinition<T> def) {
            super(key, cls);
            this.def = def;
        }

        @Override
        public boolean isEnabled() {
            return getServer() != null;
        }

        protected T getServer() {
            return def.getServerFlag().getFlag();
        }
    }

    protected abstract Image getServerIcon();

    @Override
    public Image getOpenedIcon(int type) {
        return getIcon(type);
    }

    @Override
    public Image getIcon(int type) {

        Object server = getServerDefinition().getServerFlag().getFlag();
        Image badge = null;
        if (server == null) {
            badge = getBadgeIcon("Error");
        } else {
            badge = getBadgeIcon("Running");
        }

        return ImageUtilities.mergeImages(getServerIcon(), badge, 4, 4);
    }

    private Image getBadgeIcon(String badge) {
        return ImageUtilities.loadImage("cz/cuni/amis/nb/pogamut/base/icons/" + badge + "BadgeIcon.png");
    }
}
