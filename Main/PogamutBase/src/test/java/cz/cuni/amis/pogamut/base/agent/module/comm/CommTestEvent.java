package cz.cuni.amis.pogamut.base.agent.module.comm;

import org.junit.Ignore;

@Ignore
public class CommTestEvent extends CommEvent {
	
	public int num;
	
	public CommTestEvent(int num) {
		this.num = num;
	}

	@Override
	public String toString() {
		return "CommTestEvent[num=" + num + "]";
	}
	
}
