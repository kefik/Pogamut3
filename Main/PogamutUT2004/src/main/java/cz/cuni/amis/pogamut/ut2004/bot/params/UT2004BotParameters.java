package cz.cuni.amis.pogamut.ut2004.bot.params;

import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.agent.params.IAgentParameters;
import cz.cuni.amis.pogamut.base.communication.connection.IWorldConnectionAddress;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.base3d.worldview.object.Rotation;
import cz.cuni.amis.pogamut.ut2004.agent.params.UT2004AgentParameters;

public class UT2004BotParameters extends UT2004AgentParameters {

	private Integer team;
	private Location initialLocation;
	private Rotation initialRotation;

	/**
	 * If you need to populate the parameters after instantiation, use setters available in this
	 * class: {@link UT2004BotParameters#setAgentId(IAgentId)}, {@link UT2004BotParameters#setWorldAddress(IWorldConnectionAddress)}, {@link UT2004BotParameters#setTeam(int)}.
	 */
	public UT2004BotParameters() {
		super();
	}
	
	@Override
	public UT2004BotParameters setAgentId(IAgentId agentId) {
		super.setAgentId(agentId);
		return this;
	}
	
	@Override
	public UT2004BotParameters setWorldAddress(IWorldConnectionAddress address) {
		super.setWorldAddress(address);
		return this;
	}

	/**
	 * Preferred team. If illegal or no team provided, one will be
	 * provided for you. Normally a team game has team 0 and team 1. 
	 * In BotDeathMatch, team is meaningless.
	 * 
	 * @return
	 */
	public Integer getTeam() {
		return team;
	}

	/**
	 * Preferred team. If illegal or no team provided, one will be
	 * provided for you. Normally a team game has team 0 and team 1. 
	 * In BotDeathMatch, team is meaningless.
	 * 
	 * @param team
	 * @return
	 */
	public UT2004BotParameters setTeam(Integer team) {
		this.team = team;
		return this;
	}
	
	public UT2004BotParameters setInitialLocation(Location location) {
		this.initialLocation = location;
		return this;
	}
	
	public Location getInitialLocation() {
		return initialLocation;
	}
	
	public UT2004BotParameters setInitialRotation(Rotation rotation) {
		this.initialRotation = rotation;
		return this;
	}
	
	public Rotation getInitialRotation() {
		return initialRotation;
	}

	@Override
	public void assignDefaults(IAgentParameters defaults) {
		super.assignDefaults(defaults);
		if (defaults instanceof UT2004BotParameters) {
			if (team == null) {
				team = ((UT2004BotParameters)defaults).getTeam();
			}
			if (initialLocation == null) {
				initialLocation = ((UT2004BotParameters)defaults).getInitialLocation();
			}
			if (initialRotation == null) {
				initialRotation = ((UT2004BotParameters)defaults).getInitialRotation();
			}
		}
	}
	
}
