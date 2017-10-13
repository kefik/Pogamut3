package cz.cuni.amis.pogamut.base.utils.logging;

import java.util.logging.LogRecord;

import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.utils.logging.DefaultLogFormatter;

/**
 * Pogamut custom formatter used as default formatter for {@link LogPublisher}.
 *
 * @author Jimmy
 */
public class LogFormatter extends DefaultLogFormatter {
	
	private IAgentId agentId;

	public LogFormatter() {
		this(null, false);
	}

	public LogFormatter(boolean lineEnds) {
		this(null, lineEnds);
	}

	public LogFormatter(IAgentId agentId) {
		this(agentId, false);
	}
	
	public LogFormatter(IAgentId agentId, boolean lineEnds) {
		super(null, lineEnds);
		this.agentId = agentId;
		this.lineEnds = lineEnds;
	}

	@Override
	public synchronized String format(LogRecord record) {
		if (agentId != null && agentId.getName() != null) {
			this.name = this.agentId.getName().getFlag();
		}
		return super.format(record);
	}

}
