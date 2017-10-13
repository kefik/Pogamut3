package cz.cuni.amis.pogamut.ut2004.bot.killbot;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.bot.params.UT2004BotParameters;

public class KillBotParameters extends UT2004BotParameters {

	private Location turnToLocation;
	private Location spawningLocation;

	public KillBotParameters(Location spawningLocation, Location turnToLocation) {
		super();
		this.spawningLocation = spawningLocation;
		this.turnToLocation = turnToLocation;
	}

	public Location getTurnToLocation() {
		return turnToLocation;
	}

	public Location getSpawningLocation() {
		return spawningLocation;
	}
	
}
