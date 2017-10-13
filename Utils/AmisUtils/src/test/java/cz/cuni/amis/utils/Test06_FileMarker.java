package cz.cuni.amis.utils;

import org.junit.Test;

import cz.cuni.amis.tests.BaseTest;

public class Test06_FileMarker extends BaseTest {
	
	@Test
	public void test01() {
		FileMarker marker = new FileMarker("test");
		
		assertFalse("Mark alreay exists, invalid, clean/test again.", marker.isExists("testing"));
		
		marker.touch("testing");
		
		assertTrue("Mark does not exist.", marker.isExists("testing"));
		
		marker.remove("testing");
		
		assertTrue("Mark was not removed.", !marker.isExists("testing"));
		
		testOk();		
	}
	
	@Test
	public void test02() {
		FileMarker marker = new FileMarker("test");
		
		assertFalse("Mark alreay exists, invalid, clean/test again [1].", marker.isExists("testing", 1));
		assertFalse("Mark alreay exists, invalid, clean/test again [2].", marker.isExists("testing", 2));
		
		marker.touch("testing", 1);
		marker.touch("testing", 2);
		
		assertTrue("Mark does not exist [1].", marker.isExists("testing", 1));
		assertTrue("Mark does not exist [2].", marker.isExists("testing", 2));
		
		marker.remove("testing", 1);
		marker.remove("testing", 2);
		
		assertTrue("Mark was not removed [1].", !marker.isExists("testing", 1));
		assertTrue("Mark was not removed [2].", !marker.isExists("testing", 2));
		
		testOk();		
	}
	
	@Test
	public void test03() {
		FileMarker marker = new FileMarker("test");
		
		assertFalse("Mark alreay exists, invalid, clean/test again [0].", marker.isExists("testing", 1));
		assertFalse("Mark alreay exists, invalid, clean/test again [1].", marker.isExists("testing", 1));
		assertFalse("Mark alreay exists, invalid, clean/test again [2].", marker.isExists("testing", 2));
		
		marker.touch("testing");
		marker.touch("testing", 1);
		marker.touch("testing", 2);
		
		assertTrue("Mark does not exist [0].", marker.isExists("testing"));
		assertTrue("Mark does not exist [1].", marker.isExists("testing", 1));
		assertTrue("Mark does not exist [2].", marker.isExists("testing", 2));
		
		marker.removeAllMarks();
		
		assertTrue("Mark was not removed [0].", !marker.isExists("testing"));
		assertTrue("Mark was not removed [1].", !marker.isExists("testing", 1));
		assertTrue("Mark was not removed [2].", !marker.isExists("testing", 2));
		
		testOk();		
	}
	
	@Test
	public void test04() {
		FileMarker marker = new FileMarker("test");
		
		String weird1 = "()[]sv0gsfpset;',..]'\68";
		String weird2 = "`~/.';,][o09eysg7suirvdoser";
		String weird3 = "+-*/4685=-03posnkblknx\\\\asd\\asfd";
		
		assertFalse("Mark alreay exists, invalid, clean/test again [0].", marker.isExists(weird1, 1));
		assertFalse("Mark alreay exists, invalid, clean/test again [1].", marker.isExists(weird2, 1));
		assertFalse("Mark alreay exists, invalid, clean/test again [2].", marker.isExists(weird3, 2));
		
		marker.touch(weird1);
		marker.touch(weird2, 1);
		marker.touch(weird3, 2);
		
		assertTrue("Mark does not exist [0].", marker.isExists(weird1));
		assertTrue("Mark does not exist [1].", marker.isExists(weird2, 1));
		assertTrue("Mark does not exist [2].", marker.isExists(weird3, 2));
		
		marker.removeAllMarks();
		
		assertTrue("Mark was not removed [0].", !marker.isExists(weird1));
		assertTrue("Mark was not removed [1].", !marker.isExists(weird2, 1));
		assertTrue("Mark was not removed [2].", !marker.isExists(weird3, 2));
		
		testOk();				
	}

}
