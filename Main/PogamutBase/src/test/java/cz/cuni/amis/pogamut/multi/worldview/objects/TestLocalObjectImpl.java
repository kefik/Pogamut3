package cz.cuni.amis.pogamut.multi.worldview.objects;

import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;
import cz.cuni.amis.utils.flag.FlagInteger;
import cz.cuni.amis.utils.flag.ImmutableFlag;

public class TestLocalObjectImpl extends TestLocalObject{

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
	protected Long longVal;
	
	public TestLocalObjectImpl( WorldObjectId id, long simTime, String localString, long localLong )
	{
		super(id, simTime);
		instances.increment(1);
		stringVal = localString;
		longVal = localLong;
	}
	
	public TestLocalObjectImpl( TestLocalObject other)
	{
		super(other.getId(), other.getSimTime());
		instances.increment(1);
		this.stringVal = new String(other.getLocalString());
		this.longVal = other.getLocalLong();
	}
	
	@Override
	public String getLocalString() {
		return stringVal;
	}

	@Override
	public long getLocalLong() {
		return longVal;
	}

	@Override
	public TestLocalObject clone() {
		return new TestLocalObjectImpl(this);
	}
	

}
