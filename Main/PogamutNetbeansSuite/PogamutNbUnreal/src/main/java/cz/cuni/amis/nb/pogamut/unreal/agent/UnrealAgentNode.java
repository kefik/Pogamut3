package cz.cuni.amis.nb.pogamut.unreal.agent;

import cz.cuni.amis.nb.pogamut.base.NamedAction;
import cz.cuni.amis.nb.pogamut.base.agent.Agent3DNode;
import cz.cuni.amis.nb.pogamut.unreal.services.IPogamutEnvironments;
import cz.cuni.amis.pogamut.unreal.bot.IUnrealBot;
import cz.cuni.amis.pogamut.unreal.bot.impl.NativeUnrealBotAdapter;
import cz.cuni.amis.pogamut.unreal.communication.worldview.map.IUnrealMap;
import cz.cuni.amis.pogamut.unreal.server.IUnrealServer;
import cz.cuni.amis.utils.exception.PogamutException;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Arrays;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.openide.nodes.Sheet;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;

/**
 * Node representing Unreal agent. Adds respawn action (TODO and agent configuration).
 * @author ik
 */
public abstract class UnrealAgentNode<T extends IUnrealBot> extends Agent3DNode<T> implements LookupListener {
    private boolean selectedFlag = false;

    private IUnrealMap map;
    private Lookup.Template selectedTemplate;
    private Lookup.Result<NativeUnrealBotAdapter> selectedResult;

    public UnrealAgentNode(T agent, IUnrealServer server) {
        super(agent);

        // Add listener for selected agents
        IPogamutEnvironments environments = Lookup.getDefault().lookup(IPogamutEnvironments.class);
        if (environments == null)
            return;

        map = server.getMap();
        selectedTemplate = new Lookup.Template(IUnrealBot.class);
        selectedResult = environments.getEnvironmentSelection(map).lookup(selectedTemplate);
        selectedResult.addLookupListener(this);
    }

    @Override
    public Action[] getActions(boolean context) {
        Action[] oldActs = super.getActions(context);
        Action[] acts = Arrays.copyOf(oldActs, oldActs.length + 2);

        // spectating action
        acts[oldActs.length + 1] = new NamedAction("ACT_Respawn", UnrealAgentNode.class) {

            @Override
            protected void action(ActionEvent e) throws PogamutException {
                agent.respawn();
            }
        };
        return acts;
    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet = super.createSheet();
        Sheet.Set props = new Sheet.Set();
        props.setDisplayName("Agent configuration");
        props.setName("AgentConfig");
        sheet.put(props);
/* TODO show props
        BoolBotParam[] vals = BoolBotParam.values();

        Property[] propArr = new Property[vals.length];
        for (int i = 0; i < vals.length; i++) {
            propArr[i] = new ConfigProp(vals[i]);
        }

        updatableProps.addAll(Arrays.asList(propArr));
        props.put(propArr);
*/
        return sheet;
    }
/*
    protected class ConfigProp extends AutoNamedProp<Boolean> {

        BoolBotParam param = null;

        public ConfigProp(BoolBotParam param) {
            super(Boolean.class, param.getPropName(), "Bot configuration property.", true);
            this.param = param;
        }

        @Override
        public Boolean getValue() throws IllegalAccessException, InvocationTargetException {
            return agent.getBoolConfigure(param);
        }

        @Override
        public void setValue(Boolean arg0) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            agent.setBoolConfigure(param, arg0);
        }
    }
*/
    @Override
    final public void resultChanged(LookupEvent lookupEvent) {
        boolean isNowSelected = false;

        Lookup.Result selected = (Lookup.Result)lookupEvent.getSource();

        for (Object selectedEntity : selected.allInstances()) {
            if (agent.equals(selectedEntity)) {
                isNowSelected = true;
            }
        }
        if (isNowSelected != selectedFlag) {
            selectedFlag = isNowSelected;
            fireDisplayNameChange(null, null);
        }
    }

    @Override
    public String getHtmlDisplayName() {
        if (selectedFlag) {
            return "<b>" + getDisplayName() + "</b>";
        }
        return null;
    }

    @Override
    public void destroy() throws IOException {
        selectedResult.removeLookupListener(this);
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

                environments.getEnvironmentSelection(map).changeSelected(agent);
                
                // Do the original action
                Action original = UnrealAgentNode.super.getPreferredAction();
                if (original != null)
                    original.actionPerformed(e);
            }
        };
    }
}
