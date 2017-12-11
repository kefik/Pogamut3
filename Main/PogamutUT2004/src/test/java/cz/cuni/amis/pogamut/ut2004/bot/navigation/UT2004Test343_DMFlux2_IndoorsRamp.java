package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

public class UT2004Test343_DMFlux2_IndoorsRamp extends UT2004BotTest {

    @Override
    protected String getMapName() {
        return "DM-Flux2";
    }
    
    @Override
    protected String getGameType() {
        return "BotDeathMatch";
    }
    
    @Test
    public void test342_inramp_1_time() {
        startTest(
            // use NavigationTestBot for the test
            NavigationTestBot.class,
            // timeout: 10 minutes
            10,
            // test movement between        start                       end                            number of repetitions      both ways
            new NavigationTestBotParameters("DM-Flux2.PathNode92",      "DM-Flux2.PlayerStart8",    1,                         true)
        );
    }
    
    @Test
    public void test342_inramp_20_time() {
        startTest(
            // use NavigationTestBot for the test
            NavigationTestBot.class,
            // timeout: 10 minutes
            10,
            // test movement between        start                       end                            number of repetitions      both ways
            new NavigationTestBotParameters("DM-Flux2.PathNode92",      "DM-Flux2.PlayerStart8",    20,                        true)
        );
    }
}
