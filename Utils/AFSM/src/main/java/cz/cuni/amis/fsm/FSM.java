package cz.cuni.amis.fsm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Context aware FSM implementation.
 * <p><p>
 * FSM = Finite State Machine, also called FSA = Finite State Automaton, more info at http://en.wikipedia.org/wiki/Finite_state_machine
 * <p><p>
 * This FSM is based upon the annotations FSMState, FSMTransition, FSMInitialState and FSMTerminalState.
 * <p><p>
 * Every class annotated with FSMState may be inserted to the FSM (in constructor) as FSM state. The
 * annotation always contains the transitions that leads from the state to other states.
 * <p><p>
 * Unlike many other implementations of the FSMs there is out there in the wild, this one isn't driven
 * by integers. It works with whole objects instead treating their classes as the symbols. Generic
 * parameter SYMBOL should the common ancestor of all symbols you will want to pass to the FSM.
 * This allows you (for instance) to implement protocols based upon real messages (where every 
 * message has it's own class!).
 * <p><p>
 * Note that every action of FSM's states and transitions have "context" parameter. Use this
 * object to pass the common interface you will need during the state/transition work (stateEntering(),
 * stateSymbol(), stateLeaving(), stepped()).
 * <p><p>
 * Pros of this implementation: 
 * <ul>
 * <li>pure java-based, code-insight works (quick navigation between states that references themselves via FSMTransition)</li>
 * <li>passed context object makes state implementation really easy</li>
 * <li>all states/transitions that has parameter-less constructor can be instantiated automatically - saves your time ;-)</li>
 * </ul>
 * Cons of this implementation:
 * <ul>
 * <li>reusability of the code is crippled - if you need one state in two different scenarios you have to make descendants / copy-pasting.</li>
 * </ul>  
 * 
 * @author Jimmy
 *
 * @param <SYMBOL>
 * @param <CONTEXT>
 */
public class FSM<SYMBOL, CONTEXT> implements IFSM<SYMBOL, CONTEXT> {
	
	protected static class TransitionWrapper<SYMBOL, CONTEXT> {
		
		private StateWrapper<SYMBOL, CONTEXT> target;
		private List<IFSMTransition<SYMBOL, CONTEXT>> transitions;
		private Logger log;

		/**
		 * @param target
		 * @param log may be null
		 */
		public TransitionWrapper(StateWrapper<SYMBOL, CONTEXT> target, Logger log) {
			this(target, null, log);			
		}
		
		/**
		 * @param target
		 * @param transitions
		 * @param log may be null
		 */
		public TransitionWrapper(StateWrapper<SYMBOL, CONTEXT> target, List<IFSMTransition<SYMBOL, CONTEXT>> transitions, Logger log) {
			this.log = log;
			this.target = target;
			this.transitions = transitions;			
		}

		public StateWrapper<SYMBOL, CONTEXT> step(CONTEXT context, StateWrapper<SYMBOL, CONTEXT> fromState, SYMBOL bySymbol) {
			this.log.finer("FSM[" + fromState.getWrappedState() + "]: symbol " + bySymbol);
			if (target == null) {
				for (IFSMTransition<SYMBOL, CONTEXT> transition : transitions) {
					this.log.finest("FSM[" + fromState.getWrappedState() + "]: invoking transition " + transition);
					transition.stepped(context, fromState.myState, bySymbol, target);
				}
				return fromState;
			} else {
				fromState.stateLeaving(context, target, bySymbol);
				for (IFSMTransition<SYMBOL, CONTEXT> transition : transitions) {
					this.log.finest("FSM[" + fromState.getWrappedState() + "]: invoking transition " + transition);
					transition.stepped(context, fromState.myState, bySymbol, target.myState);
				}
				target.stateEntering(context, fromState.myState, bySymbol);
				return target;
			}
		}

        public StateWrapper<SYMBOL, CONTEXT> getTarget() {
            return target;
        }
                
                
	}
	
	protected static class StateWrapper<SYMBOL, CONTEXT> implements IFSMState<SYMBOL, CONTEXT> {
		
		private IFSMState<SYMBOL, CONTEXT> myState;
		
		private Map<Class, TransitionWrapper<SYMBOL, CONTEXT>> transitions = new HashMap<Class, TransitionWrapper<SYMBOL, CONTEXT>>();
		
		private boolean terminal;
		
		private Logger log;
		
		/**
		 * @param state
		 * @param log may be null
		 */
		public StateWrapper(IFSMState<SYMBOL, CONTEXT> state, Logger log) {
			this.log = log;
			if (!state.getClass().isAnnotationPresent(FSMState.class)) { 
				throw new FSMBuildException("state " + state.getClass() + " doesn't contain @State annotation", log); 
			}
			this.myState = state;
			this.terminal = state.getClass().isAnnotationPresent(FSMTerminalState.class);
		}
		
