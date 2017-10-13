package cz.cuni.amis.pogamut.multi.communication.messages;

import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldChangeEvent;

/**
 * This message is used by a localWorldView to notify sharedWorldView, that a new batch has started and the LocalWorldView is
 * waiting for the sharedWorldView to process all events from this batch.
 * Since the batches are agent-dependent, the event contains information about both the time of the batch and the Agent that sent it.
 * @author srlok
 *
 */
public class SharedBatchBeginEvent implements IWorldChangeEvent {

	private long time;
	private IAgentId agentId;
	
	public SharedBatchBeginEvent(long time, IAgentId agentId)
	{
		this.time = time;
		this.agentId = agentId;
	}
	
	/**
	 * Returns ID of the agent from which comes the batch.
	 * @return
	 */
	public IAgentId getAgentId()
	{
		return agentId;
	}
	
	@Override
	public long getSimTime() {
		return time;
	}
	
	@Override
	public String toString() {
		return "SharedBatchBeginEvent[agentId=" + getAgentId() + ", time=" + getSimTime() + "]";
	}
	

}
