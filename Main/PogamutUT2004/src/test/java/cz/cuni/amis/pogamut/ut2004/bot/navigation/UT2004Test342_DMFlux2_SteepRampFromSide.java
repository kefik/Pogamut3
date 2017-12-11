package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

public class UT2004Test342_DMFlux2_SteepRampFromSide extends UT2004BotTest {

    @Override
    protected String getMapName() {
        return "DM-Flux2";
    }
    
    @Override
    protected String getGameType() {
        return "BotDeathMatch";
    }
    
    @Test
    public void test342_sideramp_1_time() {
        startTest(
            // use NavigationTestBot for the test
            NavigationTestBot.class,
            // timeout: 10 minutes
            10,
            // test movement between        start                       end                            number of repetitions      both ways
            new NavigationTestBotParameters("DM-Flux2.PathNode12",      "DM-Flux2.InventorySpot57",    1,                         true)
        );
    }
    
    @Test
    public void test342_sideramp_20_time() {
        startTest(
            // use NavigationTestBot for the test
            NavigationTestBot.class,
            // timeout: 10 minutes
            10,
            // test movement between        start                       end                            number of repetitions      both ways
            new NavigationTestBotParameters("DM-Flux2.PathNode12",      "DM-Flux2.InventorySpot57",    20,                        true)
        );
    }
}