		protected void initTransitionMap(Map<Class, StateWrapper<SYMBOL, CONTEXT>> states, Map<Class, IFSMTransition<SYMBOL, CONTEXT>> transitions) {
			FSMState s = myState.getClass().getAnnotation(FSMState.class);			
			for (FSMTransition transition : s.map()) {
				if (transition.symbol().length == 0) {
					throw new FSMBuildException("state " + myState.getClass() + ": one of the transition doesn't have symbols specified", log);
				}	
				StateWrapper<SYMBOL, CONTEXT> targetState = null;
				if (transition.state() != FSMOriginalState.class) {
					targetState = states.get(transition.state());
					if (targetState == null) {
						targetState = new StateWrapper<SYMBOL, CONTEXT>(getState(transition.state(), log), log);
						states.put(transition.state(), targetState);
						targetState.initTransitionMap(states, transitions);
					}
				}					
				List<IFSMTransition<SYMBOL, CONTEXT>> nextTransitions = new ArrayList<IFSMTransition<SYMBOL, CONTEXT>>(transition.transition().length);
				for (Class tran : transition.transition()) {
					IFSMTransition<SYMBOL, CONTEXT> t = transitions.get(tran);
					if (t == null) {
						t = getTransition(tran, log);
						transitions.put(tran, t);
					}
					nextTransitions.add(t);
				}
				TransitionWrapper<SYMBOL, CONTEXT> transitionWrapper = new TransitionWrapper<SYMBOL, CONTEXT>(targetState, nextTransitions, log);
				for (Class symbol : transition.symbol()) {					
					this.transitions.put(symbol, transitionWrapper);							
				}
			}
		}
			
		@Override
		public void init(CONTEXT context) {
			log.finest("FSM: " + myState + " init()");
			myState.init(context);
		}

		@Override
		public void restart(CONTEXT context) {
			log.finest("FSM: " + myState + " restart()");
			myState.restart(context);
		}

		@Override
		public void stateEntering(CONTEXT context, IFSMState<SYMBOL, CONTEXT> fromState, SYMBOL symbol) {
			log.finest("FSM[" + fromState + "]: entering state " + myState);
			myState.stateEntering(context, fromState, symbol);
		}

		@Override
		public void stateLeaving(CONTEXT context, IFSMState<SYMBOL, CONTEXT> toState, SYMBOL symbol) {
			log.finest("FSM[" + myState + "]: leaving state");
			myState.stateLeaving(context, toState, symbol);
		}

		@Override
		public void stateSymbol(CONTEXT context, SYMBOL symbol) {
			log.finest("FSM[" + myState + "]: consuming symbol " + symbol);
			myState.stateSymbol(context, symbol);	
		}
		
		public StateWrapper pushSymbol(CONTEXT context, SYMBOL symbol) {
			TransitionWrapper transition = transitions.get(symbol.getClass());
			if (transition == null) {
				stateSymbol(context, symbol);
				return this;
			} else {
				return transition.step(context, this, symbol);
			}
		}
		
		public boolean isTerminal() {
			return terminal;
		}
		
		public String toString() {
			return "FSM$StateWrapper(myState=" + myState + ")";
		}

        public IFSMState<SYMBOL, CONTEXT> getWrappedState() {
            return myState;
        }

        public Map<Class, TransitionWrapper<SYMBOL, CONTEXT>> getTransitions() {
            return Collections.unmodifiableMap(transitions);
        }
                
	}
	
	private Collection<StateWrapper<SYMBOL, CONTEXT>> states;
	
	private Collection<IFSMTransition<SYMBOL, CONTEXT>> transitions;
	
	private StateWrapper<SYMBOL, CONTEXT> initialState = null;
	
	private StateWrapper<SYMBOL, CONTEXT> currentState = null;
	
	private Logger log = null;
	
	private Object mutex = new Object();

    public Collection<StateWrapper<SYMBOL, CONTEXT>> getStates() {
        return Collections.unmodifiableCollection(states);
    }

    public Collection<IFSMTransition<SYMBOL, CONTEXT>> getTransitions() {
        return Collections.unmodifiableCollection(transitions);
    }
	
	/**
	 * Construct and returns IFSMState of the given class, parameter-less constructor is sought.
	 * <p><p>
	 * Throws FSMBuildException if constructor not found.
	 * @param state
	 * @param log may be null
	 * @return
	 */
	public static IFSMState getState(Class<? extends IFSMState> state, Logger log) {
		try {
			return state.getConstructor().newInstance();
		} catch (Exception e) {
			throw new FSMBuildException("can't construct " + state.getClass().getName() + ": " + e.getMessage(), log);
		}
	}
	
