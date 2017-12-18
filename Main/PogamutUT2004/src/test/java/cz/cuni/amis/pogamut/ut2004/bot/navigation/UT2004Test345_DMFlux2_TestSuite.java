package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

public class UT2004Test345_DMFlux2_TestSuite extends UT2004BotTest {

    @Override
    protected String getMapName() {
        return "DM-Flux2";
    }
    
    @Override
    protected String getGameType() {
        return "BotDeathMatch";
    }
    
    @Test
    public void test2() {
        startTest(
            // use NavigationTestBot for the test
            NavigationTestBot.class,
            10,
            new NavigationTestBotParameters(
                    "DM-Flux2.InventorySpot85", 
                    "DM-Flux2.InventorySpot91", 
                    10, 
                    true)
        );
    }
    
    @Test
    public void test3() {
        startTest(
            // use NavigationTestBot for the test
            NavigationTestBot.class,
            10,
            new NavigationTestBotParameters(
                    "DM-Flux2.PathNode54",
                    "DM-Flux2.InventorySpot95", 
                    10, 
                    true)
        );
    }
    
    @Test
    public void test4() {
        startTest(
            // use NavigationTestBot for the test
            NavigationTestBot.class,
            10,
            new NavigationTestBotParameters(
                    "DM-Flux2.PathNode54", 
                    "DM-Flux2.InventorySpot91",
                    10, 
                    true)
        );
    }
    
    @Test
    public void test5() {
        startTest(
            // use NavigationTestBot for the test
            NavigationTestBot.class,
            10,
            new NavigationTestBotParameters(
                    "DM-Flux2.PlayerStart20", 
                    "DM-Flux2.InventorySpot71", 
                    10, 
                    true)
        );
    }
    
    @Test
    public void test6() {
        startTest(
            // use NavigationTestBot for the test
            NavigationTestBot.class,
            10,
            new NavigationTestBotParameters(
                    "DM-Flux2.InventorySpot91", 
                    "DM-Flux2.PathNode92",
                    10, 
                    true)
        );
    }
}
