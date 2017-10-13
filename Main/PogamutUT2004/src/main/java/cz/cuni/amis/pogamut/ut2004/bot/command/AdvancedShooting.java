package cz.cuni.amis.pogamut.ut2004.bot.command;

import java.util.logging.Logger;

import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEventListener;
import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Shoot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.StopShooting;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BeginMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;

/**
 * Class providing Pogamut2 UT2004 advanced shooting commands for the bot -
 * shooting in secondary mode, grenade launcher shooting, etc.
 * 
 * @author Michal 'Knight' Bida
 */
public class AdvancedShooting extends SimpleShooting {

	/** Here we store current UT time used to issue charge shoot commands */
	private double currentTime = 0;
	/** Time last charge shoot command was issued, -1 for command not issued */
	private double lastChargeShootCommandTime = -1;
	/** Delay of last charge shoot command */
	private double lastChargeShootCommandDelay = 0;
	/**
	 * Listener to begin message - used to get UT04 current time.
	 */
	private IWorldEventListener<BeginMessage> myBegListener = new IWorldEventListener<BeginMessage>() {

		@Override
		public void notify(BeginMessage bm) {
			currentTime = bm.getTime();

			// Here we will stop shooting if the charge dealy is exceeded
			if ((lastChargeShootCommandTime >= 0)
					&& ((currentTime - lastChargeShootCommandTime) > lastChargeShootCommandDelay)) {
				agent.getAct().act(new StopShooting());
				lastChargeShootCommandTime = -1;
				lastChargeShootCommandDelay = 0;
			}
		}
	};

	/**
	 * The bot will stop shooting completely (regardless on the mode of shooting).
	 * <p><p>
	 * (issues GB STOPSHOOT command)
	 */
	@Override
	public void stopShooting() {
		super.stopShooting();
	}
	
	/**
	 * Bot will start shooting his current weapon with selected mode.
	 * <p><p>
	 * (issues GB SHOOT command)
	 * 
	 * @param secondaryMode
	 *            If true secondary firing mode will be issued.
	 * 
	 */
	public void shootWithMode(boolean secondaryMode) {
		Shoot shoot = new Shoot();
		shoot.setAlt(secondaryMode);
		agent.getAct().act(shoot);
	}
	
	/**
	 * Bot will start shooting his current weapon with selected mode.
	 * <p><p>
	 * (issues GB SHOOT command)
	 * 
	 * @param secondaryMode
	 *            If true secondary firing mode will be issued.
	 * 
	 */
	public void shootSecondary() {
		Shoot shoot = new Shoot();
		shoot.setAlt(true);
		agent.getAct().act(shoot);
	}

	/**
	 * Bot will start shooting his current weapon with primary firing mode at
	 * the location specified. The bot will shoot on this location even when he
	 * will turn a little bit from the direction to the location. If he turn out
	 * more then approx 15 - 30 degrees he won't be able to hit the location
	 * anymore.
	 * <p><p>
	 * (issues GB SHOOT command)
	 * 
	 * @param location
	 *            Location we will be shooting at.
	 * 
	 * @see shootPrimary(UnrealId)
	 */
	public void shootPrimary(ILocated location) {
		if (location instanceof Player) {
			shootPrimary((Player)location);
			return;
		}
		Shoot shoot = new Shoot();
		shoot.setLocation(location.getLocation());
		agent.getAct().act(shoot);
	}

	/**
	 * Bot will start shooting his current weapon with primary firing mode at
	 * the target specified. The target should exist in the environment. The bot
	 * will track the target in the environment as long as other commands won't
	 * change his focus (strafe(), turnTo()..). If they will the bot will still
	 * shoot on target location until he will turn from target more then approx
	 * 15 - 30 degrees. Then he won't be able to hit the target location
	 * anymore.
	 * <p><p>
	 * (issues GB SHOOT command)
	 * 
	 * @param target
	 *            Object in the environment we will shoot at.
	 * 
	 * @see shootPrimary(ILocated)
	 */
	public void shootPrimary(UnrealId target) {
		Shoot shoot = new Shoot();
		shoot.setTarget(target);
		agent.getAct().act(shoot);
	}
	
