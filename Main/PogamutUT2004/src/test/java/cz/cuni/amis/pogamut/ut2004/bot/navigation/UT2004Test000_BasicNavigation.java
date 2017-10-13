package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import org.junit.Test;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;

public class UT2004Test000_BasicNavigation extends UT2004BotTest {
	
	@Override
	protected String getMapName() {
		return "DM-TrainingDay";
	}
	
	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}
	
	@Test
	public void test() {
		startTest(
			NavigationTestBot.class,
			5,
			new NavigationTestBotParameters("DM-TrainingDay.InventorySpot45", "DM-TrainingDay.PathNode84",20)
			
		);
	}
	
}