package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test012_DMGoliath_Teleport extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-Goliath";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test12_teleport_1_time_bothways() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-Goliath.PathNode36, end: DM-Goliath.PathNode40 number of repetitions   both ways
			new NavigationTestBotParameters("DM-Goliath.PathNode36",      "DM-Goliath.PathNode40",    1,                        true)
		);
	}

        @Test
	public void test12_teleport_20_time_bothways() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-Goliath.PathNode36, end: DM-Goliath.PathNode40 DM-Goliath.InventorySpot191 number of repetitions   both ways
			new NavigationTestBotParameters("DM-Goliath.PathNode36",      "DM-Goliath.PathNode40",    20,                        true)
		);
	}

}
