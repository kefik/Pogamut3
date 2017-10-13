package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test103_DMDesertIsle_NarrowRamp extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-DesertIsle";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test103_narrow_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-DesertIsle.PathNode180, end: DM-DesertIsle.PathNode12 number of repetitions   both ways
			new NavigationTestBotParameters("DM-DesertIsle.PathNode180",      "DM-DesertIsle.PathNode12",    1,                        true)
		);
	}


        /*
        * TODO: Test fails
        * - misto, aby sel bot nejkratsi cestou, vezme to velikou oklikou -> proto to nestihne do casoveho limitu
        *  dost mozna je to chyba v mape
        */
        @Test
	public void test103_narrow_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 3 minutes
			3,
			// test movement between        start: DM-DesertIsle.PathNode180, end: DM-DesertIsle.PathNode12 number of repetitions   both ways
			new NavigationTestBotParameters("DM-DesertIsle.PathNode180",      "DM-DesertIsle.PathNode12",    20,                        true)
		);
	}

}