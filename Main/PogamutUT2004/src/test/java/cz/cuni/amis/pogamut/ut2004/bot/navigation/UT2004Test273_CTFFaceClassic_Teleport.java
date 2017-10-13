package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test273_CTFFaceClassic_Teleport extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-FaceClassic";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test273_teleport_1_time() {
		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-FaceClassic.AIMarker8, end: CTF-FaceClassic.AIMarker9 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-FaceClassic.AIMarker8",      "CTF-FaceClassic.AIMarker9",    1,                        true)
		);
	}


        /*
        * TODO: Test fails
        */
        @Test
	public void test273_teleport_20_time() {


		startTest(
			// use NavigationTestBot for the test
			NavigationTestBot.class,
			// timeout: 10 minutes
			10,
			// test movement between        start: CTF-FaceClassic.AIMarker8, end: CTF-FaceClassic.AIMarker9 number of repetitions   both ways
			new NavigationTestBotParameters("CTF-FaceClassic.AIMarker8",      "CTF-FaceClassic.AIMarker9",    20,                        true)
		);
	}

}