package cz.cuni.amis.pogamut.unreal.communication.messages;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;
import cz.cuni.amis.utils.token.Token;
import cz.cuni.amis.utils.token.Tokens;

/**
 * Interface that is returning an unique id of the world object. The id must be unique
 * among all the objects in the world.
 * <p><p>
 * hashmaps/sets.
 * 
 * @author Jimmy
 */
public class UnrealId extends WorldObjectId implements Serializable {
	
	/**
	 * For wrappers...
	 */
	protected UnrealId() {
		super("wrapper");
	}
	
	private UnrealId(String unrealStringId) {
		super(unrealStringId);
	}
	
	private UnrealId(Token id) {
		super(id);
	}
	
	private UnrealId(UnrealId id) {
		super(id.token);
	}
	
	private static Map<String, UnrealId> map = new HashMap<String, UnrealId>();
	
	public static final UnrealId NONE = UnrealId.get(Tokens.NONE_TOKEN.getToken());

	/**
	 * stringId == is the String representation of Unreal ID that is generated & managed by underlying UT2004 server.
	 * <p>
	 * Note that if you run some map from cmdline, NavPoint-s name prefix is generated
	 * case censitive. So on win platform you can run map "CtF-losTFAith", which will
	 * start correct map (file system not case sensitive) and navpoints will be
	 * seen with different prefix name as if you start map "CTF-LostFaith".
	 * </p>
	 *
	 * @param unrealStringId
	 * @return
	 */
	public static UnrealId get(String unrealStringId) {
		UnrealId id = null;
		id = map.get(unrealStringId);
		if (id != null) return id;
		synchronized(map) {
			id = map.get(unrealStringId);
			if (id != null) return id;
			id = new UnrealId(unrealStringId);
			map.put(unrealStringId, id);
			return id;
		}
	}
	
	private Object readResolve() {
		return get(token.getToken());
	}
	
}
