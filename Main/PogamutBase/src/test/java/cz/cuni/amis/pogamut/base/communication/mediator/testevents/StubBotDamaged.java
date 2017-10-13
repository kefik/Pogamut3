package cz.cuni.amis.pogamut.base.communication.mediator.testevents;

import cz.cuni.amis.pogamut.base.communication.messages.InfoMessage;
import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldChangeEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEvent;

/**
 * Asynchronous message. This bot has been damaged.
 * 
 * Corresponding GameBots message is DAM.
 */

public class StubBotDamaged extends InfoMessage

implements IWorldEvent, IWorldChangeEvent

{

	/**
	 * Creates new instance of command BotDamaged.
	 * 
	 * Asynchronous message. This bot has been damaged. Corresponding GameBots
	 * message for this command is .
	 * 
	 * @param Damage
	 *            Amount of damage taken.
	 * @param DamageType
	 *            A string describing what kind of damage.
	 * @param WeaponName
	 *            Name of the weapon that caused this damage.
	 * @param Flaming
	 *            If this damage is causing our bot to burn. TODO
	 * @param CausedByWorld
	 *            If this damage was caused by world - falling into lava, or
	 *            falling down.
	 * @param DirectDamage
	 *            If the damage is direct. TODO
	 * @param BulletHit
	 *            If this damage was caused by bullet.
	 * @param VehicleHit
	 *            If this damage was caused by vehicle running over us.
	 * @param Instigator
	 *            Id of the player who is damaging the bot, filled only if
	 *            instigator is in the field of view of the bot.
	 */
	public StubBotDamaged(int Damage, String DamageType, String WeaponName,
			boolean Flaming, boolean CausedByWorld, boolean DirectDamage,
			boolean BulletHit, boolean VehicleHit) {

		this.Damage = Damage;

		this.DamageType = DamageType;

		this.WeaponName = WeaponName;

		this.Flaming = Flaming;

		this.CausedByWorld = CausedByWorld;

		this.DirectDamage = DirectDamage;

		this.BulletHit = BulletHit;

		this.VehicleHit = VehicleHit;

	}

	/** Example how the message looks like - used during parser tests. */
	public static final String PROTOTYPE = "DAM {Damage 0} {DamageType text} {WeaponName text} {Flaming False} {CausedByWorld False} {DirectDamage False} {BulletHit False} {VehicleHit False} {Instigator unreal_id}";

	// ///// Properties BEGIN

	/**
	 * Amount of damage taken.
	 */
	protected int Damage = 0;

	/**
	 * Amount of damage taken.
	 */
	public int getDamage() {
		return Damage;
	}

	/**
	 * A string describing what kind of damage.
	 */
	protected String DamageType = null;

	/**
	 * A string describing what kind of damage.
	 */
	public String getDamageType() {
		return DamageType;
	}

	/**
	 * Name of the weapon that caused this damage.
	 */
	protected String WeaponName = null;

	/**
	 * Name of the weapon that caused this damage.
	 */
	public String getWeaponName() {
		return WeaponName;
	}

	/**
	 * If this damage is causing our bot to burn. TODO
	 */
	protected boolean Flaming = false;

	/**
	 * If this damage is causing our bot to burn. TODO
	 */
	public boolean isFlaming() {
		return Flaming;
	}

	/**
	 * If this damage was caused by world - falling into lava, or falling down.
	 */
	protected boolean CausedByWorld = false;

	/**
	 * If this damage was caused by world - falling into lava, or falling down.
	 */
	public boolean isCausedByWorld() {
		return CausedByWorld;
	}

	/**
	 * If the damage is direct. TODO
	 */
	protected boolean DirectDamage = false;

	/**
	 * If the damage is direct. TODO
	 */
	public boolean isDirectDamage() {
		return DirectDamage;
	}

	/**
	 * If this damage was caused by bullet.
	 */
	protected boolean BulletHit = false;

	/**
	 * If this damage was caused by bullet.
	 */
	public boolean isBulletHit() {
		return BulletHit;
	}

	/**
	 * If this damage was caused by vehicle running over us.
	 */
	protected boolean VehicleHit = false;

	/**
	 * If this damage was caused by vehicle running over us.
	 */
	public boolean isVehicleHit() {
		return VehicleHit;
	}

	// ///// Properties END

	// ///// Extra Java code BEGIN

	// ///// Additional code from xslt BEGIN

	// ///// Additional code from xslt END

	// ///// Extra Java from XML BEGIN

	// ///// Extra Java from XML END

	// ///// Extra Java code END

	/**
	 * Cloning constructor.
	 */
	public StubBotDamaged(StubBotDamaged original) {

		this.Damage = original.Damage;

		this.DamageType = original.DamageType;

		this.WeaponName = original.WeaponName;

		this.Flaming = original.Flaming;

		this.CausedByWorld = original.CausedByWorld;

		this.DirectDamage = original.DirectDamage;

		this.BulletHit = original.BulletHit;

		this.VehicleHit = original.VehicleHit;

	}

	/**
	 * Used by Yylex to create empty message then to fill it's protected fields
	 * (Yylex is in the same package).
	 */
	public StubBotDamaged() {
	}

	public String toString() {
		return

		super.toString() + " | " +

		"Damage = " + String.valueOf(Damage) + " | " +

		"DamageType = " + String.valueOf(DamageType) + " | " +

		"WeaponName = " + String.valueOf(WeaponName) + " | " +

		"Flaming = " + String.valueOf(Flaming) + " | " +

		"CausedByWorld = " + String.valueOf(CausedByWorld) + " | " +

		"DirectDamage = " + String.valueOf(DirectDamage) + " | " +

		"BulletHit = " + String.valueOf(BulletHit) + " | " +

		"VehicleHit = " + String.valueOf(VehicleHit) + " | ";

	}

	public String toHtmlString() {
		return super.toString() +

		"<b>Damage</b> : " + String.valueOf(Damage) + " <br/> " +

		"<b>DamageType</b> : " + String.valueOf(DamageType) + " <br/> " +

		"<b>WeaponName</b> : " + String.valueOf(WeaponName) + " <br/> " +

		"<b>Flaming</b> : " + String.valueOf(Flaming) + " <br/> " +

		"<b>CausedByWorld</b> : " + String.valueOf(CausedByWorld) + " <br/> " +

		"<b>DirectDamage</b> : " + String.valueOf(DirectDamage) + " <br/> " +

		"<b>BulletHit</b> : " + String.valueOf(BulletHit) + " <br/> " +

		"<b>VehicleHit</b> : " + String.valueOf(VehicleHit) + " <br/> ";

	}

	@Override
	public long getSimTime() {
		return 0;
	}

}
