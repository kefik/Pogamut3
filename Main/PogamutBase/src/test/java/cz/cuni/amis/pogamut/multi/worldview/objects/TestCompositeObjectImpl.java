package cz.cuni.amis.pogamut.multi.worldview.objects;

import cz.cuni.amis.pogamut.multi.communication.worldview.object.ILocalWorldObject;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.ISharedWorldObject;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.IStaticWorldObject;
import cz.cuni.amis.utils.exception.PogamutException;
import cz.cuni.amis.utils.flag.FlagInteger;
import cz.cuni.amis.utils.flag.ImmutableFlag;

public class TestCompositeObjectImpl extends TestCompositeObject{
	
	protected TestLocalObject localPart;
	protected TestSharedObject sharedPart;
	protected TestStaticObject staticPart;
	
	private static FlagInteger instances = new FlagInteger(0);
	
	public static ImmutableFlag<Integer> getInstances() {
		return instances.getImmutable();
	}
	
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		instances.decrement(1);
	}
	
	public TestCompositeObjectImpl( TestLocalObject localPart, TestSharedObject sharedPart, TestStaticObject staticPart)
	{
		super(localPart.getId());
		instances.increment(1);
		this.localPart = localPart;
		if ( sharedPart.getId() != this.id || staticPart.getId() != this.getId())
		{
			throw new PogamutException("Trying to create composite object from objects with different Ids", this);
		}
		
		this.sharedPart = sharedPart;
		this.staticPart = staticPart;
			
	}

	@Override
	public ILocalWorldObject getLocal() {
		return localPart;
	}

	@Override
	public ISharedWorldObject getShared() {
		return sharedPart;
	}

	@Override
	public IStaticWorldObject getStatic() {
		return staticPart;
	}

	@Override
	public long getSimTime() {
		if ( localPart.getSimTime() < sharedPart.getSimTime() )
		{
			return sharedPart.getSimTime();
		}
		return localPart.getSimTime();
	}

	@Override
	public String getLocalString() {
		return localPart.getLocalString();
	}

	@Override
	public long getLocalLong() {
		return localPart.getLocalLong();
	}

	@Override
	public String getStaticString() {
		return staticPart.getStaticString();
	}

	@Override
	public long getStaticLong() {
		return staticPart.getStaticLong();
	}

	@Override
	public String getSharedString() {
		return sharedPart.getSharedString();
	}

	@Override
	public long getSharedLong() {
		return sharedPart.getSharedLong();
	}
	
	@Override
	public String toString() {
		return "TestCompositeObjectImpl[id=" + getId() + ", time=" + getSimTime() + ", sharedString=" + getSharedString() + ", sharedLong=" + getSharedLong() + ", localString=" + getLocalString() + ", localLong=" + getLocalLong() + ", staticString=" + getStaticString() + ", staticLong=" + getStaticLong() + "]";
	}
	
}
