package cz.cuni.amis.pogamut.ut2004.bot;

import java.util.concurrent.CountDownLatch;

import cz.cuni.amis.pogamut.base.agent.module.LogicModule;
import cz.cuni.amis.pogamut.base.component.bus.event.BusAwareCountDownLatch;
import cz.cuni.amis.pogamut.base.utils.guice.AgentScoped;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.AgentInfo;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.UT2004AgentInfo;
import cz.cuni.amis.pogamut.ut2004.bot.command.AdvancedLocomotion;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.utils.flag.Flag;
import cz.cuni.amis.utils.flag.ImmutableFlag;

@AgentScoped
public class UT2004BotTestController<BOT extends UT2004Bot> extends UT2004BotModuleController<BOT> {

	private CountDownLatch latch;
	private Flag<Boolean> success = new Flag<Boolean>(false);
	private Flag<Boolean> failure = new Flag<Boolean>(false);
	
	/**
	 * Message of failure / success (if provided).
	 */
	private String message;
	
	/**
	 * Cause of failure (if provided).
	 */
	private Throwable cause;

	
    /**
     * Additionally initializes {@link UT2004BotTestController#latch} field with 
     * <code>new BusAwareCountDownLatch(1, bot.getEventBus())</code>
     */
    @Override
    public void initializeController(BOT bot) { 
    	super.initializeController(bot);
		latch = new BusAwareCountDownLatch(1, bot.getEventBus(), bot.getWorldView());
		info = new UT2004AgentInfo(bot);
		move = new AdvancedLocomotion(bot, getLog());
    }   

    /**
     * Message why the bot has succeeded or failed... if provided via {@link UT2004BotTestController#setFailure(String)} or {@link UT2004BotTestController#setSuccess(String)}.
     * @return
     */
    public String getMessage() {
		return message;
	}
    
	/**
	 * The exception that caused the bot to fail... if provided via {@link UT2004BotTestController#setFailure(Throwable)}.
	 * 
	 * @return
	 */
	public Throwable getCause() {
		return cause;
	}

	/**
     * Sets different latch for the bot controller.
     * @param latch
     */
    public void setTestLatch(CountDownLatch latch) {
        this.latch = latch;
    }
    
    /**
     * Declares "success" - i.e., the test has ended correctly, everything has passed.
     * <p><p>
     * Also raises the {@link UT2004BotTestController#latch}.
     */
    public void setSuccess() {
		this.success.setFlag(true);
		latch.countDown();
	}
    
    /**
     * Declares "success" - i.e., the test has ended correctly, everything has passed.
     * <p><p>
     * Also raises the {@link UT2004BotTestController#latch}.
     */
    public void setSuccess(String message) {
    	this.message = message;
    	log.info(message);
		this.success.setFlag(true);
		latch.countDown();
	}
	
	
	/**
	 * Called from the outside by the test itself to notify the bot about the timeout.
	 * <p><p>
	 * Calls {@link UT2004BotTestController#setFailure(String)} + raises the test latch.
	 */
	public void timeout() {				
		latch.countDown();
		setFailure("Timeout!");
	}
	
	/**
	 * Declares "failure" - i.e., the test has ended in a wrong way.
	 * <p>
	 * <p>
	 * Also raises the {@link UT2004BotTestController#latch}.
	 */
	public void setFailure() {
		this.failure.setFlag(true);
		latch.countDown();
	}

	/**
	 * Declares "failure" - i.e., the test has ended in a wrong way.
	 * <p>
	 * <p>
	 * Also raises the {@link UT2004BotTestController#latch}.
	 */
	public void setFailure(String message) {
		setMessage(message);
		setFailure();
	}
	/**
	 * Declares "failure" - i.e., the test has ended in a wrong way.
	 * <p>
	 * <p>
	 * Also raises the {@link UT2004BotTestController#latch}.
	 */
	public void setFailure(Throwable cause) {
		setCause(cause);
		setFailure();
	}
	/**
	 * Declares "failure" - i.e., the test has ended in a wrong way.
	 * <p>
	 * <p>
	 * Also raises the {@link UT2004BotTestController#latch}.
	 */
	public void setFailure(String message, Throwable cause) {
		setMessage(message);
		setCause(cause);
		setFailure();
	}

	/**
	 * Sets cause of a failure ...if any. Logs if a cause had been set before.
	 * 
	 * @param cause
	 */
	private void setCause(Throwable cause) {
		this.cause = cause;
		if (cause != null)
			log.severe(message);
		else
			log.severe("setFailure(cause): cause == null !!!");
	}

	/**
	 * Sets message of a failure ...if any. Logs a if a message had been set before.
	 * 
	 * @param cause
	 */

	private void setMessage(String message) {
		this.message = message;
		if (message != null)
			log.severe(message);
		else
			log.severe("setFailure(message): message == null !!!");
	}

	/**
	 * Whether the test has failed (may be called after the latch has been risen). 
	 */
	public boolean isFailure() {
		return failure.getFlag() || (latch.getCount() == 0 && !failure.getFlag() && !success.getFlag());
	}
	
	/**
	 * Returns a failure flag, may be used to attach listeners to it. 
	 * <p><p>
	 * Note that the flag's value semantics is a bit different
	 * from {@link UT2004BotTestController#isFailure()} as it will switch itself to true only iff
	 * the bot reports a failure via {@link UT2004BotTestController#setFailure()}. 
	 * @return
	 */
	public ImmutableFlag<Boolean> getFailureFlag() {
		return failure.getImmutable();
	}

	/**
	 * Whether the test has succeeded (may be called after the latch has been risen).
	 * @return
	 */
	public boolean isSuccess() {
		return success.getFlag();
	}
	
	/**
	 * Success flag, may be used to attach listeners to it...
	 * @return
	 */
	public ImmutableFlag<Boolean> getSuccessFlag() {
		return success.getImmutable();
	}
	
	/**
	 * Returns a latch on which you may {@link CountDownLatch#await()} till the test finishes.
	 * @return
	 */
	public CountDownLatch getTestLatch() {
		return latch;
	}
    
	@SuppressWarnings("unchecked")
	protected LogicModule getLogicModule(){
		return logicModule;
	}
}
