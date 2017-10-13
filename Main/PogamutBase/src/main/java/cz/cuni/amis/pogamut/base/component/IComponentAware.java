package cz.cuni.amis.pogamut.base.component;

import cz.cuni.amis.pogamut.base.component.bus.IComponentBus;
import cz.cuni.amis.pogamut.base.utils.guice.AgentScoped;

public interface IComponentAware {

	/**
	 * {@link IComponentBus} that the instance is working with.
	 * <p><p>
	 * Note that by design-choice - the {@link IComponentBus} is a singleton inside {@link AgentScoped},
	 * therefore you don't have to necessarily obtain the instance through the component, it suffice
	 * to obtain it using injection into your object.
	 * 
	 * @return
	 */
	public IComponentBus getEventBus();
	
}
