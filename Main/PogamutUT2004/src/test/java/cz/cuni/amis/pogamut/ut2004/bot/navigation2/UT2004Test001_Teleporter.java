package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import org.junit.Test;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;

public class UT2004Test001_Teleporter extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-FaceClassic";
	}
	
	@Override
	protected String getGameType() {
		return "BotCTFGame";
	}
	
	@Test
	public void testTeleport1_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between start: CTF-FaceClassic.PathNode51, end: CTF-FaceClassic.InventorySpot99 number of repetitions
			new Navigation2TestBotParameters("CTF-FaceClassic.PathNode51", "CTF-FaceClassic.InventorySpot99", 1)
			
		);
	}
	
	@Test
	public void testTeleport1_20_times() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 3 minutes
			3,
			// test movement between start: CTF-FaceClassic.PathNode51, end: CTF-FaceClassic.InventorySpot99 number of repetitions
			new Navigation2TestBotParameters("CTF-FaceClassic.PathNode51", "CTF-FaceClassic.InventorySpot99", 20)
			
		);
	}
	
	@Test
	public void testTeleport2_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between start: CTF-FaceClassic.PathNode56, end: CTF-FaceClassic.InventorySpot110  number of repetitions
			new Navigation2TestBotParameters("CTF-FaceClassic.PathNode56", "CTF-FaceClassic.InventorySpot31",   1)
			
		);
	}
	
	@Test
	public void testTeleport2_20_times() {
		startTest(
			Navigation2TestBot.class,
			// timeout in minutes
			4,
			//                               start navpoint                end navpoint                      number of repetitions			
			new Navigation2TestBotParameters("CTF-FaceClassic.PathNode56", "CTF-FaceClassic.InventorySpot31", 20)
			
		);
	}
	
	@Test
	public void testTeleport3_1_time() {
		startTest(
			Navigation2TestBot.class,
			// timeout in minutes
			1,
			//						         start navpoint                end navpoint                       number of repetitions
			new Navigation2TestBotParameters("CTF-FaceClassic.PathNode56", "CTF-FaceClassic.InventorySpot107", 1)
			
		);
	}
	
	@Test
	public void testTeleport3_20_time() {
		startTest(
			Navigation2TestBot.class,
			// timeout in minutes
			10,
			//						         start navpoint                end navpoint                       number of repetitions
			new Navigation2TestBotParameters("CTF-FaceClassic.PathNode56", "CTF-FaceClassic.InventorySpot107", 20)
		);
	}

}
