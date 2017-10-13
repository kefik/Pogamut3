package cz.cuni.amis.pogamut.ut2004multi.communication.worldview.stubs;

import java.util.Collection;
import java.util.HashMap;

import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.ICompositeWorldObject;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.ISharedProperty;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.ISharedWorldObject;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.AliveMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.AliveMessageSharedImpl;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.AutoTraceRay;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.AutoTraceRaySharedImpl;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BombInfo;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BombInfoSharedImpl;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ConfigChange;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ConfigChangeSharedImpl;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.FlagInfo;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.FlagInfoSharedImpl;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfoSharedImpl;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.IncomingProjectile;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.IncomingProjectileSharedImpl;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InitedMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InitedMessageSharedImpl;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Item;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ItemSharedImpl;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Mover;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.MoverSharedImpl;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.MyInventory;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.MyInventorySharedImpl;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPointSharedImpl;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.PlayerSharedImpl;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Self;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.SelfSharedImpl;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.TeamScore;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.TeamScoreSharedImpl;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.UT2004SharedObjectCreator;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Vehicle;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.VehicleSharedImpl;
import cz.cuni.amis.pogamut.ut2004multi.communication.worldview.objects.TestCompositeViewableObject;
import cz.cuni.amis.pogamut.ut2004multi.communication.worldview.objects.TestSharedViewableObjectImpl;
import cz.cuni.amis.utils.NullCheck;
import cz.cuni.amis.utils.exception.PogamutException;

/**
 * Creator implementing both TestObjects and UT2004 Messages
 * @author srlok
 *
 */
public class UT2004TestSharedObjectCreator {

	public static interface ISharedObjectCreator<T extends ICompositeWorldObject>
	{
		public ISharedWorldObject create(WorldObjectId id, Collection<ISharedProperty> c);
	}
	
	
		public static class AliveMessageSharedCreator implements ISharedObjectCreator<AliveMessage>
		{
			@Override
			public ISharedWorldObject create(WorldObjectId id, Collection<ISharedProperty> c) {
				return new AliveMessageSharedImpl(id, c);
			}
		}
	
		public static class AutoTraceRaySharedCreator implements ISharedObjectCreator<AutoTraceRay>
		{
			@Override
			public ISharedWorldObject create(WorldObjectId id, Collection<ISharedProperty> c) {
				return new AutoTraceRaySharedImpl(id, c);
			}
		}
	
		public static class BombInfoSharedCreator implements ISharedObjectCreator<BombInfo>
		{
			@Override
			public ISharedWorldObject create(WorldObjectId id, Collection<ISharedProperty> c) {
				return new BombInfoSharedImpl(id, c);
			}
		}
	
		public static class ConfigChangeSharedCreator implements ISharedObjectCreator<ConfigChange>
		{
			@Override
			public ISharedWorldObject create(WorldObjectId id, Collection<ISharedProperty> c) {
				return new ConfigChangeSharedImpl(id, c);
			}
		}
	
		public static class FlagInfoSharedCreator implements ISharedObjectCreator<FlagInfo>
		{
			@Override
			public ISharedWorldObject create(WorldObjectId id, Collection<ISharedProperty> c) {
				return new FlagInfoSharedImpl(id, c);
			}
		}
	
		public static class GameInfoSharedCreator implements ISharedObjectCreator<GameInfo>
		{
			@Override
			public ISharedWorldObject create(WorldObjectId id, Collection<ISharedProperty> c) {
				return new GameInfoSharedImpl(id, c);
			}
		}
	
		public static class IncomingProjectileSharedCreator implements ISharedObjectCreator<IncomingProjectile>
		{
			@Override
			public ISharedWorldObject create(WorldObjectId id, Collection<ISharedProperty> c) {
				return new IncomingProjectileSharedImpl(id, c);
			}
		}
	
		public static class InitedMessageSharedCreator implements ISharedObjectCreator<InitedMessage>
		{
			@Override
			public ISharedWorldObject create(WorldObjectId id, Collection<ISharedProperty> c) {
				return new InitedMessageSharedImpl(id, c);
			}
		}
	
		public static class ItemSharedCreator implements ISharedObjectCreator<Item>
		{
			@Override
			public ISharedWorldObject create(WorldObjectId id, Collection<ISharedProperty> c) {
				return new ItemSharedImpl(id, c);
			}
		}
	
