package cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.drawing;

import cz.cuni.amis.pogamut.ut2004.server.impl.UT2004Server;

public interface IUT2004ServerProvider {

	public UT2004Server getServer();
	
	public void killServer();
	
}
