package cz.cuni.amis.pogamut.base.communication.worldview.object;

import org.junit.Test;

import cz.cuni.amis.pogamut.base3d.worldview.object.Rotation;
import cz.cuni.amis.tests.BaseTest;
				
public class Test02_Rotation extends BaseTest {

	private void testRotation(Rotation l) {
		Rotation source;
		Rotation target;
		
		source = l;
		
		target = new Rotation(source.toString());
		
		if (!source.equals(target)) {
			testFailed("Source location '" + source.toString() + "', does not matched parsed location '" + target.toString() + "'.");
		} else {
			log.info("Rotation" + source.toString() + " parsed OK");
		}
	}
	
	@Test
	public void test01() {
		testRotation(new Rotation(1,2,3));
		testOk();		
	}
	
	@Test
	public void test02() {
		testRotation(new Rotation(-1,2,3));
		testOk();	
	}
	
	@Test
	public void test03() {
		testRotation(new Rotation(1,-2,3));
		testOk();	
	}
	
	@Test
	public void test04() {
		testRotation(new Rotation(1,2,-3));
		testOk();	
	}
	
	@Test
	public void test05() {
		testRotation(new Rotation(-1,-2,3));
		testOk();	
	}
	
	@Test
	public void test06() {
		testRotation(new Rotation(-1,2,-3));
		testOk();	
	}
	
	@Test
	public void test07() {
		testRotation(new Rotation(1,-2,-3));
		testOk();	
	}
	
	@Test
	public void test08() {
		testRotation(new Rotation(-1,-2,-3));
		testOk();	
	}
	
	@Test
	public void test01_F() {
		testRotation(new Rotation(1.1,2.2,3.3));
		testOk();		
	}
	
	@Test
	public void test02_F() {
		testRotation(new Rotation(-1.1,2.2,3.3));
		testOk();	
	}
	
	@Test
	public void test03_F() {
		testRotation(new Rotation(1.1,-2.2,3.3));
		testOk();	
	}
	
	@Test
	public void test04_F() {
		testRotation(new Rotation(1.1,2.2,-3.3));
		testOk();	
	}
	
	@Test
	public void test05_F() {
		testRotation(new Rotation(-1.1,-2.2,3.3));
		testOk();	
	}
	
	@Test
	public void test06_F() {
		testRotation(new Rotation(-1.1,2.2,-3.3));
		testOk();	
	}
	
	@Test
	public void test07_F() {
		testRotation(new Rotation(1.1,-2.2,-3.3));
		testOk();	
	}
	
	@Test
	public void test08_F() {
		testRotation(new Rotation(-1.1,-2.2,-3.3));
		testOk();	
	}

	public static void main(String[] args) {
		Test02_Rotation test = new Test02_Rotation();
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
