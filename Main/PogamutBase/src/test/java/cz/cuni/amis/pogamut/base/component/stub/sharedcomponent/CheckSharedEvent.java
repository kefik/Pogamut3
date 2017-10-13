package cz.cuni.amis.pogamut.base.component.stub.sharedcomponent;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;

import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.component.IComponent;
import cz.cuni.amis.pogamut.base.component.bus.IComponentEvent;
import cz.cuni.amis.utils.Const;
import cz.cuni.amis.utils.NullCheck;
import cz.cuni.amis.utils.SafeEquals;

public class CheckSharedEvent {
	
	private List<Class> eventClasses = new ArrayList<Class>();
	private IComponent source = null;
	private IAgentId agentId;

	/**
	 * Takes first interface as that one to consider.
	 */
	public CheckSharedEvent(IAgentId agentId, IComponentEvent checkEvent) {
		this.agentId = agentId;
		NullCheck.check(agentId, "agentId");
		for (Class cls : checkEvent.getClass().getInterfaces()) {
			eventClasses.add(cls);
		}
		this.source = checkEvent.getSource();
		NullCheck.check(this.source, "checkEvent.getSource()");
	}
	
	public CheckSharedEvent(IAgentId agentId, Class eventClass) {
		this.agentId = agentId;
		NullCheck.check(agentId, "agentId");
		NullCheck.check(eventClass, "eventClass");
		this.eventClasses.add(eventClass);		
	}
	
	public CheckSharedEvent(IAgentId agentId, Class eventClass, IComponent source) {
		this.agentId = agentId;
		NullCheck.check(agentId, "agentId");
		NullCheck.check(eventClass, "eventClass");
		this.eventClasses.add(eventClass);		
		this.source = source;
		NullCheck.check(this.source, "source");
	}
	
	public CheckSharedEvent(IAgentId agentId, IComponent source) {
		this.agentId = agentId;
		NullCheck.check(agentId, "agentId");
		this.source = source;
		NullCheck.check(this.source, "source");
	}
	
	public void check(IAgentId agentId, IComponentEvent event) {
		// checking agent
		if (!SafeEquals.equals(this.agentId, agentId)) {
			System.out.println("UNEXPECTED EVENT - WRONG AGENT");
			System.out.println("EXPECTED");
			System.out.println("    FROM AGENT:     " + this.agentId);
			System.out.println("    EVENT:          " + eventClasses.get(0).getName());
			if (source != null) {
				System.out.println("    FROM COMPONENT: " + source);
			}
			System.out.println("GOT");
			System.out.println("    AGENT:          " + agentId);
			System.out.println("    " + event);
			System.out.println("    FROM: " + event.getSource());
			Assert.fail(Const.NEW_LINE + "Event from wrong agent, expected from " + this.agentId + " got from " + agentId + ".");
		}
		// checking event classes
		for (Class cls : eventClasses) {
			if (!cls.isAssignableFrom(event.getClass())) {				
				System.out.println("UNEXPECTED EVENT - WRONG EVENT CLASS");
				System.out.println("EXPECTED");
				System.out.println("    FROM AGENT:     " + this.agentId);
				System.out.println("    EVENT CLASS:    " + cls);
				if (source != null) {
					System.out.println("    FROM COMPONENT: " + source);
				}
				System.out.println("GOT");
				System.out.println("    AGENT:          " + agentId);
				System.out.println("    EVENT CLASS:    " + event.getClass());
				System.out.println("    FROM COMPONENT: " + event.getSource());
				Assert.fail(Const.NEW_LINE + "Event of wrong class, expected " + cls + " got " + event.getClass() + ".");
			}
		}
		// checking source
		if (source != null) {
			if (source != event.getSource()) {
				System.out.println("UNEXPECTED EVENT - WRONG EVENT SOURCE");
				System.out.println("EXPECTED");
				System.out.println("    FROM AGENT:     " + this.agentId);
				System.out.println("    EVENT CLASS:    " + eventClasses.get(0).getName());
				if (source != null) {
					System.out.println("    FROM COMPONENT: " + source);
				}
				System.out.println("GOT");
				System.out.println("    AGENT:          " + agentId);
				System.out.println("    EVENT CLASS:    " + event.getClass());
				System.out.println("    FROM COMPONENT: " + event.getSource());
				Assert.fail(Const.NEW_LINE + "Event from wrong source, expected " + source + " got " + event.getSource() + ".");
			}
		}
	}
	
	public boolean checkNoException(IAgentId agentId, IComponentEvent event) {
		// checking agent
		if (!SafeEquals.equals(this.agentId, agentId)) {
			System.out.println("UNEXPECTED EVENT - WRONG AGENT");
			System.out.println("EXPECTED");
			System.out.println("    FROM AGENT:     " + this.agentId);
			System.out.println("    EVENT:          " + eventClasses.get(0).getName());
			if (source != null) {
				System.out.println("    FROM COMPONENT: " + source);
			}
			System.out.println("GOT");
			System.out.println("    AGENT:          " + agentId);
			System.out.println("    " + event);
			System.out.println("    FROM: " + event.getSource());
			return false;
		}
		// checking event classes
		for (Class cls : eventClasses) {
			if (!cls.isAssignableFrom(event.getClass())) {				
				System.out.println("UNEXPECTED EVENT - WRONG EVENT CLASS");
				System.out.println("EXPECTED");
				System.out.println("    FROM AGENT:     " + this.agentId);
				System.out.println("    EVENT CLASS:    " + cls);
				if (source != null) {
					System.out.println("    FROM COMPONENT: " + source);
				}
				System.out.println("GOT");
				System.out.println("    AGENT:          " + agentId);
				System.out.println("    EVENT CLASS:    " + event.getClass());
				System.out.println("    FROM COMPONENT: " + event.getSource());
				return false;
			}
		}
		// checking source
		if (source != null) {
			if (source != event.getSource()) {
				System.out.println("UNEXPECTED EVENT - WRONG EVENT SOURCE");
				System.out.println("EXPECTED");
				System.out.println("    FROM AGENT:     " + this.agentId);
				System.out.println("    EVENT CLASS:    " + eventClasses.get(0).getName());
				if (source != null) {
					System.out.println("    FROM COMPONENT: " + source);
				}
				System.out.println("GOT");
				System.out.println("    AGENT:          " + agentId);
				System.out.println("    EVENT CLASS:    " + event.getClass());
				System.out.println("    FROM COMPONENT: " + event.getSource());
				return false;
			}
		}
		return true;
	}
	

}
