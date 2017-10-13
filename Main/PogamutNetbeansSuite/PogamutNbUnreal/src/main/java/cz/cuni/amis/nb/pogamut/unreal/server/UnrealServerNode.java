package cz.cuni.amis.nb.pogamut.unreal.server;

import cz.cuni.amis.nb.api.pogamut.base.server.ServerTypesManager;
import cz.cuni.amis.nb.api.pogamut.unreal.server.UnrealServerDefinition;
import cz.cuni.amis.nb.pogamut.base.NamedAction;
import cz.cuni.amis.nb.pogamut.base.server.ServerNode;
import cz.cuni.amis.nb.pogamut.unreal.map.PureMapTopComponent;
import cz.cuni.amis.nb.util.flag.FlagChildren;
import cz.cuni.amis.pogamut.unreal.communication.messages.gbinfomessages.IMapList;
import cz.cuni.amis.pogamut.unreal.server.IUnrealServer;
import cz.cuni.amis.pogamut.unreal.server.exception.MapChangeException;

import cz.cuni.amis.utils.exception.PogamutException;
import cz.cuni.amis.utils.flag.FlagListener;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Action;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.actions.PropertiesAction;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.actions.SystemAction;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 * Basic UT2004 server node.
 * @author ik
 */
public abstract class UnrealServerNode<T extends UnrealServerDefinition> extends ServerNode<T> {

    public UnrealServerNode(T serverDefinition, String serversManagerID) {
        super(serverDefinition,
                ServerTypesManager.getServersManager(serversManagerID),
                new FlagChildren(serverDefinition.getServerFlag()) {

                    @Override
                    protected Node[] createNodes(Object arg0) {

                        if (arg0 != FlagChildren.EMPTY) {
                            IUnrealServer server = (IUnrealServer) arg0;
                            return new Node[]{
                                        new AgentsNode(server),
                                        new PlayersNode(server),
                                        new TimelinesNode(server)};
                        } else {
                            return new Node[0];
                        }
                    }
                });
    }

    @Override
    protected String createName() {
        IUnrealServer server = (IUnrealServer) serverDef.getServerFlag().getFlag();
        String name = (String) serverDef.getServerNameFlag().getFlag();
        if (server != null && server.getMapName() != null) {
            return name + " [" + server.getMapName() + "]";
        } else {
            return name;
        }
    }

    @Override
    protected Image getServerIcon() {
        return ImageUtilities.loadImage("cz/cuni/amis/nb/pogamut/ut2004/server/UT2004Servers.gif");
    }
    /**
     * Default properties of the server.
     */
    Sheet.Set unrealProps = null;

    protected Sheet.Set createPropSet() {
        Sheet.Set propset = Sheet.createPropertiesSet();
        propset.setName("UnrealProps");
        propset.setDisplayName("Unreal Properties");

        // server map property
        // TODO how to dynamicaly update unrealProps sheet?

        propset.put(new MapProperty());

        return propset;
    }

    protected class MapProperty extends Node.Property {//PropertySupport.ReadWrite {

        int[] keys;
        String[] mapNames;
        static final String noMap = "N/A";
        Map<String, Integer> mapToKey;
        boolean serverUp = false;

        public MapProperty() {
            super(Integer.class);
            setDisplayName("Map");
            setName("serverMap");
            getServerDefinition().getServerFlag().addListener(new FlagListener<IUnrealServer>() {

                @Override
                public void flagChanged(IUnrealServer changedValue) {
                    initValues(changedValue);
                    try {
                        firePropertyChange(getName(), null, getValue());
                    } catch (Exception ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            });
            initValues((IUnrealServer) getServerDefinition().getServerFlag().getFlag());
        }

        protected void initValues(IUnrealServer server) {

            if (server != null) {
                serverUp = true;
                // there is server assigned
                Collection<IMapList> maps = server.getAvailableMaps();
                mapNames = new String[maps.size()];
                mapToKey = new HashMap<String, Integer>();

                keys = new int[maps.size()];
                int i = 0;
                for (IMapList map : maps) {
                    String name = map.getName();
                    mapNames[i] = name;
                    keys[i] = i;
                    mapToKey.put(name.toUpperCase(), i);
                    i++;
                }
            } else {
                serverUp = false;
                keys = new int[]{0};
                mapNames = new String[]{noMap};
            }

            setValue("intValues", keys);
            setValue("stringKeys", mapNames);
        }

        @Override
        public boolean canRead() {
            return true;
        }

        @Override
        public boolean canWrite() {
            return serverUp;
        }

        @Override
        public void setValue(Object val) {
            try {
                ((IUnrealServer)getServerDefinition().getServerFlag().getFlag()).setGameMap(mapNames[(Integer) val]);
            } catch (MapChangeException ex) {
                // TODO
                Exceptions.printStackTrace(ex);
            }
        }

        @Override
        public Object getValue() {
            if (serverUp) {
                return mapToKey.get(((IUnrealServer)getServerDefinition().getServerFlag().getFlag())
                        .getMapName().toUpperCase());
            } else {
                return 0;
            }
        }
    }
    protected static final String UT_SHEET = "UTPropSet";

    @Override
    protected Sheet createSheet() {
        Sheet sheet = super.createSheet();
        unrealProps = createPropSet();
        sheet.put(unrealProps);
        // TODO game type property

        return sheet;
    }

    @Override
    public Action[] getActions(boolean context) {
        Action[] oldActs = super.getActions(context);
        Action[] acts = Arrays.copyOf(oldActs, oldActs.length + 7);

        // open map
        acts[oldActs.length + 1] = getPreferredAction();
        // spectating action
        acts[oldActs.length + 2] = new NamedAction("ACT_Spectate", UnrealServerNode.class) {

            @Override
            protected void action(ActionEvent e) throws PogamutException {
                try {
                    serverDef.spectate();
                } catch (PogamutException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        };

        // connect native bot action
        acts[oldActs.length + 4] = new ServerUpAction<IUnrealServer>("ACT_AddNativeBot", UnrealServerNode.class, serverDef) {

            @Override
            protected void action(ActionEvent e) throws PogamutException {
                // show dialog
                NotifyDescriptor.InputLine botTypeDialog = new NotifyDescriptor.InputLine("Bot type (optional): ", "Native bot type");
                DialogDisplayer.getDefault().notify(botTypeDialog);

                if (botTypeDialog.isValid()) {

                    String botType = botTypeDialog.getInputText().trim();
                    getServer().connectNativeBot(null, botType);
                }
            }
        };

        acts[oldActs.length + 6] = SystemAction.get(PropertiesAction.class);
        return acts;
    }
    private Action openMapAction;

    /**
     * Open a TC with map.
     * @return
     */
    @Override
    public Action getPreferredAction() {
        if (openMapAction == null) {
            openMapAction = new OpenPureMap();
        }
        return openMapAction;
    }

    /**
     * Open pure map for serverdef this node represents
     */
    protected class OpenPureMap extends NamedAction {

        public OpenPureMap() {
            super("ACT_OpenMap", UnrealServerNode.class);
        }
        // Unique id of map tc
        private String uid;

        @Override
        public synchronized void action(ActionEvent e) {
            if (serverDef.getServerFlag().getFlag() != null) {
                TopComponent map = WindowManager.getDefault().findTopComponent(uid);
                if (map == null) {
                    map = new PureMapTopComponent(serverDef);
                    uid = WindowManager.getDefault().findTopComponentID(map);
                }
                map.open();
                map.requestActive();
            }
        }
    }
}
