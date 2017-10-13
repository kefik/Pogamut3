package cz.cuni.amis.utils.maps;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

/**
 * @author srlok
 */
public class Test01_HashTriMap {

	@Test
	public void testCreateGet() {
		HashTriMap<Integer,String,String,String> testMap = new HashTriMap<Integer,String,String,String>();
		if ( !testMap.isEmpty() )
		{
			fail("Not empty after start");
		}
		Object pointer = testMap.get(10,"Ahoj");
		if (pointer == null)
		{
			fail("Null get");
		}
		testMap.put(9, "Ahoj","Klic3","Hodnota");
		String testVal = testMap.get(9, "Ahoj", "Klic3");
		if (testVal != "Hodnota")
		{
			fail("Selhalo get");
		}
		testMap.put(9,"Ahoj","Klic3","Hodnota2");
		if (testVal == testMap.get(9, "Ahoj", "Klic3"))
		{
			fail("selhalo put");
		}		
		
		System.out.println("---/// TEST OK ///---");
	}
	
	@Test
	public void testShallowCopy() {
		HashTriMap<String,String,String,StringBuffer> map1 = new HashTriMap<String, String, String, StringBuffer>();
		StringBuffer b1 = new StringBuffer("value1");
		StringBuffer b2 = new StringBuffer("value2");
		StringBuffer b3 = new StringBuffer("value3");
		StringBuffer b4 = new StringBuffer("value4");
		map1.put("l1key1","l2key1","l3key1",b1);
		map1.put("l1key1","l2key1","l3key2",b2);
		map1.put("l1key1","l2key2","l3key3",b3);
		map1.put("l1key2","l2key1","l3key1",b4);
		
		System.out.println("---/// TEST OK ///---");
	}
	
	private boolean sCompare(StringBuffer buffer, CharSequence string)
	{
		if ( buffer == null) { return false; };
		return (buffer.toString().equals(string));
	}
}
