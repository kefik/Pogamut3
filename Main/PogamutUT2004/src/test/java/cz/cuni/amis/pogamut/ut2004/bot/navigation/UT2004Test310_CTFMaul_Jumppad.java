package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test310_CTFMaul_Jumppad extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-Maul";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test310_jumppad_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-Maul.PathNode81, end: CTF-Maul.xRedFlagBase0 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-Maul.PathNode81",      "CTF-Maul.xRedFlagBase0",    1,                        false)
		);
	}

        @Test
	public void test310_jumppad_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 2 minutes
			2,
			// test movement between        start: CTF-Maul.PathNode81, end: CTF-Maul.xRedFlagBase0 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-Maul.PathNode81",      "CTF-Maul.xRedFlagBase0",    20,                        false)
		);
	}

}