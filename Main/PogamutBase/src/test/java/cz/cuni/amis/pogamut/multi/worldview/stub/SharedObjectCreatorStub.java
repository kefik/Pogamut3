package cz.cuni.amis.pogamut.multi.worldview.stub;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import cz.cuni.amis.pogamut.multi.communication.worldview.object.ICompositeWorldObject;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.ISharedProperty;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.ISharedWorldObject;
import cz.cuni.amis.pogamut.multi.worldview.objects.TestCompositeObject;
import cz.cuni.amis.pogamut.multi.worldview.objects.TestSharedObject;
import cz.cuni.amis.pogamut.multi.worldview.objects.TestSharedObjectImpl;
import cz.cuni.amis.utils.exception.PogamutException;

/**
 * Used for testing purposes only
 * @author srlok
 *
 */
public abstract class SharedObjectCreatorStub {
	
	private static Map<Class, ISharedObjectCreator> creatorMap = new HashMap<Class, ISharedObjectCreator>();

	static 
	{
		creatorMap.put( TestCompositeObject.class, new TestSharedObjectCreator() );
	}
	
	public static ISharedWorldObject create(Collection<ISharedProperty> properties)
	{
		Class compClass = null;
		for ( ISharedProperty p : properties)
		{
			if ( compClass == null)
			{
				compClass = p.getCompositeClass();
			}
			else if ( !compClass.equals( p.getCompositeClass()))
			{
				throw new PogamutException("Composite classes don't match in SharedObjectCreatorStub.create()... c1 : " + compClass.getSimpleName() + "  !=" +
						" " + p.getCompositeClass().getSimpleName() + " .", p);
			}
		}
		if ( creatorMap.get(compClass) == null)
		{
			throw new PogamutException("Wrong CompositeObjectClass -- sharedObjectCreator nonexistent : " + compClass.getSimpleName(), compClass);
		}
		return ( creatorMap.get(compClass).create(properties));
	}
	
	protected static interface ISharedObjectCreator<T extends ICompositeWorldObject>
	{
		public ISharedWorldObject create(Collection<ISharedProperty> properties);
	}
	
	protected static class TestSharedObjectCreator implements ISharedObjectCreator<TestCompositeObject>
	{
		@Override
		public TestSharedObject create(Collection<ISharedProperty> properties) {
			return new TestSharedObjectImpl(properties);
		}
		
	}
	
	
}
