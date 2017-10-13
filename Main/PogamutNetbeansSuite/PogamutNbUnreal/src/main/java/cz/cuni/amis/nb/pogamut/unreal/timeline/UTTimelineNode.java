package cz.cuni.amis.nb.pogamut.unreal.timeline;

import cz.cuni.amis.nb.pogamut.base.NamedAction;
import cz.cuni.amis.nb.pogamut.unreal.server.TimelinesNode;
import cz.cuni.amis.nb.pogamut.unreal.timeline.dataobject.TLDataObject;
import cz.cuni.amis.nb.pogamut.unreal.timeline.records.TLAgentEntity;
import cz.cuni.amis.nb.pogamut.unreal.timeline.records.TLDatabase;
import cz.cuni.amis.nb.pogamut.unreal.timeline.records.TLEntity;
import cz.cuni.amis.nb.pogamut.unreal.timeline.view.TLTools;
import cz.cuni.amis.nb.util.NodeFactory;
import cz.cuni.amis.nb.util.collections.ObservableCollectionNode;
import cz.cuni.amis.pogamut.base.agent.IAgent;
import cz.cuni.amis.pogamut.unreal.bot.IUnrealBot;
import cz.cuni.amis.pogamut.unreal.bot.impl.NativeUnrealBotAdapter;
import cz.cuni.amis.pogamut.unreal.server.IUnrealServer;
import cz.cuni.amis.utils.collections.CollectionEventListener;
import cz.cuni.amis.utils.collections.ObservableCollection;
import cz.cuni.amis.utils.collections.ObservableList;
import cz.cuni.amis.utils.exception.PogamutException;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.openide.cookies.OpenCookie;
import org.openide.cookies.SaveCookie;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;

/**
 * Node representing one Timeline under a list of timelines in the server node.
 * 
 * This is "maybe alive" timeline so it has listeners for new entities and when 
 * some new entity is created in timeline, this node gets a new child node representing
 * new entity.
 *
 * @see TimelinesNode
 * @author Honza
 */
public class UTTimelineNode extends ObservableCollectionNode<TLEntity> {

    private TLDataObject dataObject;
    private IUnrealServer server;
    private DBAgentsUpdater agentUpdater;
    private DBAgentsUpdater nativeBotsUpdates;

    public UTTimelineNode(TLDataObject dataObj, IUnrealServer server) {
        super(new ObservableList<TLEntity>(new LinkedList<TLEntity>()),
                new NodeFactory<TLEntity>() {

                    @Override
                    public Node[] create(TLEntity entity) {
                        TLEntityNode node = new TLEntityNode(entity.getDatabase(), entity);

                        return new Node[]{node};
                    }
                });

        setName(dataObj.getName() + " (" + dataObj.getMapName() + ")");

        this.dataObject = dataObj;
        this.server = server;

        TLDatabase db = dataObject.getDatabase();

        this.agentUpdater = new DBAgentsUpdater(db);
        this.nativeBotsUpdates = new DBAgentsUpdater(db);

        db.addDBListener(new UpdateEntityNodes());

    }


