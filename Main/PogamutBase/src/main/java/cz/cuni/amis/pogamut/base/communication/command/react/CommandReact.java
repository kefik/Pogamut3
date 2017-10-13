package cz.cuni.amis.pogamut.base.communication.command.react;

import cz.cuni.amis.pogamut.base.communication.command.IAct;
import cz.cuni.amis.pogamut.base.communication.command.ICommandListener;
import cz.cuni.amis.pogamut.base.communication.messages.CommandMessage;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.react.EventReact;
import cz.cuni.amis.pogamut.base.communication.worldview.react.EventReactOnce;

/**
 * This abstract class allows you to easily hook a specific outgoing-command-handling behavior. It automatically
 * register a listener for a specified {@link CommandMessage} for you and calls {@link CommandReact#react(CommandMessage)}
 * method automatically.
 * <p><p>
 * If you need to react only once to the event, use {@link EventReactOnce}.
 * <p><p>
 * Use {@link EventReact#enable()} and {@link EventReact#disable()} to enable react / disable react. The reaction is enabled
 * as default.
 * <p><p>
 * <b>WARNING:</b>Use as anonymous class, but <b>save it as a field</b> of your class! Note, that we're using weak-references to 
 * listeners and if you do not save pointer to the object, it will be gc()ed!
 * 
 * @author Jimmy
 *
 * @param <EVENT>
 */
public abstract class CommandReact<COMMAND extends CommandMessage> {

	protected ICommandListener<COMMAND> reactListener = new ICommandListener<COMMAND>() {

		@Override
		public void notify(COMMAND event) {
			preReact(event);
			react(event);
			postReact(event);
		}
	
	};
	
	protected IAct reactAct;

	protected Class<COMMAND> reactCommandClass;
	
	private boolean reactHooked = false;
	
	public CommandReact(Class<COMMAND> commandClass, IAct worldView) {
		this.reactAct = worldView;
		this.reactCommandClass = commandClass;
		enable();
	}
	
	/**
	 * Disables the reaction.
	 */
	public synchronized void disable() {
		if (reactHooked) {
			reactHooked = false;
			reactAct.removeCommandListener(reactCommandClass, reactListener);
		}
	}
	
	/**
	 * Enables the reaction.
	 */
	public synchronized void enable() {
		if (!reactHooked) {
			reactHooked = true;
			if (!reactAct.isCommandListening(reactCommandClass, reactListener)) reactAct.addCommandListener(reactCommandClass, reactListener);
		}
	}
	
	/**
	 * pre-{@link EventReact#react(IWorldEvent)} hook allowing you to do additional work before the react method.
	 * @param event
	 */
	protected void preReact(COMMAND event) {
	}

	/**
	 * React upon event notification.
	 * @param event
	 */
	protected abstract void react(COMMAND event);
	
	/**
	 * post-{@link EventReact#react(IWorldEvent)} hook allowing you to do additional work after the react method.
	 * @param event
	 */
	protected void postReact(COMMAND event) {
	}

}
