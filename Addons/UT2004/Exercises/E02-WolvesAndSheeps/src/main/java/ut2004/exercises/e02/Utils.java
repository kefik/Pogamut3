package ut2004.exercises.e02;

import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GlobalChat;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import cz.cuni.amis.pogamut.ut2004.utils.UnrealUtils;

public class Utils {
	
	public static final int CATCH_DISTANCE = 150;
	
	public static boolean gameRunning = false;
	
	public static void handleMessage(GlobalChat msg) {
		if (msg.getText().toLowerCase().contains("restart")) gameRunning = true;
		if (msg.getText().toLowerCase().contains("start")) gameRunning = true;
		if (msg.getText().toLowerCase().contains("stop")) gameRunning = false;
	}
	
	public static boolean isSheep(Player player) {
		return player.getName().toLowerCase().contains("sheepbot");
	}
	
	public static boolean isWolf(Player player) {
		return !isSheep(player);
	}

	
}
