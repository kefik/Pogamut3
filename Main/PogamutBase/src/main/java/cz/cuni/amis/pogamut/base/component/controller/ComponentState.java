package cz.cuni.amis.pogamut.base.component.controller;

import java.util.HashSet;
import java.util.Set;

public enum ComponentState {
	
	INSTANTIATED, STARTING, STARTING_PAUSED, RUNNING, PAUSING, PAUSED, RESUMING, STOPPING, STOPPED, KILLING, KILLED, RESETTING, RESETED;
	
	/**
	 * Tests whether 'state' is inside 'inside'.
	 * @param state
	 * @param inside
	 * @return 
	 */
	public static boolean inside(ComponentState state, ComponentState... inside) {
		for (ComponentState cs : inside) {
			if (state.equals(cs)) return true;
		}
		return false;
	}
	
	/**
	 * Returns true if one state from 'states' is found 'inside', O(n) complexity.
	 * @param states
	 * @param inside
	 * @return
	 */
	public static boolean partOf(ComponentState[] states, ComponentState ... inside) {
		Set<ComponentState> insideSet = new HashSet<ComponentState>();
		for (ComponentState state : inside) {
			insideSet.add(state);
		}
		for (ComponentState state : states) {
			if (insideSet.contains(state)) return true;
		}
		return false;
	}
	
	/**
	 * Wether 'state' is not part of 'inside'.
	 * @param state
	 * @param inside
	 * @return
	 */
	public static boolean notInside(ComponentState state, ComponentState... inside) {
		for (ComponentState cs : inside) {
			if (state.equals(cs)) return false;
		}
		return true;
	}

}
