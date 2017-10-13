package cz.cuni.amis.fsm;

/**
 * Marks that the transition leads back to the state from where it originates. It is used
 * when you need to trigger specific IFSMTransition without leaving / entering the same
 * state again and again.
 * <p><p>
 * Or you may use it when you need the state to silently consume specific symbols.
 *  
 * @author Jimmy
 */
public class FSMOriginalState implements IFSMState {

	@Override
	public void init(Object context) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void restart(Object context) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stateEntering(Object context, IFSMState fromState, Object symbol) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stateLeaving(Object context, IFSMState toState, Object symbol) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stateSymbol(Object context, Object symbol) {
		// TODO Auto-generated method stub
		
	}

}
