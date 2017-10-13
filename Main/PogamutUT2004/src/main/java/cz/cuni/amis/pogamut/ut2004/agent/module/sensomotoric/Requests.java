package cz.cuni.amis.pogamut.ut2004.agent.module.sensomotoric;

import java.util.logging.Logger;

import cz.cuni.amis.pogamut.base.agent.module.SensomotoricModule;
import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEventListener;
import cz.cuni.amis.pogamut.ut2004.bot.IUT2004BotController;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Ping;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Pong;
import cz.cuni.amis.utils.flag.Flag;

/**
 * Memory module specialized on requests to the map.
 * <p><p>
 * It is designed to be initialized inside {@link IUT2004BotController#initializeController(UT2004Bot)} method call
 * and may be used since {@link IUT2004BotController#botInitialized(cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo, cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ConfigChange, cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InitedMessage)}
 * is called.
 *
 * @author Juraj 'Loque' Simlovic
 */
public class Requests extends SensomotoricModule<UT2004Bot>
{
	/**
	 * Sends PING message to the server, use {@link Requests#getLastPong()} to see the results.
	 * @return flag that contains {@link System#currentTimeMillis()} readings of the last {@link Pong} message received.
	 */
	public Flag<Long> ping() {
		act.act(new Ping());
		return lastPong;
	}

	/**
	 * Returns flag that holds the value of 'last pong receive time'.
	 * @returnflag that contains {@link System#currentTimeMillis()} readings of the last {@link Pong} message received.
	 */
	public Flag<Long> getLastPong() {
		return lastPong;
	}

	
	/*========================================================================*/

	/**
	 * Pong listener.
	 */
	private class PongListener implements IWorldEventListener<Pong>
	{
		@Override
		public void notify(Pong event)
		{
			lastPong.setFlag(System.currentTimeMillis());
		}

		/**
		 * Constructor. Registers itself on the given WorldView object.
		 * @param worldView WorldView object to listent to.
		 */
		public PongListener(IWorldView worldView)
		{
			worldView.addEventListener(Pong.class, this);
		}
	}

	/** Pong listener */
	PongListener pongListener;
	
	Flag<Long> lastPong = new Flag<Long>((long)-1);

	/*========================================================================*/

	
	/**
	 * Constructor. Setups the memory module based on given WorldView.
	 * @param bot worldView WorldView object to read the info from.
	 */
	public Requests(UT2004Bot bot)
	{
		this(bot, null);
	}
	
	/**
	 * Constructor. Setups the memory module based on given WorldView.
	 * @param bot worldView WorldView object to read the info from.
	 * @param log Logger to be used for logging runtime/debug info. If <i>null</i>, module creates its own logger.
	 */
	public Requests(UT2004Bot bot, Logger log)
	{
		super(bot, log);

		// create listeners
		pongListener = new PongListener(worldView);
		
		cleanUp();
	}

	@Override
	protected void cleanUp() {
		lastPong.setFlag((long)-1);
	}
    
}
