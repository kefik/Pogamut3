package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 * Attribute NeededJump is not empty... Bot NEVER manages to get there.
 * 
 * Link:
 * INGP {Id DM-Compressed.JumpSpot22} {Flags 256} {CollisionR 72} {CollisionH 100}
 * {ForceDoubleJump False} {CalculatedGravityZ -950.00} {NeededJump 313.82,308.41,563.48}
 * {NeverImpactJump True} {NoLowGrav False} {OnlyTranslocator False}
 * {TranslocTargetTag None} {TranslocZOffset 0.00}
 * 
 * @todo fixme
 * @author Knight
 */
public class UT2004Test011_JumpUp extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-Compressed";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

	@Test
	public void testJumpUp_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between start: DM-Corrugation.InventorySpot112, end: DM-Corrugation.PathNode1 number of repetitions
			new Navigation2TestBotParameters("DM-Compressed.PathNode18", "DM-Compressed.JumpSpot22",1)			
		);
	}

	@Test
	public void testJumpUp_20_times() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 5 minutes
			5,
			// test movement between start: DM-Corrugation.InventorySpot112, end: DM-Corrugation.PathNode1 number of repetitions
			new Navigation2TestBotParameters("DM-Compressed.PathNode18", "DM-Compressed.JumpSpot22", 20)			
		);
	}
}
