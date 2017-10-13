package cz.cuni.amis.fsm;

/**
 * Interface for the FSM. Contains three methods:
 * <p><p>
 * push() ... to insert next symbol to the FSM.
 * <p>
 * restart() ... to restart the fsm (calls restart() on all states and transitions) and set itself to the initial state
 * <p>
 * isTerminal() ... query the fsm whether it is in the terminal state
 * 
 * @author Jimmy
 *
 * @param <SYMBOL>
 * @param <CONTEXT>
 */
public interface IFSM<SYMBOL, CONTEXT> {

	public void push(CONTEXT context, SYMBOL symbol);
	
	public void restart(CONTEXT context);
	
	public boolean isTerminal();

	
}
