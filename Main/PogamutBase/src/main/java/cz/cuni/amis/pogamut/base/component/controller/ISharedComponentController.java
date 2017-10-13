package cz.cuni.amis.pogamut.base.component.controller;

import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.component.IComponent;
import cz.cuni.amis.pogamut.base.component.ISharedComponent;
import cz.cuni.amis.pogamut.base.component.lifecyclebus.ILifecycleBus;
import cz.cuni.amis.utils.token.IToken;

/**
 * The component controller is meant for simple {@link ISharedComponent} NOT {@link IComponent}s (for them, use {@link IComponentController} instead).
 * <p><p>
 * It is suitable for controlling lifecycle of one component inside one component bus. It provides methods for
 * querying components the controlled component is depending on. 
 *
 * @author Jimmy
 */
public interface ISharedComponentController<COMPONENT extends ISharedComponent> extends IComponentControllerBase<COMPONENT>, ISharedComponent {

	/**
	 * Tells whether the agent identified by 'agentId' is currently using the controlled component, i.e., this component controller
	 * registers the component to agent's {@link ILifecycleBus} and is watching it for auto start/stop/pause/resume/...
	 * 
	 * @param agentId
	 * @return
	 */
	public boolean isUsedBy(IAgentId agentId);
	
	/**
	 * Whether the controlled component is dependent on component (identified by 'componentId') of the agent identified
	 * by 'agentId'.
	 * <p><p>
	 * Note that two {@link IComponent} belonging to different agents may have the same 'componentId'.
	 * 
	 * @param agentId
	 * @param componentId
	 * @return
	 */
	public boolean isDependent(IAgentId agentId, IToken componentId);
	
	/**
	 * Whether the controlled component is dependent on 'component' of the agent identified by 'agentId'.
	 * 
	 * @param component
	 * @return
	 */
	public boolean isDependent(IAgentId agentId, IComponent component);
	
}
