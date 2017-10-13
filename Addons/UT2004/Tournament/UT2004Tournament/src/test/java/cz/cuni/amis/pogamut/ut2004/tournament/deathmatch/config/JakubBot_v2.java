package cz.cuni.amis.pogamut.ut2004.tournament.deathmatch.config;

import java.io.File;

import cz.cuni.amis.pogamut.ut2004.tournament.match.UT2004BotConfig;

public class JakubBot_v2 {

	public static UT2004BotConfig createConfig() {
		UT2004BotConfig config = new UT2004BotConfig();
		
		config.setBotId("JakubBot_v2");
		config.setPathToBotJar("bots" + File.separator + "KefikBot_v2" + File.separator + "KefikBot_v2.jar");
		config.setRedirectStdErr(true);
		config.setRedirectStdOut(true);
		config.setBotTeam(255);
	
		return config;
	}
	
}
