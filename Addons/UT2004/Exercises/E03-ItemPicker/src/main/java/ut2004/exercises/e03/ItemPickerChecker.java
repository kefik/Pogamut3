package ut2004.exercises.e03;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;

import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEventListener;
import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.drawing.UT2004ServerProvider;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.EndMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Item;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ItemListEnd;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import cz.cuni.amis.pogamut.ut2004.server.impl.UT2004Server;

/**
 * This class is checking, whether you have picked all items you need,
 * @author Jimmy
 */
public class ItemPickerChecker {
	
	public static final int BOTS_COUNT = 3; 
	
	private static Set<UnrealId> picked = new HashSet<UnrealId>();
	
	private static Map<UnrealId, Item> itemsToPick = new HashMap<UnrealId, Item>();
	
	private static Set<UnrealId> bots = new HashSet<UnrealId>();
	
	private static long startTime = -1;
	
	private static long endTime = -1;
	
	private static UT2004ServerProvider serverProvider;
	
	private static UT2004Server getServer() {
		if (serverProvider == null) {
			serverProvider = new UT2004ServerProvider(new LogCategory("IPC-UT2004ServerPriveder"));
		}
		serverProvider.getServer(); // initialization
		serverProvider.getServer().getLog().setLevel(Level.INFO);
		return serverProvider.getServer();
	}
	
	private static void init() {
		getServer().getWorldView().addEventListener(EndMessage.class, new IWorldEventListener<EndMessage>() {
			@Override
			public void notify(EndMessage event) {
				tickListener(event);
				
			}
		});
	}
	
	private static int state = 0;
	
	private static void tickListener(EndMessage event) {
		switch (state) {
		case 0: {
			++state;
		} break;
		case 1:
		case 2: {
			++state;

		} break;
		case 3: {
			itemsReceived(null);
			++state;
		}
		}
	}
	
	private static void itemsReceived(ItemListEnd event) {
		for (Entry<UnrealId, Item> entry : (Set<Entry<UnrealId, Item>>)(Set)getServer().getWorldView().getAll(Item.class).entrySet()) {
			if (entry.getValue().isDropped()) continue;
			if (entry.getValue().getType().getCategory() == ItemType.Category.WEAPON) itemsToPick.put(entry.getKey(), entry.getValue());
			if (entry.getValue().getType().getCategory() == ItemType.Category.SHIELD) itemsToPick.put(entry.getKey(), entry.getValue());
			if (entry.getValue().getType().getCategory() == ItemType.Category.ARMOR) itemsToPick.put(entry.getKey(), entry.getValue());
			
		}
		
		for (Item item : itemsToPick.values()) {
			getServer().getLog().warning("ITEM TO PICK: " + item);			
		}
		getServer().getLog().warning("+-- total: " + itemsToPick.values().size());
	}
	
	public synchronized static void register(UnrealId botId) {
		if (bots.contains(botId)) return;
		bots.add(botId);
		if (bots.size() == BOTS_COUNT) {
			startTime = System.currentTimeMillis();
			new Thread(new Runnable() { public void run() { init(); } }).start();
		}
	}
	
	public static boolean isRunning() {
		return startTime > 0 && endTime < 0;
	}
	
	public static boolean isVictory() {
		if (endTime < 0) return false;
		
		getServer().getLog().warning("YOU HAVE WON! Time: " + (endTime - startTime) + "ms");
		return false;
	}
	
	public synchronized static boolean itemPicked(UnrealId who, Item what) {
		if (!isRunning()) {
			System.out.println("ItemPickerChecker.itemPicked(): NOT RUNNING!!!");
			return false;
		}
		
		Player bot = getServer().getWorldView().getAll(Player.class).get(who);
		if (bot == null) {
			getServer().getLog().severe("YOUR BOT IS NOT PRESENT IN THE UT2004Server");
			return false;			
		}
		
		if (bot.getLocation().getDistance(what.getLocation()) > 200) {
			getServer().getLog().warning("YOU ARE TOO FAR FROM THE ITEM: " + bot.getLocation().getDistance(what.getLocation()) + " > 200 | try to wait a cycle...");
			return false;
		}
		
		if (!itemsToPick.keySet().contains(what.getId())) {
			getServer().getLog().warning("THIS ITEM IS RUBBISH!");
			return false;
		}
		
		if (picked.contains(what.getId())) {
			getServer().getLog().warning("YOU HAVE ALREADY PICKED THIS ITEM: " + what);
			return false;
		}
		
		picked.add(what.getId());
		
		getServer().getLog().warning("ITEM: " + what.getId() + " PICKED!");
		
		if (picked.size() == itemsToPick.size()) {
			endTime = System.currentTimeMillis();
			getServer().getLog().warning("YOU HAVE WON! Time: " + (endTime - startTime) + "ms");
		} else {
			getServer().getLog().warning("+-- " + (itemsToPick.size() - picked.size()) + " to go!");
			if (itemsToPick.size() - picked.size() < 3) {
				for (Item item : itemsToPick.values()) {
					if (!picked.contains(item.getId())) {
						getServer().getLog().warning("  +-- " + item);
					}
				}
			}
		}
		
		return true;
	}
	

}
