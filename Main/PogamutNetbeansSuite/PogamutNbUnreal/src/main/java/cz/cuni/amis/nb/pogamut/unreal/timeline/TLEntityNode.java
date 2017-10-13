package cz.cuni.amis.nb.pogamut.unreal.timeline;

import cz.cuni.amis.nb.pogamut.unreal.services.IPogamutEnvironments;
import cz.cuni.amis.nb.pogamut.unreal.timeline.records.TLDatabase;
import cz.cuni.amis.nb.pogamut.unreal.timeline.records.TLEntity;
import cz.cuni.amis.nb.pogamut.unreal.timeline.records.TLFolder;
import cz.cuni.amis.nb.util.NodeFactory;
import cz.cuni.amis.nb.util.collections.ObservableCollectionNode;
import cz.cuni.amis.pogamut.unreal.communication.worldview.map.IUnrealMap;
import cz.cuni.amis.utils.collections.ObservableList;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.LinkedList;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;

/**
 * Node representing one entity(Player or Agent) of the timeline.
 * The node has children that represent introspectable folders of the entity.
 *
 * Name of the node is name of the entity + "(" + state_of_entity + ")"
 *
 * @see TLFolder
 * @see TLFolderNode
 * @author Honza
 */
class TLEntityNode extends ObservableCollectionNode<TLFolder> implements LookupListener {

    /**
     * Reference to the entity this node is representing.
     */
    private TLEntity entity;
    
    /**
     * Reference to the database that 
     */
    private TLDatabase db;

    // need global variable, template is weakly referenced IIRC
    private Lookup.Template<TLEntity> lookupTemplate;
    private Lookup.Result<TLEntity> lookupResult = null;


    private final TLDatabase.Adapter currentTimeListener = new TLDatabase.Adapter() {
        @Override
        public void currentTimeChanged(long previousCurrentTime, long currentTime) {
                // Update icon to show dead or alive entity
                //...todo
                // Update name to show dead or alive
                setDisplayName(entity.getDisplayName() + "(" + getEntityStateInTime(currentTime) + ")");

                // Update the properties in the nodes
                Node[] nodes = getChildren().getNodes();
                if (nodes != null) {
                    for (Node n : nodes) {
                        ((TLFolderNode) n).updateToTime(currentTime);
                    }
                }
        }


    };
    /**
     * Create a node representing passed entity.
     * 
     * @param db Database the entity is in.
     * @param ent entity this node is representing.
     */
    public TLEntityNode(final TLDatabase db, TLEntity ent) {
        super(new ObservableList<TLFolder>(new LinkedList<TLFolder>()),
                new NodeFactory<TLFolder>() {

                    @Override
                    public Node[] create(TLFolder obj) {
                        return new Node[]{new TLFolderNode(obj)};
                    }
                });
        this.db = db;
        this.entity = ent;

        // set name of the entity
        setDisplayName(entity.getDisplayName() + "(" + getEntityStateInTime(db.getCurrentTime()) + ")");

        // create folders
        this.getChildrenCollection().add(entity.getFolder());

        this.db.addDBListener(currentTimeListener);

        IPogamutEnvironments environments = Lookup.getDefault().lookup(IPogamutEnvironments.class);
        if (environments == null) {
            return;
        }

        IUnrealMap map = db.getMap();

        lookupTemplate = new Lookup.Template<TLEntity>(TLEntity.class);
        lookupResult = (Lookup.Result<TLEntity>) environments.getEnvironmentSelection(map).lookup(lookupTemplate);
        lookupResult.addLookupListener(this);
    }

    /**
     * Return state of entity in passed time.
     * 
     * @param time what was state of entity in this time.
     * @return State of entity
     */
    private TLEntity.State getEntityStateInTime(long time) {
        if (time < this.entity.getStartTime()) {
            return TLEntity.State.INSTANTIATED;
        }
        if (time <= this.entity.getEndTime()) {
            return TLEntity.State.RECORDING;
        }
        return TLEntity.State.FINISHED;
    }

    private boolean htmlName = false;


    @Override
    public String getHtmlDisplayName() {
        if (htmlName) {
            return "<b>" + getDisplayName() + "</b>";
        }
        return null;
    }

    @Override
    public void resultChanged(LookupEvent lookupEvent) {
        boolean newHtmlName = false;

        Lookup.Result<TLEntity> selected = (Lookup.Result<TLEntity>)lookupEvent.getSource();

        for (TLEntity selectedEntity : selected.allInstances()) {
            if (selectedEntity == entity) {
                newHtmlName = true;
            }
        }
        if (newHtmlName != htmlName) {
            htmlName = newHtmlName;
            fireDisplayNameChange(null, null);
        }
    }

    @Override
    public void destroy() throws IOException {
        lookupResult.removeLookupListener(this);
        super.destroy();
    }

    /**
     * On default select the represented agent and do the former original action
     * @return
     */
    @Override
    public Action getPreferredAction() {
        return new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                IPogamutEnvironments environments = Lookup.getDefault().lookup(IPogamutEnvironments.class);
                if (environments == null)
                    return;

                environments.getEnvironmentSelection(db.getMap()).changeSelected(entity);

                // Do the original action
                Action original = TLEntityNode.super.getPreferredAction();
                if (original != null)
                    original.actionPerformed(e);
            }
        };
    }
}
