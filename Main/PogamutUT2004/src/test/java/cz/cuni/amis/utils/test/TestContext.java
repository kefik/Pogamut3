package cz.cuni.amis.utils.test;

import java.util.logging.Level;
import java.util.logging.Logger;

import cz.cuni.amis.pogamut.base.utils.Pogamut;
import cz.cuni.amis.pogamut.base.utils.PogamutProperty;
import cz.cuni.amis.utils.NullCheck;

public class TestContext {
	
	private static Logger log;

	public TestContext(Logger log) {
		this.log = log;
		NullCheck.check(this.log, "log");
	}

	public Logger getLog() {
		if (log.getLevel() == null) {
			log.setLevel(Level.parse(Pogamut.getPlatform().getProperty(PogamutProperty.POGAMUT_LOGGER_LEVEL_DEFAULT.getKey())));
		}
		return log;
	}
	
}
