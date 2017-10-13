package cz.cuni.pogamut.posh;

import java.io.IOException;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataNode;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.MultiFileLoader;
import org.openide.nodes.Node;
import org.openide.nodes.Children;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;


public class PoshDataObject extends MultiDataObject implements Lookup.Provider {

    final InstanceContent ic;
    private AbstractLookup lookup;

    public PoshDataObject(FileObject pf, MultiFileLoader loader) throws DataObjectExistsException, IOException {
        super(pf, loader);

        ic = new InstanceContent();
        lookup = new AbstractLookup(ic);
        ic.add(new PoshEditorSupport(this));
        ic.add(this);
    }

    @Override
    protected Node createNodeDelegate() {
        DataNode dn = new DataNode(this, Children.LEAF, getLookup());
        dn.setIconBaseWithExtension("cz/cuni/pogamut/posh/POSH_icon.png");
        return dn;
    }

    @Override
    public <T extends Node.Cookie> T getCookie(Class<T> type) {
        return lookup.lookup(type);
    }

    @Override
    public Lookup getLookup() {
        return lookup;
    }
}
