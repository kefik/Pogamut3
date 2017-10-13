package cz.cuni.amis.pogamut.base.component.stub.sharedcomponent;

import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.component.ISharedComponent;
import cz.cuni.amis.utils.exception.PogamutException;

public class MethodToString {

	public static String kill(ISharedComponent component) {
		return component.getComponentId().getToken() + " kill()";
	}

	public static String localKill(ISharedComponent component, IAgentId agentId) {
		return component.getComponentId().getToken() + " localKill(" + agentId + ")";
	}

	public static String localPause(ISharedComponent component, IAgentId agentId) throws PogamutException {
		return component.getComponentId().getToken() + " localPause(" + agentId + ")";
	}

	public static String localPrePause(ISharedComponent component, IAgentId agentId)
			throws PogamutException {
		return component.getComponentId().getToken() + " localPrePause(" + agentId + ")";
	}

	public static String localPreResume(ISharedComponent component, IAgentId agentId)
			throws PogamutException {
		return component.getComponentId().getToken() + " localPreResume(" + agentId + ")";
	}

	public static String localPreStart(ISharedComponent component, IAgentId agentId)
			throws PogamutException {
		return component.getComponentId().getToken() + " localPreStart(" + agentId + ")";
	}

	public static String localPreStartPaused(ISharedComponent component, IAgentId agentId)
			throws PogamutException {
		return component.getComponentId().getToken() + " localPreStartPaused(" + agentId + ")";
	}

	public static String localPreStop(ISharedComponent component, IAgentId agentId) throws PogamutException {
		return component.getComponentId().getToken() + " localPreStop(" + agentId + ")";
	}

	public static String localReset(ISharedComponent component, IAgentId agentId) {
		return component.getComponentId().getToken() + " localReset(" + agentId + ")";
	}

	public static String localResume(ISharedComponent component, IAgentId agentId) throws PogamutException {
		return component.getComponentId().getToken() + " localResume(" + agentId + ")";
	}

	public static String localStart(ISharedComponent component, IAgentId agentId) throws PogamutException {
		return component.getComponentId().getToken() + " localStart(" + agentId + ")";
	}

	public static String localStartPaused(ISharedComponent component, IAgentId agentId)
			throws PogamutException {
		return component.getComponentId().getToken() + " localStartPaused(" + agentId + ")";
	}

	public static String localStop(ISharedComponent component, IAgentId agentId) throws PogamutException {
		return component.getComponentId().getToken() + " localStop(" + agentId + ")";
	}

	public static String pause(ISharedComponent component) throws PogamutException {
		return component.getComponentId().getToken() + " pause()";
	}

	public static String prePause(ISharedComponent component) throws PogamutException {
		return component.getComponentId().getToken() + " prePause()";
	}

	public static String preResume(ISharedComponent component) throws PogamutException {
		return component.getComponentId().getToken() + " preResune()";
	}

	public static String preStart(ISharedComponent component) throws PogamutException {
		return component.getComponentId().getToken() + " preStart()";
	}

	public static String preStartPaused(ISharedComponent component) throws PogamutException {
		return component.getComponentId().getToken() + " preStartPaused()";
	}

	public static String preStop(ISharedComponent component) throws PogamutException {
		return component.getComponentId().getToken() + " preStop()";
	}

	public static String reset(ISharedComponent component) throws PogamutException {
		return component.getComponentId().getToken() + " reset()";
	}

	public static String resume(ISharedComponent component) throws PogamutException {
		return component.getComponentId().getToken() + " resume()";
	}

	public static String start(ISharedComponent component) throws PogamutException {
		return component.getComponentId().getToken() + " start()";
	}

	public static String startPaused(ISharedComponent component) throws PogamutException {
		return component.getComponentId().getToken() + " startPaused()";
	}

	public static String stop(ISharedComponent component) throws PogamutException {
		return component.getComponentId().getToken() + " stop()";
	}

}
