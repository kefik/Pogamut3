package cz.cuni.amis.pogamut.base.communication.command.impl;

import com.google.inject.Inject;

import cz.cuni.amis.pogamut.base.communication.command.ICommandSerializer;
import cz.cuni.amis.pogamut.base.communication.messages.CommandMessage;
import cz.cuni.amis.pogamut.base.utils.guice.AgentScoped;

@AgentScoped
public class StringCommandSerializer implements ICommandSerializer<String> {

	@Inject
	public StringCommandSerializer() {
	}
	
	@Override
	public String serialize(CommandMessage command) {
		return command.toString();
	}
	
	public String toString() {
		return "StringCommandSerializer";
	}

}
