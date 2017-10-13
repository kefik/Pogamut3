package cz.cuni.amis.utils.flag.connective;

import java.util.Map;
import java.util.WeakHashMap;

import cz.cuni.amis.utils.flag.Flag;

public class Or extends Connective {
	
	private static Map<Or, Or> ors = new WeakHashMap<Or, Or>();
	
	public static Or get(Flag<Boolean> flag1, Flag<Boolean> flag2) {
		return get(new Flag[]{flag1, flag2});
	}
	
	public static Or get(Flag<Boolean>[] flags) {
		synchronized(ors) {
			Or or = new Or(flags);
			Or existingOr = ors.get(or);
			if (existingOr != null) return existingOr;
			ors.put(or, or);
			return or;
		}		
	}

	private Or(Flag<Boolean>[] states) {
		super(states);
	}

	@Override
	protected void truthValueChanged() {
		synchronized(truthValue) {
			setFlag(truthValue[0] > 0);
		}
		
	}
	
}

