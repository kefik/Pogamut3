package cz.cuni.amis.utils.flag.connective;

import cz.cuni.amis.utils.flag.Flag;
import cz.cuni.amis.utils.flag.FlagListener;

public class ConnectiveListener implements FlagListener<Boolean> {
	
	Flag<Boolean> myFlag;
	
	private Object mutex = new Object();
	
	private int intTrue;
	private int intFalse;
	
	private Connective connective;
	
	public ConnectiveListener(Connective connective, Flag<Boolean> flag, int myIndex) {
		this.myFlag = flag;
		this.connective = connective; 			
		intTrue = 1 << myIndex;
		intFalse = 0 ^ intTrue;
		synchronized(mutex) {
			this.myFlag.addListener(this);
			if (flag.getFlag()) {
				synchronized(connective.truthValue) {
					connective.truthValue[0] = connective.truthValue[0] | intTrue;
				}
			} else {
				synchronized(connective.truthValue) {
					connective.truthValue[0] = connective.truthValue[0] & intFalse;
				}
			}				
		}
	}

	@Override
	public void flagChanged(Boolean changedValue) {
		if (changedValue == null || !changedValue) {
			synchronized(connective.truthValue) {
				connective.truthValue[0] = connective.truthValue[0] | intTrue;
				connective.truthValueChanged();
			}
		} else {
			synchronized(connective.truthValue) {
				connective.truthValue[0] = connective.truthValue[0] & intFalse;
				connective.truthValueChanged();
			}
		}
		
	}
	
}
