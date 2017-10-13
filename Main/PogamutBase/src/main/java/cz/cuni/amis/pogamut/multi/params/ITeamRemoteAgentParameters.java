package cz.cuni.amis.pogamut.multi.params;

import cz.cuni.amis.pogamut.base.agent.params.IRemoteAgentParameters;
import cz.cuni.amis.pogamut.multi.communication.worldview.ISharedWorldView;

/**
 * Combines {@link ITeamAgentParameters} and {@link IRemoteAgentParameters}.
 * 
 * @author Jimmy
 *
 * @param <SHARED_WORLDVIEW>
 */
public interface ITeamRemoteAgentParameters<SHARED_WORLDVIEW extends ISharedWorldView> extends ITeamAgentParameters<SHARED_WORLDVIEW>, IRemoteAgentParameters {

}
