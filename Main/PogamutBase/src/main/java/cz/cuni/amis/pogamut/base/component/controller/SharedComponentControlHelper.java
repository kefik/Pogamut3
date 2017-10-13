package cz.cuni.amis.pogamut.base.component.controller;

import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.utils.exception.PogamutException;

/**
 * Provides empty implementations of life-cycle methods defined by {@link ISharedComponentControlHelper} - override only these that you need.
 * 
 * @author Jimmy
 */
public class SharedComponentControlHelper implements ISharedComponentControlHelper {

	@Override
	public void localKill(IAgentId agentId) {
	}

	@Override
	public void localPause(IAgentId agentId) throws PogamutException {
	}

	@Override
	public void localPrePause(IAgentId agentId) throws PogamutException {
	}

	@Override
	public void localPreResume(IAgentId agentId) throws PogamutException {
	}

	@Override
	public void localPreStart(IAgentId agentId) throws PogamutException {
	}

	@Override
	public void localPreStartPaused(IAgentId agentId) throws PogamutException {
	}

	@Override
	public void localPreStop(IAgentId agentId) throws PogamutException {
	}

	@Override
	public void localReset(IAgentId agentId) {
	}

	@Override
	public void localResume(IAgentId agentId) throws PogamutException {
	}

	@Override
	public void localStart(IAgentId agentId) throws PogamutException {
	}

	@Override
	public void localStartPaused(IAgentId agentId) throws PogamutException {
	}

	@Override
	public void localStop(IAgentId agentId) throws PogamutException {
	}

	@Override
	public void kill() {
	}
	
	@Override
	public void pause() throws PogamutException {
	}

	@Override
	public void prePause() throws PogamutException {
	}

	@Override
	public void preResume() throws PogamutException {
	}

	@Override
	public void preStart() throws PogamutException {
	}

	@Override
	public void preStartPaused() throws PogamutException {
	}

	@Override
	public void preStop() throws PogamutException {
	}

	@Override
	public void reset() throws PogamutException {
	}

	@Override
	public void resume() throws PogamutException {
	}

	@Override
	public void start() throws PogamutException {
	}

	@Override
	public void startPaused() throws PogamutException {
	}

	@Override
	public void stop() throws PogamutException {
	}

}
