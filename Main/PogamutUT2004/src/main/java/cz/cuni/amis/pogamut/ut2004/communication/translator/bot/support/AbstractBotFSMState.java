package cz.cuni.amis.pogamut.ut2004.communication.translator.bot.support;

import cz.cuni.amis.fsm.IFSMState;

public abstract class AbstractBotFSMState<SYMBOL, CONTEXT> implements IFSMState<SYMBOL, CONTEXT> {

	public String toString() {
		return getClass().getSimpleName();
	}
	
}
