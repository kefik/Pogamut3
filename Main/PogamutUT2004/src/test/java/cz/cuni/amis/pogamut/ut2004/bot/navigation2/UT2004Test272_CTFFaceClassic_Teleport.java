package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 *
 * @author Peta Michalik
 */
public class UT2004Test272_CTFFaceClassic_Teleport extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-FaceClassic";
	}

	@Override
	protected String getGameType() {
		return "BotDeathMatch";
	}

        @Test
	public void test272_teleport_1_time() {
		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 1 minute
			1,
			// test movement between        start: CTF-FaceClassic.PathNode79, end: CTF-FaceClassic.AIMarker6 number of repetitions   both ways
			new Navigation2TestBotParameters("CTF-FaceClassic.PathNode79",      "CTF-FaceClassic.AIMarker6",    1,                        true)
		);
	}

        @Test
	public void test272_teleport_20_time() {


		startTest(
			// use NavigationTestBot for the test
			Navigation2TestBot.class,
			// timeout: 6 minutes
			6,
			// test movement between        start: CTF-FaceClassic.PathNode79, end: CTF-FaceClassic.AIMarker6 number of repetitions   both ways
			new Navigation2TestBotParameters("CTF-FaceClassic.PathNode79",      "CTF-FaceClassic.AIMarker6",    20,                        true)
		);
	}

}
