package cz.cuni.amis.pogamut.base.utils.logging;

import java.util.logging.LogRecord;

import cz.cuni.amis.pogamut.base.agent.IAgentId;

/**
 * Publisher that is used by {@link AbstractAgentLogger} that passes all the logs into {@link NetworkLogManager}.
 * 
 * @author Pyroh
 * @author Jimmy
 */
public class NetworkLogPublisher extends LogPublisher {
    
    /**
     * Who we are logging for, passed as a parameter along with log message to the {@link NetworkLogPublisher#manager} via
     * {@link NetworkLogManager#processLog(NetworkLogEnvelope, IAgentId)}.
     */
    private IAgentId agent;

    /**
     * Instantiates a log publisher that delivers the log into the {@link NetworkLogManager}.
     * <p><p>
     * The class is not meant to be instantiated outside {@link AgentLogger}.
     * 
     * @param name ID of the agent who wants to publish.
     * @param man  The reference to NetworkLogManager which sends logs to further client sockets.
     */
    NetworkLogPublisher(IAgentId name) {
    	super(name);
        agent = name;
        NetworkLogManager.getNetworkLogManager().addAgent(name);
    }

    @Override
    public void publish(LogRecord record) {
    	NetworkLogManager.getNetworkLogManager().processLog(new NetworkLogEnvelope(record.getLoggerName(), record.getLevel(),record.getMillis(),record.getMessage()), agent);
    }

    @Override
    public void publish(LogRecord record, String formattedMsg) {
    	NetworkLogManager.getNetworkLogManager().processLog(new NetworkLogEnvelope(record.getLoggerName(), record.getLevel(),record.getMillis(),record.getMessage()), agent);
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() throws SecurityException {        
    }

}
