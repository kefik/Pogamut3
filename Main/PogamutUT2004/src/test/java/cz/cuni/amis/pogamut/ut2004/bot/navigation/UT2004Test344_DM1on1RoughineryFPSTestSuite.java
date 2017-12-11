package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import org.junit.Test;

public class UT2004Test344_DM1on1RoughineryFPSTestSuite extends UT2004BotTest {

    @Override
    protected String getMapName() {
        return "DM-1on1-Roughinery-FPS";
    }
    
    @Override
    protected String getGameType() {
        return "BotDeathMatch";
    }
    
    @Test
    public void t2() {
        startTest(
            NavigationTestBot.class,
            10,
            new NavigationTestBotParameters("DM-1on1-Roughinery-FPS.InventorySpot7", "DM-1on1-Roughinery-FPS.PathNode13", 20, false)
        );
    }
    
    @Test
    public void t3() {
        startTest(
            NavigationTestBot.class,
            10,
            new NavigationTestBotParameters("DM-1on1-Roughinery-FPS.InventorySpot41", "DM-1on1-Roughinery-FPS.InventorySpot37", 20, false)
        );
    }
    
    @Test
    public void t5() {
        startTest(
            NavigationTestBot.class,
            10,
            new NavigationTestBotParameters("DM-1on1-Roughinery-FPS.InventorySpot6", "DM-1on1-Roughinery-FPS.InventorySpot2", 20, false)
        );
    }
    
    @Test
    public void t7() {
        startTest(
            NavigationTestBot.class,
            10,
            new NavigationTestBotParameters("DM-1on1-Roughinery-FPS.InventorySpot17", "DM-1on1-Roughinery-FPS.InventorySpot23", 20, false)
        );
    }
    
    @Test
    public void t9() {
        startTest(
            NavigationTestBot.class,
            10,
            new NavigationTestBotParameters("DM-1on1-Roughinery-FPS.InventorySpot19", "DM-1on1-Roughinery-FPS.InventorySpot12", 20, false)
        );
    }
    
    @Test
    public void t10() {
        startTest(
            NavigationTestBot.class,
            10,
            new NavigationTestBotParameters("DM-1on1-Roughinery-FPS.InventorySpot11", "DM-1on1-Roughinery-FPS.InventorySpot12", 20, false)
        );
    }
    
    @Test
    public void t11() {
        startTest(
            NavigationTestBot.class,
            10,
            new NavigationTestBotParameters("DM-1on1-Roughinery-FPS.InventorySpot7", "DM-1on1-Roughinery-FPS.InventorySpot28", 20, false)
        );
    }
}