	/**
	 * Shortcut for 'shootPrimary(player.getId())', see {@link AdvancedShooting#shootPrimary(UnrealId)}.
	 * <p><p>
	 * (issues GB SHOOT command)
	 * 
	 * @param target
	 * 			Player the bot wants to shoot at.
	 */
	public void shootPrimary(Player target) {
		shootPrimary(target.getId());
	}

	/**
	 * Bot will start shooting his current weapon with secondary firing mode at
	 * the location specified. The bot will shoot on this location even when he
	 * will turn a little bit from the direction to the location. If he turn out
	 * more then approx 15 - 30 degrees he won't be able to hit the location
	 * anymore.
	 * <p><p>
	 * (issues GB SHOOT command)
	 * 
	 * @param location
	 *            Location we will be shooting at.
	 * 
	 * @see shootSecondary(UnrealId)
	 */
	public void shootSecondary(ILocated location) {
		if (location instanceof Player) {
			shootSecondary((Player)location);
			return;
		}
		Shoot shoot = new Shoot();
		shoot.setLocation(location.getLocation());
		shoot.setAlt(true);
		agent.getAct().act(shoot);
	}

	/**
	 * Bot will start shooting his current weapon with secondary firing mode at
	 * the target specified. The target should exist in the environment. The bot
	 * will track the target in the environment as long as other commands won't
	 * change his focus (strafe(), turnTo()..). If they will the bot will still
	 * shoot on target location until he will turn from target more then approx
	 * 15 - 30 degrees. Then he won't be able to hit the target location
	 * anymore.
	 * <p><p>
	 * (issues GB SHOOT command)
	 * 
	 * @param target
	 *            Object in the environment we will shoot at.
	 * 
	 * @see shootSecondary(ILocated)
	 */
	public void shootSecondary(UnrealId target) {
		Shoot shoot = new Shoot();
		shoot.setTarget(target);
		shoot.setAlt(true);
		agent.getAct().act(shoot);
	}
	
	/**
	 * Shortcut for 'shootSecondary(player.getId())', see {@link AdvancedShooting#shootSecondary(UnrealId)}.
	 * <p><p>
	 * (issues GB SHOOT command)
	 * 
	 * @param target
	 * 			Player the bot wants to shoot at.
	 */
	public void shootSecondary(Player target) {
		shootSecondary(target.getId());
	}

	/**
	 * This method can be used for UT2004 charging weapons. Some weapons in
	 * UT2004 feature charging firing modes. These modes works as follows. To
	 * shoot with a charging firing mode the bot has to start shooting first -
	 * this will trigger the weapon to start charging (it won't fire yet). To
	 * fire the weapon the bot needs to send STOPSHOOT command to stop charging
	 * the weapon and to release the projectile. This method does this
	 * automatically for primary firing mode of the weapon. The time of charging
	 * can be specified too (second parameter in seconds).
	 * <p><p>
	 * This method can be also used for non-charing (primary) firing mode of the
	 * weapon - then it will work as burst fire - the bot will continue firing
	 * for the amout of seconds specified.
	 * <p><p>
	 * So if the current weapon primary firing mode is charging, the bot will
	 * release the projectiles once. With normal primary firing mode the bot
	 * will fire a burst.
	 * <p><p>
	 * Note: We will shoot at location specified. The bot will continue to aim
	 * on the location in the environment (for the time of charging or bursting)
	 * as long as other commands won't change his focus (strafe(), turnTo()..).
	 * If they will the bot will still shoot on location until he will turn from
	 * it more then approx 15 - 30 degrees. Then he won't be able to hit the
	 * location anymore.
	 * <p><p>
	 * (issues GB SHOOT command)
	 * 
	 * @param location
	 *            Location we will be shooting at.
	 * @param chargeTime
	 *            In seconds - how long we will charge the weapon (or how long
	 *            will be the burst fire).
	 * 
	 * @see shootPrimaryCharged(UnrealId, double)
	 */
	public void shootPrimaryCharged(ILocated location, double chargeTime) {
		if (location instanceof Player) {
			shootPrimaryCharged((Player)location, chargeTime);
			return;
		}
		Shoot shoot = new Shoot();
		shoot.setLocation(location.getLocation());
		agent.getAct().act(shoot);

		// Stop shoot command will be issued after delay
		lastChargeShootCommandTime = currentTime;
		lastChargeShootCommandDelay = chargeTime;
	}

