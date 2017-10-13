package cz.cuni.amis.pogamut.ut2004.tournament;

import java.io.File;
import java.io.InputStream;

import org.junit.Test;

import cz.cuni.amis.tests.BaseTest;
import cz.cuni.amis.utils.IniFile;

public class Test01_UT2004Ini extends BaseTest {
	
	public static final String[] UT2004_INI_FILE_NAMES = 
		new String[] {
			"/cz/cuni/amis/pogamut/ut2004/tournament/UT2004.test.1.ini"
		};
	
	public static final String INI_FILE_NAME = "temp_test_ut2004ini_file_to_be_deleted.ini";
	
	@Test
	public void test01() {
		for (int i = 0; i < UT2004_INI_FILE_NAMES.length; ++i) {
			log.info("[" + (i+1) + " / " + (UT2004_INI_FILE_NAMES.length) + "]: Checking file " + UT2004_INI_FILE_NAMES[i]);
			testFile(i);
		}
		
		testOk();
	}	
	
	private void testFile(int index) {
		String UT2004_INI_FILE_NAME = UT2004_INI_FILE_NAMES[index];

		log.info("[" + (index+1) + "]: Reading file...");
		
		InputStream defaults = GameBots2004Ini.class.getResourceAsStream(UT2004_INI_FILE_NAME);
		UT2004Ini ini1 = new UT2004Ini(false);
		ini1.load(defaults);
		
		log.info("[" + (index+1) + "]: File read, " + ini1.getSections().size() + " sections loaded.");
		
		log.info("[" + (index+1) + "]: Writing INI back...");
		
		File file = new File(INI_FILE_NAME);
		
		if (file.isFile() && file.exists()) {
			file.delete();
		}
		
		ini1.output(file);
		
		if (!file.isFile() && !file.exists()) {
			testFailed("FAILED TO SAVE THE INI FILE INTO: " + file.getAbsolutePath());
		}
		
		UT2004Ini ini2 = new UT2004Ini(false);
		
		try {
			log.info("[" + (index+1) + "]: Written.");
		
			log.info("[" + (index+1) + "]: Re-reading written INI...");
			
			ini2.load(file);
		} finally {
			file.delete(); // UNCOMMENT FOR PRODUCTION
		}
			
		log.info("[" + (index+1) + "]: File read, " + ini2.getSections().size() + " sections loaded.");
		
		log.info("[" + (index+1) + "]: Comparing read/written inis...");
		
		checkEquals(ini1, ini2);
					
		log.info("[" + (index+1) + "] INI MATCHES!");
	}
	
	private void checkEquals(IniFile read, IniFile written) {
		log.info("-------------------------");
		log.info("COMPARING READ vs WRITTEN");
		log.info("-------------------------");
		
		log.info("PRINTING READ:");
		log.info("\n"+read.output());
		log.info("PRINTING WRITTEN:");
		log.info("\n"+written.output());
		
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
	}

}
