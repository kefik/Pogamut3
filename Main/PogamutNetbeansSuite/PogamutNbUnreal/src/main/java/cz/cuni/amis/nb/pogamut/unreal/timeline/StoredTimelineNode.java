package cz.cuni.amis.nb.pogamut.unreal.timeline;

import cz.cuni.amis.nb.pogamut.unreal.timeline.dataobject.TLDataObject;
import cz.cuni.amis.nb.pogamut.unreal.timeline.records.TLDatabase;
import cz.cuni.amis.nb.pogamut.unreal.timeline.records.TLDatabase.TLDatabaseListener;
import cz.cuni.amis.nb.pogamut.unreal.timeline.records.TLEntity;
import cz.cuni.amis.nb.util.NodeFactory;
import cz.cuni.amis.nb.util.collections.ObservableCollectionNode;
import cz.cuni.amis.utils.collections.ObservableSet;
import java.awt.event.ActionEvent;
import java.util.HashSet;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.openide.cookies.OpenCookie;
import org.openide.nodes.Node;

/**
 * This is a node used in the Services window under Experiments node (=it's child
 * of a UTTimelinesRootNode ).
 *
 * It represents one timeline that was exists in the Pogamut IDE(it was newly
 * created for some server or loaded from computer).
 *
 * Children of the node are enties that are part of the timeline.
 * 
 * @see UTTimelinesRootNode
 * @see TLEntityNode
 * @see TLEntity
 * 
 * @author Honza
 */
class StoredTimelineNode extends ObservableCollectionNode<TLEntity> {

    private TLDataObject dataObj;
    private TLDatabase.Adapter newEntitiesListener = new TLDatabase.Adapter() {

        @Override
        public void onEntityEntered(TLDatabase db, TLEntity entity) {
            getChildrenCollection().add(entity);
        }
    };

    public StoredTimelineNode(TLDataObject obj) {
        super(new ObservableSet<TLEntity>(new HashSet<TLEntity>()),
                new NodeFactory<TLEntity>() {

                    @Override
                    public Node[] create(TLEntity entity) {
                        TLEntityNode node = new TLEntityNode(entity.getDatabase(), entity);

                        return new Node[]{node};
                    }
                });

        this.dataObj = obj;
        setName(this.dataObj.getName() + " (" + this.dataObj.getMapName() + ")");

        for (TLEntity entity : obj.getDatabase().getEntities()) {
            this.getChildrenCollection().add(entity);
        }

        dataObj.getDatabase().addDBListener(newEntitiesListener);
    }

    @Override
    public Action getPreferredAction() {
        return new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                OpenCookie oc = dataObj.getLookup().lookup(OpenCookie.class);
                if (oc != null) {
                    oc.open();
                }
            }
        };

    }
}
