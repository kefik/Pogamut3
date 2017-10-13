package cz.cuni.amis.nb.pogamut.ut2004.server;

import cz.cuni.amis.nb.api.pogamut.ut2004.server.UTServerDefinition;
import cz.cuni.amis.nb.pogamut.unreal.server.UnrealServerNode;
import cz.cuni.amis.nb.util.editors.URIEditorSupport;
import java.beans.PropertyEditor;

/**
 *
 * @author ik
 */
public class UTServerNode<T extends UTServerDefinition> extends UnrealServerNode<T> {

    public static class GB04URIEditor extends URIEditorSupport {

        public GB04URIEditor() {
            super(UTServersRootNode.UT_SERVERS_ID);
        }
    }

    public UTServerNode(T def) {
        super(def, UTServersRootNode.UT_SERVERS_ID);
    }

    @Override
    protected Class<? extends PropertyEditor> getURIPropEditorClass() {
        return GB04URIEditor.class;
    }
}
