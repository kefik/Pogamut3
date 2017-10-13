package cz.cuni.amis.pogamut.ut2004multi.communication.worldview.stubs;

import java.util.HashMap;
import java.util.Map;

import cz.cuni.amis.pogamut.multi.communication.worldview.object.ICompositeWorldObject;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.ILocalWorldObject;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.ISharedWorldObject;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.IStaticWorldObject;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.AliveMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.AliveMessageCompositeImpl;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.AliveMessageLocalImpl;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.AliveMessageSharedImpl;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.AliveMessageStaticImpl;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.AutoTraceRay;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.AutoTraceRayCompositeImpl;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.AutoTraceRayLocalImpl;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.AutoTraceRaySharedImpl;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.AutoTraceRayStaticImpl;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BombInfo;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BombInfoCompositeImpl;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BombInfoLocalImpl;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BombInfoSharedImpl;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BombInfoStaticImpl;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ConfigChange;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ConfigChangeCompositeImpl;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ConfigChangeLocalImpl;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ConfigChangeSharedImpl;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ConfigChangeStaticImpl;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.FlagInfo;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.FlagInfoCompositeImpl;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.FlagInfoLocalImpl;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.FlagInfoSharedImpl;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.FlagInfoStaticImpl;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfoCompositeImpl;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfoLocalImpl;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfoSharedImpl;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfoStaticImpl;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.IncomingProjectile;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.IncomingProjectileCompositeImpl;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.IncomingProjectileLocalImpl;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.IncomingProjectileSharedImpl;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.IncomingProjectileStaticImpl;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InitedMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InitedMessageCompositeImpl;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InitedMessageLocalImpl;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InitedMessageSharedImpl;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InitedMessageStaticImpl;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Item;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ItemCompositeImpl;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ItemLocalImpl;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ItemSharedImpl;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ItemStaticImpl;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Mover;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.MoverCompositeImpl;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.MoverLocalImpl;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.MoverSharedImpl;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.MoverStaticImpl;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.MyInventory;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.MyInventoryCompositeImpl;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.MyInventoryLocalImpl;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.MyInventorySharedImpl;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.MyInventoryStaticImpl;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPointCompositeImpl;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPointLocalImpl;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPointSharedImpl;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPointStaticImpl;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.PlayerCompositeImpl;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.PlayerLocalImpl;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.PlayerSharedImpl;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.PlayerStaticImpl;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Self;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.SelfCompositeImpl;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.SelfLocalImpl;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.SelfSharedImpl;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.SelfStaticImpl;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.TeamScore;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.TeamScoreCompositeImpl;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.TeamScoreLocalImpl;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.TeamScoreSharedImpl;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.TeamScoreStaticImpl;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.UT2004CompositeObjectCreator;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Vehicle;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.VehicleCompositeImpl;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.VehicleLocalImpl;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.VehicleSharedImpl;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.VehicleStaticImpl;
import cz.cuni.amis.pogamut.ut2004multi.communication.worldview.objects.TestCompositeViewableObject;
import cz.cuni.amis.pogamut.ut2004multi.communication.worldview.objects.TestCompositeViewableObjectImpl;
import cz.cuni.amis.pogamut.ut2004multi.communication.worldview.objects.TestLocalViewableObject;
import cz.cuni.amis.pogamut.ut2004multi.communication.worldview.objects.TestSharedViewableObject;
import cz.cuni.amis.pogamut.ut2004multi.communication.worldview.objects.TestStaticViewableObject;
import cz.cuni.amis.utils.NullCheck;
import cz.cuni.amis.utils.exception.PogamutException;

/**
 * Creator implementing both testObjects and UT2004 messages
 * @author srlok
 *
 */
public class UT2004TestCompositeObjectCreator {
	
	public static interface ICompositeWorldObjectCreator<T extends ICompositeWorldObject> {
		
		public T create(ILocalWorldObject localPart, ISharedWorldObject sharedPart, IStaticWorldObject staticPart);
		
	}
	
	
		public static class AliveMessageCreator implements ICompositeWorldObjectCreator<AliveMessage> {
			
