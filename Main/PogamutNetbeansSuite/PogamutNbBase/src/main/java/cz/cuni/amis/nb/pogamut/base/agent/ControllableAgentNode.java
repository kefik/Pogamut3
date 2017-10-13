package cz.cuni.amis.nb.pogamut.base.agent;

import cz.cuni.amis.pogamut.base.agent.IAgent;
import cz.cuni.amis.pogamut.base.agent.exceptions.AgentException;
import cz.cuni.amis.pogamut.base.agent.state.level0.IAgentState;
import cz.cuni.amis.utils.flag.FlagListener;
import java.awt.event.ActionEvent;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.Action;

/**
 * Agent node that can be controled by the user.
 * @author ik
 */
public abstract class ControllableAgentNode<T extends IAgent> extends AgentNode<T> {

    protected static Timer updater = new Timer("Agent node periodical updater");
    protected TimerTask updaterTask = null;
    protected FlagListener<IAgentState> stopUpdatingListener = null;
    protected static final long UPDATE_PERIOD = 500;

    public ControllableAgentNode(final T agent) {
        super(agent);

        // start updating
        updater.schedule(updaterTask = new TimerTask() {

            @Override
            public void run() {
                for (Runnable r : updateSubtasks) {
                    r.run();
                }
            }
        }, UPDATE_PERIOD, UPDATE_PERIOD);

        // stop updating when agent terminates
        agent.getState().addListener(stopUpdatingListener = new FlagListener<IAgentState>() {

            @Override
            public void flagChanged(IAgentState changedValue) {
                if (isEndState(changedValue)) {
                    updaterTask.cancel();
                    agent.getState().removeListener(stopUpdatingListener);
                }
            }
        });

    }

 
    @Override
    public Action[] getActions(boolean context) {
        return new Action[]{
                    new StopAgent(agent),
                    new KillAgent(agent),
                    null,
                    new PauseAgent(agent),
                    new ResumeAgent(agent),};
    }

    public static class StopAgent extends AgentAction {

        public StopAgent(IAgent agent) {
            super(agent, "ACT_Stop");
        }

        @Override
        protected void action(ActionEvent e) throws AgentException {
            agent.stop();
        }
    }

    public static class PauseAgent extends AgentAction {

        public PauseAgent(IAgent agent) {
            super(agent, "ACT_Pause");
        }

        @Override
        protected void action(ActionEvent e) throws AgentException {
            agent.pause();
        }
    }

    public static class ResumeAgent extends AgentAction {

        public ResumeAgent(IAgent agent) {
            super(agent, "ACT_Resume");
        }

        @Override
        protected void action(ActionEvent e) throws AgentException {
            agent.resume();
        }
    }

    public static class KillAgent extends AgentAction {

        public KillAgent(IAgent agent) {
            super(agent, "ACT_Kill");
        }

        @Override
        protected void action(ActionEvent e) throws AgentException {
            agent.kill();
        }
    }

    public static class SetDefault extends AgentAction {

        public SetDefault(IAgent agent) {
            super(agent, "ACT_SetDefault");
        }

        @Override
        protected void action(ActionEvent e) throws AgentException {
            // Lookup.getDefault().
        }
    }
}