    /**
     * When used doubleclicks on the node, open the timeline viewer for this timeline.
     * @return
     */
    @Override
    public Action getPreferredAction() {
        return new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                OpenCookie oc = dataObject.getLookup().lookup(OpenCookie.class);
                if (oc != null) {
                    oc.open();
                }
            }
        };
    }

    @Override
    public Image getIcon(int type) {
        return ImageUtilities.loadImage("cz/cuni/amis/nb/pogamut/ut2004/timeline/UTTimelineNodeIcon.png");
    }

    @Override
    public Image getOpenedIcon(int type) {
        return getIcon(type);
    }

    /**
     * If timeline was only created, return Start recording and save,
     * If timeline is recorded, return Stop recording and save.
     * In other cases (like recording has already ended), return save.
     *
     * @param context
     * @return
     */
    @Override
    public Action[] getActions(boolean context) {
        TLDatabase db = dataObject.getDatabase();

        /*
         * FIXME: convert this
         if (db.getState() == DBState.INSTANTIATED) {
            return new Action[]{
                        new StartRecordingAction(db, agentUpdater, nativeBotsUpdates),
                        new DBSaveAction(dataObject)};
        }

        if (db.getState() == DBState.RECORDING) {
            return new Action[]{
                        new StopRecordingAction(db),
                        new DBSaveAction(dataObject)};
        }
*/
        return new Action[]{
            new StartRecordingAction(db, agentUpdater, nativeBotsUpdates),
            new StopRecordingAction(db),
            new DBSaveAction(dataObject)};
    }

    /**
     * Listener that adds new node here when entity enters the environment.
     * It only adds entities, it doesn't remove them because we want to see
     * all entities that were in the game.
     */
    private class UpdateEntityNodes extends TLDatabase.Adapter {

        Logger logger = Logger.getLogger("UpdateEntityNodes");

        /**
         * When entity enters the db, add it to the db
         * @param db
         * @param entity
         */
        @Override
        public void onEntityEntered(TLDatabase db, TLEntity entity) {
            if (logger.isLoggable(Level.INFO)) logger.info("ENTITY ENTERED UT timelinenode  112 " + entity.getDisplayName());
            getChildrenCollection().add(entity);
        }

        @Override
        public void onEntityLeft(TLDatabase db, TLEntity entity) {
            if (logger.isLoggable(Level.INFO)) logger.info("ENTITYLEFT UT timelinenode  140 " + entity.getDisplayName());
            //throw new UnsupportedOperationException("Not supported yet.");
        }
    };

    private class DBSaveAction extends NamedAction {

        private TLDataObject dObj;

        public DBSaveAction(TLDataObject dObj) {
            super("ACT_TLSaveRecording");
            this.dObj = dObj;
        }

        @Override
        public void action(ActionEvent e) throws PogamutException {
            SaveCookie sc = dObj.getLookup().lookup(SaveCookie.class);
            if (sc != null) {
                try {
                    sc.save();
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
    }

    private class StartRecordingAction extends NamedAction {

        private TLDatabase db;
        private DBAgentsUpdater agentUpdater;
        private DBAgentsUpdater nativeBotUpdater;

        public StartRecordingAction(TLDatabase db, DBAgentsUpdater agentUpdater, DBAgentsUpdater playerUpdater) {
            super("ACT_TLStartRecording");

            this.db = db;
            this.agentUpdater = agentUpdater;
            this.nativeBotUpdater = playerUpdater;
        }

        @Override
        protected void action(ActionEvent e) throws PogamutException {
            db.startRecording();

            ObservableCollection<IUnrealBot> agents = server.getAgents();
            ObservableCollection<NativeUnrealBotAdapter> nativeBots = server.getNativeAgents();

            for (IUnrealBot agent : agents) {
                agentUpdater.reflectAddedAgent(agent);
            }

            for (NativeUnrealBotAdapter nativeBot : nativeBots) {
                nativeBotUpdater.reflectAddedAgent(nativeBot);
            }

            agents.addCollectionListener(agentUpdater);
            nativeBots.addCollectionListener(nativeBotsUpdates);
        }
    }

    private class StopRecordingAction extends NamedAction {

        private TLDatabase db;

        public StopRecordingAction(TLDatabase db) {
            super("ACT_TLStopRecording");

            this.db = db;
        }

        @Override
        protected void action(ActionEvent e) throws PogamutException {
            db.stopRecording();

            ObservableCollection<IUnrealBot> agents = server.getAgents();
            ObservableCollection<NativeUnrealBotAdapter> nativeBots = server.getNativeAgents();

            /*			for (IAgent agent : agents) {
            System.out.println("StopAction " + agent.getDisplayName());
            agentUpdater.reflectRemovedAgent(agent);
            }

            for (Player player : players) {
            playerUpdater.reflectRemovedPlayer(player);
            }
             */
            agents.removeCollectionListener(agentUpdater);
            nativeBots.removeCollectionListener(nativeBotsUpdates);

        }
    }

    /**
     * Updater of agents in the timeline
     */
    private class DBAgentsUpdater implements CollectionEventListener<IUnrealBot> {

        private Logger logger;
        /**
         * Database that will be updated according to the added or removed agents
         */
        private TLDatabase db;
        private List<TLAgentEntity> addedAgents = new LinkedList<TLAgentEntity>();

        public DBAgentsUpdater(TLDatabase db) {
            this.db = db;
            this.logger = Logger.getLogger("DBAgentUpdater");
            this.logger.setLevel(Level.OFF);
        }

        @Override
        public void preAddEvent(Collection<IUnrealBot> toBeAdded, Collection<IUnrealBot> whereToAdd) {
        }

        @Override
        public void postAddEvent(Collection<IUnrealBot> alreadyAdded, Collection<IUnrealBot> whereWereAdded) {
            for (IUnrealBot agent : alreadyAdded) {
                reflectAddedAgent(agent);
            }
        }

        protected void reflectAddedAgent(IUnrealBot newAgent) {
            if (logger.isLoggable(Level.FINE)) logger.fine("Reflect added agent " + newAgent + " " + newAgent.getName() + " in " + this);
            final TLAgentEntity agentEntity = new TLAgentEntity(db, newAgent);

            TLTools.runAndWaitInAWTThread(new Runnable() {

                @Override
                public void run() {
                    addedAgents.add(agentEntity);
                    db.entityEntered(agentEntity);
                }
            });
        }

        @Override
        public void preRemoveEvent(Collection<IUnrealBot> toBeRemoved, Collection<IUnrealBot> whereToRemove) {
            for (IAgent agent : toBeRemoved) {
                reflectRemovedAgent(agent);
            }
        }

        @Override
        public void postRemoveEvent(Collection<IUnrealBot> alreadyAdded, Collection<IUnrealBot> whereWereRemoved) {
        }

        protected void reflectRemovedAgent(IAgent removedAgent) {
/*            logger.fine("ReflectRemovedAgent " + removedAgent + " " + removedAgent.getName() + " in " + this);
            logger.fine("List of agents added by this AgentUpdater, size " + addedAgents.size());

            for (TLAgentEntity agentEntity : addedAgents) {
                logger.fine(" item " + agentEntity + " " + agentEntity.getAgent().getName());
            }
            logger.fine("End of list");
*/
            TLAgentEntity[] agentEntities = addedAgents.toArray(new TLAgentEntity[]{});


            for (final TLAgentEntity agentEntity : agentEntities) {
//                logger.fine("Comparing to " + agentEntity.getAgent().getName());
                if (agentEntity.getAgent() == removedAgent) {
                    if (logger.isLoggable(Level.FINE)) logger.fine("Found added agent in array, agent is leaving");
                    TLTools.runAndWaitInAWTThread(new Runnable() {

                        @Override
                        public void run() {
                            agentEntity.finish();
//					db.entityLeft(agentEntity, agentEntity.getLastTimestamp(), "Agent " + removedAgent.getDisplayName() + " has left the level.");
                        }
                    });
                    addedAgents.remove(agentEntity);
                }
            }
        }
    }
}
