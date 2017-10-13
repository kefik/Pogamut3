package cz.cuni.amis.pogamut.ut2004.navmeshmaker;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class JavaHome {

	private static int JAVA_ENVS_WINNER = -1;
	
	private static String javaHome = null;
	
	private static String javaCommand = null;
	
	public static final String fileSeparator = System.getProperty("file.separator");
	
	public static final String[] JAVA_ENVS = new String[]{ "JAVA_HOME", "JVM_HOME", "JAVA", "JVM" };  
	
	public static final String[] JAVA_PATHS_WIN = new String[]{ fileSeparator + "bin" + fileSeparator + "java.exe", fileSeparator + "java.exe" };
	
	public static final String[] JAVA_PATHS_NIX = new String[]{ fileSeparator + "bin" + fileSeparator + "java", fileSeparator + "java.exe" };
	
	public static final String[] JAVA_PATHS_MAC = new String[]{ fileSeparator + "bin" + fileSeparator + "java", fileSeparator + "java.exe" };
	
	public static boolean isMac() {
		return System.getProperty("os.name").contains("Mac");
	}
	
	public static boolean isWindows() {
		return System.getProperty("os.name").contains("Windows");
	}
	
	public static boolean isLinux() {
		return System.getProperty("os.name").contains("Linux");
	}
	
	public static String getJavaHome() {
		if (javaHome != null) return javaHome;
		
		String env = getJavaHomeEnvProperty();
		if (env == null) return null;
		
		javaHome = System.getenv(env); 
		
		return javaHome;
	}
	
	public static String getJavaCommand() {
		if (javaCommand != null) return javaCommand;
		
		String home = getJavaHome();
		if (home == null) return null;
		
		String[] JAVA_PATHS = null;
		
		if (isWindows()) JAVA_PATHS = JAVA_PATHS_WIN;
		else if (isLinux()) JAVA_PATHS = JAVA_PATHS_NIX;
		else if (isMac()) JAVA_PATHS = JAVA_PATHS_MAC;
		else JAVA_PATHS = JAVA_PATHS_NIX;
		
		if (JAVA_PATHS == null || JAVA_PATHS.length == 0) return null;
		
		for (String javaPath : JAVA_PATHS) {
			String fullPath = home + javaPath;
			File file = new File(fullPath);
			if (file.exists() && file.isFile()) {
				javaCommand = fullPath;
				return fullPath;
			}
		}
		
		return null;		
	}
	
	public static String getJavaHomeEnvProperty() {
		
		if (JAVA_ENVS_WINNER >= 0) {
			return JAVA_ENVS[JAVA_ENVS_WINNER];
		}
		
		List<Integer> suitability = new ArrayList<Integer>(JAVA_ENVS.length);
		
		for (String javaEnv : JAVA_ENVS) {
			suitability.add(getJavaHomeEnvSuitability(javaEnv));			
		}
		
		if (suitability.size() == 0) return null;
		
		int winner = 0;
		int winnerScore = suitability.get(0);
		
		for (int i = 1; i < suitability.size(); ++i) {
			if (suitability.get(i) > winnerScore) winner = i;
		}
		
		JAVA_ENVS_WINNER = winner;
		
		return JAVA_ENVS[winner];		
	}
	
	public static int getJavaHomeEnvSuitability(String env) {
		String prop = System.getenv(env);
		if (prop == null) return 0;
		if (prop.length() == 0) return 0;
		if (prop.contains(fileSeparator)) return 100;
		return 50;
	}
	
}
