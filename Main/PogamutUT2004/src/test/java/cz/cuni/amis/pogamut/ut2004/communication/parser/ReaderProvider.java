package cz.cuni.amis.pogamut.ut2004.communication.parser;

import java.io.Reader;
import java.io.StringReader;

import cz.cuni.amis.pogamut.base.communication.connection.IWorldReaderProvider;
import cz.cuni.amis.pogamut.base.communication.connection.WorldReader;
import cz.cuni.amis.pogamut.base.communication.exception.CommunicationException;
import cz.cuni.amis.pogamut.base.component.bus.IComponentBus;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InfoMessages;
import cz.cuni.amis.pogamut.ut2004.component.ComponentStub;
import cz.cuni.amis.utils.Const;
import cz.cuni.amis.utils.token.IToken;
import cz.cuni.amis.utils.token.Tokens;

public class ReaderProvider extends ComponentStub implements IWorldReaderProvider {

	private Reader reader;
	
	public ReaderProvider(IAgentLogger logger, IComponentBus bus) {
		super(logger, bus);
		StringBuffer sb = new StringBuffer();
		for (String str : InfoMessages.PROTOTYPES) {
			if (str == null) continue;
			sb.append(str);
			sb.append(Const.NEW_LINE);
		}
		this.reader = new StringReader(sb.toString());
	}
	
	@Override
	public WorldReader getReader() throws CommunicationException {
		return new WorldReader.WorldReaderWrapper(reader);	}

	@Override
	public IToken getComponentId() {
		return Tokens.get("ReaderProvider");
	}

}
