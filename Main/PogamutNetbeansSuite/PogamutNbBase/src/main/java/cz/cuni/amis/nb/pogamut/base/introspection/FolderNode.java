package cz.cuni.amis.nb.pogamut.base.introspection;

import cz.cuni.amis.introspection.Folder;
import cz.cuni.amis.introspection.FolderShadow;
import cz.cuni.amis.introspection.IntrospectionException;
import cz.cuni.amis.nb.util.Updater;
import cz.cuni.amis.pogamut.base.agent.IAgent;
import cz.cuni.amis.pogamut.base.agent.state.level0.IAgentState;
import cz.cuni.amis.utils.flag.FlagListener;
import java.awt.Image;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.TimerTask;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;

/**
 *
 * @author ik
 */
public class FolderNode extends AbstractNode {

    Folder folder = null;
    /** Set of all properties associated with this node. */
    protected Set<PropertyAdapter> propertyAdapters = new HashSet<PropertyAdapter>();

    public FolderNode(Folder folder) {
        super(new IntrospectionChildren(folder));
        setName(folder.getName());
        this.folder = folder;
    }

    public Folder getFolder() {
        return folder;
    }

    @Override
    public Image getIcon(int type) {
        return ImageUtilities.loadImage("cz/cuni/amis/nb/pogamut/base/introspection/IntrospectionRootIcon.gif");
    }

    @Override
    public Image getOpenedIcon(int type) {
        return getIcon(type);
    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet = super.createSheet();
        Sheet.Set props = sheet.get(Sheet.PROPERTIES);
        if (props == null) {
            props = Sheet.createPropertiesSet();
            sheet.put(props);
        }
        try {
            if (folder.getProperties() != null) {
                for (cz.cuni.amis.introspection.Property p : folder.getProperties()) {

                    PropertyAdapter adapter = new PropertyAdapter.NamedAdapter(p);
                    props.put(adapter);
                    propertyAdapters.add(adapter);
                }
            }
        } catch (IntrospectionException ex) {
            Exceptions.printStackTrace(ex);
        }

        return sheet;
    }

    /**
     * Update all properties associated with this node and all subnodes.
     */
    protected void updateProps() {
        // update properties on this node
        for (PropertyAdapter prop : propertyAdapters) {
            try {
                firePropertyChange(prop.getPropertyID(), null, prop.getValue());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // update all subnodes
        for (Node n : getChildren().getNodes()) {
            ((FolderNode) n).updateProps();
        }

    }

    /**
     *Root node for introspection, it registers listener that will update properties after each iteration of logic.
     */
    public static class Root extends FolderNode {

        TimerTask guiUpdaterTask = null;
        TimerTask propUpdaterTask = null;
        /**
         * Agent whose properties are being displayed.
         */
        protected WeakReference<IAgent> agent = null;
        protected FlagListener<IAgentState> stopUpdatingListener = null;

        /**
         * Root node for agent introspection.
         */
        public Root(final IAgent agent, Updater updater) throws IntrospectionException {
            super(new FolderShadow(agent.getIntrospection()));
            this.agent = new WeakReference<IAgent>(agent);
            setDisplayName("Introspection");
            updater.addUpdateTask(new Runnable() {

                @Override
                public void run() {
                    try {
                        ((FolderShadow) getFolder()).synchronize();
                    } catch (IntrospectionException ex) {
                        // bot program failed, do not show this message to the user
                        // TODO or choose other solution?
                    }
                    updateProps();
                }
            });
        }

        @Override
        protected void finalize() throws Throwable {
            super.finalize();
        }
    }

    protected static class IntrospectionChildren extends Children.Keys {

        protected Folder parent;

        public IntrospectionChildren(Folder parent) {
            this.parent = parent;
        }

        @Override
        protected void addNotify() {
            try {
                if (parent.getFolders() == null) {
                    setKeys(Collections.EMPTY_SET);
                } else {
                    setKeys(parent.getFolders());
                }
            } catch (IntrospectionException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

        @Override
        protected void removeNotify() {
            setKeys(Collections.EMPTY_SET);
        }

        @Override
        protected Node[] createNodes(Object obj) {
            return new Node[]{new FolderNode((Folder) obj)};
        }
    }
}
