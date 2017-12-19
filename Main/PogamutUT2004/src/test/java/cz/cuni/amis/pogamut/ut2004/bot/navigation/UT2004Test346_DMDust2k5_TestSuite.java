package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

public class UT2004Test346_DMDust2k5_TestSuite extends UT2004BotTest {

    @Override
    protected String getMapName() {
        return "DM-Dust2k5";
    }
    
    @Override
    protected String getGameType() {
        return "BotDeathMatch";
    }
    
    // Go for the minigun
    @Test
    public void test1() {
        startTest(
            // use NavigationTestBot for the test
            NavigationTestBot.class,
            10,
            new NavigationTestBotParameters(
                    "DM-Dust2k5.InventorySpot117",
                    "DM-Dust2k5.InventorySpot110",
                    10,
                    true)
        );
    }
}
