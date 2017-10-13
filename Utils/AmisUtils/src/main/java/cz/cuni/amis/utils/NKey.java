package cz.cuni.amis.utils;

/**
 * N-argument key - used to store multiple keys within one object to provide n-argument key for maps.
 * <p><p>
 * The keys are not commutative! If you need to have commutativity for key's parameters use {@link NKeyCommutative}.
 *
 * @author jgemrot
 */
public class NKey {

	private Object[] keys;
	private int hashCode;

	/**
	 * Should be used by descendants together with init()
	 */
	protected NKey() {

	}

	public NKey(Object... keys) {
		init(keys);
	}

	protected void init(Object[] keys) {
		this.keys = keys;
		HashCode hc = new HashCode();
		for (Object key : keys) {
			hc.add(key);
		}
		this.hashCode = hc.getHash();
	}

	@Override
	public int hashCode() {
		return this.hashCode;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj.hashCode() != hashCode()) return false;
		if (!(obj instanceof NKey)) return false;
		NKey key = (NKey)obj;
		if (getCount() != key.getCount()) return false;
		for (int i = 0; i < getCount(); ++i) {
			if (!SafeEquals.equals(keys[i], key.getKey(i))) return false;
		}
		return true;
	}

	public int getCount() {
		return keys.length;
	}

	public Object getKey(int index) {
		return keys[index];
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("NKey[");
		if (keys.length > 0) {
			sb.append(keys[0]);
			for (int i = 1; i < keys.length; ++i) {
				sb.append(", ");
				sb.append(keys[i]);
			}
		}
		sb.append("]");
		return sb.toString();
	}

}
