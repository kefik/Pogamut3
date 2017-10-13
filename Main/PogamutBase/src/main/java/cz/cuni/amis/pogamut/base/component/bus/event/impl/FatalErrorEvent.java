package cz.cuni.amis.pogamut.base.component.bus.event.impl;

import java.util.Arrays;
import java.util.List;

import cz.cuni.amis.pogamut.base.component.IComponent;
import cz.cuni.amis.pogamut.base.component.bus.event.IFatalErrorEvent;
import cz.cuni.amis.utils.Const;

public class FatalErrorEvent<SOURCE extends IComponent> implements IFatalErrorEvent<SOURCE> {
		
	protected Object origin;
	protected SOURCE component;
	protected String message;
	protected Throwable cause;
	protected StackTraceElement[] stackTrace;
	
	public FatalErrorEvent(SOURCE component, String message) {
		this.message = message;
		this.component = component;
		this.origin = origin;
		stackTrace();
	}
	
	public FatalErrorEvent(SOURCE component, String message, Throwable cause) {
		this.message = message;
		this.component = component;
		this.cause = cause;
		stackTrace();
	}
	
	public FatalErrorEvent(SOURCE component, Throwable cause) {
		this.message = cause.getMessage();
		this.component = component;
		this.cause = cause;
		stackTrace();
	}
	
	private void stackTrace() {
		Exception e = new Exception();
		this.stackTrace = e.getStackTrace();
		this.stackTrace = Arrays.copyOfRange(this.stackTrace, 2, this.stackTrace.length);
	}
	
	@Override
	public SOURCE getSource() {
		return component;
	}

	@Override
	public Throwable getCause() {
		return cause;
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public StackTraceElement[] getStackTrace() {
		return stackTrace;
	}

	protected String printStackStrace(StackTraceElement[] stackTraceToPrint, String indent) {
		StringBuffer sb = new StringBuffer();
		sb.append(indent);
		sb.append(stackTraceToPrint[0]);
		for (int i = 1; i < stackTraceToPrint.length; ++i) {
			sb.append(Const.NEW_LINE);
			sb.append(indent);
			sb.append(stackTraceToPrint[i]);
		}
		return sb.toString();
	}
	
	public String toString() {
		return getSummary();
	}
	
	@Override
	public String getSummary() {
		StringBuffer sb = new StringBuffer();
		sb.append("FatalErrorEvent[");
		sb.append(Const.NEW_LINE);
		sb.append("    Component:  " + component);
		sb.append(Const.NEW_LINE);
		sb.append("    Message:    " + message);
		if (cause != null) {
			Throwable cur = cause;
			while (cur != null) {
				sb.append(Const.NEW_LINE);
				sb.append("    Cause:      " + cur.getClass() + ": " + cur.getMessage() + 
						(cur.getStackTrace() == null || cur.getStackTrace().length == 0 ? 
								" (at UNAVAILABLE)"
							:	" (at " + cur.getStackTrace()[0].toString() + ")")
				);
				cur = cur.getCause();
			}
			sb.append(Const.NEW_LINE);
			sb.append("    Stacktrace:");
			sb.append(Const.NEW_LINE);
			sb.append(printStackStrace(stackTrace, "        "));
			cur = cause;
			while (cur != null) {
				sb.append(Const.NEW_LINE);
				sb.append("    Caused by: " + cur.getClass() + ": " + cur.getMessage() + 
						(cur.getStackTrace() == null || cur.getStackTrace().length == 0 ? 
								" (at UNAVAILABLE)"
							:	" (at " + cur.getStackTrace()[0].toString() + ")")
				);
				sb.append(Const.NEW_LINE);
				sb.append(printStackStrace(cur.getStackTrace(), "        "));
				cur = cur.getCause();
			}
		} else {
			sb.append(Const.NEW_LINE);
			sb.append("    Stacktrace:");
			sb.append(Const.NEW_LINE);
			sb.append(printStackStrace(stackTrace, "        "));
		}
		sb.append(Const.NEW_LINE);
		sb.append("]");
		return sb.toString();
	}
	
}
