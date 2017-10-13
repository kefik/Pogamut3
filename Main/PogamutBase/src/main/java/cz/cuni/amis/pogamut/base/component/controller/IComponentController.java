package cz.cuni.amis.pogamut.base.component.controller;

import cz.cuni.amis.pogamut.base.component.IComponent;
import cz.cuni.amis.pogamut.base.component.ISharedComponent;
import cz.cuni.amis.utils.token.IToken;

/**
 * The component controller is meant for simple {@link IComponent} NOT {@link ISharedComponent}s (for them, use {@link ISharedComponentController} instead).
 * <p><p>
 * It is suitable for controlling lifecycle of one component inside one component bus. It provides methods for
 * querying components the controlled component is depending on. 
 * <p><p>
 * For more information, how {@link IComponentController} should behave, see (quite exhausting) javadoc for its concrete implementation {@link ComponentController}.
 * 
 * @author Jimmy
 */
public interface IComponentController<COMPONENT extends IComponent> extends IComponentControllerBase<COMPONENT> {
	
	/**
	 * Whether the controlled component is dependent on the component identified by 'componentId'.
	 * @param componentId
	 * @return
	 */
	public boolean isDependent(IToken componentId);
	
	/**
	 * Whether the controlled component is dependent on 'component'.
	 * @param component
	 * @return
	 */
	public boolean isDependent(IComponent component);
	
}
