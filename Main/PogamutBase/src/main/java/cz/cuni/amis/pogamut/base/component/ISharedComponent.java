package cz.cuni.amis.pogamut.base.component;

import javax.management.MXBean;

import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.component.controller.ComponentDependencies;
import cz.cuni.amis.pogamut.base.component.lifecyclebus.ILifecycleBus;

/**
 * Every agent consists of components. Typically the component are ordinary {@link IComponent}. But there are times
 * when is feasible that more agents shares the same instance of one component. That means such component must behave
 * differently in terms of starting/stopping itself when it comes to lifecycle management of the component. Thus {@link ISharedComponent} 
 * was created.
 * <p><p>
 * Every implementor of {@link ISharedComponent} must be prepared to be used by multiple agent instances. That is, it must be carefully designed
 * to be thread-safe. Moreover it should run if any of its users (agents) is running. That is the component must start together with
 * the start of the first user-agent and stop itself with the stop of the last user-agent (i.e., when the last running agent is going to be
 * stopped). 
 * <p><p>
 * Again, the component itself does not need to know anything apart the {@link ILifecycleBus} the user-agent is using, therefore there is only 
 * a single simple method {@link ISharedComponent#addLifecycleBus(ILifecycleBus)} that informs the {@link ISharedComponent} that is has become
 * used by another agent.
 * 
 * @author Jimmy
 */
@MXBean
public interface ISharedComponent extends IComponent {

	/**
	 * Informs the component that it is part of another {@link ILifecycleBus}, i.e., it has become used by new agent with 'agentId'.
	 * <p><p>
	 * The component is obliged to register to that bus a watch for the lifecycle state of various components inside the bus.
	 * 
	 * @param agentId
	 * @param bus
	 */
	public void addComponentBus(IAgentId agentId, ILifecycleBus bus, ComponentDependencies dependencies);
	
	/**
	 * Informs the component that it ceased to be the part of the {@link ILifecycleBus}, i.e., it has stopped to be used by agent with 'agentId'.
	 * 
	 * @param agentId
	 * @param bus
	 */
	public void removeComponentBus(IAgentId agentId, ILifecycleBus bus);
	
}
