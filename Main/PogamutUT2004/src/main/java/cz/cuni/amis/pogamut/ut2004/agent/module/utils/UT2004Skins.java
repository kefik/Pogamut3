package cz.cuni.amis.pogamut.ut2004.agent.module.utils;

import java.util.concurrent.atomic.AtomicInteger;

import cz.cuni.amis.utils.collections.MyCollections;

public class UT2004Skins {

	public static final String[] SKINS = new String[] { "Bot.BotC", "Ophelia", "Roc", "Axon",
			"HumanFemaleA.EgyptFemaleB", "HumanFemaleA.NightFemaleB", "Aliens.AlienFemaleB", "Jugg.JuggMaleB",
			"Aliens.AlienMaleB", "Barktooth", "Bot.BotD", "Bot.BotA", "Skrilax", "Bot.BotB", "Dominator",
			"Aliens.AlienMaleA", "HumanFemaleA.EgyptFemaleA", "HumanFemaleA.MercFemaleA", "Aliens.AlienFemaleA",
			"HumanFemaleA.MercFemaleB", "HumanFemaleA.NightFemaleA", "HumanMaleA.MercMaleA", "HumanMaleA.MercMaleB",
			"HumanMaleA.MercMaleC", "HumanMaleA.MercMaleD", "HumanMaleA.NightMaleA", "HumanMaleA.NightMaleB",
			"HumanFemaleA.MercFemaleC", "Jugg.JuggFemaleA", "Jugg.JuggFemaleB",

			"Karag", "Kragoth", "Mekkor", "Scarab", "Jugg.JuggMaleA", "ThunderCrash.JakobM" };

	public static final AtomicInteger INSTANCE_COUNT = new AtomicInteger(0);

	/**
	 * Returns 'next skin' from {@link #SKINS} array; we're doing own {@link UT2004Skins#INSTANCE_COUNT}ing here...
	 * @return
	 */
	public static String getSkin() {
		int index = INSTANCE_COUNT.getAndAdd(1);
		return SKINS[index];
	}
	
	public static String getRandomSkin() {
		return MyCollections.getRandom(SKINS);
	}

}
