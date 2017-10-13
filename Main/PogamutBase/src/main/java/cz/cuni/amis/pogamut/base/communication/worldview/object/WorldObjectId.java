package cz.cuni.amis.pogamut.base.communication.worldview.object;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import cz.cuni.amis.utils.exception.PogamutException;
import cz.cuni.amis.utils.token.Token;
import cz.cuni.amis.utils.token.Tokens;

/**
 * Interface that is returning an unique id of the world object. The id must be unique
 * among all the objects in the world.
 * <p><p>
 * Every implementations MUST implement equals() and hashCode() correctly as we will use this inside
 * hashmaps/sets.
 * 
 * @author Jimmy
 */
public class WorldObjectId implements Serializable {
	
	/**
	 * Unique representation of this id, contains ints that uniquely identifies it.
	 */
	protected Token token;
	
	/**
	 * Whether the id wolds 'long' inside {@link WorldObjectId#token}.
	 */
	protected boolean isLong;
	
	/**
	 * Whether the id wolds 'double' inside {@link WorldObjectId#token}.
	 */
	protected boolean isDouble;
	
	/**
	 * Instantiates a new object with id of 'name'.
	 * @param name
	 */
	protected WorldObjectId(String name) {
		this.token = Tokens.get(name);
		isLong = false;
		isDouble = false;
	}
	
	/**
	 * Instantiates a new object with id of 'id'.
	 * @param id
	 */
	protected WorldObjectId(long id) {
		this.token = Tokens.get(id);
		isLong = true;
		isDouble = false;
	}
	
	/**
	 * Instantiates a new object with id of 'id'.
	 * @param id
	 */
	protected WorldObjectId(double id) {
		this.token = Tokens.get(id);
		isLong = false;
		isDouble = true;
	}
	
	protected WorldObjectId(Token token) {
		this.token = token;
	}
	
	@Override
	public int hashCode() {
		return token.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == this) return true; // early-success
		if (o == null) return false;
		if (!(o instanceof WorldObjectId)) return false;
		return token.equals(((WorldObjectId)o).token);
	}

	/**
	 * Always returns a string representation of the ID.
	 * @return
	 */
	public String getStringId() {
		return token.getToken();
	}
	
	/**
	 * Whether the id holds a numeric value (i.e., it is either {@link WorldObjectId#isLongId()} or {@link WorldObjectId#isDoubleId()}).
	 * @return
	 */
	public boolean isNumericId() {
		return isLong || isDouble;
	}
	
	/**
	 * Whether the id is a long one.
	 * @return
	 */
	public boolean isLongId() {
		return isLong;
	}
	
	/**
	 * Whether the is is a double one.
	 * @return
	 */
	public boolean isDoubleId() {
		return isDouble;
	}	
	
	/**
	 * Returns id as 'long', only iff {@link WorldObjectId#isLongId()}, otherwise it raises a {@link PogamutException}.
	 * 
	 * @return long
	 */
	public long getLongId() {
        if (!isLong) {
        	try {
        		long result = Long.parseLong(token.getToken());
        		isLong = true;
        		return result;
        	} catch (Exception e) {
        		throw new PogamutException("Id does not hold long value! Id: " + this, this);
        	}
        }
        return Long.parseLong(token.getToken());
	}
	
	/**
	 * Returns id as 'double', only iff {@link WorldObjectId#isDoubleId()}, otherwise it raises a {@link PogamutException}.
	 * 
	 * @return long
	 */
	public double getDoubleId() {
        if (!isDouble) {
        	try {
        		double result = Double.parseDouble(token.getToken());
        		isDouble = true;
        		return result;
        	} catch (Exception e) {
        		throw new PogamutException("Id does not hold double value! Id: " + this, this);
        	}        	
        }
        return Double.parseDouble(token.getToken());
	}
	
	/**
	 * Returns string representation of the id, format: WorldObjectId[id] 
	 * 
	 * @return string representation of this object
	 */
	@Override
	public String toString() {
		return "WorldObjectId["+getStringId()+"]";
	}

	/**
	 * Map that serves for translation of WorldObjectIds...
	 */
	private static Map<String, WorldObjectId> map  = new HashMap<String, WorldObjectId>();
	
	/**
	 * Returns shared instance of the {@link WorldObjectId} for 'name'. If no {@link WorldObjectId} exists for 'name',
	 * new one is created.
	 * <p><p>
	 * THREAD-SAFE!
	 * 
	 * @param name
	 * @return
	 */
	public static WorldObjectId get(String name) {
		if (name == null) throw new PogamutException("Could not return a WorldObjectId for 'null'!", WorldObjectId.class);
		WorldObjectId id = null;
		id = map.get(name);
		if (id != null) return id;
		synchronized(map) {
			id = map.get(name);
			if (id != null) return id;
			id = new WorldObjectId(name);
			map.put(name, id);
		}
		return id;
	}
	
	/**
	 * Returns shared instance of the {@link WorldObjectId} for 'objId'. If no {@link WorldObjectId} exists for 'objId',
	 * new one is created.
	 * <p><p>
	 * THREAD-SAFE!
	 * 
	 * @param name
	 * @return
	 */
	public static WorldObjectId get(long objId) {
		return get(String.valueOf(objId));
	}
	
	/**
	 * Returns shared instance of the {@link WorldObjectId} for 'objId'. If no {@link WorldObjectId} exists for 'objId',
	 * new one is created.
	 * <p><p>
	 * THREAD-SAFE!
	 * 
	 * @param name
	 * @return
	 */
	public static WorldObjectId get(double objId) {
		return get(String.valueOf(objId));
	}
	
}
