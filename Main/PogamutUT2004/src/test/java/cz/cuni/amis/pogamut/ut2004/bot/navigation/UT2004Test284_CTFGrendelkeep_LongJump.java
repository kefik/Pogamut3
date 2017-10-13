package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

/**
 * 
 * @author Peta Michalik
 */
public class UT2004Test284_CTFGrendelkeep_LongJump extends UT2004BotTest {

	@Override
	protected String getMapName() {
		return "CTF-Grendelkeep";
	}

	@Override
	protected String getGameType() {
		return "BotCTFGame";
	}

	@Test
	public void test284_long_jump_1_time_high_to_low() {
		startTest(
		// use NavigationTestBot for the test
				NavigationTestBot.class,
				// timeout:
				5,
				
				new NavigationTestBotParameters(
						// test movement between 
						"CTF-Grendelkeep.JumpSpot45", "CTF-Grendelkeep.LiftExit31", 
						//number of repetitions 
						1, 
						//both ways
						false));
	}

	@Test
	public void test284_long_jump_1_time_low_to_high() {
		startTest(
		// use NavigationTestBot for the test
				NavigationTestBot.class,
				// timeout:
				5,
				
				new NavigationTestBotParameters(
						// test movement between 
						"CTF-Grendelkeep.LiftExit31", "CTF-Grendelkeep.JumpSpot45", 
						//number of repetitions 
						1,
						//both ways
						false));
	}

	/*
	 * TODO: Test fails
	 */
	@Test
	public void test284_long_jump_20_time_high_to_low() {

		startTest(
		// use NavigationTestBot for the test
				NavigationTestBot.class,
				// timeout: 25 minutes
				25,
				new NavigationTestBotParameters("CTF-Grendelkeep.JumpSpot45", "CTF-Grendelkeep.LiftExit31", 20, false));
	}

	/*
	 * TODO: Test fails
	 */
	@Test
	public void test284_long_jump_20_time_low_to_high() {

		startTest(
		// use NavigationTestBot for the test
				NavigationTestBot.class,
				// timeout: 
				25,
				new NavigationTestBotParameters("CTF-Grendelkeep.LiftExit31","CTF-Grendelkeep.JumpSpot45", 20, false));
	}

}