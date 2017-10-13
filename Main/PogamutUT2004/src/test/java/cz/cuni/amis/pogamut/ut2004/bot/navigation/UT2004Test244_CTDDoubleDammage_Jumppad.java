package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test244_CTDDoubleDammage_Jumppad extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-DoubleDammage";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test244_jumppad_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-DoubleDammage.PathNode112, end: CTF-DoubleDammage.AIMarker45 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-DoubleDammage.PathNode112",      "CTF-DoubleDammage.AIMarker45",    1,                        true)
		);
	}

        @Test
	public void test244_jumppad_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 6 minutes
			6,
			// test movement between        start: CTF-DoubleDammage.PathNode112, end: CTF-DoubleDammage.AIMarker45 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-DoubleDammage.PathNode112",      "CTF-DoubleDammage.AIMarker45",    20,                        true)
		);
	}

}