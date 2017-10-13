package cz.cuni.amis.configuration;

import org.junit.Test;

import cz.cuni.amis.tests.BaseTest;
import cz.cuni.amis.utils.configuration.PropertiesManager;

public class Test01_PropertiesManager_Instantiation extends BaseTest {

	@Test
	public void test() {
		new PropertiesManager();
		
		testOk();
	}
	
}
