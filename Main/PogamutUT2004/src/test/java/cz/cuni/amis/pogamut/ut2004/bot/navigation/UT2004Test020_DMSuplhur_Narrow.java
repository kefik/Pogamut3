package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test020_DMSuplhur_Narrow extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "DM-Sulphur";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test20_narrow_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: DM-Sulphur.PlayerStart16, end: DM-Sulphur.PlayerStart9 number of repetitions   both ways
			new NavigationTestBotParameters("DM-Sulphur.PlayerStart16",      "DM-Sulphur.PlayerStart9",    1,                        true)
		);
	}

        @Test
	public void test20_narrow_20_time() {
                
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 8 minutes
			8,
			// test movement between        start: DM-Sulphur.PlayerStart16, end: DM-Sulphur.PlayerStart9 number of repetitions   both ways
			new NavigationTestBotParameters("DM-Sulphur.PlayerStart16",      "DM-Sulphur.PlayerStart9",    20,                        true)
		); 
	}

}
