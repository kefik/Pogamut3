package cz.cuni.amis.utils;

import java.io.File;

import org.junit.Test;

import cz.cuni.amis.tests.BaseTest;
import cz.cuni.amis.utils.IniFile.Section;

public class Test05_IniFile extends BaseTest {
	
	public static final String INI_FILE_NAME = "temp_test_ini_file_to_be_deleted.ini";
	
	@Test
	public void test01() {
		File file = new File(INI_FILE_NAME);
		
		log.info("Checking INI FILE manual creation");
		
		IniFile written = new IniFile();
		
		// SECTION 1
		
		String section1Name = "SEC1";
		Section section1 = written.addSection(section1Name);
		if (section1 != written.getSection(section1Name)) {
			testFailed("Failed to add section " + section1Name);
		}
		
		section1.set("KEY1", "VALUE1", "This is comment for KEY1=VALUE1");
		if (!SafeEquals.equals("VALUE1", section1.getOne("KEY1"))) {
			testFailed("Failed to set KEY1=VALUE1");
		}		
		section1.addComment("KEY1=old value1", "This is comment for OLD value for KEY1");
		
		section1.set("KEY2", "VALUE2", "This is comment for KEY2=VALUE2");
		if (!SafeEquals.equals("VALUE2", section1.getOne("KEY2"))) {
			testFailed("Failed to set KEY2=VALUE2");
		}		
		section1.addComment("KEY2=old value2", "This is comment for OLD value for KEY2");
	
		// RETESTING SECTION 1
		if (!SafeEquals.equals("VALUE1", section1.getOne("KEY1"))) {
			testFailed("Failed to set KEY2=VALUE2");
		}			
		if (section1.getKeys().size() != 2) {
			testFailed("Failed to set keys, expected 2 != " + section1.getKeys().size());
		}
		if (!SafeEquals.equals("VALUE1", section1.getOne("KEY1"))) {
			testFailed("Failed to set KEY1=VALUE1");
		}
		if (!SafeEquals.equals("VALUE2", section1.getOne("KEY2"))) {
			testFailed("Failed to set KEY2=VALUE2");
		}

		// SECTION 2
		
		String section2Name = "SEC2";
		Section section2 = written.addSection(section2Name);
		if (section2 != written.getSection(section2Name)) {
			testFailed("Failed to add section " + section2Name);
		}
		
		section2.set("KEY3", "VALUE3", "This is comment for KEY3=VALUE3");
		if (!SafeEquals.equals("VALUE3", section2.getOne("KEY3"))) {
			testFailed("Failed to set KEY3=VALUE3");
		}		
		section2.addComment("KEY3=old value3", "This is comment for OLD value for KEY3");
		
		section2.set("KEY4", "VALUE4", "This is comment for KEY4=VALUE4");
		if (!SafeEquals.equals("VALUE4", section2.getOne("KEY4"))) {
			testFailed("Failed to set KEY4=VALUE4");
		}		
		section2.addComment("KEY4=old value4", "This is comment for OLD value for KEY4");		
		
		// RETESTING
		if (section2.getKeys().size() != 2) {
			testFailed("Failed to set keys, expected 2 != " + section1.getKeys().size());
		}
		if (!SafeEquals.equals("VALUE3", section2.getOne("KEY3"))) {
			testFailed("Failed to set KEY3=VALUE3");
		}
		if (!SafeEquals.equals("VALUE4", section2.getOne("KEY4"))) {
			testFailed("Failed to set KEY4=VALUE4");
		}
		
		log.info("OK");
		
		log.info("Writing INI FILE into " + file.getAbsolutePath());
		
		written.output(file);
		
		if (!file.isFile() && !file.exists()) {
			testFailed("FAILED TO SAVE THE INI FILE INTO: " + file.getAbsolutePath());
		}
		
		log.info("PRINTING WRITTEN");
		log.info("\n"+written.output());
		
		log.info("Reading INI FILE from " + file.getAbsolutePath());
		
		IniFile read = new IniFile(file);
		
		log.info("PRINTING READ");
		log.info("\n"+read.output());
		
		log.info("Comparing WRITTEN ---> READ");
		
		if (!written.isSubset(read, "WRITTEN", "READ", log)) {
			testFailed("WRITTEN is not subset of READ!");
		}
		
		log.info("OK");
		
		log.info("Comparing READ ---> WRITTEN");
		
		if (!read.isSubset(written, "READ", "WRITTEN", log)) {
			testFailed("READ is not subset of WRITTEN!");
		}
		
		log.info("OK");		
		
		file.delete();
		
		testOk();	
	}

}
