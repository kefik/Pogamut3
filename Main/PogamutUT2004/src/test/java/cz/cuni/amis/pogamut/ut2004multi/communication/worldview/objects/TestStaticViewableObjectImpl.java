package cz.cuni.amis.pogamut.ut2004multi.communication.worldview.objects;

import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.IStaticWorldObject;
import cz.cuni.amis.utils.flag.FlagInteger;
import cz.cuni.amis.utils.flag.ImmutableFlag;

public class TestStaticViewableObjectImpl extends TestStaticViewableObject{

private static FlagInteger instances = new FlagInteger(0);
	
	public static ImmutableFlag<Integer> getInstances() {
		return instances.getImmutable();
	}
	
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		instances.decrement(1);
	}
	
	protected String stringVal;
	protected long longVal;
	
	public TestStaticViewableObjectImpl(WorldObjectId id, long simTime, String staticString, long staticLong) 
	{
		super(id, simTime);
		instances.increment(1);
		this.stringVal = staticString;
		this.longVal = staticLong;
	}
	
	public TestStaticViewableObjectImpl(TestStaticViewableObject other)
	{
		super(other.id, other.simTime );
		instances.increment(1);
		this.stringVal = other.getStaticString();
		this.longVal = other.getStaticLong();
	}
	
	
	@Override
	public String getStaticString() {
		return stringVal;
	}

	@Override
	public long getStaticLong() {
		return longVal;
	}

	@Override
	public boolean isDifferentFrom(IStaticWorldObject other) {
		return (! this.equals(other));
	}
	

	
}
