package cz.cuni.amis.pogamut.multi.worldview.objects;

import java.util.HashMap;
import java.util.Map;

import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.ILocalWorldObject;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.ISharedProperty;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.ISharedWorldObject;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.IStaticWorldObject;
import cz.cuni.amis.pogamut.multi.communication.worldview.property.LongProperty;
import cz.cuni.amis.pogamut.multi.communication.worldview.property.PropertyId;
import cz.cuni.amis.pogamut.multi.communication.worldview.property.StringProperty;
import cz.cuni.amis.utils.flag.FlagInteger;
import cz.cuni.amis.utils.flag.ImmutableFlag;

import cz.cuni.amis.tests.BaseTest;
				
public class TestCompositeObjectMessage extends TestCompositeObject {

private static FlagInteger instances = new FlagInteger(0);
	
	public static ImmutableFlag<Integer> getInstances() {
		return instances.getImmutable();
	}
	
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		instances.decrement(1);
	}
	
	public TestCompositeObjectMessage(WorldObjectId id, long time, String localString, Long localLong, String sharedString, long sharedLong,
			String staticString, Long staticLong) 
	{
		super(id);
		instances.increment(1);
		this.localString = localString;
		this.localLong = localLong;
		this.sharedString = sharedString;
		this.sharedLong = sharedLong;
		this.staticLong = staticLong;
		this.staticString = staticString;
		this.time = time;
	}
	
	protected long time;
	protected String localString;
	protected Long localLong;
	protected String sharedString;
	protected Long sharedLong;
	protected String staticString;
	protected Long staticLong;

	@Override
	public long getSimTime() {
		return time;
	}

	@Override
	public String getLocalString() {
		return localString;
	}

	@Override
	public long getLocalLong() {
		return localLong;
	}

	@Override
	public String getStaticString() {
		return staticString;
	}

	@Override
	public long getStaticLong() {
		return staticLong;
	}

	@Override
	public String getSharedString() {
		return sharedString;
	}

	@Override
	public long getSharedLong() {
		return sharedLong;
	}

	
	protected class TestLocalObjectMessage extends TestLocalObject
	{
		public TestLocalObjectMessage()
		{
			super( TestCompositeObjectMessage.this.id, TestCompositeObjectMessage.this.time );
		}

		@Override
		public String getLocalString() {
			return TestCompositeObjectMessage.this.localString;
		}

		@Override
		public long getLocalLong() {
			return TestCompositeObjectMessage.this.localLong;
		}

		@Override
		public TestLocalObject clone() {
			return this;
		}
		
	}
	
	protected class TestSharedObjectMessage extends TestSharedObject
	{

		LongProperty longProp;
		StringProperty stringProp;
		HashMap<PropertyId, ISharedProperty> hMap = new HashMap<PropertyId, ISharedProperty>(2);
		
		TestSharedObjectMessage()
		{
			super(TestCompositeObjectMessage.this.id, TestCompositeObjectMessage.this.time);
			this.longProp = new LongProperty(TestCompositeObjectMessage.this.id, "LongProperty",TestCompositeObjectMessage.this.sharedLong,TestCompositeObject.class);
			this.stringProp = new StringProperty(TestCompositeObjectMessage.this.id, "StrignProp", TestCompositeObjectMessage.this.sharedString, TestCompositeObject.class);
			hMap.put(longProp.getPropertyId(), longProp);
			hMap.put(stringProp.getPropertyId(), stringProp);
		}
		
		@Override
		public ISharedProperty getProperty(PropertyId id) {
			return hMap.get(id);
		}

		@Override
		public Map<PropertyId, ISharedProperty> getProperties() {
			return hMap;
		}

		@Override
		public TestSharedObject clone() {
			return this;
		}

		@Override
		public String getSharedString() {
			return stringProp.getValue();
		}

		@Override
		public long getSharedLong() {
			return longProp.getValue();
		}	
	}
	
	protected class TestStaticObjectMessage extends TestStaticObject
	{
		public TestStaticObjectMessage()
		{
			super( TestCompositeObjectMessage.this.id, TestCompositeObjectMessage.this.getSimTime());
		}

		@Override
		public String getStaticString() {
			return TestCompositeObjectMessage.this.staticString;
		}

		@Override
		public long getStaticLong() {
			return TestCompositeObjectMessage.this.staticLong;
		}

		@Override
		public boolean isDifferentFrom(IStaticWorldObject other) {
			return this.equals(other);
		}
		
		
	}
	
	@Override
	public ILocalWorldObject getLocal() {
		return new TestLocalObjectMessage();
	}

	@Override
	public ISharedWorldObject getShared() {
		return new TestSharedObjectMessage();
	}

	@Override
	public IStaticWorldObject getStatic() {
		return new TestStaticObjectMessage();
	}

}
