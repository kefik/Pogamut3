package cz.cuni.amis.pogamut.release;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import cz.cuni.amis.netbeans.publicpkgs.NetBeansPublicPackages;
import cz.cuni.amis.netbeans.publicpkgs.NetBeansPublicPackagesConfig;
import cz.cuni.amis.pogamut.release.archetype.ArchetypeRefresh;
import cz.cuni.amis.pogamut.release.archetype.ArchetypeRefreshConfig;
import cz.cuni.amis.utils.ExceptionToString;
import cz.cuni.amis.utils.FileMarker;
import cz.cuni.amis.utils.NullCheck;
import cz.cuni.amis.utils.StopWatch;
import cz.cuni.amis.utils.exception.PogamutException;
import cz.cuni.amis.utils.process.ProcessExecution;
import cz.cuni.amis.utils.process.ProcessExecutionConfig;
import cz.cuni.amis.utils.rewrite.RewriteFiles;
import cz.cuni.amis.utils.rewrite.RewriteFilesConfig;
import cz.cuni.amis.utils.simple_logging.SimpleLogging;

public class PogamutRelease {
	
	private PogamutReleaseConfig config;
	
	private Logger log;

	private boolean continueTheBuild = false;
	
	private FileMarker marker;

	public PogamutRelease(PogamutReleaseConfig config, boolean continueTheBuild) {
		NullCheck.check(config, "config");
		this.config = config;
		this.continueTheBuild = continueTheBuild;
		marker = new FileMarker(config.getId(), ".");
	}
	
	public PogamutRelease(File xmlFile, boolean continueTheBuild) {
		if (xmlFile == null) throw new IllegalArgumentException("'xmlFile' can't be null!");
		this.config = PogamutReleaseConfig.loadXML(xmlFile);
		this.continueTheBuild = continueTheBuild;
		marker = new FileMarker(config.getId(), ".");
	}

	public PogamutReleaseConfig getConfig() {
		return config;
	}

	public void setConfig(PogamutReleaseConfig config) {
		this.config = config;
	}
	
	public Logger getLog() {
		return log;
	}

	public void setLog(Logger log) {
		this.log = log;
	}

