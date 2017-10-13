package cz.cuni.amis.utils.token;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import cz.cuni.amis.utils.HashCode;
import cz.cuni.amis.utils.exception.PogamutException;

/**
 * Represents a String that can be used inside {@link Map} as keys or {@link Set}s as
 * {@link Token#equals(Object)} has O(1) time complexity which is much better when
 * compared to the O(N) time complexity of {@link String#equals(Object)}.
 * <p><p>
 * Note that you can't instantiate this token, instead use {@link Tokens#get(long)} or {@link Tokens#get(String)}
 * which is THREAD-SAFE.
 * <p><p>
 * The object is suitable for serialization / deserialization, even sending the object between
 * JVM won't broke anything as it has {@link Token#readObject()} implemented the way that
 * ensures that two same {@link Token#name} won't receive different {@link Token#ids}.
 *    
 * @author Jimmy
 */
public class Token implements IToken, Serializable {
	
	/**
	 * Contains the string identifier of the token.
	 */
	private String name;
	
	/**
	 * Contains the 'ids' that belongs to this token.
	 * <p><p>
	 * Transient - no need to serialize it as the {@link Token#readObject()} is retranslating
	 * the {@link Token#name} through {@link Tokens#get(String)} again.
	 */
	transient private long[] ids;
	
	/**
	 * 
	 */
	transient private int hashCode;
	transient private String nameWithIds = null;

	/**
	 * Hidden constructor used by {@link Tokens} in a THREAD-SAFE way.
	 * @param name
	 * @param ids
	 */
	Token(String name, long[] ids) {
		this.name = name;
		this.ids = ids;
		HashCode hc = new HashCode();
		hc.add(ids.length);
		for (long id : ids) {
			hc.add(id);
		}
		this.hashCode = hc.getHash();
	}
	
	/**
	 * Utilized by the de-serialization, whenever we receive a token from different
	 * JVM, we must retranslate its 'name' to correct ids/hashcode.
	 * @return
	 */
	private void readObject(ObjectInputStream ois) {
		try {
			ois.defaultReadObject();
		} catch (IOException e) {
			throw new PogamutException("Could not deserialize Token", e);
		} catch (ClassNotFoundException e) {
			throw new PogamutException("Could not deserialize Token", e);
		}
		Token trueToken = Tokens.get(name);
		this.ids = trueToken.ids;
		this.hashCode = trueToken.hashCode;
	}
	
	@Override
	public int hashCode() {
		return hashCode;
	}
	
	/**
	 * Actually the implementation is as good as it can be, containing 
	 * early-success checking + NPE-proofed.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true; // early-success
		if (obj == null) return false;		
		if (hashCode != obj.hashCode()) return false;
		if (!(obj instanceof IToken)) return false;
		IToken token = (IToken)obj;
		if (ids.length != token.getIds().length) return false;
		for (int i = 0; i < ids.length; ++i) {
			if (ids[i] != token.getIds()[i]) return false;
		}
		return true;
	}

	/**
	 * Returns an underlying String identifier (might be useful when storing
	 * human-readable names).
	 */
	public String getToken() {
		return name;
	}

	/**
	 * Returns unique ID of the token. No two different instances has the same ids.
	 */
	public long[] getIds() {
		return ids;
	}
	
	/**
	 * Returns the name with ids as suffix enclosed inside '[', ']' brackets.
	 * @return
	 */
	public String getNameWithIds() {
		if (nameWithIds != null) return nameWithIds ;
		StringBuffer sb = new StringBuffer();
		sb.append(name);
		sb.append("[");
		sb.append(ids[0]);
		for (int i = 1; i < ids.length; ++i) {
			sb.append(",");
			sb.append(ids[i]);
		}
		sb.append("]");
		nameWithIds = sb.toString();
		return nameWithIds;
	}
	
	/**
	 * Returns {@link Token#getNameWithIds()}.
	 */
	@Override
	public String toString() {
		return getNameWithIds();
	}

}
