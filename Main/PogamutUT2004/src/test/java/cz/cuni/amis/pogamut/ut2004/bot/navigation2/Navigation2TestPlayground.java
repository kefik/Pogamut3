package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import org.junit.Test;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;

public class Navigation2TestPlayground extends UT2004BotTest {
	
	@Override
	protected String getMapName() {
		return "DM-1on1-Albatross";
	}
	
	@Override
	protected String getGameType() {
		return "BotCTFGame";
	}
	
	// FAILS
//	@Test
//	public void test1() {
//		startTest(
//			NavigationTestBot.class,
//			5,
//			new NavigationTestBotParameters("CTF-UG-Chrome.LiftExit5",   "CTF-UG-Chrome.PathNode169", 20, true)
////			new NavigationTestBotParameters("CTF-UG-Chrome.PathNode169", "CTF-UG-Chrome.LiftExit5",   20)
//		);
//	}
	
	// SUCCEEDS
	@Test
	public void test2() {
		startTest(
			Navigation2TestBot.class,
			20, // global timeout
			new Navigation2TestBotParameters("DM-1on1-Albatross.PathNode9",    "DM-1on1-Albatross.InventorySpot355",  5, true)
//			new Navigation2TestBotParameters("CTF-UG-Chrome.PathNode174",  "CTF-UG-Chrome.PathNode169",  100, true),
//			new Navigation2TestBotParameters("CTF-UG-Chrome.PathNode168",  "CTF-UG-Chrome.LiftExit5",    100, true)
		);
	}
	
}
