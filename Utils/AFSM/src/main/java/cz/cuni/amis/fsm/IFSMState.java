package cz.cuni.amis.fsm;

public interface IFSMState<SYMBOL, CONTEXT> {
	
	public void stateEntering(CONTEXT context, IFSMState<SYMBOL, CONTEXT> fromState, SYMBOL symbol);
	
	public void stateSymbol(CONTEXT context, SYMBOL symbol);
	
	public void stateLeaving(CONTEXT context, IFSMState<SYMBOL, CONTEXT> toState, SYMBOL symbol);
	
	public void init(CONTEXT context);
	
	public void restart(CONTEXT context);
	
}
