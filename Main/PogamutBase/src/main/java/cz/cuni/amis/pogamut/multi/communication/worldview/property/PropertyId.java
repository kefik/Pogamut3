package cz.cuni.amis.pogamut.multi.communication.worldview.property;

import java.io.Serializable;

import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;
import cz.cuni.amis.utils.HashCode;
import cz.cuni.amis.utils.maps.HashMapMap;
import cz.cuni.amis.utils.token.Token;
import cz.cuni.amis.utils.token.Tokens;


/**
 * Unique Id for each SharedProperty in the world.
 * PropertyId is dependent on WorldObjectId. 
 * @author srlok
 *
 */
public class PropertyId implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7196633078273572529L;
	
	
	private Token token;
	private WorldObjectId objectId;
	private int hashCode;
	
	private PropertyId(WorldObjectId objectId, Token token)
	{
		this.objectId = objectId;
		this.token = token;
		this.hashCode = new HashCode().add(objectId).add(token).getHash();
	}
	
	private PropertyId(WorldObjectId objectId, String name)
	{
		this.objectId = objectId;
		this.token = Tokens.get(name);
		this.hashCode = new HashCode().add(objectId).add(token).getHash();
	}
	
	private PropertyId(WorldObjectId objectId, long id)
	{
		this.objectId = objectId;
		this.token = Tokens.get(id);
		this.hashCode = new HashCode().add(objectId).add(token).getHash();
	}
	
	/**
	 * Returns id of the object, this property belongs to.
	 * @return
	 */
	public WorldObjectId getWorldObjectId()
	{
		return this.objectId;
	}
	
	public Token getPropertyToken() {
		return token;
	}
	
	private static HashMapMap<WorldObjectId,String,PropertyId> stringMap = new HashMapMap<WorldObjectId, String, PropertyId>();
	
	// TODO: [srlok] why we have longMap ... "123" != 123 !!!
	private static HashMapMap<WorldObjectId,Long,PropertyId> longMap = new HashMapMap<WorldObjectId, Long, PropertyId>();
	
	@Override
	public boolean equals(Object other)
	{
		if ( other == null) { return false; }
		if ( ! (other instanceof PropertyId))
		{
			return false;
		}
		return ( (token.equals(((PropertyId)other).token)) && 
				( objectId.equals(((PropertyId)other).objectId)));
	}
	
	public String getStringId()
	{
		return objectId.getStringId() + "-" + token.getToken();
	}
	
	@Override
	public String toString()
	{
		return ("PropertyId[" + objectId.toString() + "," + getStringId() + "]");
	}
	
	@Override
	public int hashCode()
	{
		return hashCode;
	}
	
	/**
	 * Returns a new PropertyId object.
	 * Method makes sure that there is only one instance for each (WorldObjectId,String) pair.
	 * @param objectId
	 * @param name
	 * @return
	 */
	public static PropertyId get(WorldObjectId objectId, String name)
	{
		PropertyId value = stringMap.get(objectId, name);
		if ( value != null)
		{
			return value;
		}
		value = new PropertyId(objectId, name);
		stringMap.put(objectId, name, value);
		return value;			
	}
	
	/**
	 * Returns a new PropertyId object.
	 * Method makes sure that there is only one instance for each (WorldObjectId,long) pair.
	 * @param objectId
	 * @param id
	 * @return
	 */
	public static PropertyId get(WorldObjectId objectId, long id)
	{
		PropertyId value = longMap.get(objectId, id);
		if ( value != null)
		{
			return value;
		}
		value = new PropertyId(objectId, id);
		longMap.put(objectId, id, value);
		return value;
		
	}
	
	
}
