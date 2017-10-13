package cz.cuni.amis.utils.flag.connective;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cz.cuni.amis.utils.HashCode;
import cz.cuni.amis.utils.flag.Flag;

/**
 * Used to create logical expression out of different flags. (Warning: spagetthi code ahead!)
 * @author Jimmy
 */
public abstract class Connective extends Flag<Boolean> {
	
	protected List<ConnectiveListener> listeners = new ArrayList<ConnectiveListener>();
	
	protected int[] truthValue = new int[]{0};
	
	protected Set<Flag<Boolean>> flags = new HashSet<Flag<Boolean>>();
	
	private int hashCode = 0;
	
	public Connective(Flag<Boolean> flag1, Flag<Boolean> flag2) {
		this(new Flag[]{flag1, flag2});
	}

	public Connective(Flag<Boolean>[] flags) {
		if (flags.length > Integer.SIZE) throw new IllegalArgumentException("Can't have connective with more then " + Integer.SIZE + " arguments (int bit count).");
		synchronized(truthValue) {
			int i = 0;
			Arrays.sort(flags);
			HashCode hc = new HashCode();
			for (Flag<Boolean> flag : flags) {
				this.flags.add(flag);
				hc.add(flag);
				ConnectiveListener listener = new ConnectiveListener(this, flag, i);
				listeners.add(listener);
				++i;			
			}
			hashCode = hc.getHash();
		}
	}
	
	public int hashCode() {
		return hashCode;
	}
	
	protected abstract void truthValueChanged();

}