			@Override
			public AliveMessage
				   create(ILocalWorldObject localPart, ISharedWorldObject sharedPart, IStaticWorldObject staticPart) 
			{
				return 
					new
					AliveMessageCompositeImpl 
					( 
					 (
					  AliveMessageLocalImpl
					 )localPart, 
					 (
					  AliveMessageSharedImpl
					 )sharedPart, 
					 (
					  AliveMessageStaticImpl
					 )staticPart 
					)
				;
			}
		}
	
	
		public static class AutoTraceRayCreator implements ICompositeWorldObjectCreator<AutoTraceRay> {
			
			@Override
			public AutoTraceRay
				   create(ILocalWorldObject localPart, ISharedWorldObject sharedPart, IStaticWorldObject staticPart) 
			{
				return 
					new
					AutoTraceRayCompositeImpl 
					( 
					 (
					  AutoTraceRayLocalImpl
					 )localPart, 
					 (
					  AutoTraceRaySharedImpl
					 )sharedPart, 
					 (
					  AutoTraceRayStaticImpl
					 )staticPart 
					)
				;
			}
		}
	
	
		public static class BombInfoCreator implements ICompositeWorldObjectCreator<BombInfo> {
			
			@Override
			public BombInfo
				   create(ILocalWorldObject localPart, ISharedWorldObject sharedPart, IStaticWorldObject staticPart) 
			{
				return 
					new
					BombInfoCompositeImpl 
					( 
					 (
					  BombInfoLocalImpl
					 )localPart, 
					 (
					  BombInfoSharedImpl
					 )sharedPart, 
					 (
					  BombInfoStaticImpl
					 )staticPart 
					)
				;
			}
		}
	
	
		public static class ConfigChangeCreator implements ICompositeWorldObjectCreator<ConfigChange> {
			
			@Override
			public ConfigChange
				   create(ILocalWorldObject localPart, ISharedWorldObject sharedPart, IStaticWorldObject staticPart) 
			{
				return 
					new
					ConfigChangeCompositeImpl 
					( 
					 (
					  ConfigChangeLocalImpl
					 )localPart, 
					 (
					  ConfigChangeSharedImpl
					 )sharedPart, 
					 (
					  ConfigChangeStaticImpl
					 )staticPart 
					)
				;
			}
		}
	
	
		public static class FlagInfoCreator implements ICompositeWorldObjectCreator<FlagInfo> {
			
			@Override
			public FlagInfo
				   create(ILocalWorldObject localPart, ISharedWorldObject sharedPart, IStaticWorldObject staticPart) 
			{
				return 
					new
					FlagInfoCompositeImpl 
					( 
					 (
					  FlagInfoLocalImpl
					 )localPart, 
					 (
					  FlagInfoSharedImpl
					 )sharedPart, 
					 (
					  FlagInfoStaticImpl
					 )staticPart 
					)
				;
			}
		}
	
	
		public static class GameInfoCreator implements ICompositeWorldObjectCreator<GameInfo> {
			
			@Override
			public GameInfo
				   create(ILocalWorldObject localPart, ISharedWorldObject sharedPart, IStaticWorldObject staticPart) 
			{
				return 
					new
					GameInfoCompositeImpl 
					( 
					 (
					  GameInfoLocalImpl
					 )localPart, 
					 (
					  GameInfoSharedImpl
					 )sharedPart, 
					 (
					  GameInfoStaticImpl
					 )staticPart 
					)
				;
			}
		}
	
	
		public static class IncomingProjectileCreator implements ICompositeWorldObjectCreator<IncomingProjectile> {
			
			@Override
			public IncomingProjectile
				   create(ILocalWorldObject localPart, ISharedWorldObject sharedPart, IStaticWorldObject staticPart) 
			{
				return 
					new
					IncomingProjectileCompositeImpl 
					( 
					 (
					  IncomingProjectileLocalImpl
					 )localPart, 
					 (
					  IncomingProjectileSharedImpl
					 )sharedPart, 
					 (
					  IncomingProjectileStaticImpl
					 )staticPart 
					)
				;
			}
		}
	
	
		public static class InitedMessageCreator implements ICompositeWorldObjectCreator<InitedMessage> {
			
