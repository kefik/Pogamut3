/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.amis.nb.pogamut.base.agent;

import cz.cuni.amis.nb.pogamut.base.NamedAction;
import cz.cuni.amis.pogamut.base.agent.IAgent;
import cz.cuni.amis.pogamut.base.agent.exceptions.AgentException;
import java.awt.event.ActionEvent;

/**
 * Base for agent associated actions.
 * @author ik
 */
public abstract class AgentAction extends NamedAction {

    protected IAgent agent = null;

    public AgentAction(IAgent agent, String key) {
        super(key);
        this.agent = agent;
    }

    protected abstract void action(ActionEvent e) throws AgentException;
}
