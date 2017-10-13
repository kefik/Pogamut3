package cz.cuni.amis.pogamut.base.communication.command.react;

import cz.cuni.amis.pogamut.base.communication.command.IAct;
import cz.cuni.amis.pogamut.base.communication.messages.CommandMessage;

/**
 * This abstract class allows you to easily hook a specific event-handling behavior. It automatically
 * register a listener for a specified {@link IWorldCommand} for you and calls {@link CommandReactOnce#react(IWorldCommand)}
 * method automatically. The {@link CommandReactOnce#react(IWorldCommand)} will be called only once (upon first event received).
 * <p><p>
 * If you need to react every time, use {@link CommandReact}.
 * <p><p>
 * <p><p>
 * Use {@link CommandReactOnce#enable()} and {@link CommandReactOnce#disable()} to enable react / disable react. The reaction is enabled
 * as default.
 * <b>WARNING:</b>Use as anonymous class, but <b>save it as a field</b> of your class! Note, that we're using weak-references to 
 * listeners and if you do not save pointer to the object, it will be gc()ed!
 * 
 * @author Jimmy
 *
 * @param <COMMAND>
 */
public abstract class CommandReactOnce<COMMAND extends CommandMessage> extends CommandReact<COMMAND> {

	public CommandReactOnce(Class<COMMAND> commandClass, IAct act) {
		super(commandClass, act);
	}
	
	/**
	 * Disables the reaction.
	 */
	@Override
	protected void postReact(COMMAND event) {
		super.postReact(event);
		disable();
	}

}