			@Override
			public InitedMessage
				   create(ILocalWorldObject localPart, ISharedWorldObject sharedPart, IStaticWorldObject staticPart) 
			{
				return 
					new
					InitedMessageCompositeImpl 
					( 
					 (
					  InitedMessageLocalImpl
					 )localPart, 
					 (
					  InitedMessageSharedImpl
					 )sharedPart, 
					 (
					  InitedMessageStaticImpl
					 )staticPart 
					)
				;
			}
		}
	
	
		public static class ItemCreator implements ICompositeWorldObjectCreator<Item> {
			
			@Override
			public Item
				   create(ILocalWorldObject localPart, ISharedWorldObject sharedPart, IStaticWorldObject staticPart) 
			{
				return 
					new
					ItemCompositeImpl 
					( 
					 (
					  ItemLocalImpl
					 )localPart, 
					 (
					  ItemSharedImpl
					 )sharedPart, 
					 (
					  ItemStaticImpl
					 )staticPart 
					)
				;
			}
		}
	
	
		public static class MoverCreator implements ICompositeWorldObjectCreator<Mover> {
			
			@Override
			public Mover
				   create(ILocalWorldObject localPart, ISharedWorldObject sharedPart, IStaticWorldObject staticPart) 
			{
				return 
					new
					MoverCompositeImpl 
					( 
					 (
					  MoverLocalImpl
					 )localPart, 
					 (
					  MoverSharedImpl
					 )sharedPart, 
					 (
					  MoverStaticImpl
					 )staticPart 
					)
				;
			}
		}
	
	
		public static class MyInventoryCreator implements ICompositeWorldObjectCreator<MyInventory> {
			
			@Override
			public MyInventory
				   create(ILocalWorldObject localPart, ISharedWorldObject sharedPart, IStaticWorldObject staticPart) 
			{
				return 
					new
					MyInventoryCompositeImpl 
					( 
					 (
					  MyInventoryLocalImpl
					 )localPart, 
					 (
					  MyInventorySharedImpl
					 )sharedPart, 
					 (
					  MyInventoryStaticImpl
					 )staticPart 
					)
				;
			}
		}
	
	
		public static class NavPointCreator implements ICompositeWorldObjectCreator<NavPoint> {
			
			@Override
			public NavPoint
				   create(ILocalWorldObject localPart, ISharedWorldObject sharedPart, IStaticWorldObject staticPart) 
			{
				return 
					new
					NavPointCompositeImpl 
					( 
					 (
					  NavPointLocalImpl
					 )localPart, 
					 (
					  NavPointSharedImpl
					 )sharedPart, 
					 (
					  NavPointStaticImpl
					 )staticPart 
					)
				;
			}
		}
	
	
		public static class PlayerCreator implements ICompositeWorldObjectCreator<Player> {
			
			@Override
			public Player
				   create(ILocalWorldObject localPart, ISharedWorldObject sharedPart, IStaticWorldObject staticPart) 
			{
				return 
					new
					PlayerCompositeImpl 
					( 
					 (
					  PlayerLocalImpl
					 )localPart, 
					 (
					  PlayerSharedImpl
					 )sharedPart, 
					 (
					  PlayerStaticImpl
					 )staticPart 
					)
				;
			}
		}
	
	
		public static class SelfCreator implements ICompositeWorldObjectCreator<Self> {
			
			@Override
			public Self
				   create(ILocalWorldObject localPart, ISharedWorldObject sharedPart, IStaticWorldObject staticPart) 
			{
				return 
					new
					SelfCompositeImpl 
					( 
					 (
					  SelfLocalImpl
					 )localPart, 
					 (
					  SelfSharedImpl
					 )sharedPart, 
					 (
					  SelfStaticImpl
					 )staticPart 
					)
				;
			}
		}
	
	
		public static class TeamScoreCreator implements ICompositeWorldObjectCreator<TeamScore> {
			
			@Override
			public TeamScore
				   create(ILocalWorldObject localPart, ISharedWorldObject sharedPart, IStaticWorldObject staticPart) 
			{
				return 
					new
					TeamScoreCompositeImpl 
					( 
					 (
					  TeamScoreLocalImpl
					 )localPart, 
					 (
					  TeamScoreSharedImpl
					 )sharedPart, 
					 (
					  TeamScoreStaticImpl
					 )staticPart 
					)
				;
			}
		}
	
	
		public static class VehicleCreator implements ICompositeWorldObjectCreator<Vehicle> {
			
