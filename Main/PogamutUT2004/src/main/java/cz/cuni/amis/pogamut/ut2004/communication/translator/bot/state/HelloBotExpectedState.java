package cz.cuni.amis.pogamut.ut2004.communication.translator.bot.state;

import cz.cuni.amis.fsm.FSMInitialState;
import cz.cuni.amis.fsm.FSMState;
import cz.cuni.amis.fsm.FSMTransition;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.HelloBotHandshake;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Password;
import cz.cuni.amis.pogamut.ut2004.communication.translator.TranslatorContext;
import cz.cuni.amis.pogamut.ut2004.communication.translator.bot.support.BotMessageExpectedState;
import cz.cuni.amis.pogamut.ut2004.communication.translator.shared.transition.ReadyRequestedTransition;

/**
 * First and initial state of the BotFSM handler. It expects HELLO_BOT msg and throws exception if it doesn't come.
 * @author Jimmy
 */
@FSMState(map={
				@FSMTransition(
						state=HandshakeControllerState.class, 
						symbol={HelloBotHandshake.class}, 
						transition={ReadyRequestedTransition.class}
					),
				@FSMTransition(
						state = PasswordState.class, 
						symbol = { Password.class }, 
						transition = {})
				}
)
@FSMInitialState
public class HelloBotExpectedState extends BotMessageExpectedState<TranslatorContext> {

	public HelloBotExpectedState() {
		super(HelloBotHandshake.class);
	}

}