	/**
	 * This method can be used for UT2004 charging weapons. Some weapons in
	 * UT2004 feature charging firing modes. These modes works as follows. To
	 * shoot with a charging firing mode the bot has to start shooting first -
	 * this will trigger the weapon to start charging (it won't fire yet). To
	 * fire the weapon the bot needs to send STOPSHOOT command to stop charging
	 * the weapon and to release the projectile. This method does this
	 * automatically for primary firing mode of the weapon. The time of charging
	 * can be specified too (second parameter in seconds).
	 * <p><p>
	 * This method can be also used for non-charing (primary) firing mode of the
	 * weapon - then it will work as burst fire - the bot will continue firing
	 * for the amout of seconds specified.
	 * <p><p>
	 * So if the current weapon primary firing mode is charging, the bot will
	 * release the projectiles once. With normal primary firing mode the bot
	 * will fire a burst.
	 * <p><p>
	 * Note: The target for shooting should exist in the environment. The bot
	 * will track the target in the environment (for the time of charging or
	 * bursting) as long as other commands won't change his focus (strafe(),
	 * turnTo()..). If they will the bot will still shoot on target location
	 * until he will turn from target more then approx 15 - 30 degrees. Then he
	 * won't be able to hit the target location anymore.
	 * <p><p>
	 * (issues GB SHOOT command)
	 * 
	 * @param target
	 *            Object in the environment we will shoot at (basic tracking
	 *            provided).
	 * @param chargeTime
	 *            In seconds - how long we will charge the weapon (or how long
	 *            will be the burst fire).
	 * 
	 * @see shootPrimaryCharged(ILocated, double)
	 * 
	 * @todo Implement somehow the charging delay.
	 */
	public void shootPrimaryCharged(UnrealId target, double chargeTime) {
		Shoot shoot = new Shoot();
		shoot.setTarget(target);
		agent.getAct().act(shoot);

		// Stop shoot command will be issued after delay
		lastChargeShootCommandTime = currentTime;
		lastChargeShootCommandDelay = chargeTime;
	}
	
	/**
	 * Shortcut for 'shootPrimaryCharged(player.getId())', see {@link AdvancedShooting#shootPrimaryCharged(UnrealId, double)}.
	 * <p><p>
	 * (issues GB SHOOT command)
	 * 
	 * @param target
	 * 			Player the bot wants to shoot at.
	 */
	public void shootPrimaryCharged(Player target, double chargeTime) {
		shootPrimaryCharged(target.getId(), chargeTime);
	}

	/**
	 * This method can be used for UT2004 charging weapons. Some weapons in
	 * UT2004 feature charging firing modes. These modes works as follows. To
	 * shoot with a charging firing mode the bot has to start shooting first -
	 * this will trigger the weapon to start charging (it won't fire yet). To
	 * fire the weapon the bot needs to send STOPSHOOT command to stop charging
	 * the weapon and to release the projectile. This method does this
	 * automatically for secondary firing mode of the weapon. The time of
	 * charging can be specified too (second parameter in seconds).
	 * <p><p>
	 * This method can be also used for non-charing (secondary) firing mode of
	 * the weapon - then it will work as burst fire - the bot will continue
	 * firing for the amout of seconds specified.
	 * <p><p>
	 * So if the current weapon secondary firing mode is charging, the bot will
	 * release the projectiles once. With normal secondary firing mode the bot
	 * will fire a burst.
	 * <p><p>
	 * Note: We will shoot at location specified. The bot will continue to aim
	 * on the location in the environment (for the time of charging or bursting)
	 * as long as other commands won't change his focus (strafe(), turnTo()..).
	 * If they will the bot will still shoot on location until he will turn from
	 * it more then approx 15 - 30 degrees. Then he won't be able to hit the
	 * location anymore.
	 * <p><p>
	 * (issues GB SHOOT command)
	 * 
	 * @param location
	 *            Location we will be shooting at.
	 * @param chargeTime
	 *            In seconds - how long we will charge the weapon (or how long
	 *            will be the burst fire).
	 * 
	 * @see shootSecondaryCharged(UnrealId, double)
	 * 
	 * @todo Implement somehow the charging delay.
	 */
	public void shootSecondaryCharged(ILocated location, double chargeTime) {
		if (location instanceof Player) {
			shootSecondaryCharged((Player)location, chargeTime);
			return;
		}
		Shoot shoot = new Shoot();
		shoot.setLocation(location.getLocation());
		shoot.setAlt(true);
		agent.getAct().act(shoot);

		// Stop shoot command will be issued after delay
		lastChargeShootCommandTime = currentTime;
		lastChargeShootCommandDelay = chargeTime;
	}

