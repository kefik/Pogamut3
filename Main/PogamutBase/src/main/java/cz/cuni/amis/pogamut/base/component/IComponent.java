package cz.cuni.amis.pogamut.base.component;

import javax.management.MXBean;

import cz.cuni.amis.utils.token.IToken;

/**
 * Every agent consists of components. Component is anything that wraps some kind of functionality.
 * Such as WorldView, ShootingBehavior or the Agent itself.
 * <p><p>
 * Every component has its own ID (string based), that can be thought of as Spring-bean identifier.
 * Every component may register listener on events from other components using this ID and EventBus.
 * 
 * @author Jimmy
 */
@MXBean
public interface IComponent {
	
	/**
	 * Unique identification of the component.
	 * @return
	 */
	public IToken getComponentId();
	
// Could not be here, as Logger has trouble with serialization...
//	/**
//	 * Returns log used by the component.
//	 * @return
//	 */
//	public Logger getLog();
	
}
