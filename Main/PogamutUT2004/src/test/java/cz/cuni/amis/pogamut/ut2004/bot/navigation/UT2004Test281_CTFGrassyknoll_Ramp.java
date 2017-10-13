package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test281_CTFGrassyknoll_Ramp extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-Grassyknoll";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test281_ramp_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-Grassyknoll.InventorySpot180, end: CTF-Grassyknoll.InventorySpot181 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-Grassyknoll.InventorySpot180",      "CTF-Grassyknoll.InventorySpot181",    1,                        true)
		);
	}

        /*
         * TODO: Test fails
         */
        @Test
	public void test281_ramp_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 8 minutes
			8,
			// test movement between        start: CTF-Grassyknoll.InventorySpot180, end: CTF-Grassyknoll.InventorySpot181 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-Grassyknoll.InventorySpot180",      "CTF-Grassyknoll.InventorySpot181",    20,                        true)
		);
	}

}