package cz.cuni.amis.pogamut.ut2004.utils;

import java.util.ArrayList;

/**
 * This is enum for edge flag between navpoints. <br>
 * Each navpoint has a list of neighbour navpoints (getOutgoingEdges()), each 
 * edge (NavPointNeighbourLink instance) has also property flag (getFlag())
 * <br>
 * Here is the list of respective bits according to http://wiki.beyondunreal.com/wiki/ReachSpec
 * <p><p>
 * Use getFlags() to obtain the list of enums from a particular integer.
 */
public enum LinkFlag {

	WALK(1), // walking required
	FLY(2),  // flying required
	SWIM(4), // swimming required
	JUMP(8), // jumping required
	DOOR(16), 
	SPECIAL(32), 
	LADDER(64), 
	PROSCRIBED(128), 
	FORCED(256), 
	PLAYERONLY(512);

	private int flag = 0;

	private LinkFlag(int flag) {
		this.flag = flag;
	}

	/**
	 * Returns integer of the flag.
	 */
	public int get() {
		return flag;
	}

	/**
	 * @param flag
	 * @return whether the flag is raised.
	 */
	public boolean isSet(int flag) {
		return (this.flag & flag) != 0;
	}

	/**
	 * Returns list of flags, which are raised.
	 * 
	 * @param flags
	 * @return list of raised flags
	 */
	public static ArrayList<LinkFlag> getFlags(int flags) {
		ArrayList<LinkFlag> list = new ArrayList<LinkFlag>();

		for (LinkFlag flag : LinkFlag.values()) {
			if ((flags & flag.get()) != 0) {
				list.add(flag);
			}
		}
		return list;
	}

	public static String getFlagsString(int flags) {
		ArrayList<LinkFlag> allFlags = getFlags(flags);
		String str = "";
		if (allFlags.size() == 0)
			return "none";
		str = allFlags.get(0).name();
		allFlags.remove(0);
		for (LinkFlag flag : allFlags) {
			str = ", " + flag.name();
		}
		return str;
	}
}