package cz.cuni.amis.pogamut.base.communication.worldview.object;

import org.junit.Test;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.tests.BaseTest;
				
public class Test01_Location extends BaseTest {

	private void testLocation(Location l) {
		Location source;
		Location target;
		
		source = l;
		
		target = new Location(source.toString());
		
		if (!source.equals(target)) {
			testFailed("Source location '" + source.toString() + "', does not matched parsed location '" + target.toString() + "'.");
		} else {
			log.info("Location" + source.toString() + " parsed OK");
		}
	}
	
	@Test
	public void test01() {
		testLocation(new Location(1,2,3));
		testOk();		
	}
	
	@Test
	public void test02() {
		testLocation(new Location(-1,2,3));
		testOk();	
	}
	
	@Test
	public void test03() {
		testLocation(new Location(1,-2,3));
		testOk();	
	}
	
	@Test
	public void test04() {
		testLocation(new Location(1,2,-3));
		testOk();	
	}
	
	@Test
	public void test05() {
		testLocation(new Location(-1,-2,3));
		testOk();	
	}
	
	@Test
	public void test06() {
		testLocation(new Location(-1,2,-3));
		testOk();	
	}
	
	@Test
	public void test07() {
		testLocation(new Location(1,-2,-3));
		testOk();	
	}
	
	@Test
	public void test08() {
		testLocation(new Location(-1,-2,-3));
		testOk();	
	}
	
	@Test
	public void test01_F() {
		testLocation(new Location(1.1,2.2,3.3));
		testOk();		
	}
	
	@Test
	public void test02_F() {
		testLocation(new Location(-1.1,2.2,3.3));
		testOk();	
	}
	
	@Test
	public void test03_F() {
		testLocation(new Location(1.1,-2.2,3.3));
		testOk();	
	}
	
	@Test
	public void test04_F() {
		testLocation(new Location(1.1,2.2,-3.3));
		testOk();	
	}
	
	@Test
	public void test05_F() {
		testLocation(new Location(-1.1,-2.2,3.3));
		testOk();	
	}
	
	@Test
	public void test06_F() {
		testLocation(new Location(-1.1,2.2,-3.3));
		testOk();	
	}
	
	@Test
	public void test07_F() {
		testLocation(new Location(1.1,-2.2,-3.3));
		testOk();	
	}
	
	@Test
	public void test08_F() {
		testLocation(new Location(-1.1,-2.2,-3.3));
		testOk();	
	}

	public static void main(String[] args) {
		Test01_Location test = new Test01_Location();
		test.test01();
		test.test02();
		test.test03();
		test.test04();
		test.test05();
		test.test06();
		test.test07();
		test.test08();
		
		test.test01_F();
		test.test02_F();
		test.test03_F();
		test.test04_F();
		test.test05_F();
		test.test06_F();
		test.test07_F();
		test.test08_F();
	}

}
