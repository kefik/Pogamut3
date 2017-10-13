package cz.cuni.amis.pogamut.base.component.lifecyclebus;

import java.lang.ref.WeakReference;

import cz.cuni.amis.pogamut.base.agent.IAgent;
import cz.cuni.amis.pogamut.base.component.IComponent;
import cz.cuni.amis.pogamut.base.component.ISharedComponent;
import cz.cuni.amis.pogamut.base.component.bus.ComponentBus;
import cz.cuni.amis.pogamut.base.component.bus.IComponentBus;
import cz.cuni.amis.pogamut.base.component.bus.exception.MoreComponentsForClassException;
import cz.cuni.amis.pogamut.base.component.controller.ComponentController;
import cz.cuni.amis.pogamut.base.component.controller.ComponentDependencies;
import cz.cuni.amis.pogamut.base.component.controller.ComponentState;
import cz.cuni.amis.pogamut.base.component.controller.IComponentControlHelper;
import cz.cuni.amis.pogamut.base.component.controller.IComponentController;
import cz.cuni.amis.pogamut.base.component.exception.ComponentLifecycleManagementAlreadyRegisteredException;
import cz.cuni.amis.utils.flag.FlagListener;
import cz.cuni.amis.utils.flag.ImmutableFlag;
import cz.cuni.amis.utils.token.IToken;

/**
 * {@link ILifecycleBus} is extending {@link IComponentBus} by implementing the knowledge of lifecycle states of various {@link IComponent}s. It watches
 * over the {@link ComponentState}s, providing information about them.
 * <p><p>
 * Additionally the class is providing the functionality of {@link ComponentController} offering components a helping hand to watch over
 * events that are being broadcast over the bus and calls their lifecycle methods ({@link IComponentControlHelper} in correct times and 
 * according to their {@link ComponentDependencies}.
 * <p><p>
 * Additionally the {@link LifecycleBus} may accomodate {@link ISharedComponent}s as we're providing lifecycle management here as well.
 * 
 * @author Jimmy
 */
public interface ILifecycleBus extends IComponentBus {

	/**
	 * Returns current {@link ComponentState} of the component identified by 'componentId'.
	 * <p><p>
	 * Note that nothing guarantees that the state will change afterwards.
	 * <p><p>
	 * Note that we're not returning mere {@link ComponentState} but the immutable flag that you may use to attach {@link FlagListener}s to it
	 * allowing you to react on its changes as they happen.
	 * <p><p>
	 * WARNING: {@link ISharedComponent} state is always reported from the point of view of the bus, not the component! That
	 * means that the {@link ISharedComponent} may mask its behavior as if it would be a simple {@link IComponent} owned by single agent.
	 * For instance, after the bus broadcast {@link IFatalErrorEvent} as it is believed
	 * that all components will be killed thus the lifecycle bus will switch the component state into "KILLED" and after reset to "RESETED".
	 * Moreover, such behavior is quite a desired one because otherwise it would add unnecessary complexity from the point of view of the single agent
	 * and management of component auto-starting feature provided by {@link IComponentController}.
	 * 
	 * @param componentId
	 * @return
	 */
	public ImmutableFlag<ComponentState> getComponentState(IToken componentId);
	
	/**
	 * Returns current {@link ComponentState} of the component that implements / inherit the 'cls' class.
	 * <p><p>
	 * Note that nothing guarantees that the state will change afterwards.
	 * <p><p>
	 * If no components exist for 'cls', returns null.
	 * <p><p>
	 * Raises an exception {@link MoreComponentsForClassException} if more components for 'cls' exists.
	 * <p><p>
	 * Note that we're not returning mere {@link ComponentState} but the immutable flag that you may use to attach {@link FlagListener}s to it
	 * allowing you to react on its changes as they happen.
	 * <p><p>
	 * WARNING: {@link ISharedComponent} state is always reported from the point of view of the bus, not the component! That
	 * means that the {@link ISharedComponent} may mask its behavior as if it would be a simple {@link IComponent} owned by single agent.
	 * For instance, after the bus broadcast {@link IFatalErrorEvent} as it is believed
	 * that all components will be killed thus the lifecycle bus will switch the component state into "KILLED" and after reset to "RESETED".
	 * Moreover, such behavior is quite a desired one because otherwise it would add unnecessary complexity from the point of view of the single agent
	 * and management of component auto-starting feature provided by {@link IComponentController}.
	 * 
	 * @param <T>
	 * @param cls
	 * @return flag with the state of the component
	 * @throws MoreComponentsForClassException
	 */
	public ImmutableFlag<ComponentState> getComponentState(Class<? extends IComponent> cls) throws MoreComponentsForClassException;
	
	/**
	 * Registers 'lifecycleMethods' to be called in correct times according to 'componentDependencies' for the 'component'.
	 * <p><p>
	 * This method provides a powerful feature for {@link IComponent} to offload the troubles with sensing starting/stopping events of components
	 * it depends on in order to start/stop in correct times. It supplies the functionality of {@link ComponentController} which provides the same
	 * for simpler {@link ComponentBus}.
	 * <p><p>
	 * Every component may register only one lifecycle management.
	 * <p><p>
	 * The lifecycle management may be removed using {@link ILifecycleBus#removeEventListener(Class, cz.cuni.amis.pogamut.base.component.bus.IComponentEventListener)}.
	 * <p><p>
	 * WARNING: the 'lifecycleMethods' object is hold via {@link WeakReference}, you must store it for yourself! It also means you must have to
	 * store the instance of your component somewhere in the strongly referenced part of your code (i.e., in the {@link IAgent} instance). 
	 * 
	 * @param component
	 * @param lifecyleMethods
	 * @param componentDependencies
	 * @return controller that is used for the control of the component inside this bus
	 * @throws ComponentLifecycleManagementAlreadyRegisteredException
	 */
	public <T extends IComponent> IComponentController<T> addLifecycleManagement(T component, IComponentControlHelper lifecyleMethods, ComponentDependencies componentDependencies) throws ComponentLifecycleManagementAlreadyRegisteredException;
	
	/**
	 * Removes lifecycle management for a concrete 'component'.
	 * <p><p>
	 * Does nothing if the component does not have life-cycle methods registered inside the bus.
	 * 
	 * @param componentId
	 */
	public void removeLifecycleManagement(IComponent component);
	
}
