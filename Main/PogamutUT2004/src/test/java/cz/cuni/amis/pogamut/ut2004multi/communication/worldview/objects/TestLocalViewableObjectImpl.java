package cz.cuni.amis.pogamut.ut2004multi.communication.worldview.objects;

import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;
import cz.cuni.amis.pogamut.multi.communication.translator.event.ILocalWorldObjectUpdatedEvent;
import cz.cuni.amis.utils.flag.FlagInteger;
import cz.cuni.amis.utils.flag.ImmutableFlag;

public class TestLocalViewableObjectImpl extends TestLocalViewableObject{

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
	protected boolean visible;
	
	public TestLocalViewableObjectImpl( WorldObjectId id, long simTime, String localString, long localLong , boolean visible)
	{
		super(id, simTime);
		instances.increment(1);
		stringVal = localString;
		longVal = localLong;
		this.visible = visible;
	}
	
	public TestLocalViewableObjectImpl( TestLocalViewableObject other)
	{
		super(other.getId(), other.getSimTime());
		instances.increment(1);
		this.stringVal = new String(other.getLocalString());
		this.longVal = other.getLocalLong();
		this.visible = other.isVisible();
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
	public TestLocalViewableObject clone() {
		return new TestLocalViewableObjectImpl(this);
	}

	@Override
	public ILocalWorldObjectUpdatedEvent createDisappearEvent() 
	{
		TestLocalViewableObjectImpl data = new TestLocalViewableObjectImpl(this);
		data.visible = false;
		return new TestLocalViewableObjectUpdatedEvent(data, this.simTime);
	}
	
	@Override
	public boolean isVisible() {
		return this.visible;
	}
	

}