	public boolean run() {
		logInfo("===================================================");
		logInfo("-------      EXECUTING POGAMUT RELEASE      -------");
		logInfo("===================================================");
		
		if (config.getSteps() == null || config.getSteps().size() == 0) {
			logWarning("No steps defined in the configuration! ABORTING!");
			logWarning("FAILURE!!!");
			return false;
		}
		
		List<Double>    times = new ArrayList<Double>(config.getSteps().size());
		List<Boolean> success = new ArrayList<Boolean>(config.getSteps().size());
		
		StopWatch allWatch = new StopWatch();
		StopWatch stepWatch = new StopWatch();
		boolean allSuccess  = true;
		boolean stepSuccess = true;
		boolean itemSuccess = true;
		
		try {
			int i = 1;
			for (PogamutReleaseStep step : config.getSteps()) {
				stepWatch.start();
				stepSuccess = true;
				logInfo("");
				logInfo("     ----------------------------------     ");
				logInfo("===========================================");
				logInfo("     STEP (" + i + " / " + config.getSteps().size() + "): " + step.getId());
				logInfo("===========================================");
				logInfo("     ----------------------------------     ");

				
				try {
					if (continueTheBuild && marker.isExists(step.getId() + "-done")) {
						logInfo("DONE MARK EXISTS, skipping the step...");
					} else {
			//
			// REWRITE FILES
			//
						if (step.getRewriteFiles() != null && step.getRewriteFiles().size() > 0) {
							int j = 1;
							for (RewriteFilesConfig rewriteConfig : step.getRewriteFiles()) {
								StopWatch itemWatch = new StopWatch();
								itemSuccess = true;
								try {
									logInfo("");
									logInfo("--- STEP (" + i + " / " + config.getSteps().size() + "): " + step.getId());
									logInfo("");
									logInfo("------ REWRITE FILES (" + j + " / " + step.getRewriteFiles().size() + ")");
									
									if (continueTheBuild) {
										if (marker.isExists(step.getId() + "-RewriteFiles-done", j)) {
											logInfo("DONE MARK EXISTS, skipping the step...");
											++j;
											continue;
										}
									} 
									
									boolean ok = false;
									RewriteFiles rewrite = null;
									try {
										rewrite = new RewriteFiles(rewriteConfig);
										rewrite.setLog(log);
										ok = rewrite.rewrite();
									} catch (PogamutException e) {
										e.logExceptionOnce(log);
										ok = false;
									} catch (Exception e2) {
										logWarning(ExceptionToString.process("Rewrite failed.", e2));
										ok = false;
									}
									if (!ok) {
										logSevere("FAILURE!!!");
										itemSuccess = false;
										stepSuccess = false;
										allSuccess = false;
										if (step.isStopOnFail()) {
											logWarning("Step.failStop == true, TERMINATING!");
											return false;
										}
									} else {
										itemSuccess = true;
										marker.touch(step.getId() + "-RewriteFiles-done", j);
									}
									++j;
								} finally {
									times.add(itemWatch.stop());
									success.add(itemSuccess);
									logInfo("------ ITEM TIME: " + formatTime(itemWatch.time()));
								}
							}
						}
			//
			// PROCESS EXECUTION
			//
						if (step.getProcessExecution() != null && step.getProcessExecution().size() > 0) {
							int j = 1;
							for (ProcessExecutionConfig processConfig : step.getProcessExecution()) {
								StopWatch itemWatch = new StopWatch();
								itemSuccess = true;
								try {
									logInfo("");
									logInfo("--- STEP (" + i + " / " + config.getSteps().size() + "): " + step.getId());
									logInfo("");
									logInfo("------ PROCESS EXECUTION (" + j + " / " + step.getProcessExecution().size() + ") " + processConfig.getId());
									
									if (continueTheBuild) {
										if (marker.isExists(step.getId() + "-Process-done", j)) {
											logInfo("DONE MARK EXISTS, skipping the step...");
											++j;
											continue;
										}
									} 
									
									if (processConfig.getTimeout() == null) {
										processConfig.setTimeout((60 * 60 * 1000));
										logInfo("Timeout for process not specified, setting: 1 hour");
									}
									boolean ok = false;
									ProcessExecution process = null;
									try {
										process = new ProcessExecution(processConfig, log);					
										process.start();
										process.getRunning().waitFor(false);
										if (process.isFailed(true)) {
											ok = false;
										} else {
											ok = true;
										}
									} catch (PogamutException e) {
										e.logExceptionOnce(log);
										ok = false;
									} catch (Exception e2) {
										logWarning(ExceptionToString.process("Process failed.", e2));
										ok = false;
									}
									if (!ok) {
										logWarning("FAILURE!!!");
										itemSuccess = false;
										stepSuccess = false;
										allSuccess = false;
										if (step.isStopOnFail()) {
											logWarning("Step.failStop == true, TERMINATING!");
											return false;
										}
									} else {
										itemSuccess = true;
										marker.touch(step.getId() + "-Process-done", j);
									}
									++j;
								} finally {
									times.add(itemWatch.stop());
									success.add(itemSuccess);
									logInfo("------ ITEM TIME: " + formatTime(itemWatch.time()));
								}
							}
						}				
		//
		// ARCHETYPE REFRESH
		//
						if (step.getArchetypeRefresh() != null && step.getArchetypeRefresh().size() > 0) {
							int j = 1;
							for (ArchetypeRefreshConfig archetypeConfig : step.getArchetypeRefresh()) {
								StopWatch itemWatch = new StopWatch();
								itemSuccess = true;
								try {
									logInfo("");
									logInfo("--- STEP (" + i + " / " + config.getSteps().size() + "): " + step.getId());
									logInfo("");
									logInfo("------ ARCHETYPE REFRESH (" + j + " / " + step.getArchetypeRefresh().size() + ") " + archetypeConfig.getTargetDir());
									
									if (continueTheBuild) {
										if (marker.isExists(step.getId() + "-ArchetypeRefresh-done", j)) {
											logInfo("DONE MARK EXISTS, skipping the step...");
											++j;
											continue;
										}
									} 
									
									boolean ok = false;
									ArchetypeRefresh archetype = null;
									try {
										archetype = new ArchetypeRefresh(archetypeConfig);
										archetype.setLog(log);
										if (!archetype.refresh()) {
											ok = false;
										} else {
											ok = true;
										}
									} catch (PogamutException e) {
										e.logExceptionOnce(log);
										ok = false;
									} catch (Exception e2) {
										logWarning(ExceptionToString.process("Archetype refresh failed.", e2));
										ok = false;
									}
									if (!ok) {
										logWarning("FAILURE!!!");
										itemSuccess = false;
										stepSuccess = false;
										allSuccess = false;
										if (step.isStopOnFail()) {
											logWarning("Step.failStop == true, TERMINATING!");
											return false;
										}
									} else {
										itemSuccess = true;
										marker.touch(step.getId() + "-ArchetypeRefresh-done", j);
									}
									++j;
								} finally {
									times.add(itemWatch.stop());
									success.add(itemSuccess);
									logInfo("------ ITEM TIME: " + formatTime(itemWatch.time()));
								}
							}
						}
		//
		// NETBEANS PUBLIC PACKAGES REFRESH
		//
						if (step.getPublicPackages() != null && step.getPublicPackages().size() > 0) {
							int j = 1;
							for (NetBeansPublicPackagesConfig packagesConfig : step.getPublicPackages()) {
								StopWatch itemWatch = new StopWatch();
								itemSuccess = true;
								try {
									logInfo("");
									logInfo("--- STEP (" + i + " / " + config.getSteps().size() + "): " + step.getId());
									logInfo("");
									logInfo("------ NETBEANS PUBLIC PACKAGES REFRESH (" + j + " / " + step.getArchetypeRefresh().size() + ")");
									
									if (continueTheBuild) {
										if (marker.isExists(step.getId() + "-NetBeansPublicPackages-done", j)) {
											logInfo("DONE MARK EXISTS, skipping the step...");
											++j;
											continue;
										}
									} 
									
									boolean ok = false;
									NetBeansPublicPackages publicPackages = null;
									try {
										publicPackages = new NetBeansPublicPackages(packagesConfig);
										publicPackages.setLog(log);
										if (!publicPackages.rewrite()) {
											ok = false;
										} else {
											ok = true;
										}
									} catch (PogamutException e) {
										e.logExceptionOnce(log);
										ok = false;
									} catch (Exception e2) {
										logWarning(ExceptionToString.process("NetBeans public packages refresh failed.", e2));
										ok = false;
									}
									if (!ok) {
										logWarning("FAILURE!!!");
										itemSuccess = false;
										stepSuccess = false;
										allSuccess = false;
										if (step.isStopOnFail()) {
											logWarning("Step.failStop == true, TERMINATING!");
											return false;
										}
									} else {
										itemSuccess = true;
										marker.touch(step.getId() + "-NetBeansPublicPackages-done", j);
									}
									++j;
								} finally {
									times.add(itemWatch.stop());
									success.add(itemSuccess);
									logInfo("------ ITEM TIME: " + formatTime(itemWatch.time()));
								}
							}
						}
					}
				} finally {
					double time = stepWatch.stop();
					times.add(time);
					success.add(stepSuccess);
					if (stepSuccess) {
						logInfo("--- STEP (" + i + " / " + config.getSteps().size() + "): " + step.getId() + " [SUCCESS, " + formatTime(time) + "]");
						marker.touch(step.getId() + "-done");
					} else {
						logInfo("--- STEP (" + i + " / " + config.getSteps().size() + "): " + step.getId() + " [FAILURE, " + formatTime(time) + "]");
					}
				}
				
				++i;
			}
		} finally {
			allWatch.stop();
			logInfo("--------------------------");
			logInfo("STATISTICS:");
			int index = 0;
			int stepIndex = 1;
			for (PogamutReleaseStep step : config.getSteps()) {
				int rewriteCount = step.getRewriteFiles().size();
				int processCount = step.getProcessExecution().size();
				int archetypeCount = step.getArchetypeRefresh().size();
				int packagesCount = step.getPublicPackages().size();
				double stepTime = 0;
				boolean stepSucc = false;
				if (index + rewriteCount + processCount + archetypeCount + packagesCount >= success.size()) {
					// PREMATURE TERMINATION -> FAILURE
					stepTime = times.get(times.size()-1);
					stepSucc = success.get(success.size()-1);
				} else {
					stepTime = times.get(index + rewriteCount + processCount + archetypeCount + packagesCount);
					stepSucc = success.get(index + rewriteCount + processCount + archetypeCount + packagesCount);
				}
				logInfo(" -- Step: " + step.getId());
				if (stepSucc) {
					logInfo("                            SUCCESS [ " + formatTime(stepTime) + " ]");
				} else {
					logInfo("                            FAILURE [ " + formatTime(stepTime) + " ]");
				}
				for (RewriteFilesConfig rewriteConfig : step.getRewriteFiles()) {
					if (index+1 >= times.size()) break;
					if (success.get(index)) {
						logInfo(" ---- Rewrite:                SUCCESS [ " + formatTime(times.get(index)) + " ]");
					} else {
						logInfo(" ---- Rewrite:                FAILURE [ " + formatTime(times.get(index)) + " ]");
					}
					++index;
				}
				for (ProcessExecutionConfig processConfig : step.getProcessExecution()) {
					if (index+1 >= times.size()) break;
					logInfo(" ---- Process " + processConfig.getId() + ":");
					if (success.get(index)) {
						logInfo("                              SUCCESS [ " + formatTime(times.get(index)) + " ]");
					} else {
						logInfo("                              FAILURE [ " + formatTime(times.get(index)) + " ]");
					}
					++index;
				}
				for (ArchetypeRefreshConfig processConfig : step.getArchetypeRefresh()) {
					if (index+1 >= times.size()) break;
					logInfo(" ---- Archetype refresh " + processConfig.getTargetDir() + ":");
					if (success.get(index)) {
						logInfo("                              SUCCESS [ " + formatTime(times.get(index)) + " ]");
					} else {
						logInfo("                              FAILURE [ " + formatTime(times.get(index)) + " ]");
					}
					++index;
				}
				for (NetBeansPublicPackagesConfig packagesConfig : step.getPublicPackages()) {
					if (index+1 >= times.size()) break;
					logInfo(" ---- NetBeans public packages refresh:");
					if (success.get(index)) {
						logInfo("                              SUCCESS [ " + formatTime(times.get(index)) + " ]");
					} else {
						logInfo("                              FAILURE [ " + formatTime(times.get(index)) + " ]");
					}
					++index;
				}
				
				++stepIndex;
				index += 1;
			}
			logWarning("--------------------------");
			if (allSuccess) {
				logWarning("PogamutRelease:          SUCCESS [ " + formatTime(allWatch.time()) + " ]");
				marker.removeAllMarks();
			} else {
				logWarning("PogamutRelease:          FAILURE [ " + formatTime(allWatch.time()) + " ]");
			}
		}
		return allSuccess;
	}
	
