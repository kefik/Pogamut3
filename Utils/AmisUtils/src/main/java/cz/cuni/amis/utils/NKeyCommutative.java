package cz.cuni.amis.utils;

import java.util.Arrays;
import java.util.Comparator;

/**
 * N-Key used for maps.
 * <p><p>
 * Remember that all passed "commutatives" are flatted (but within the commutatives parts)!
 * <p><p>
 * NKeyCommutative can be equal to NKey and vice versa!
 *
 * @author jgemrot
 *
 */
public class NKeyCommutative extends NKey {

	@SuppressWarnings("unchecked")
	private Comparator HASH_CODE_COMPARATOR = new Comparator() {

		@Override
		public int compare(Object arg0, Object arg1){
			if (arg0 == null) {
				if (arg1 == null) return 0;
				return -1;
			}
			if (arg1 == null) return 1;
			return arg0.hashCode() - arg1.hashCode();
		}

	};

	public NKeyCommutative(Object[]... commutatives) {
		if (commutatives.length == 1) {
			Arrays.sort(commutatives[0], HASH_CODE_COMPARATOR);
			init(commutatives[0]);
		} else {
			int length = 0;
			for (Object[] c : commutatives) {
				Arrays.sort(c, HASH_CODE_COMPARATOR);
				length += c.length;
			}
			Object[] keys = new Object[length];
			int index = 0;
			for (Object[] c : commutatives) {
				System.arraycopy(c, 0, keys, index, c.length);
				index += c.length;
			}
			init(keys);
		}
	}

}
