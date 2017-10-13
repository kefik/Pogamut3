package cz.cuni.amis.pogamut.base.communication.worldview.object;

import org.junit.Test;

import cz.cuni.amis.pogamut.base3d.worldview.object.Velocity;
import cz.cuni.amis.tests.BaseTest;
				
public class Test03_Velocity extends BaseTest {

	private void testVelocity(Velocity l) {
		Velocity source;
		Velocity target;
		
		source = l;
		
		target = new Velocity(source.toString());
		
		if (!source.equals(target)) {
			testFailed("Source location '" + source.toString() + "', does not matched parsed location '" + target.toString() + "'.");
		} else {
			log.info("Velocity" + source.toString() + " parsed OK");
		}
	}
	
	@Test
	public void test01() {
		testVelocity(new Velocity(1,2,3));
		testOk();		
	}
	
	@Test
	public void test02() {
		testVelocity(new Velocity(-1,2,3));
		testOk();	
	}
	
	@Test
	public void test03() {
		testVelocity(new Velocity(1,-2,3));
		testOk();	
	}
	
	@Test
	public void test04() {
		testVelocity(new Velocity(1,2,-3));
		testOk();	
	}
	
	@Test
	public void test05() {
		testVelocity(new Velocity(-1,-2,3));
		testOk();	
	}
	
	@Test
	public void test06() {
		testVelocity(new Velocity(-1,2,-3));
		testOk();	
	}
	
	@Test
	public void test07() {
		testVelocity(new Velocity(1,-2,-3));
		testOk();	
	}
	
	@Test
	public void test08() {
		testVelocity(new Velocity(-1,-2,-3));
		testOk();	
	}
	
	@Test
	public void test01_F() {
		testVelocity(new Velocity(1.1,2.2,3.3));
		testOk();		
	}
	
	@Test
	public void test02_F() {
		testVelocity(new Velocity(-1.1,2.2,3.3));
		testOk();	
	}
	
	@Test
	public void test03_F() {
		testVelocity(new Velocity(1.1,-2.2,3.3));
		testOk();	
	}
	
	@Test
	public void test04_F() {
		testVelocity(new Velocity(1.1,2.2,-3.3));
		testOk();	
	}
	
	@Test
	public void test05_F() {
		testVelocity(new Velocity(-1.1,-2.2,3.3));
		testOk();	
	}
	
	@Test
	public void test06_F() {
		testVelocity(new Velocity(-1.1,2.2,-3.3));
		testOk();	
	}
	
	@Test
	public void test07_F() {
		testVelocity(new Velocity(1.1,-2.2,-3.3));
		testOk();	
	}
	
	@Test
	public void test08_F() {
		testVelocity(new Velocity(-1.1,-2.2,-3.3));
		testOk();	
	}

	public static void main(String[] args) {
		Test03_Velocity test = new Test03_Velocity();
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
