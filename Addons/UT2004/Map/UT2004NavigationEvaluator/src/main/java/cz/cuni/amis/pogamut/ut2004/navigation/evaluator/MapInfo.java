/*
 * Copyright (C) 2014 AMIS research group, Faculty of Mathematics and Physics, Charles University in Prague, Czech Republic
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package cz.cuni.amis.pogamut.ut2004.navigation.evaluator;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Helper class containing names of all maps for supported modes. Useful for
 * batch creation of various tasks.
 *
 * @author Bogo
 */
public class MapInfo {

    /**
     * Get correct game type for given map.
     *
     * @param mapName Name of the map.
     * @return Game type of the map.
     */
    static String getGameType(String mapName) {
        GameType gameType = GameType.BotDeathMatch;
        if (mapName.startsWith("CTF")) {
            gameType = GameType.BotCTFGame;
        } else if (mapName.startsWith("DOM")) {
            gameType = GameType.BotDoubleDomination;
        } else if (mapName.startsWith("BR")) {
            gameType = GameType.BotBombingRun;
        }
        return gameType.name();
    }

    /**
     * Enumeration of supported game types.
     */
    public enum GameType {

        BotDoubleDomination, BotDeathMatch, BotCTFGame, BotBombingRun
    }

    /**
     * All CTF maps.
     */
    public static final String[] CTF_MAPS = new String[]{
        "CTF-1on1-Joust",
        "CTF-AbsoluteZero",
        "CTF-Avaris",
        "CTF-BridgeOfFate",
        "CTF-Chrome",
        "CTF-Citadel",
        "CTF-Colossus",
        "CTF-December",
        "CTF-DE-ElecFields",
        "CTF-DoubleDammage",
        "CTF-Face3",
        "CTF-FaceClassic",
        "CTF-Geothermal",
        "CTF-Grassyknoll",
        "CTF-Grendelkeep",
        "CTF-January",
        "CTF-Lostfaith",
        "CTF-Magma",
        "CTF-Maul",
        "CTF-MoonDragon",
        "CTF-Orbital2",
        "CTF-Smote",
        "CTF-TwinTombs"
    };

    /**
     * All DM maps.
     */
    public static String[] DM_MAPS = new String[]{
        "DM-1on1-Albatross",
        "DM-1on1-Crash",
        "DM-1on1-Desolation",
        "DM-1on1-Idoma",
        "DM-1on1-Irondust",
        "DM-1on1-Mixer",
        "DM-1on1-Roughinery",
        "DM-1on1-Serpentine",
        "DM-1on1-Spirit",
        "DM-1on1-Squader",
        "DM-1on1-Trite",
        "DM-Antalus",
        "DM-Asbestos",
        "DM-Compressed",
        "DM-Corrugation",
        "DM-Curse4",
        "DM-Deck17",
        "DM-DE-Grendelkeep",
        "DM-DE-Ironic",
        "DM-DE-Osiris2",
        "DM-DesertIsle",
        "DM-Flux2",
        "DM-Gael",
        "DM-Gestalt",
        "DM-Goliath",
        "DM-HyperBlast2",
        "DM-Icetomb",
        "DM-Inferno",
        "DM-Injector",
        "DM-Insidious",
        "DM-IronDeity",
        "DM-Junkyard",
        "DM-Leviathan",
        "DM-Metallurgy",
        "DM-Morpheus3",
        "DM-Oceanic",
        "DM-Phobos2",
        "DM-Plunge",
        "DM-Rankin",
        "DM-Rrajigar",
        "DM-Rustatorium",
        "DM-Sulphur",
        "DM-TokaraForest",
        "DM-TrainingDay"
    };

    /**
     * All BR maps.
     */
    public static final String[] BR_MAPS = new String[]{
        "BR-Anubis", "BR-Bifrost", "BR-BridgeOfFate", "BR-Canyon", "BR-Colossus", "BR-DE-ElecFields", "BR-Disclosure", "BR-IceFields", "BR-Serenity", "BR-Skyline", "BR-Slaughterhouse", "BR-TwinTombs"
    };

    /**
     * All DOM maps.
     */
    public static final String[] DOM_MAPS = new String[]{
        "DOM-Access", "DOM-Aswan", "DOM-Atlantis", "DOM-Conduit", "DOM-Core", "DOM-Junkyard", "DOM-OutRigger", "DOM-Renascent", "DOM-Ruination", "DOM-ScorchedEarth", "DOM-SepukkuGorge", "DOM-Suntemple"
    };

    /**
     * Gets all DM maps.
     *
     * @return All DM map names.
     */
    public static List<String> getAllDMMaps() {
        return Arrays.asList(DM_MAPS);
    }

    /**
     * Gets all CTF maps.
     *
     * @return All CTF map names.
     */
    public static List<String> getAllCTFMaps() {
        return Arrays.asList(CTF_MAPS);
    }

    /**
     * Gets all BR maps.
     *
     * @return All BR map names.
     */
    public static List<String> getAllBRMaps() {
        return Arrays.asList(BR_MAPS);
    }

    /**
     * Gets all DOM maps.
     *
     * @return All DOM map names.
     */
    public static List<String> getAllDOMMaps() {
        return Arrays.asList(DOM_MAPS);
    }

    /**
     * Gets all known maps.
     *
     * @return All known map names.
     */
    public static List<String> getAllMaps() {
        LinkedList<String> all = new LinkedList<String>(getAllDMMaps());
        all.addAll(getAllCTFMaps());
        all.addAll(getAllBRMaps());
        all.addAll(getAllDOMMaps());
        return all;
    }
}