	/**
	 * Constructs and returns IFSMTransition of the given class, parameter-less constructor is sought.
	 * <p><p>
	 * Throws FSMBuildException if constructor not found.
	 * 
	 * @param state
	 * @param log may be null
	 * @return
	 */
	protected static IFSMTransition getTransition(Class<? extends IFSMTransition> transition, Logger log) {
		try {
			return transition.getConstructor().newInstance();
		} catch (Exception e) {
			throw new FSMBuildException("can't construct " + transition.getClass().getName() + ": " + e.getMessage(), log);
		}
	}
	
	/**
	 * FSM constructor that tries to instantiate all states / transitions for itself.
	 * <p><p>
	 * First state to try is "state".
	 * 
	 * @param context
	 * @param state
	 * @param log may be null
	 */
	public FSM(CONTEXT context, Class<? extends IFSMState<SYMBOL, CONTEXT>> state, Logger log) {
		this(context, getState(state, log), log);
	}
	
	/**
	 * FSM constructor that tries to instantiate all (but first "state") states / transitions for itself.
	 * <p><p>
	 * First state to try is "state".
	 * 
	 * @param context
	 * @param state
	 * @param log may be null
	 */
	public FSM(CONTEXT context, IFSMState<SYMBOL, CONTEXT> state, Logger log) {
		this(context, new IFSMState[]{ state }, null, log);
	}
	
	/**
	 * Note that this constructor will use "states" and "transitions" to instantiate the FSM but 
	 * you may omit those states/transitions that contains implicite constructor (parameter-less). This
	 * constructor will create those states/transitions for itself via reflection.
	 * 
	 * @param context
	 * @param states
	 * @param transitions
	 * @param log may be null
	 */
	public FSM(CONTEXT context, IFSMState<SYMBOL, CONTEXT>[] states, IFSMTransition[] transitions, Logger log) {
		this.log = log;
		
		if (states == null) throw new FSMBuildException("states can't be null", log);
		
		Map<Class, StateWrapper<SYMBOL, CONTEXT>> statesMap = new HashMap<Class, StateWrapper<SYMBOL, CONTEXT>>();
		Map<Class, IFSMTransition<SYMBOL, CONTEXT>> transitionsMap = new HashMap<Class, IFSMTransition<SYMBOL, CONTEXT>>();		
		
		for (IFSMState<SYMBOL, CONTEXT> state : states) {
			if (state == null) continue;
			StateWrapper<SYMBOL, CONTEXT> wrapper = new StateWrapper<SYMBOL, CONTEXT>(state, log);
			statesMap.put(state.getClass(), wrapper);			
		}
		
		if (transitions != null) {
			for (IFSMTransition<SYMBOL, CONTEXT> transition : transitions) {
				if (transition == null) continue;				
				transitionsMap.put(transition.getClass(), transition);
				this.transitions.add(transition);
			}
		}
		
		StateWrapper<SYMBOL, CONTEXT>[] tempStates = statesMap.values().toArray(new StateWrapper[0]);
		for (StateWrapper<SYMBOL, CONTEXT> wrapper : tempStates) {
			wrapper.initTransitionMap(statesMap, transitionsMap);
		}
		
		this.states = statesMap.values();
		this.transitions = transitionsMap.values();
		
		for (StateWrapper<SYMBOL, CONTEXT> wrapper : this.states) {
			if (wrapper.myState.getClass().isAnnotationPresent(FSMInitialState.class)) {
				if (initialState != null) {
					throw new FSMBuildException("there are more then one initial state, examples: " + this.initialState.myState.getClass() + ", " + wrapper.myState.getClass(), log);
				}
				this.initialState = wrapper;
			}
		}
		
		if (this.initialState == null) {
			throw new FSMBuildException("Failed to initialize the FSM as there is no initial state defined. Note that at least one state class must be flagged with @FSMInitialState annotation!", log);
		}
		
		init(context);		
	}

	/**
	 * Called from the constructor (must not be called after that ever) to finish the initialization of all states.
	 * @param context
	 */
	private void init(CONTEXT context) {
		currentState = initialState;
		for (StateWrapper state : states) state.init(context);
		for (IFSMTransition<SYMBOL, CONTEXT> transition : transitions) transition.init(context);
		if (currentState == null) {
			throw new IllegalStateException("currentState of the FSM is 'null' after the init! Ivalid state of the FSM object, you've hit a bug!");
		}
	}
	
	@Override
	public boolean isTerminal() {
		synchronized(mutex) {
			return currentState.isTerminal();
		}
	}

	@Override
	public void push(CONTEXT context, SYMBOL symbol) {
		synchronized(mutex) {
			currentState = currentState.pushSymbol(context, symbol);
		}
	}

	@Override
	public void restart(CONTEXT context) {
		synchronized(mutex) {
			currentState = initialState;
			for (StateWrapper state : states) state.restart(context);
			for (IFSMTransition<SYMBOL, CONTEXT> transition : transitions) transition.restart(context);
			if (currentState == null) {
				throw new IllegalStateException("currentState of the FSM is 'null' after the restart! Ivalid state of the FSM object, you've hit a bug!");
			}
		}
	}

}
