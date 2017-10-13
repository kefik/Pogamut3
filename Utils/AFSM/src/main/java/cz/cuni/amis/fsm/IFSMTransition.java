package cz.cuni.amis.fsm;

/**
 * Interface for the fsm transition. Note that this transition doesn't really describe target
 * state nor symbols of the transition. It's used only if you need to perform certain operations
 * when some transitions are triggered. You have to reference implementation of this interface in the
 * map() part of the FSMState annotations transition() of the FSMTransition annotation respectively.
 * 
 * @author Jimmy
 *
 * @param <SYMBOL>
 * @param <CONTEXT>
 */
public interface IFSMTransition<SYMBOL, CONTEXT> {
	
	/**
	 * Method that is called when the transition is triggered.
	 * @param context
	 * @param fromState
	 * @param bySymbol
	 * @param toState
	 */
	public void stepped(CONTEXT context, IFSMState<SYMBOL, CONTEXT> fromState, SYMBOL bySymbol, IFSMState<SYMBOL, CONTEXT> toState);
	
	/**
	 * Called when the certain FSM (the transition belongs to) is created.
	 * @param context
	 */
	public void init(CONTEXT context);

	/**
	 * Called every time somebody restarts the FSM the transition belongs to.
	 * @param context
	 */
	public void restart(CONTEXT context);
	
}
