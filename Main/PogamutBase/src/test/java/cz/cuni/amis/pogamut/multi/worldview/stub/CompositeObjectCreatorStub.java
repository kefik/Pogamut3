package cz.cuni.amis.pogamut.multi.worldview.stub;

import java.util.HashMap;
import java.util.Map;

import cz.cuni.amis.pogamut.multi.communication.worldview.object.ICompositeWorldObject;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.ILocalWorldObject;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.ISharedWorldObject;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.IStaticWorldObject;
import cz.cuni.amis.pogamut.multi.worldview.objects.TestCompositeObject;
import cz.cuni.amis.pogamut.multi.worldview.objects.TestCompositeObjectImpl;
import cz.cuni.amis.pogamut.multi.worldview.objects.TestLocalObject;
import cz.cuni.amis.pogamut.multi.worldview.objects.TestSharedObject;
import cz.cuni.amis.pogamut.multi.worldview.objects.TestStaticObject;
import cz.cuni.amis.utils.NullCheck;
import cz.cuni.amis.utils.exception.PogamutException;

public class CompositeObjectCreatorStub {

	private static Map<Class,ICompositeObjectCreator> creators = new HashMap<Class, ICompositeObjectCreator>();
	
	static 
	{
		creators.put(TestCompositeObject.class, new TestCompositeObjectCreator());
	}
	
	protected static interface ICompositeObjectCreator<T extends ICompositeWorldObject>
	{
		public T create(ILocalWorldObject localPart, ISharedWorldObject sharedPart, IStaticWorldObject staticPart);
	}
	
	public static ICompositeWorldObject create(ILocalWorldObject localPart, ISharedWorldObject sharedPart, IStaticWorldObject staticPart)
	{
		NullCheck.check(localPart, "localPart");
		NullCheck.check(sharedPart, "sharedPart");
		NullCheck.check(staticPart, "staticPart");
		if ( ! ( localPart.getCompositeClass() == sharedPart.getCompositeClass() && localPart.getCompositeClass() == staticPart.getCompositeClass()))
		{
			throw new PogamutException("Composite classes do not match in CompositeObjectCreatorStub create", localPart);
		}
		return creators.get(localPart.getCompositeClass()).create(localPart, sharedPart, staticPart);
	}
	
	protected static class TestCompositeObjectCreator implements ICompositeObjectCreator<TestCompositeObject>
	{

		@Override
		public TestCompositeObject create(ILocalWorldObject localPart,
				ISharedWorldObject sharedPart, IStaticWorldObject staticPart) 
		{
			return new TestCompositeObjectImpl((TestLocalObject)localPart, (TestSharedObject)sharedPart, (TestStaticObject)staticPart );
		}
	}
	
}