	/**
	 * This method can be used for UT2004 charging weapons. Some weapons in
	 * UT2004 feature charging firing modes. These modes works as follows. To
	 * shoot with a charging firing mode the bot has to start shooting first -
	 * this will trigger the weapon to start charging (it won't fire yet). To
	 * fire the weapon the bot needs to send STOPSHOOT command to stop charging
	 * the weapon and to release the projectile. This method does this
	 * automatically for secondary firing mode of the weapon. The time of
	 * charging can be specified too (second parameter in seconds).
	 * <p><p>
	 * This method can be also used for non-charing (secondary) firing mode of
	 * the weapon - then it will work as burst fire - the bot will continue
	 * firing for the amout of seconds specified.
	 * <p><p>
	 * So if the current weapon secondary firing mode is charging, the bot will
	 * release the projectiles once. With normal secondary firing mode the bot
	 * will fire a burst.
	 * <p><p>
	 * Note: The target for shooting should exist in the environment. The bot
	 * will track the target in the environment (for the time of charging or
	 * bursting) as long as other commands won't change his focus (strafe(),
	 * turnTo()..). If they will the bot will still shoot on target location
	 * until he will turn from target more then approx 15 - 30 degrees. Then he
	 * won't be able to hit the target location anymore.
	 * <p><p>
	 * (issues GB SHOOT command)
	 * 
	 * @param target
	 *            Object in the environment we will shoot at (basic tracking
	 *            provided).
	 * @param chargeTime
	 *            In seconds - how long we will charge the weapon (or how long
	 *            will be the burst fire).
	 * 
	 * @see shootSecondaryCharged(ILocated, double)
	 * 
	 * @todo Implement somehow the charging delay.
	 */
	public void shootSecondaryCharged(UnrealId target, double chargeTime) {
		Shoot shoot = new Shoot();
		shoot.setTarget(target);
		shoot.setAlt(true);
		agent.getAct().act(shoot);

		// Stop shoot command will be issued after delay
		lastChargeShootCommandTime = currentTime;
		lastChargeShootCommandDelay = chargeTime;
	}
	
	/**
	 * Shortcut for 'shootSecondaryCharged(player.getId())', see {@link AdvancedShooting#shootSecondaryCharged(UnrealId, double)}.
	 * <p><p>
	 * (issues GB SHOOT command)
	 * 
	 * @param target
	 * 			Player the bot wants to shoot at.
	 */
	public void shootSecondaryCharged(Player target, double chargeTime) {
		shootSecondaryCharged(target.getId(), chargeTime);
	}

	/**
	 * Resets the agent module so it may be reused.
	 */
	@Override
	protected void reset() {
		lastChargeShootCommandTime = -1;
		lastChargeShootCommandDelay = 0;
	}

	/**
	 * Constructor. Setups the command module based on given agent and logger.
	 * 
	 * @param agent
	 *            AbstractUT2004Bot we will send commands for
	 * @param log
	 *            Logger to be used for logging runtime/debug info.
	 * 
	 * @todo Implement somehow the charging delay.
	 */
	public AdvancedShooting(UT2004Bot agent, Logger log) {
		super(agent, log);
		agent.getWorldView()
				.addEventListener(BeginMessage.class, myBegListener);
	}

    @Override
    public void shoot() {
        super.shoot();
    }

    @Override
    public void shoot(UnrealId target) {
        super.shoot(target);
    }
    
    @Override
    public void shoot(Player target) {
        super.shoot(target);
    }

    @Deprecated
    @Override
    public void stopShoot() {
        super.stopShoot();
    }


}
