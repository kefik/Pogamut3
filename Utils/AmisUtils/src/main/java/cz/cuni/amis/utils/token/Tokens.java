package cz.cuni.amis.utils.token;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides a way for string-to-long translation for quick handling of "String" keys inside
 * maps or sets. The method {@link Tokens#get(long)} or {@link Tokens#get(String)} will
 * return you an instance of {@link Token} that has {@link Token#hashCode()}
 * and {@link Token#equals(Object)} correctly specified to be quick (i.e., O(1) time complexity
 * agains String's O(n) time complexity).
 * <p><p>
 * Also notice that {@link Token} is using an array of 'long' ({@link Token#getIds()}) to identify
 * itself, i.e., you can never run out of ids for Strings (meaning you may truly allocate
 * an arbitrary number of tokens via get methods ... as much as JVM heap allows you to).
 * <p><p>
 * THREAD-SAFE! You do not need to worry that two threads will request the same token
 * and be provided with different {@link Token} instances.
 * 
 * @author Jimmy
 */
public class Tokens {
	
	/**
	 * Last unique identifier. 
	 */
	private volatile static long[] lastIds = new long[0];
	
	/**
	 * Memory leak ... sticking many "tokens" will result in memory leak as it is never GC()ed from the map.
	 * <p><p>
	 * TODO: we need better {@link Map} implementation for {@link Tokens#tokenMap} is needed, we need
	 * to GC the whole entry whenever a value disappears.
	 * 
	 */
	private static Map<String, WeakReference<Token>> tokenMap = new HashMap<String, WeakReference<Token>>();
	
	/**
	 * Token representing "null" / "empty token".
	 */
	public static final Token NULL_TOKEN = get("null");
	
	/**
	 * Token representing "none" information.
	 */
	public static final Token NONE_TOKEN = get("@@NONE@@");
	
	/**
	 * Used for testing! Reinitialize the whole singleton. So future calls to
	 * {@link Tokens#get(long)} or {@link Tokens#get(String)} starts to give different 
	 * results!
	 */
	static void restart() {
		lastIds = new long[0];
		tokenMap = new HashMap<String, WeakReference<Token>>();
	}
	
	/**
	 * Returns {@link Token} with name "tokenStr", do not use {@link Tokens#NULL_TOKEN} string "null" as param 'tokenStr' that represents null tokens 
	 * (tokens without names).
	 * <p><p>
	 * Notice that even though the method is not synchronized, the creation of {@link Token}
	 * is.
	 * <p><p>
	 * THREAD-SAFE!
	 * 
	 * @param tokenStr
	 * @return
	 */
	public static Token get(String tokenStr) {
		if (tokenStr == null) return NULL_TOKEN;
		WeakReference<Token> refToken = tokenMap.get(tokenStr);
		Token token = null;
		if (refToken != null) {
			token = refToken.get();
		}
		if (token == null) {
			// we have found out that we need to create a new token
			// now we have to be synchronized
			synchronized(tokenMap) {
				// as get-check-lock does not work, perform another check
				refToken = tokenMap.get(tokenStr);
				if (refToken != null) {
					token = refToken.get(); 
				}
				if (token != null) {
					// the token exists (racing conditions, somebody was quiker than us)
					return token;
				}
				// the token is still null - create a new one
				token = newToken(tokenStr);
				tokenMap.put(tokenStr, new WeakReference<Token>(token));
			}
		}
		return token;
	}
	
	/**
	 * Returns {@link Token} of a specified 'id'. Note that 'id' is actual translated
	 * into {@link String} first, meaning that (long)1 and (String)"1" is the same token.
	 * <p><p>
	 * Notice that even though the method is not synchronized, the creation of {@link Token}
	 * is.
	 * <p><p>
	 * THREAD-SAFE!
	 * 
	 * @param tokenStr
	 * @return
	 */
	public static Token get(long id) {
		return get(String.valueOf(id));
	}
	
	/**
	 * Returns {@link Token} of a specified 'id'. Note that 'id' is actual translated
	 * into {@link String} first, meaning that (double)1 and (String)"1" is the same token (but watch out for actual translation of double to string!).
	 * <p><p>
	 * Notice that even though the method is not synchronized, the creation of {@link Token}
	 * is.
	 * <p><p>
	 * THREAD-SAFE!
	 * 
	 * @param tokenStr
	 * @return
	 */
	public static Token get(double id) {
		return get(String.valueOf(id));
	}
	
	/**
	 * Removes 'token' from {@link Tokens#tokenMap}.
	 * @param token
	 */
	public static void destroy(Token token) {
		synchronized(tokenMap) {
			tokenMap.remove(token.getToken());
		}
	}

	/**
	 * Factory-method, returns new {@link Token} instance containing unique ids. 
	 * <p><p>
	 * THREAD-UNSAFE! Must be called only from synchronized statements/methods.
	 * @param tokenStr
	 * @return
	 */
	private static Token newToken(String tokenStr) {
		return new Token(tokenStr, nextLastIds());
	}

	/**
	 * Issues another unique ids.
	 * 
	 * THREAD-UNSAFE! Must be called only from synchronized statments/methods.
	 * 
	 * @return array holding next unique identifier
	 */
	private static long[] nextLastIds() {
		for (int i = 0; i < lastIds.length; ++i) {
			if (lastIds[i] == Long.MAX_VALUE) {
				lastIds[i] = Long.MIN_VALUE;
				break;
			}
			if (lastIds[i] == -1) {
				lastIds[i] = 0;
				continue;
			}
			lastIds[i] += 1;
			return lastIds;
		}
		lastIds = new long[lastIds.length+1];
		Arrays.fill(lastIds, 0);
		return lastIds;
	}

}
