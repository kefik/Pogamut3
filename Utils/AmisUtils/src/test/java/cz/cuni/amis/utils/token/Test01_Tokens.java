package cz.cuni.amis.utils.token;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;

import cz.cuni.amis.tests.BaseTest;

public class Test01_Tokens extends BaseTest {

	@Test
	public void test01_Equals() {
		Token token1 = Tokens.get("ahoj");
		Token token2 = Tokens.get("cau");
		Token token3 = Tokens.get("ahoj");
		Token token4_1 = Tokens.get("4");
		Token token5_1 = Tokens.get("5");
		Token token4_2 = Tokens.get(4);
		Token token5_2 = Tokens.get(5);
		
		Assert.assertTrue("token1 should be equal to itself",      token1.equals(token1));
		Assert.assertTrue("token1 should not be equal to null",   !token1.equals(null));
		Assert.assertTrue("token2 should not be equal to token1", !token1.equals(token2));
		Assert.assertTrue("token2 should not be equal to token3", !token2.equals(token3));
		Assert.assertTrue("token1 should be equal to token3",      token1.equals(token3));
		Assert.assertTrue("token4_1 should be equal to token4_2",  token4_1.equals(token4_2));
		Assert.assertTrue("token5_1 should be equal to token5_2",  token5_1.equals(token5_2));
		Assert.assertTrue("token4_1 should not be equal to token5_1",  !token4_1.equals(token5_1));
		Assert.assertTrue("token4_1 should not be equal to token5_2",  !token4_1.equals(token5_2));
		Assert.assertTrue("token5_1 should not be equal to token4_1",  !token5_1.equals(token4_1));
		Assert.assertTrue("token5_1 should not be equal to token4_2",  !token5_1.equals(token4_2));
		Assert.assertTrue("token4_1 should not be equal to token1",  !token4_1.equals(token1));
		Assert.assertTrue("token5_1 should not beequal to token2",  !token5_1.equals(token2));
		
		testOk();		
	}
	
	@Test
	public void test02_TokensInHashSet() {
		Token token1 = Tokens.get("ahoj");
		Token token2 = Tokens.get("cau");
		Token token3 = Tokens.get("ahoj");
		Token token4_1 = Tokens.get("4");
		Token token5_1 = Tokens.get("5");
		Token token4_2 = Tokens.get(4);
		Token token5_2 = Tokens.get(5);
		
		Set<Token> setOfTokens = new HashSet<Token>();
		setOfTokens.add(token1);
		setOfTokens.add(token2);
		setOfTokens.add(token4_1);
		setOfTokens.add(token5_1);
		
		Assert.assertTrue("Set should contain token1", setOfTokens.contains(token1));
		Assert.assertTrue("Set should contain token2", setOfTokens.contains(token2));
		Assert.assertTrue("Set should contain token3", setOfTokens.contains(token3));
		Assert.assertTrue("Set should contain token4_1", setOfTokens.contains(token4_1));
		Assert.assertTrue("Set should contain token4_2", setOfTokens.contains(token4_2));
		Assert.assertTrue("Set should contain token5_1", setOfTokens.contains(token5_1));
		Assert.assertTrue("Set should contain token5_2", setOfTokens.contains(token5_2));
		
		setOfTokens.remove(token3);
		setOfTokens.remove(token4_2);
		
		Assert.assertTrue("Set should NOT contain token1", !setOfTokens.contains(token1));
		Assert.assertTrue("Set should still contain token2", setOfTokens.contains(token2));
		Assert.assertTrue("Set should NOT contain token4_1", !setOfTokens.contains(token4_1));
		Assert.assertTrue("Set should NOT contain token4_2", !setOfTokens.contains(token4_2));
		Assert.assertTrue("Set should still contain token5_1", setOfTokens.contains(token5_1));
		Assert.assertTrue("Set should still contain token5_2", setOfTokens.contains(token5_2));
		
		testOk();
	}
	
	@Test
	public void test03_TokensInHashMapAsKeys() {
		Token token1 = Tokens.get("ahoj");
		Token token2 = Tokens.get("cau");
		Token token3 = Tokens.get("ahoj");
		Token token4_1 = Tokens.get("4");
		Token token5_1 = Tokens.get("5");
		Token token4_2 = Tokens.get(4);
		Token token5_2 = Tokens.get(5);
		
		Map<Token, Integer> map = new HashMap<Token, Integer>();
		
		map.put(token1, 1);
		map.put(token2, 2);
		map.put(token4_1, 3);
		map.put(token5_1, 4);
		
		Assert.assertTrue("token1 should be inside the map as a key", map.containsKey(token1));
		Assert.assertTrue("token2 should be inside the map as a key", map.containsKey(token2));
		Assert.assertTrue("token3 should be inside the map as a key", map.containsKey(token3));
		Assert.assertTrue("token4_1 should be inside the map as a key", map.containsKey(token4_1));
		Assert.assertTrue("token4_2 should be inside the map as a key", map.containsKey(token4_2));
		Assert.assertTrue("token5_1 should be inside the map as a key", map.containsKey(token5_1));
		Assert.assertTrue("token5_2 should be inside the map as a key", map.containsKey(token5_2));
		
		Assert.assertTrue("token1 should give the same number as token3", map.get(token1) == map.get(token3));
		Assert.assertTrue("token1 should give different number than token2", map.get(token1) != map.get(token2));
		Assert.assertTrue("token1 should give different number than token4_1", map.get(token1) != map.get(token4_1));
		Assert.assertTrue("token1 should give different number than token5_2", map.get(token1) != map.get(token5_2));
		
		Assert.assertTrue("token4_1 should give the same number as token4_2", map.get(token4_1) == map.get(token4_2));
		Assert.assertTrue("token5_1 should give the same number as token5_2", map.get(token5_1) == map.get(token5_2));
		
		Assert.assertTrue("token4_1 should give different number than token5_1", map.get(token4_1) != map.get(token5_1));
		Assert.assertTrue("token4_1 should give different number than token5_2", map.get(token4_1) != map.get(token5_2));
		
		Assert.assertTrue("token5_1 should give different number than token4_1", map.get(token5_1) != map.get(token4_1));
		Assert.assertTrue("token5_1 should give different number than token4_2", map.get(token5_1) != map.get(token4_2));		
		
		testOk();
	}
	
	public static void main(String[] args) {
		Test01_Tokens test = new Test01_Tokens();
		test.test01_Equals();
		test.test02_TokensInHashSet();
		test.test03_TokensInHashMapAsKeys();
	}
	
}
