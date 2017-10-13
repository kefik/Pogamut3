package cz.cuni.amis.pogamut.ut2004multi.communication.worldview.objects;

import java.util.HashMap;
import java.util.Map;

import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;
import cz.cuni.amis.pogamut.multi.communication.translator.event.ILocalWorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.ILocalWorldObject;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.ISharedProperty;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.ISharedWorldObject;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.IStaticWorldObject;
import cz.cuni.amis.pogamut.multi.communication.worldview.property.LongProperty;
import cz.cuni.amis.pogamut.multi.communication.worldview.property.PropertyId;
import cz.cuni.amis.pogamut.multi.communication.worldview.property.StringProperty;
import cz.cuni.amis.utils.flag.FlagInteger;
import cz.cuni.amis.utils.flag.ImmutableFlag;

public class TestCompositeViewableObjectMessage extends TestCompositeViewableObject {

private static FlagInteger instances = new FlagInteger(0);
	
	public static ImmutableFlag<Integer> getInstances() {
		return instances.getImmutable();
	}
	
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		instances.decrement(1);
	}
	
	public TestCompositeViewableObjectMessage(WorldObjectId id, long time, String localString, Long localLong, String sharedString, long sharedLong,
			String staticString, Long staticLong, boolean visible) 
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
		this.visible = visible;
	}
	
	protected long time;
	protected String localString;
	protected Long localLong;
	protected String sharedString;
	protected Long sharedLong;
	protected String staticString;
	protected Long staticLong;
	protected boolean visible;

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

	
	protected class TestLocalViewableObjectMessage extends TestLocalViewableObject
	{
		public TestLocalViewableObjectMessage()
		{
			super( TestCompositeViewableObjectMessage.this.id, TestCompositeViewableObjectMessage.this.time );
		}

		@Override
		public String getLocalString() {
			return TestCompositeViewableObjectMessage.this.localString;
		}

		@Override
		public long getLocalLong() {
			return TestCompositeViewableObjectMessage.this.localLong;
		}

		@Override
		public TestLocalViewableObject clone() {
			return this;
		}

		@Override
		public ILocalWorldObjectUpdatedEvent createDisappearEvent() {
			TestLocalViewableObjectImpl obj = new TestLocalViewableObjectImpl(this);
			obj.visible = false;
			return new TestLocalViewableObjectUpdatedEvent(obj, this.simTime);
			
		}

		@Override
		public boolean isVisible() {
			return TestCompositeViewableObjectMessage.this.visible;
		}
		
	}
	
	protected class TestSharedViewableObjectMessage extends TestSharedViewableObject
	{

		LongProperty longProp;
		StringProperty stringProp;
		HashMap<PropertyId, ISharedProperty> hMap = new HashMap<PropertyId, ISharedProperty>(2);
		
		TestSharedViewableObjectMessage()
		{
			super(TestCompositeViewableObjectMessage.this.id, TestCompositeViewableObjectMessage.this.time);
			this.longProp = new LongProperty(TestCompositeViewableObjectMessage.this.id, "LongProperty",TestCompositeViewableObjectMessage.this.sharedLong,TestCompositeViewableObject.class);
			this.stringProp = new StringProperty(TestCompositeViewableObjectMessage.this.id, "StrignProp", TestCompositeViewableObjectMessage.this.sharedString, TestCompositeViewableObject.class);
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
		public TestSharedViewableObject clone() {
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
	
	protected class TestStaticViewableObjectMessage extends TestStaticViewableObject
	{
		public TestStaticViewableObjectMessage()
		{
			super( TestCompositeViewableObjectMessage.this.id, TestCompositeViewableObjectMessage.this.getSimTime());
		}

		@Override
		public String getStaticString() {
			return TestCompositeViewableObjectMessage.this.staticString;
		}

		@Override
		public long getStaticLong() {
			return TestCompositeViewableObjectMessage.this.staticLong;
		}

		@Override
		public boolean isDifferentFrom(IStaticWorldObject other) {
			return (!this.equals(other));
		}
		
		
	}
	
	@Override
	public ILocalWorldObject getLocal() {
		return new TestLocalViewableObjectMessage();
	}

	@Override
	public ISharedWorldObject getShared() {
		return new TestSharedViewableObjectMessage();
	}

	@Override
	public IStaticWorldObject getStatic() {
		return new TestStaticViewableObjectMessage();
	}

	@Override
	public IWorldObjectUpdatedEvent createDisappearEvent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isVisible() {
		return this.visible;
	}

}
