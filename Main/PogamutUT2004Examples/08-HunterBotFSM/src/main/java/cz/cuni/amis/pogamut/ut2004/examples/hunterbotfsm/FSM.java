package cz.cuni.amis.pogamut.ut2004.examples.hunterbotfsm;

import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;

public class FSM<TContext extends UT2004BotModuleController<UT2004Bot>> {
    private State<TContext> activeState;
    
    public FSM(State<TContext> initialState) {
        activeState = initialState;
    }
    
    public void execute(TContext context) {
        activeState = activeState.execute(context);
    }
}
