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
    
    @Test
    public void minigunTest() {
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
    
    @Test
    public void flakCannonTest() {
        startTest(
            // use NavigationTestBot for the test
            NavigationTestBot.class,
            10,
            new NavigationTestBotParameters(
                    "DM-Dust2k5.PathNode84",
                    "DM-Dust2k5.InventorySpot161",
                    10,
                    true)
        );
    }
    
    @Test
    public void armorUndergroundTest1() {
        startTest(
            // use NavigationTestBot for the test
            NavigationTestBot.class,
            10,
            new NavigationTestBotParameters(
                    "DM-Dust2k5.PathNode54",
                    "DM-Dust2k5.InventorySpot109",
                    10,
                    true)
        );
    }
    
    @Test
    public void armorUndergroundTest2() {
        startTest(
            // use NavigationTestBot for the test
            NavigationTestBot.class,
            10,
            new NavigationTestBotParameters(
                    "DM-Dust2k5.PathNode51",
                    "DM-Dust2k5.InventorySpot109",
                    10,
                    true)
        );
    }
    
    @Test
    public void ctLinkGun() {
        startTest(
            // use NavigationTestBot for the test
            NavigationTestBot.class,
            10,
            new NavigationTestBotParameters(
                    "DM-Dust2k5.PathNode97",
                    "DM-Dust2k5.InventorySpot149",
                    10,
                    true)
        );
    }
    
    @Test
    public void tBioRifle() {
        startTest(
            // use NavigationTestBot for the test
            NavigationTestBot.class,
            10,
            new NavigationTestBotParameters(
                    "DM-Dust2k5.PathNode10",
                    "DM-Dust2k5.InventorySpot144",
                    10,
                    true)
        );
    }
    
    @Test
    public void tLightningGun() {
        startTest(
            // use NavigationTestBot for the test
            NavigationTestBot.class,
            10,
            new NavigationTestBotParameters(
                    "DM-Dust2k5.PathNode24",
                    "DM-Dust2k5.InventorySpot145",
                    10,
                    true)
        );
    }
    
    @Test
    public void tShockRifle() {
        startTest(
            // use NavigationTestBot for the test
            NavigationTestBot.class,
            10,
            new NavigationTestBotParameters(
                    "DM-Dust2k5.PathNode21",
                    "DM-Dust2k5.InventorySpot148",
                    10,
                    true)
        );
    }
}
