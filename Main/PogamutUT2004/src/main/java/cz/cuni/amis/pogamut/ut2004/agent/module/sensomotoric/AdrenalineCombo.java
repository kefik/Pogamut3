package cz.cuni.amis.pogamut.ut2004.agent.module.sensomotoric;

import java.util.logging.Logger;

import cz.cuni.amis.pogamut.base.agent.module.SensomotoricModule;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.AgentInfo;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Combo;

/**
 * Simplifies performance of "adrenaline combos"
 * 
 * @author Jakub Gemrot aka Jimmy
 */
public class AdrenalineCombo extends SensomotoricModule<UT2004Bot> {

	private AgentInfo info;

	/**
	 * Tells whether you have adrenaline >= 100, note that using the combo won't change the adrenaline level in the same "logic-cycle".
	 * @return
	 */
	public boolean canPerformCombo() {
		return info.getAdrenaline() >= 100;
	}
    
	/**
	 * Perform "Berserk" combo (bigger damage).
	 * @return whether the combo has been executed
	 */
	public boolean performBerserk() {
		if (canPerformCombo()) {
			act.act(new Combo("xGame.ComboBerserk"));
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Perform "Defensive" combo (every few seconds adds health).
	 * @return whether the combo has been executed
	 */
	public boolean performDefensive() {
		if (canPerformCombo()) {
			act.act(new Combo("xGame.ComboDefensive"));
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Perform "Defensive" combo (bot is invisible and is very hard to spot), note that this combo does not affects PogamutBots ;-(
	 * @return whether the combo has been executed
	 */
	public boolean performInvisible() {
		if (canPerformCombo()) {
			act.act(new Combo("xGame.ComboDefensive"));
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Perform "Defensive" combo (bots speed is increased), not advised as it usually breaks running constants, which breaks bot navigation in turn.
	 * @return whether the combo has been executed
	 */
	public boolean performSpeed() {
		if (canPerformCombo()) {
			act.act(new Combo("xGame.ComboDefensive"));
			return true;
		} else {
			return false;
		}
	}
	
    public AdrenalineCombo(UT2004Bot bot, AgentInfo info) {
        this(bot, info, null);
    }
    
    public AdrenalineCombo(UT2004Bot bot, AgentInfo info, Logger log) {
        super(bot, log);
        this.info = info;
        cleanUp();
    }
    
    @Override
    protected void cleanUp() {
    	super.cleanUp();
    }
    
}

