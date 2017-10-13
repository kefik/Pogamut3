package cz.cuni.amis.pogamut.ut2004.bot.navigation.focus;

import org.junit.Test;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;

public class UT2004Test01_FocusDMTrainingDay extends UT2004BotTest {
	
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
			FocusTestBot.class,
			5,
			new FocusTestBotParameters("DM-TrainingDay.InventorySpot45", "DM-TrainingDay.PathNode84",20).setFocus("DM-TrainingDay.InventorySpot59")
		);
	}
	
}