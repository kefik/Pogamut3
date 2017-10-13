package cz.cuni.amis.utils.flag.connective;

import java.util.Map;
import java.util.WeakHashMap;

import cz.cuni.amis.utils.flag.Flag;


public class And extends Connective {
	
	private static Map<And, And> ands = new WeakHashMap<And, And>();
	
	public static And get(Flag<Boolean> flag1, Flag<Boolean> flag2) {
		return get(new Flag[]{flag1, flag2});
	}
	
	public static And get(Flag<Boolean>[] flags) {
		synchronized(ands) {
			And and = new And(flags);
			And existingAnd = ands.get(and);
			if (existingAnd != null) return existingAnd;
			ands.put(and, and);
			return and;
		}		
	}
	
	protected int trueValue = 0;
	
	private And(Flag<Boolean> flag1, Flag<Boolean> flag2) {
		this(new Flag[]{flag1, flag2});
	}

	private And(Flag<Boolean>[] flags) {
		super(flags);
		trueValue = (int) Math.pow(2, flags.length-1);
	}

	@Override
	protected void truthValueChanged() {
		synchronized(truthValue) {
			setFlag(truthValue[0] == trueValue);
		}
	}
	
	public boolean equals(Object obj) {
		if (!(obj instanceof And)) return false;
		And and = (And)obj;
		for (Flag<Boolean> flag : this.flags) {
			if (!and.flags.contains(flag)) return false;
		}
		return true;
	}
	
}
