package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test108_DMDeck17_NarrowRamp extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-Deck17";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test108_narrow_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-Deck17.PathNode5, end: DM-Deck17.PathNode12 number of repetitions   both ways
			new NavigationTestBotParameters("DM-Deck17.PathNode5",      "DM-Deck17.PathNode12",    1,                        true)
		);
	}

        @Test
	public void test108_narrow_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: DM-Deck17.PathNode5, end: DM-Deck17.PathNode12 number of repetitions   both ways
			new NavigationTestBotParameters("DM-Deck17.PathNode5",      "DM-Deck17.PathNode12",    20,                        true)
		);
	}

}