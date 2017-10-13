package cz.cuni.amis.pogamut.base.component.stub.component;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;

import cz.cuni.amis.pogamut.base.component.IComponent;
import cz.cuni.amis.pogamut.base.component.bus.IComponentEvent;
import cz.cuni.amis.utils.Const;
import cz.cuni.amis.utils.NullCheck;

public class CheckEvent {
	
	private List<Class> eventClasses = new ArrayList<Class>();
	private IComponent source = null;

	/**
	 * Takes first interface as that one to consider.
	 */
	public CheckEvent(IComponentEvent checkEvent) {
		for (Class cls : checkEvent.getClass().getInterfaces()) {
			eventClasses.add(cls);
		}
		this.source = checkEvent.getSource();
		NullCheck.check(this.source, "checkEvent.getSource()");
	}
	
	public CheckEvent(Class eventClass) {
		NullCheck.check(eventClass, "eventClass");
		this.eventClasses.add(eventClass);		
	}
	
	public CheckEvent(Class eventClass, IComponent source) {
		NullCheck.check(eventClass, "eventClass");
		this.eventClasses.add(eventClass);		
		this.source = source;
		NullCheck.check(this.source, "source");
	}
	
	public CheckEvent(IComponent source) {
		this.source = source;
		NullCheck.check(this.source, "source");
	}
	
	public void check(IComponentEvent event) {
		for (Class cls : eventClasses) {
			if (!cls.isAssignableFrom(event.getClass())) {
				System.out.println("EXPECTED: " + cls.getName());
				if (source != null) System.out.println("FROM: " + source);
				System.out.println("UNEXPECTED EVENT: ");
				System.out.println(event);
				System.out.println("FROM: " + event.getSource());
				Assert.fail(Const.NEW_LINE + "Expected event class: " + cls + Const.NEW_LINE + (source != null ? "From                : " + source + Const.NEW_LINE : "") + "Got class           : " + event.getClass() + Const.NEW_LINE + "From source         : " + event.getSource());
			}
		}
		if (source != null) {
			if (source != event.getSource()) {
				System.out.println("EXPECTED: " + eventClasses.get(0).getName());
				if (source != null) System.out.println("FROM: " + source);
				System.out.println("UNEXPECTED EVENT: ");
				System.out.println(event);
				System.out.println("FROM: " + event.getSource());
				Assert.fail(Const.NEW_LINE + "Event          : " + event.getClass() + Const.NEW_LINE + "Expected source: " + source + Const.NEW_LINE + "Got            : " + event.getSource());
			}
		}
	}
	
	public boolean checkNoException(IComponentEvent event) {
		for (Class cls : eventClasses) {
			if (!cls.isAssignableFrom(event.getClass())) {
				System.out.println("EXPECTED: " + cls.getName());
				if (source != null) System.out.println("FROM: " + source);
				System.out.println("UNEXPECTED EVENT: ");
				System.out.println(event);
				System.out.println("FROM: " + event.getSource());
				return false;
			}
		}
		if (source != null) {
			if (source != event.getSource()) {
				System.out.println("EXPECTED: " + eventClasses.get(0).getName());
				if (source != null) System.out.println("FROM: " + source);
				System.out.println("UNEXPECTED EVENT: ");
				System.out.println(event);
				System.out.println("FROM: " + event.getSource());
				return false;
			}
		}
		return true;
	}
	

}