	protected void logInfo(String msg) {
		if (log != null && log.isLoggable(Level.INFO)) {
			log.info(msg);
		}
	}
	
	protected void logWarning(String msg) {
		if (log != null && log.isLoggable(Level.WARNING)) {
			log.warning(msg);
		}
	}
	
	protected void logSevere(String msg) {
		if (log != null && log.isLoggable(Level.SEVERE)) {
			log.severe(msg);
		}
	}
	
	public static String formatTime(double millis) {
		double origMillis = millis;
		
		boolean time = false;
		
		StringBuffer sb = new StringBuffer();
		if (millis > 60 * 60 * 1000) {
			int hours = (int)Math.floor(millis / (60 * 60 * 1000));
			millis -= hours * 60 * 60 * 1000;
			
			sb.append(hours);
			sb.append("h ");
			
			time = true;
		}
		
		if (time || millis > 60 * 1000) {
			int mins = (int)Math.floor(millis / (60 * 1000));
			millis -= mins * 60 * 1000;
			
			String strMins = String.valueOf(mins);
			while (strMins.length() < 2) strMins = "0" + strMins; 
			sb.append(mins);
			sb.append("m ");
			
			time = true;
		}
		
		int secs = (int)Math.floor(millis / (1000));
		millis -= secs * 1000;
		int ms = (int)Math.floor(millis);
		millis -= ms;
		
		String strSecs = String.valueOf(secs);
		while (strSecs.length() < 2) strSecs = "0" + strSecs;
		
		String strMs = String.valueOf(ms);
		while (strMs.length() < 3) strMs = "0" + strMs;
		
		sb.append(strSecs);
		sb.append(".");
		sb.append(strMs);
		
		sb.append("s");
		
		return sb.toString();
	}
	
