package cz.cuni.amis.pogamut.ut2004.communication.translator;

import cz.cuni.amis.fsm.IFSMState;
import cz.cuni.amis.fsm.IFSMTransition;

public class TranslatorMessages {
	
	public static String unexpectedMessage(IFSMState state, Object symbol) {
		return "FSM[" + state + "]: unexpected message " + symbol;
	}
	
	@SuppressWarnings("unchecked")
	public static String unexpectedMessage(IFSMState state, Object symbol, Class expected) {
		return "FSM[" + state + "]:  expected " + expected.getSimpleName() + " got " + symbol;
	}
	
	public static String unprocessedMessage(IFSMState state, Object symbol) {
		return "FSM[" + state + "]: unprocessed message " + symbol;
	}
	
	public static String messageNotWorldEvent(IFSMState state, Object symbol) {
		return "FSM[" + state + "]: " + symbol.getClass().getSimpleName() + " does not implement IWorldChangeEvent interface, can't push to the mediator";
	}
	
	public static String unexpectedMessage(IFSMTransition state, Object symbol) {
		return "FSM[" + state + "]: unexpected message";
	}
	
	@SuppressWarnings("unchecked")
	public static String unexpectedMessage(IFSMTransition state, Object symbol, Class expected) {
		return "FSM[" + state + "]:  expected " + expected.getSimpleName() + " got " + symbol;
	}
	
	public static String unprocessedMessage(IFSMTransition state, Object symbol) {
		return "FSM[" + state + "]: unprocessed message " + symbol;
	}
	
	public static String messageNotWorldEvent(IFSMTransition state, Object symbol) {
		return "FSM[" + state + "]: " + symbol.getClass().getSimpleName() + " does not implement IWorldChangeEvent interface, can't push to the mediator";
	}

}
