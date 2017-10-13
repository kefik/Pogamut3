package cz.cuni.amis.pogamut.lectures.linetagger.checker;

import java.util.logging.Logger;

import cz.cuni.amis.utils.rewrite.RewriteFilesConfig;

public class Checker {

	private Logger log = Logger.getLogger(getClass().getSimpleName());
	private RewriteFilesConfig config;
	private String resultFile;;

	public Checker(RewriteFilesConfig config, String resultFile) {
		this.config = config;
		this.resultFile = resultFile;
	}

	public void setLog(Logger log) {
		this.log  = log;
	}

	public synchronized void check() {
		// TODO Auto-generated method stub
		
	}

}