	// ..........................................
	// ------------------------------------------
	// ==========================================
	// ------------------------------------------
	// ..........................................
	
	public static void main(String[] args) {
		
		// FOR TESTING ONLY!
		//args = new String[] { "D:/Workspaces/Pogamut-Trunk/Main/PogamutRelease/release-all/config/PogamutRelease-step00-DeployOldSnapshots-Libs.xml", "-c" };
		
		if (args == null) {
			args = new String[1];
			args[0] = "PogamutRelease.xml";
		} else
		if (args.length == 0) {
			args = new String[1];
			args[0] = "PogamutRelease.xml";
		} 		
		
		SimpleLogging.initLogging(true);
		SimpleLogging.addFileLogging("PogamutRelease.log");
		
		Logger log = Logger.getAnonymousLogger();
		log.info("---[[ POGAMUT RELEASE ]]---");
		
		boolean continueTheBuild = false;
		for (String arg : args) {
			if (arg != null && (arg.equals("-c") ||arg.equals("--continue"))) {
				continueTheBuild = true;
				log.info("CONTINUING THE WORK!");
				break;
			}
		}
		
		int i = 0;
		for (String definition : args) {
			if (definition != null && (definition.equals("-c") || definition.equals("--continue"))) {
				continue;
			}
			++i;
			log.info("======================================");
			log.info("DEFINITION " + i + " / " + (args.length - (continueTheBuild ? 1 : 0)));
			log.info("Loading definition from xml file: " + definition + " --> " + new File(definition).getAbsoluteFile());
			log.info("======================================");			
			log.info("");
			File file = new File(definition);
			if (!file.exists() || !file.isFile()) {
				log.severe("FAILED! Definition file not found at: " + file.getAbsolutePath());
				log.severe("Usage: java -jar PogamutRelease.jar [path-to-definition-xml-file] [-c | --continue]");
				log.info("---[[ FAIL ]]---");
				System.exit(1);
				return;
			}
			PogamutRelease release;
			try {
				release = new PogamutRelease(file, continueTheBuild);
			} catch (Exception e) {
				e.printStackTrace();
				log.severe("Usage: java -jar PogamutRelease.jar [path-to-definition-xml-file-1] [path-to-definition-xml-file-2] [...] [-c | --continue]");
				log.info("---[[ FAIL ]]---");
				return;
			}
			release.setLog(log);
			log.info("Definition file loaded.");
			if (!release.run()) {
				log.info("---[[ FAIL ]]---");
				System.exit(1);
				return;
			}
		}
		
		log.info("---[[ OK ]]---");
		System.exit(0);
	}

}
