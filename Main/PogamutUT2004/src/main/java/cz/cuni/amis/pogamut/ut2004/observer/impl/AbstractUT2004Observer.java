package cz.cuni.amis.pogamut.ut2004.observer.impl;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import com.google.inject.Inject;

import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.agent.impl.AbstractGhostAgent;
import cz.cuni.amis.pogamut.base.agent.state.impl.AgentStateStarting;
import cz.cuni.amis.pogamut.base.communication.command.IAct;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.react.ObjectEventReactOnce;
import cz.cuni.amis.pogamut.base.component.bus.IComponentBus;
import cz.cuni.amis.pogamut.base.component.bus.event.BusAwareCountDownLatch;
import cz.cuni.amis.pogamut.base.component.exception.ComponentCantStartException;
import cz.cuni.amis.pogamut.base.utils.guice.AgentScoped;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.pogamut.base3d.worldview.IVisionWorldView;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.PasswordReply;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Ready;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Password;
import cz.cuni.amis.pogamut.ut2004.communication.translator.shared.events.ReadyCommandRequest;
import cz.cuni.amis.pogamut.ut2004.observer.IUT2004Observer;
import cz.cuni.amis.utils.exception.PogamutInterruptedException;

/**
 * Abstract class - ancestor of all UT2004 observer controls.
 * <p>
 * <p>
 * It counts with GameBots2004 protocol therefore taking care of:
 * <ol>
 * <li>ReadyCommandRequest - sending automatically ready(), override
 * readyCommandRequested() if you're not comfortable with this</li>
 * <li>Password - when password is requested it calls method
 * createPasswordReply()</li>
 * </ol>
 * <p>
 * <p>
 * You may use setPassword() method to specify the password before starting the
 * agent.
 * 
 * @author Jimmy
 */
@AgentScoped
public abstract class AbstractUT2004Observer<WORLD_VIEW extends IVisionWorldView, ACT extends IAct> extends AbstractGhostAgent<WORLD_VIEW, ACT> implements IUT2004Observer {
	
	/**
	 * If specified - used for the construction of the PasswordReply in
	 * createPasswordReply() method.
	 */
	private String desiredPassword = null;
	
	private ObjectEventReactOnce<GameInfo, IWorldObjectEvent<GameInfo>> latchRaiseReact;
	
	private BusAwareCountDownLatch latch;
	
	@Inject
	public AbstractUT2004Observer(IAgentId agentId, IComponentBus bus, IAgentLogger agentLogger, WORLD_VIEW worldView, ACT act) {
		super(agentId, bus, agentLogger, worldView, act);

		getWorldView().addEventListener(ReadyCommandRequest.class, readyCommandRequestListener);
		getWorldView().addEventListener(Password.class, passwordRequestedListener);
		
		latch = new BusAwareCountDownLatch(1, bus, getWorldView());
		
		latchRaiseReact = new ObjectEventReactOnce<GameInfo, IWorldObjectEvent<GameInfo>>(
				GameInfo.class, getWorldView()
		) {
			@Override
			protected void react(IWorldObjectEvent<GameInfo> event) {
				latch.countDown();
			}
		};

		
	}

	/**
	 * Specify the password that should be used if required by the world.
	 * 
	 * @param password
	 */
	public void setPassword(String password) {
		this.desiredPassword = password;
	}

	// --------------
	// -=-=-=-=-=-=-=
	// READY LISTENER
	// -=-=-=-=-=-=-=
	// --------------
	/**
	 * This method is called whenever HelloBot message is parsed - the
	 * GameBots2004 is awaiting the bot to reply with Ready command to begin the
	 * handshake.
	 */
	protected void readyCommandRequested() {
		getAct().act(new Ready());
	}

