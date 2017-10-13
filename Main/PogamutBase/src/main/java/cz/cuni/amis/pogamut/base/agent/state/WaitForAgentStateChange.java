package cz.cuni.amis.pogamut.base.agent.state;

import cz.cuni.amis.pogamut.base.agent.state.level0.IAgentState;
import cz.cuni.amis.utils.flag.Flag;
import cz.cuni.amis.utils.flag.WaitForFlagChange;

public class WaitForAgentStateChange extends WaitForFlagChange<IAgentState> {
	
	public WaitForAgentStateChange(Flag<IAgentState> flag, final Class<? extends IAgentState> agentState) {
		super(flag, new IAccept<IAgentState>() {

			@Override
			public boolean accept(IAgentState flagValue) {
				return agentState.isAssignableFrom(flagValue.getClass());
			}
			
		});
	}
	
}
