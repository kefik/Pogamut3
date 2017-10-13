package cz.cuni.amis.pogamut.ut2004.tournament.deathmatch.config;

import java.io.File;

import cz.cuni.amis.pogamut.ut2004.tournament.match.UT2004BotConfig;

public class PyrohBot {

	public static UT2004BotConfig createConfig() {
		UT2004BotConfig config = new UT2004BotConfig();
		
		config.setBotId("PyrohBot");
		config.setPathToBotJar("bots" + File.separator + "TobRetnuh" + File.separator + "TobRetnuh.jar");
		config.setRedirectStdErr(true);
		config.setRedirectStdOut(true);
		config.setBotTeam(255);
	
		return config;
	}
	
}
