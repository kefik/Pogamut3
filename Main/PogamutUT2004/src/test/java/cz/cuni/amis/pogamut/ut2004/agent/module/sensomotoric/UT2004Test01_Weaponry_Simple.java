package cz.cuni.amis.pogamut.ut2004.agent.module.sensomotoric;

import org.junit.Test;

import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTestController;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Initialize;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BotKilled;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ConfigChange;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InitedMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Self;
import cz.cuni.amis.utils.exception.PogamutException;

public class UT2004Test01_Weaponry_Simple extends UT2004BotTest {

	public static class AmmoBot extends UT2004BotTestController<UT2004Bot> {


	    @Override
	    public void prepareBot(UT2004Bot bot) {
	    }

	    @Override
	    public Initialize getInitializeCommand() {
	        return new Initialize().setName("AmmoTest");
	    }
	    

	    @Override
	    public void botInitialized(GameInfo gameInfo, ConfigChange currentConfig, InitedMessage init) {
	    }


	    @Override
	    public void botFirstSpawn(GameInfo gameInfo, ConfigChange config, InitedMessage init, Self self) {
	       
	    }

	    int initialAmmo = -1;
	    
	    int shootingFor = 0;

	    @Override
	    public void logic() throws PogamutException {
	       
	    	if (isFailure() || isSuccess()) return;
	    	
	    	if (initialAmmo == -1) {
	    		initialAmmo = weaponry.getCurrentPrimaryAmmo();
	    	}
	    	
	    	shoot.shootPrimary(new ILocated() {
				@Override
				public Location getLocation() {
					return new Location(0, 0, Double.MAX_VALUE);
				}
			});
	    	System.out.println(weaponry.getWeapons());
	    		    	
	    	++shootingFor;
	    	
	    	if (shootingFor == 5) {
	    		int currentAmmo = weaponry.getCurrentPrimaryAmmo();
	    		int currentWeaponAmmo = weaponry.getCurrentWeapon().getPrimaryAmmo();
	    		int selfPrimaryAmmo = info.getCurrentAmmo();
	    		
	    		if (initialAmmo <= currentAmmo) {
	    			setFailure("initialAmmo = " + initialAmmo + " <= " + currentAmmo + " = weaponry.getCurrentAmmo()");
	    			return;
	    		}
	    		if (initialAmmo <= currentWeaponAmmo) {
	    			setFailure("initialAmmo = " + initialAmmo + " <= " + currentAmmo + " = weaponry.getCurrentWeapon().getPrimaryAmmo()");
	    			return;
	    		}
	    		if (initialAmmo <= selfPrimaryAmmo) {
	    			setFailure("initialAmmo = " + initialAmmo + " <= " + currentAmmo + " = info.getCurrentAmmo()");
	    			return;
	    		}
	    		if (currentAmmo != currentWeaponAmmo) {
	    			setFailure("weaponry.getCurrentAmmo() = " + currentAmmo + " != " + currentWeaponAmmo + " = weaponry.getCurrentWeapon().getPrimaryAmmo()");
	    			return;
	    		}
	    		if (currentAmmo != selfPrimaryAmmo) {
	    			setFailure("weaponry.getCurrentAmmo() = " + currentAmmo + " != " + selfPrimaryAmmo + " = info.getCurrentAmmo()");
	    			return;
	    		}
	    		if (currentWeaponAmmo != selfPrimaryAmmo) {
	    			setFailure("weaponry.getCurrentWeapon().getPrimaryAmmo() = " + currentWeaponAmmo + " != " + selfPrimaryAmmo + " = info.getCurrentAmmo()");
	    			return;
	    		}

	    		setSuccess("All watched ammo counts matches.");
	    		return;
	    	}
	    	
	    }

	    /**
	     * Called each time the bot dies. Good for reseting all bot's state dependent variables.
	     *
	     * @param event
	     */
	    @Override
	    public void botKilled(BotKilled event) {
	    }

	}
	
	@Test
	public void test2() {
		startTest(AmmoBot.class, 2, 5);
	}

	
}
