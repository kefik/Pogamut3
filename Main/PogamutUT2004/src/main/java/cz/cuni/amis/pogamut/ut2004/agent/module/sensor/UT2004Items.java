package cz.cuni.amis.pogamut.ut2004.agent.module.sensor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;

import cz.cuni.amis.pogamut.base.agent.module.SensorModule;
import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.event.WorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensomotoric.Weaponry;
import cz.cuni.amis.pogamut.ut2004.agent.module.utils.TabooSet;
import cz.cuni.amis.pogamut.ut2004.bot.IUT2004BotController;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.UT2004ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.EndMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Item;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ItemPickedUp;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.pogamut.ut2004.communication.translator.shared.events.MapPointListObtained;
import cz.cuni.amis.pogamut.ut2004.utils.UnrealUtils;
import cz.cuni.amis.utils.NullCheck;
import cz.cuni.amis.utils.collections.MyCollections;
import cz.cuni.amis.utils.maps.HashMapMap;


public class UT2004Items extends Items {	

	
	public UT2004Items(UT2004Bot bot, AgentInfo agentInfo, Game game, Weaponry weaponry, Logger log){
		super(bot, agentInfo, game, weaponry, log);
	}
	
	public boolean isPickable(Item item) {
    	// health
    	if ( item.getType() == UT2004ItemType.HEALTH_PACK && agentInfo.isHealthy() ) {
    		return false;
    	}
    	if ( item.getType() == UT2004ItemType.SUPER_HEALTH_PACK && agentInfo.isSuperHealthy() ) {
    		return false;
    	}
    	if ( item.getType() == UT2004ItemType.MINI_HEALTH_PACK && agentInfo.isSuperHealthy() ) {
    		return false;
    	}
    	
    	// shield
    	if ( item.getType() == UT2004ItemType.SHIELD_PACK && agentInfo.hasLowArmor() ) {
    		return false;
    	}
    	if ( item.getType() == UT2004ItemType.SUPER_SHIELD_PACK && agentInfo.hasHighArmor() ) {
    		return false;
    	}
    	
    	// weapons
    	if ( item.getType().getCategory() == ItemType.Category.WEAPON ) {
    		if ( game.getGameInfo().isWeaponStay() ) {
    			return !weaponry.hasWeapon(item.getType());
    		} else {
    			return weaponry.getPrimaryWeaponAmmo( item.getType() ) < weaponry.getWeaponDescriptor( item.getType() ).getPriMaxAmount()
    				   ||
    				   weaponry.getSecondaryWeaponAmmo( item.getType() ) < weaponry.getWeaponDescriptor( item.getType() ).getSecMaxAmount();
    		}
    	}
    	
    	// ammo
    	if ( item.getType().getCategory() == ItemType.Category.AMMO && weaponry.getAmmo(item.getType()) >= weaponry.getMaxAmmo(item.getType()) ) {
    		return false;
    	}
    	
    	// adrenaline
    	if ( item.getType() == UT2004ItemType.ADRENALINE_PACK && agentInfo.isAdrenalineFull() ) {
    		return false;
    	}
    	
    	// ultra damage
    	if ( item.getType() == UT2004ItemType.U_DAMAGE_PACK && agentInfo.hasUDamage() ) {
    		return false;
    	}

    	return true;
	}

	public double getItemRespawnTime(ItemType itemType) {
		if (itemType == UT2004ItemType.U_DAMAGE_PACK) {
			return 3 * 27.5;
		} else
		if (itemType == UT2004ItemType.SUPER_SHIELD_PACK || itemType == UT2004ItemType.SUPER_HEALTH_PACK) {
			return 2 * 27.5;
		} else {
			return 27.5;
		}
	}
}