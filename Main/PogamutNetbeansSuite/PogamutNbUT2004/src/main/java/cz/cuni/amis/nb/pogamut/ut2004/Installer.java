package cz.cuni.amis.nb.pogamut.ut2004;

import cz.cuni.amis.nb.api.pogamut.base.server.ServerTypesManager;
import cz.cuni.amis.nb.api.pogamut.unreal.server.UnrealServersManager;
import cz.cuni.amis.nb.api.pogamut.ut2004.server.UT2004ServersManager;
import cz.cuni.amis.nb.pogamut.ut2004.server.UTServersRootNode;
import cz.cuni.amis.nb.util.editors.URIEditorSupport;
import java.beans.PropertyEditorManager;
import java.net.URI;
import org.openide.modules.ModuleInstall;

/**
 *
 * @author ik
 */
public class Installer extends ModuleInstall {

    @Override
    public void restored() {
        ServerTypesManager.registerServersManager(new UT2004ServersManager());
        // TODO ServerTypesManager.registerServersManager(new UnrealServersManager(UDKServersRootNode.UDK_SERVERS_ID));

        // Custom editors
        PropertyEditorManager.registerEditor(URI.class,
                URIEditorSupport.class);

        super.restored();
    }

    @Override
    public void close() {
        ServerTypesManager.serializeAll();
        super.close();
    }
}