	/**
	 * Listener that is hooked to WorldView awaiting event ReadyCommandRequest
	 * calling setupWorldViewListeners() and then readyCommandRequested() method
	 * upon receiving the event.
	 */
	private IWorldEventListener<ReadyCommandRequest> readyCommandRequestListener = new IWorldEventListener<ReadyCommandRequest>() {

		@Override
		public void notify(ReadyCommandRequest event) {
			setState(new AgentStateStarting("GameBots2004 greeted us, sending READY."));
			readyCommandRequested();
			setState(new AgentStateStarting("READY sent."));
		}
	};
	// -----------------
	// -=-=-=-=-=-=-=-=-
	// PASSWORD LISTENER
	// -=-=-=-=-=-=-=-=-
	// -----------------
	/**
	 * Instance of the password reply command that was sent upon receivieng
	 * request for the password (the world is locked).
	 * <p>
	 * <p>
	 * If null the password was not required by the time the bot connected to
	 * the world.
	 */
	private PasswordReply passwordReply = null;

	/**
	 * Instance of the password reply command that was sent upon receivieng
	 * request for the password (the world is locked).
	 * <p>
	 * <p>
	 * If null the password was not required by the time the bot connected to
	 * the world.
	 * 
	 * @return
	 */
	public PasswordReply getPasswordReply() {
		return passwordReply;
	}

	/**
	 * This method is called whenever the Password event is caught telling us
	 * the world is locked and is requiring a password.
	 * <p>
	 * <p>
	 * May return null - in that case an empty password is sent to the server
	 * (which will probably result in closing the connection and termination of
	 * the agent).
	 * <p>
	 * <p>
	 * This message is then saved to private field passwordReply and is
	 * accessible via getPasswordReply() method if required to be probed during
	 * the bot's runtime.
	 * <p>
	 * <p>
	 * Note that if setPassword() method is called before this one it will use
	 * provided password via that method.
	 */
	protected PasswordReply createPasswordReply() {
		return desiredPassword != null ? new PasswordReply(desiredPassword)
				: null;
	}

	/**
	 * Listener that is hooked to WorldView awaiting event InitCommandRequest
	 * calling initCommandRequested() method upon receiving the event.
	 */
	private IWorldEventListener<Password> passwordRequestedListener = new IWorldEventListener<Password>() {

		@Override
		public void notify(Password event) {
			setState(new AgentStateStarting("Password requested by the world."));
			passwordReply = createPasswordReply();
			if (passwordReply == null) {
				passwordReply = new PasswordReply("");
			}
			if (log.isLoggable(Level.INFO)) log.info("Password required for the world, replying with '"
					+ passwordReply.getPassword() + "'.");
			getAct().act(passwordReply);
		}
	};

	@Override
	public WORLD_VIEW getWorldView() {
		return super.getWorldView();
	}
	
	/////
	//
	// LIFECYCLE METHODS (starting/stopping observer)
	//
	/////
	
	/**
	 * Called during stop/kill/reset events.
	 */
	protected void reset() {
		latch.countDown();
		latch = new BusAwareCountDownLatch(1, getEventBus(), getWorldView());
		latchRaiseReact.enable();
	}
	
	@Override
	protected void startAgent() {
		super.startAgent();
		if (!latch.await(60000, TimeUnit.MILLISECONDS)) {
			throw new ComponentCantStartException("GameInfo message was not received in 60 secs.", this);
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			throw new PogamutInterruptedException(e, this);
		}
	}
	
	@Override
	protected void startPausedAgent() {
		super.startPausedAgent();
		if (!latch.await(60000, TimeUnit.MILLISECONDS)) {
			throw new ComponentCantStartException("GameInfo message was not received in 60 secs.", this);
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			throw new PogamutInterruptedException(e, this);
		}
	}
	
	@Override
	protected void resetAgent() {
		super.resetAgent();
		reset();
	}
	
	@Override
	protected void stopAgent() {
		super.stopAgent();
		reset();
	}
	
	@Override
	protected void killAgent() {
		super.killAgent();
		reset();
	}
}
