package cz.cuni.amis.nb.pogamut.unreal.timeline;

import cz.cuni.amis.nb.pogamut.unreal.services.IPogamutEnvironments;
import cz.cuni.amis.nb.pogamut.unreal.timeline.dataobject.TLDataObject;
import cz.cuni.amis.nb.util.NodeFactory;
import cz.cuni.amis.nb.util.collections.ObservableCollectionNode;
import cz.cuni.amis.utils.collections.ObservableSet;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.openide.nodes.Node;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 * This is node that is in window "Services" and as its children has all
 * timelines existing in the PogamutIDE (either loaded or created).
 *
 * It is created using layer.xml, but It is not a proper way
 * 
 * FIXME: this should be done some other way, not a singleton. How do I guarantee
 * that only one node is crated? Or that it is created at all.
 *
 * @author Honza
 */
public class UTTimelinesRootNode extends ObservableCollectionNode<TLDataObject> {

    private static ResourceBundle bundle = NbBundle.getBundle(UTTimelinesRootNode.class);
    private static UTTimelinesRootNode instance = null;

    public UTTimelinesRootNode() {
        super(new ObservableSet<TLDataObject>(new HashSet()),
                new NodeFactory<TLDataObject>() {

                    @Override
                    public Node[] create(TLDataObject obj) {
                        return new Node[]{new StoredTimelineNode(obj)};
                    }
                });

        setDisplayName(bundle.getString("LBL_TimelineRootNode"));
        setShortDescription(bundle.getString("HINT_TimelineRootNode"));

        instance = this;
    }

    @Override
    public Action[] getActions(boolean context) {
        List<Action> acts = new LinkedList<Action>(Arrays.asList(super.getActions(context)));

        acts.add(new AbstractAction("Test action") {

            @Override
            public void actionPerformed(ActionEvent e) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        });

        return acts.toArray(new Action[0]);
    }

    @Override
    public Image getIcon(int type) {
        return ImageUtilities.loadImage("cz/cuni/amis/nb/pogamut/ut2004/timeline/dataobject/timeline_icon.png");
    }

    @Override
    public Image getOpenedIcon(int type) {
        return getIcon(type);
    }

    public static UTTimelinesRootNode getInstance() {
        return instance;
    }

    public void addTimeline(TLDataObject dataObj) {
        getChildrenCollection().add(dataObj);
    }
}
