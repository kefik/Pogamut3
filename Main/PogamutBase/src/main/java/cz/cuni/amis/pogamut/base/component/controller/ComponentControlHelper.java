package cz.cuni.amis.pogamut.base.component.controller;

import cz.cuni.amis.utils.exception.PogamutException;

/**
 * Provides empty implementations of life-cycle methods defined by {@link IComponentControlHelper} - override only these that you need.
 * @author Jimmy
 */
public class ComponentControlHelper implements IComponentControlHelper {

	@Override
	public void kill() {
	}

	@Override
	public void prePause() throws PogamutException {
	}
	
	@Override
	public void pause() throws PogamutException {
	}

	@Override
	public void reset() {
	}
	
	@Override
	public void preResume() throws PogamutException {
		
	}

	@Override
	public void resume() throws PogamutException {
	}

	@Override
	public void preStart() throws PogamutException {		
	}
	
	@Override
	public void start() throws PogamutException {
	}

	@Override
	public void preStartPaused() throws PogamutException {
	}

	@Override
	public void startPaused() throws PogamutException {
	}
	
	@Override
	public void preStop() throws PogamutException {
	}
	
	@Override
	public void stop() throws PogamutException {
	}
	
	@Override
	public String toString() {
		return "ComponentControlHelper";
	}

	
}
