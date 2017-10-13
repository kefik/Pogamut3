package cz.cuni.amis.nb.pogamut.unreal.timeline;

import cz.cuni.amis.nb.pogamut.unreal.timeline.records.TLFolder;
import cz.cuni.amis.nb.pogamut.unreal.timeline.records.TLProperty;
import cz.cuni.amis.nb.util.NodeFactory;
import cz.cuni.amis.nb.util.collections.ObservableCollectionNode;
import cz.cuni.amis.utils.collections.ObservableList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;

/**
 * Node representing the TLFolder (= values of introspection variables of agent).
 *
 * You can right-click on it and it will show properites in this folder and
 * for every subfolder of represented folder has this node a child TLFolderNode
 * representing the subfolder.
 *
 * Different from FolderNode because we already have the data = no exceptions
 * plus I can set time (updateToTime )here to notify the properties what data should it show.
 *
 * @see TLFolder
 * @see TLEntityNode
 * @see FolderNode
 * @author Honza
 */
class TLFolderNode extends ObservableCollectionNode<TLFolder> {

    private TLFolder folder;
    private Set<TLProperyAdapter> propertyAdapters = new HashSet<TLProperyAdapter>();

    /**
     * Create a node representing passed folder.
     * 
     * @param folder
     */
    public TLFolderNode(TLFolder folder) {
        super(new ObservableList<TLFolder>(new LinkedList<TLFolder>()),
                new NodeFactory<TLFolder>() {

                    @Override
                    public Node[] create(TLFolder obj) {
                        return new Node[]{new TLFolderNode(obj)};
                    }
                });

        this.folder = folder;
        this.setDisplayName(folder.getName());

        for (TLFolder subfolder : folder.getSubfolders()) {
            getChildrenCollection().add(subfolder);
        }
    }

    /**
     * Update to this class
     * @return
     */
    @Override
    protected Sheet createSheet() {
        Sheet sheet = super.createSheet();
        Sheet.Set props = sheet.get(Sheet.PROPERTIES);

        if (props == null) {
            props = Sheet.createPropertiesSet();
            sheet.put(props);
        }

        for (TLProperty property : folder.getProperties()) {
            TLProperyAdapter adapter = new TLProperyAdapter(property);
            props.put(adapter);
            propertyAdapters.add(adapter);
        }

        return sheet;
    }

    /**
     * Update this folder node and all its properties and children to show
     * the data from the passed time.
     * @param time
     */
    public void updateToTime(long time) {
        for (TLProperyAdapter propAdapter : propertyAdapters) {
            propAdapter.updateToTime(time);
        }

        for (TLProperyAdapter propAdapter : propertyAdapters) {
            firePropertyChange(propAdapter.getName(), null, propAdapter.getValue());
        }

        for (Node n : this.getChildren().getNodes()) {
            ((TLFolderNode) n).updateToTime(time);
        }
    }

    private static class TLProperyAdapter extends PropertySupport.ReadOnly {
        private static String prefix = "TLPROP";
        private static int propertyId = 0;

        private long time = 0;
        private Object data = null;
        private TLProperty property;

        public TLProperyAdapter(TLProperty property) {
            super(getNewPropId(property.getName()),
                    property.getType(),
                    property.getName(),
                    "");
            this.property = property;
        }

        public void updateToTime(long time) {
            this.time = time;
            this.data = this.property.getValue(time);
        }

        @Override
        public Object getValue() {
            return data;
        }

        private static String getNewPropId(String suffix) {
            return prefix + "_" + (propertyId++) + "_" + suffix;
        }
    }
}
