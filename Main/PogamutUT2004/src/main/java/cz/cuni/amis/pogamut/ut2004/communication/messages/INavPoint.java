package cz.cuni.amis.pogamut.ut2004.communication.messages;

import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import java.util.Map;

import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObject;
import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;
import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.base3d.worldview.object.IViewable;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.base3d.worldview.object.Rotation;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPointNeighbourLink;


/**
 * Synchronous message. NavPoint carries information about UT navigation point -
 * location, reachability... Also some item can be respawned at this point. Or
 * some additional information can be stored here (if it is an ambush point, or
 * sniper point..). Corresponding GameBots message is NAV.
 */
public interface INavPoint extends ILocated, IViewable, IWorldObject {

	/**
	 * A unique Id of this navigation point assigned by the game.
	 */
	public UnrealId getId();

	/**
	 * Location of navigation point.
	 */
	public Location getLocation();
		
	/**
	 * If the point is in the field of view of the bot.
	 */
	public boolean isVisible();
		
	/**
	 * Unique Id of the respawned item (the item respawns at this point).
	 */
	public WorldObjectId getItem();

	/**
	 * What type is this NavPoint. The types are: PathNode, PlayerStart,
	 * InventorySpot and AIMarker. If the type is AIMarker, more attributes
	 * appear in NAV message - see below.
	 */
	public String getFlag();

	/**
	 * If the type is AIMarker. The rotation the bot should be facing, when
	 * doing the action specified by AIMarker.
	 */
	public Rotation getRotation();

	/**
	 * Some ambush point, where is good chance to intercept approaching
	 * opponents.
	 */
	public boolean isRoamingSpot();

	/**
	 * Point good for sniping.
	 */
	public boolean isSnipingSpot();

	/**
	 * Class of the weapon that should be prefered when using this point for
	 * AIMarker specified action.
	 */
	public String getPreferedWeapon();

	/**
	 * Retuns map with links to navpoint neighbours. Maps
	 * neighbour-navpoint-UnrealId to neighbour link.
	 */
	public Map<UnrealId, NavPointNeighbourLink> getNeighbourLinks();
	
}
