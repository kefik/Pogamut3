package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test061_DMLeviathan_NarrowCorridor extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-Leviathan";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test61_narrow_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-Leviathan.PlayerStart1, end: DM-Leviathan.PathNode40 number of repetitions   both ways
			new NavigationTestBotParameters("DM-Leviathan.PlayerStart1",      "DM-Leviathan.PathNode40",    1,                        true)
		);
	}

        @Test
	public void test61_narrow_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: DM-Leviathan.PlayerStart1, end: DM-Leviathan.PathNode40 number of repetitions   both ways
			new NavigationTestBotParameters("DM-Leviathan.PlayerStart1",      "DM-Leviathan.PathNode40",    20,                        true)
		);
	}

}