		public static class MoverSharedCreator implements ISharedObjectCreator<Mover>
		{
			@Override
			public ISharedWorldObject create(WorldObjectId id, Collection<ISharedProperty> c) {
				return new MoverSharedImpl(id, c);
			}
		}
	
		public static class MyInventorySharedCreator implements ISharedObjectCreator<MyInventory>
		{
			@Override
			public ISharedWorldObject create(WorldObjectId id, Collection<ISharedProperty> c) {
				return new MyInventorySharedImpl(id, c);
			}
		}
	
		public static class NavPointSharedCreator implements ISharedObjectCreator<NavPoint>
		{
			@Override
			public ISharedWorldObject create(WorldObjectId id, Collection<ISharedProperty> c) {
				return new NavPointSharedImpl(id, c);
			}
		}
	
		public static class PlayerSharedCreator implements ISharedObjectCreator<Player>
		{
			@Override
			public ISharedWorldObject create(WorldObjectId id, Collection<ISharedProperty> c) {
				return new PlayerSharedImpl(id, c);
			}
		}
	
		public static class SelfSharedCreator implements ISharedObjectCreator<Self>
		{
			@Override
			public ISharedWorldObject create(WorldObjectId id, Collection<ISharedProperty> c) {
				return new SelfSharedImpl(id, c);
			}
		}
	
		public static class TeamScoreSharedCreator implements ISharedObjectCreator<TeamScore>
		{
			@Override
			public ISharedWorldObject create(WorldObjectId id, Collection<ISharedProperty> c) {
				return new TeamScoreSharedImpl(id, c);
			}
		}
	
		public static class VehicleSharedCreator implements ISharedObjectCreator<Vehicle>
		{
			@Override
			public ISharedWorldObject create(WorldObjectId id, Collection<ISharedProperty> c) {
				return new VehicleSharedImpl(id, c);
			}
		}
		
		public static class TestSharedViewableObjectCreator implements ISharedObjectCreator<TestCompositeViewableObject>
		{

			@Override
			public ISharedWorldObject create(WorldObjectId id,
					Collection<ISharedProperty> c) {
				return new TestSharedViewableObjectImpl(c);
			}
			
		}
	
	
	private static HashMap<Class, ISharedObjectCreator> map = new HashMap<Class, ISharedObjectCreator>();

	static {
		
			map.put(TestCompositeViewableObject.class, new TestSharedViewableObjectCreator());
		
			map.put(
				AliveMessage
				.class, 
				new AliveMessageSharedCreator()
			);
		
			map.put(
				AutoTraceRay
				.class, 
				new AutoTraceRaySharedCreator()
			);
		
			map.put(
				BombInfo
				.class, 
				new BombInfoSharedCreator()
			);
		
			map.put(
				ConfigChange
				.class, 
				new ConfigChangeSharedCreator()
			);
		
			map.put(
				FlagInfo
				.class, 
				new FlagInfoSharedCreator()
			);
		
			map.put(
				GameInfo
				.class, 
				new GameInfoSharedCreator()
			);
		
			map.put(
				IncomingProjectile
				.class, 
				new IncomingProjectileSharedCreator()
			);
		
			map.put(
				InitedMessage
				.class, 
				new InitedMessageSharedCreator()
			);
		
			map.put(
				Item
				.class, 
				new ItemSharedCreator()
			);
		
			map.put(
				Mover
				.class, 
				new MoverSharedCreator()
			);
		
			map.put(
				MyInventory
				.class, 
				new MyInventorySharedCreator()
			);
		
			map.put(
				NavPoint
				.class, 
				new NavPointSharedCreator()
			);
		
			map.put(
				Player
				.class, 
				new PlayerSharedCreator()
			);
		
			map.put(
				Self
				.class, 
				new SelfSharedCreator()
			);
		
			map.put(
				TeamScore
				.class, 
				new TeamScoreSharedCreator()
			);
		
			map.put(
				Vehicle
				.class, 
				new VehicleSharedCreator()
			);
							
	}				
	
	public static ISharedWorldObject create(Class msgClass, WorldObjectId objectId, Collection<ISharedProperty> properties )
	{
		NullCheck.check(msgClass, "msgClass");
		NullCheck.check(objectId, "objectId");
		NullCheck.check(properties, "properties");
		
		ISharedObjectCreator creator = map.get(msgClass);
		if (creator == null) {
		    throw new PogamutException("There is no shared obejct creator for class " + msgClass + ".", UT2004SharedObjectCreator.class);
		}
		return creator.create(objectId, properties);
	}
	
}
