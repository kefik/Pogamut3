package cz.cuni.amis.pogamut.unreal.communication.messages.gbinfomessages;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.base3d.worldview.object.Rotation;
import cz.cuni.amis.pogamut.base3d.worldview.object.Velocity;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;

/**
 *
 * @author ik
 */
public interface IPlayer {

    public UnrealId getId();

    public String getName();

    public String getAction();

    /**

    If the player is in the field of view of the bot.

     */
    public boolean isVisible();

    /**

    Which direction the player is facing in absolute terms.

     */
    public Rotation getRotation();

    /**

    An absolute location of the player within the map.

     */
    public Location getLocation();

    /**

    Absolute velocity of the player as a vector of movement per one
    game second.

     */
    public Velocity getVelocity();

    /**

    What team the player is on. 255 is no team. 0-3 are red,
    blue, green, gold in that order.

     */
    public int getTeam();

    /**

    Class of the weapon the player is holding. Weapon strings to
    look for include: "AssaultRifle", "ShieldGun", "FlakCannon",
    "BioRifle", "ShockRifle", "LinkGun", "SniperRifle",
    "RocketLauncher", "Minigun", "LightingGun", "Translocator".
    TODO: Look if this is all.

     */
    public String getWeapon();

    /**

    0 means is not firing, 1 - firing in primary mode, 2 -
    firing in secondary mode (alt firing).

     */
    public int getFiring();

    public long getSimTime();

    public String toHtmlString();
}
