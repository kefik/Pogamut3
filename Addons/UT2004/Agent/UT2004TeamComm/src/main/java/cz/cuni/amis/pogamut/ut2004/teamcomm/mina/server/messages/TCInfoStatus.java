package cz.cuni.amis.pogamut.ut2004.teamcomm.mina.server.messages;

import java.util.ArrayList;
import java.util.List;

import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.teamcomm.mina.messages.TCInfoData;
import cz.cuni.amis.pogamut.ut2004.teamcomm.mina.model.TCTeam;
import cz.cuni.amis.utils.token.IToken;
import cz.cuni.amis.utils.token.Tokens;

public class TCInfoStatus extends TCInfoData {
	
	/**
	 * Auto-generated.
	 */
	private static final long serialVersionUID = -3611636714625106957L;

	public static final IToken MESSAGE_TYPE = Tokens.get("TCInfoStatus");
	
	private List<UnrealId> allBots = new ArrayList<UnrealId>();
	
	private TCTeam team = null;
	
	public TCInfoStatus(long requestId, long simTime) {
		super(requestId, MESSAGE_TYPE, simTime);
	}

	public List<UnrealId> getAllBots() {
		return allBots;
	}

	public void setAllBots(List<UnrealId> allBots) {
		this.allBots = allBots;
	}

	public TCTeam getTeam() {
		return team;
	}

	public void setTeam(TCTeam team) {
		this.team = team;
	}
	
	@Override
	public String toString() {
		return "TCInfoStatus[#bots=" + (allBots == null ? "NULL" : allBots.size()) + ", team=" + team + "]";
	}


}
