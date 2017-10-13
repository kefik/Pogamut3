package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

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
			Navigation2TestBot.class,
			5,
			new Navigation2TestBotParameters("DM-TrainingDay.InventorySpot45", "DM-TrainingDay.PathNode84",20)
			
		);
	}
	
}