			@Override
			public Vehicle
				   create(ILocalWorldObject localPart, ISharedWorldObject sharedPart, IStaticWorldObject staticPart) 
			{
				return 
					new
					VehicleCompositeImpl 
					( 
					 (
					  VehicleLocalImpl
					 )localPart, 
					 (
					  VehicleSharedImpl
					 )sharedPart, 
					 (
					  VehicleStaticImpl
					 )staticPart 
					)
				;
			}
		}
		
		public static class TestCompositeViewableObjectCreator implements ICompositeWorldObjectCreator<TestCompositeViewableObject>
		{

			@Override
			public TestCompositeViewableObject create(
					ILocalWorldObject localPart, ISharedWorldObject sharedPart,
					IStaticWorldObject staticPart)
			{
				return new TestCompositeViewableObjectImpl((TestLocalViewableObject)localPart, (TestSharedViewableObject)sharedPart,
						(TestStaticViewableObject)staticPart );
			}
			
		}
	
	
	private static Map<Class, ICompositeWorldObjectCreator> creators = new HashMap<Class, ICompositeWorldObjectCreator>();
	
	static {
			creators.put(
					TestCompositeViewableObject.class,
					new TestCompositeViewableObjectCreator());
		
			creators.put(
				AliveMessage
				.class, 
				new AliveMessageCreator());
		
			creators.put(
				AutoTraceRay
				.class, 
				new AutoTraceRayCreator());
		
			creators.put(
				BombInfo
				.class, 
				new BombInfoCreator());
		
			creators.put(
				ConfigChange
				.class, 
				new ConfigChangeCreator());
		
			creators.put(
				FlagInfo
				.class, 
				new FlagInfoCreator());
		
			creators.put(
				GameInfo
				.class, 
				new GameInfoCreator());
		
			creators.put(
				IncomingProjectile
				.class, 
				new IncomingProjectileCreator());
		
			creators.put(
				InitedMessage
				.class, 
				new InitedMessageCreator());
		
			creators.put(
				Item
				.class, 
				new ItemCreator());
		
			creators.put(
				Mover
				.class, 
				new MoverCreator());
		
			creators.put(
				MyInventory
				.class, 
				new MyInventoryCreator());
		
			creators.put(
				NavPoint
				.class, 
				new NavPointCreator());
		
			creators.put(
				Player
				.class, 
				new PlayerCreator());
		
			creators.put(
				Self
				.class, 
				new SelfCreator());
		
			creators.put(
				TeamScore
				.class, 
				new TeamScoreCreator());
		
			creators.put(
				Vehicle
				.class, 
				new VehicleCreator());
		
	}
	
	
	public static ICompositeWorldObject createObject(ILocalWorldObject localPart, ISharedWorldObject sharedPart, IStaticWorldObject staticPart)
	{
		NullCheck.check(localPart,  "localPart");
		NullCheck.check(sharedPart, "sharedPart");
		NullCheck.check(staticPart, "staticPart");
		
		NullCheck.check(localPart.getCompositeClass(),  "localPart.getCompositeClass()");
		NullCheck.check(sharedPart.getCompositeClass(), "sharedPart.getCompositeClass()");
		NullCheck.check(staticPart.getCompositeClass(), "staticPart.getCompositeClass()");
		
		if ( localPart.getCompositeClass() != sharedPart.getCompositeClass() || sharedPart.getCompositeClass() != staticPart.getCompositeClass()) {
			throw new PogamutException("CompositeObject cannot be created, because the objectParts belong to different compositeObject classes : "
					+ localPart.getCompositeClass() + "," + sharedPart.getCompositeClass() + "," + staticPart.getCompositeClass() , localPart);
		}
		
		ICompositeWorldObjectCreator creator = creators.get(localPart.getCompositeClass());
		if (creator == null) {
			throw new PogamutException("There is no ICompositeWorldObjectCreator registered for class " + localPart.getCompositeClass(), UT2004CompositeObjectCreator.class);
		} 
		
		return creator.create(localPart, sharedPart, staticPart);
	}
}
