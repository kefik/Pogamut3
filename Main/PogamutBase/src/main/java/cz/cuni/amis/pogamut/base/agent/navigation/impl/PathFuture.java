package cz.cuni.amis.pogamut.base.agent.navigation.impl;

import java.util.List;

import cz.cuni.amis.pogamut.base.agent.navigation.IPathFuture;
import cz.cuni.amis.pogamut.base.component.IComponent;
import cz.cuni.amis.pogamut.base.component.bus.IComponentBus;
import cz.cuni.amis.pogamut.base.utils.future.ComponentFuture;

/**
 * Simple implementation of the {@link IPathFuture} interface that assumes the computation to be 
 * dependent on some {@link IComponent}s. Therefore the path future retrieval method ({@link PathFuture#get()} and 
 * {@link PathFuture#get(long, java.util.concurrent.TimeUnit)}) will fail if one of these components fails.
 * 
 * @author Jimmy
 *
 * @param <PATH_ELEMENT>
 */
public class PathFuture<PATH_ELEMENT> extends ComponentFuture<List<PATH_ELEMENT>> implements IPathFuture<PATH_ELEMENT> {

	private PATH_ELEMENT pathFrom;
	private PATH_ELEMENT pathTo;

	/**
	 * Initialize the path future as independent on any {@link IComponent}.
	 * 
	 * @param pathFrom
	 * @param pathTo
	 */
	public PathFuture(PATH_ELEMENT pathFrom, PATH_ELEMENT pathTo) {
		super(null);
		this.pathFrom = pathFrom;
		this.pathTo = pathTo;
	}

	
	/**
	 * Initialize the path future as dependent on 'dependants'. If one of the component the path computation depends
	 * on fails - the path future will report exception upon getting the path result.
	 * <p><p>
	 * See {@link ComponentFuture#ComponentFuture(IComponentBus, IComponent...)} for more details about 'bus' and 'dependants'
	 * parameters.
	 * 
	 * @param pathFrom
	 * @param pathTo
	 * @param bus
	 * @param depends
	 */
	public PathFuture(PATH_ELEMENT pathFrom, PATH_ELEMENT pathTo, IComponentBus bus, IComponent... dependants) {
		super(bus, dependants);
		this.pathFrom = pathFrom;
		this.pathTo = pathTo;
	}

	@Override
	public PATH_ELEMENT getPathFrom() {
		return pathFrom;
	}

	@Override
	public PATH_ELEMENT getPathTo() {
		return pathTo;
	}

}
