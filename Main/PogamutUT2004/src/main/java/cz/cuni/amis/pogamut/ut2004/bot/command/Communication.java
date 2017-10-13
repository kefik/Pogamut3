package cz.cuni.amis.pogamut.ut2004.bot.command;

import java.util.logging.Logger;

import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.SendMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Self;

/**
 * Class providing Pogamut2 UT2004 communication commands - send message, set
 * dialogs, text bubble...
 * 
 * @author Michal 'Knight' Bida
 */
public class Communication extends BotCommands {

	/**
	 * Sends global message to UT2004 in-game chat. Everyone will see it, bots
	 * will receive information too.
	 * 
	 * (issues GB MESSAGE command)
	 * 
	 * @param text
	 *            Text of the message to send.
	 * 
	 * @see sendTeamTextMessage(String)
	 * @see sendPrivateTextMessage(UnrealId, String)
	 */
	public void sendGlobalTextMessage(String text) {
		SendMessage message = new SendMessage();

		message.setGlobal(true);
		message.setText(text);
		// This will cause that the text bubble won't be shown.
		message.setFadeOut((double)-1);

		agent.getAct().act(message);
	}

	/**
	 * Sends a message to UT2004 in-game chat. Only members of the bot current
	 * team will see it. If there are no teams (e.g. no team game, just simple
	 * deathmatch) it will be handled as global message.
	 * 
	 * (issues GB MESSAGE command)
	 * 
	 * @param text
	 *            Text of the message to send.
	 * 
	 * @see sendGlobalTextMessage(String)
	 * @see sendPrivateTextMessage(UnrealId, String)
	 */
	public void sendTeamTextMessage(String text) {
		SendMessage message = new SendMessage();

		message.setGlobal(false);
		message.setText(text);
		Self self = worldView.getSingle(Self.class);
		if (self != null) {
			message.setTeamIndex(self.getTeam());
		}
		// This will cause that the text bubble won't be shown.
		message.setFadeOut((double)-1);

		agent.getAct().act(message);
	}

	/**
	 * Sends a private message to desired bot (specified by Id). There will be
	 * added "Private:" string in front of the message. Works just for GameBots
	 * RemoteBots. If the id is not of RemoteBot nothing will happen - the
	 * message won't be sent to anyone.
	 * 
	 * @param id
	 *            Here we can specify Id of the bot, that will receive this
	 *            message privately.
	 * @param text
	 *            Text of the message to send.
	 * 
	 * @see sendGlobalTextMessage(String)
	 * @see sendTeamTextMessage(String)
	 * 
	 */
	public void sendPrivateTextMessage(UnrealId id, String text) {
		SendMessage message = new SendMessage();

		message.setId(id);
		message.setText(text);
		// This will cause that the text bubble won't be shown.
		message.setFadeOut((double)-1);

		agent.getAct().act(message);
	}

	/**
	 * Sends a global message to UT2004 in-game chat and sets the text bubble
	 * visible to all human players above the bot head. Everyone will receive
	 * the message. The bubble will stay as long as specified in fadeOut
	 * variable.
	 * 
	 * @param text
	 *            Text of the message and bubble.
	 * @param fadeOut
	 *            Sets how long the bubble should stay visible (in seconds,
	 *            counted as 12 + fadeOut seconds - probably due to some UT
	 *            mechanics). If -1 the bubble won't be shown at all.
	 * 
	 * @see sendPrivateBubbleMessage(UnrealId, String, double)
	 * @see sendPrivateBubbleMessage(UnrealId, String, double)
	 */
	public void sendGlobalBubbleMessage(String text, double fadeOut) {
		SendMessage message = new SendMessage();

		message.setGlobal(true);
		message.setText(text);
		message.setFadeOut(fadeOut);

		agent.getAct().act(message);
	}

	/**
	 * Sends a message to UT2004 in-game chat and sets the text bubble visible
	 * to all human players above the bot head. The message will be send just to
	 * players and bots from the same team (although other human players will
	 * still see it through text bubble). If not team game, treated as global
	 * message. The bubble will stay as long as specified in fadeOut variable.
	 * 
	 * @param text
	 *            Text of the message and bubble.
	 * @param fadeOut
	 *            Sets how long the bubble should stay visible (in seconds,
	 *            counted as 12 + fadeOut seconds - probably due to some UT
	 *            mechanics). If -1 the bubble won't be shown at all.
	 * 
	 * @see sendGlobalBubbleMessage(String, double)
	 * @see sendPrivateBubbleMessage(UnrealId, String, double)
	 */
	public void sendTeamBubbleMessage(String text, double fadeOut) {
		SendMessage message = new SendMessage();

		message.setGlobal(false);
		message.setText(text);
		Self self = worldView.getSingle(Self.class);
		if (self != null) {
			message.setTeamIndex(self.getTeam());
		}
		message.setFadeOut(fadeOut);

		agent.getAct().act(message);
	}

	/**
	 * Sends a private message to desired bot (specified by Id). There will be
	 * added "Private:" string in front of the message. Also sets the text
	 * bubble above the bot head, that will be visible to all human players. The
	 * message can be received just by GameBots RemoteBots (although all human
	 * players will see it through text bubble).
	 * 
	 * @param id
	 *            Here we can specify Id of the bot, that will receive this
	 *            message privately. Other players will see this message through
	 *            bubble too.
	 * @param text
	 *            Text of the message and bubble.
	 * @param fadeOut
	 *            Sets how long the bubble should stay visible (in seconds,
	 *            counted as 12 + fadeOut seconds - probably due to some UT
	 *            mechanics). If -1 the bubble won't be shown at all.
	 * 
	 * @see sendGlobalBubbleMessage(String, double)
	 * @see sendTeamBubbleMessage(String, double)
	 */
	public void sendPrivateBubbleMessage(UnrealId id, String text,
			double fadeOut) {
		SendMessage message = new SendMessage();

		message.setId(id);
		message.setText(text);
		message.setFadeOut(fadeOut);

		agent.getAct().act(message);
	}

	/**
	 * Constructor. Setups the command module based on given agent and logger.
	 * 
	 * @param agent
	 *            AbstractUT2004Bot we will send commands for
	 * @param log
	 *            Logger to be used for logging runtime/debug info.
	 */
	public Communication(UT2004Bot agent, Logger log) {
		super(agent, log);
	}

}