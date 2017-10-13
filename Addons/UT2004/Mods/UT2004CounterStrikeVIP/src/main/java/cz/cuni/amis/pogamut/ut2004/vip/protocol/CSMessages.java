package cz.cuni.amis.pogamut.ut2004.vip.protocol;

import cz.cuni.amis.pogamut.ut2004.communication.messages.custom.ControlMessages;
import cz.cuni.amis.pogamut.ut2004.communication.messages.custom.ICustomControlMessage;
import cz.cuni.amis.pogamut.ut2004.vip.protocol.messages.CSAssignVIP;
import cz.cuni.amis.pogamut.ut2004.vip.protocol.messages.CSBotStateChanged;
import cz.cuni.amis.pogamut.ut2004.vip.protocol.messages.CSCounterTerroristsWin;
import cz.cuni.amis.pogamut.ut2004.vip.protocol.messages.CSSetVIPSafeArea;
import cz.cuni.amis.pogamut.ut2004.vip.protocol.messages.CSTerroristsWin;
import cz.cuni.amis.pogamut.ut2004.vip.protocol.messages.VIPGameEnd;
import cz.cuni.amis.pogamut.ut2004.vip.protocol.messages.VIPGameStart;
import cz.cuni.amis.pogamut.ut2004.vip.protocol.messages.CSRoundEnd;
import cz.cuni.amis.pogamut.ut2004.vip.protocol.messages.CSRoundStart;
import cz.cuni.amis.pogamut.ut2004.vip.protocol.messages.CSRoundState;
import cz.cuni.amis.pogamut.ut2004.vip.protocol.messages.CSTeamScoreChanged;
import cz.cuni.amis.pogamut.ut2004.vip.protocol.messages.CSVIPKilled;
import cz.cuni.amis.pogamut.ut2004.vip.protocol.messages.CSVIPSafe;

/**
 * CS Messages
 * 
 * @author Jimmy
 */
public class CSMessages extends ControlMessages {
	
	@SuppressWarnings("unchecked")
	public static final Class<? extends ICustomControlMessage>[] messages = 
		new Class[] {
			CSAssignVIP.class,
			CSBotStateChanged.class,
			CSCounterTerroristsWin.class,
			CSRoundEnd.class,
			CSRoundStart.class,
			CSRoundState.class,		
			CSSetVIPSafeArea.class,
			CSTeamScoreChanged.class,
			CSTerroristsWin.class,
			CSVIPKilled.class,
			CSVIPSafe.class,
			VIPGameEnd.class,
			VIPGameStart.class,			
		}
	;
	
	public CSMessages() {
		register(messages);
	}

}
