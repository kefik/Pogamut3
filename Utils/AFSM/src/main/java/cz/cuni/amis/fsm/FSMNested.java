package cz.cuni.amis.fsm;

/**
 * Wraps the IFSM implementation as the IFSMState thus allowing you to create hierarchical fsm.
 * <p><p>
 * Note that there is no "correct" implementation of this class (how to behave in the stateEntering(),
 * stateLeaving() methods). See the implementation and if needed create your own wrapper.
 * 
 * @author Jimmy
 *
 * @param <SYMBOL>
 * @param <CONTEXT>
 */
public class FSMNested<SYMBOL, CONTEXT> implements IFSM<SYMBOL, CONTEXT>, IFSMState<SYMBOL, CONTEXT>{
	
	private IFSM<SYMBOL, CONTEXT> fsm;
	
	public FSMNested(IFSM<SYMBOL, CONTEXT> fsm) {
		this.fsm = fsm; 
	}

	@Override
	public boolean isTerminal() {
		return fsm.isTerminal();
	}

	@Override
	public void push(CONTEXT context, SYMBOL symbol) {
		fsm.push(context, symbol);
	}

	@Override
	public void restart(CONTEXT context) {
		fsm.restart(context);
	}

	@Override
	public void init(CONTEXT context) {		
	}

	@Override
	public void stateEntering(CONTEXT context, IFSMState<SYMBOL, CONTEXT> fromState, SYMBOL symbol) {
		fsm.push(context, symbol);
	}

	@Override
	public void stateLeaving(CONTEXT context, IFSMState<SYMBOL, CONTEXT> toState, SYMBOL symbol) {
		fsm.push(context, symbol);
		
	}

	@Override
	public void stateSymbol(CONTEXT context, SYMBOL symbol) {
		fsm.push(context, symbol);		
	}

}
