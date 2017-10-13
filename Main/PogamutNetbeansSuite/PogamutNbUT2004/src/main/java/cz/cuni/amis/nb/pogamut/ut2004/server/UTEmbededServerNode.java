package cz.cuni.amis.nb.pogamut.ut2004.server;

import cz.cuni.amis.nb.api.pogamut.ut2004.server.EmbeddedUTServerDefinition;
import cz.cuni.amis.nb.pogamut.base.NamedAction;
import cz.cuni.amis.utils.exception.PogamutException;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import javax.swing.Action;
import org.openide.nodes.Sheet;

/**
 *
 * @author ik
 */
public class UTEmbededServerNode<T extends EmbeddedUTServerDefinition> extends UTServerNode<T> {

    public UTEmbededServerNode(T def) {
        super(def);
    }

 @Override
    public Action[] getActions(boolean context) {
        Action[] act = super.getActions(context);
        int N = act.length + 3;
        Action[] actions = Arrays.copyOf(act, N, Action[].class);
        
        actions[N-2] = new NamedAction("LBL_StartProcess", UTEmbededServerNode.class) {

            @Override
            protected void action(ActionEvent e) throws PogamutException {
                serverDef.startEmbeddedServer();
            }
        };
        
        actions[N-1] = new NamedAction("LBL_StopProcess", UTEmbededServerNode.class) {

            @Override
            protected void action(ActionEvent e) throws PogamutException {
                serverDef.stopEmbeddedServer();
            }
        };
        
        return actions;
    }
    

    @Override
    protected Sheet createSheet() {
        Sheet sheet = super.createSheet();
        Sheet.Set props = sheet.get(Sheet.PROPERTIES);
        if (props == null) {
            props = Sheet.createPropertiesSet();
            sheet.put(props);
        }
        //props.put(new Property[]{new ReflProp(serverDef, String.class, "unrealHome", "Unreal home")});
        props.put(new FlagProp(serverDef.getServerHomePathFlag(), "Server home dir", "Path to the directory with server executable."));
        props.put(new FlagProp(serverDef.getServerExecFlag(), "Server options", "Options used to start the server."));
        return sheet;
    }
}
