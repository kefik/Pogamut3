package cz.cuni.amis.pogamut.ut2004.tournament.generator;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class UT2004DMTableGenerator {
	
	public static final String FILE_SEPARATOR = System.getProperty("file.separator");
	
	// SINGLE MATCH CONFIG
	String map = "DM-1on1-Roughinery-FPS";
	String ut2004HomeDir = "d:\\Games\\UT2004-Devel"; // must contain correct FILE-SEPARATOR(s)
	int fragLimit = 10;
	int timeLimitMins = 10;		
	String resultDir = "..\\Round-01-Results"; // must contain correct FILE-SEPARATOR(s)
		
	// CONFIGURATION
	
	// jar file with UT2004DeathMatchConsole as one-jar
	String executor = "../ut2004-tournament-3.5.2-SNAPSHOT.one-jar.jar";
	
	// where .jar files are situated
	String sourceDir = "D:/Workspaces/Pogamut-Trunk/Competitions/PogamutCup/2013/Tournament/Bots/"; // interpreted by Java, dirs may be separated by '/'
	
	// where to generate batch files
	String targetDir = "D:/Workspaces/Pogamut-Trunk/Competitions/PogamutCup/2013/Tournament/Round-01"; // interpreted by Java, dirs may be separated by '/'
	
	// PRIVATE
	
	private String removeImplicitDirs(String dir) {
		StringBuffer result = new StringBuffer(dir.length());
		
		for (int i = 0; i < dir.length(); ++i) {
			String c1 = String.valueOf(dir.charAt(i));
			if (c1.equals(FILE_SEPARATOR)) {
				if (i+2 < dir.length()) {
					String c2 = String.valueOf(dir.charAt(i+1));
					String c3 = String.valueOf(dir.charAt(i+2));
					if (c2.equals(".") && c3.equals(FILE_SEPARATOR)) {
						result.append(FILE_SEPARATOR);
						++i;
						++i;
						continue;
					}
				}
			}
			result.append(c1);
		}
		
		return result.toString();
	}
	
	/**
	 * @param directory
	 * @param findRelative
	 * @return 'findRelative' relative path to the 'directory' 
	 */
	private String relativePath(File directory, File findRelative) {
		String result = null;
		// 1) check whether 'directory' is parent of 'findRelative'
		if (findRelative.getAbsolutePath().contains(directory.getAbsolutePath())) {
			// YES!
			// directory /..../ findRelative
			result = ".";
			while (!directory.getAbsolutePath().equals(findRelative.getAbsolutePath())) {
				String lastDir = findRelative.getAbsolutePath().substring(findRelative.getAbsolutePath().lastIndexOf(FILE_SEPARATOR)+1);
				findRelative = findRelative.getParentFile();
				result += lastDir + FILE_SEPARATOR + result;				
			}
			result += "." + FILE_SEPARATOR + result;
			if (result.equals("." + FILE_SEPARATOR + ".")) return ".";			
		} else
		// 2) check whether 'findRelative' is parent of 'directory'
		if (directory.getAbsolutePath().contains(findRelative.getAbsolutePath())) {
			// YES!
			// findRelative /..../ directory
			
			while (!findRelative.getAbsolutePath().equals(directory.getAbsolutePath())) {
				directory = findRelative.getParentFile();
				result += ".." + FILE_SEPARATOR + result;				
			}
			result += "." + FILE_SEPARATOR + result;
		} else {
			// MUST FIND COMMON PARENT FIRST!
			File commonParent = findRelative.getParentFile();
			if (commonParent == null) {
				// NO COMMON PARENT FOUND!
				return null; 
			}
			
			while (!directory.getAbsolutePath().contains(commonParent.getAbsolutePath())) {
				commonParent = findRelative.getParentFile();
				if (commonParent == null) {
					// NO COMMON PARENT FOUND!
					return null; 
				}			
			}
			
			// WE HAVE COMMON PARENT!
			
			// commonParent / ... / findRelative
			// commonParent / ... / directory
			
			result = ".";
			
			// ascend from 'directory' to commonParent first
			
			while (!commonParent.getAbsolutePath().equals(directory.getAbsolutePath())) {
				directory = directory.getParentFile();
				result = ".." + FILE_SEPARATOR + result;
			}
			
			// descend to 'findRelative'
			
			String resultRest = ".";
			
			while (!commonParent.getAbsolutePath().equals(findRelative.getAbsolutePath())) {
				String lastDir = findRelative.getAbsolutePath().substring(findRelative.getAbsolutePath().lastIndexOf(FILE_SEPARATOR)+1);
				findRelative = findRelative.getParentFile();
				resultRest = lastDir + FILE_SEPARATOR + resultRest;
			}
						
			result = result + FILE_SEPARATOR + resultRest;
		}		
		
		if (result.equals("." + FILE_SEPARATOR + ".")) return ".";
		
		result = removeImplicitDirs(result);
		
		return result;
	}
		
	public void generate() {
		File sourceDirFile = new File(sourceDir);
		File targetDirFile = new File(targetDir);
		
		if (!targetDirFile.exists()) {
			if (!targetDirFile.mkdirs()) {
				throw new RuntimeException("Failed to create directory chain towards: " + targetDirFile.getAbsolutePath());
			}
		}
		
		String sourceDirRelative = relativePath(targetDirFile, sourceDirFile);
		if (sourceDirRelative == null) sourceDirRelative = sourceDirFile.getAbsolutePath();
		
		// CONST.BAT -> batch file with constants
		try {
			
			FileWriter writer = new FileWriter(new File(targetDirFile, "const.bat"));					
			PrintWriter print = new PrintWriter(writer);
			print.println("REM All match-batch-files uses following constants");
			print.println("");
			print.println("REM Unreal Tournament 2004 home directory");
			print.println("SET UT2004_HOME=" + ut2004HomeDir);
			print.println("");
			print.println("REM UT2004DeathMatchConsole executable jar");
			print.println("SET MATCH_EXECUTOR_JAR=" + executor);
			print.println("");
			print.println("REM Directory where to output match results");
			print.println("SET MATCH_RESULT_DIR=" + resultDir);
			print.println("");
			print.println("REM Name of the UT2004 map to be played");
			print.println("SET MATCH_MAP=" + map);
			print.println("");
			print.println("REM Target frag limit for the match");
			print.println("SET MATCH_FRAG_LIMIT=" + fragLimit);
			print.println("");
			print.println("REM Time limit for the match in minutes");
			print.println("SET MATCH_TIME_LIMIT=" + timeLimitMins);
			print.println("");
			print.close();
			writer.close();
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		List<File> jars = gatherJarFiles(sourceDirFile);
		Collections.sort(jars, new Comparator<File>() {

			@Override
			public int compare(File o1, File o2) {
				return o1.getAbsolutePath().compareTo(o2.getAbsolutePath());
			}
			
		});
		
		List<String> botIds = extractBotIds(jars);
		
		List<String> jarsRelative = new ArrayList<String>(jars.size());
		for (File jar : jars) {
			String file = jar.getAbsolutePath().substring(jar.getAbsolutePath().lastIndexOf(FILE_SEPARATOR)+1);
			String relative = sourceDirRelative + FILE_SEPARATOR + file;
			relative = removeImplicitDirs(relative);
			jarsRelative.add(relative);
		}
		
		int maxMatchNum = botIds.size() * (botIds.size()-1) / 2;		
		int matchNum = 0;
		
		for (int i = 0; i < botIds.size(); ++i) {
			
			String jar1 = jarsRelative.get(i);
			String botId1 = botIds.get(i);
			
			for (int j = i+1; j < jarsRelative.size(); ++j) {
				
				String jar2 = jarsRelative.get(j);
				String botId2 = botIds.get(j);
				
				++matchNum;
				
				String strMatchNum = prefixNum(matchNum, maxMatchNum);
				
				try {
					
					FileWriter writer = new FileWriter(new File(targetDirFile, "match-" + strMatchNum + "-" + botId1 + "-vs-" + botId2 + ".bat"));					
					PrintWriter print = new PrintWriter(writer);
					
					print.println("call const.bat");
					print.println("");
					
					print.println("java -jar %MATCH_EXECUTOR_JAR% ^");
					
					print.println("-u %UT2004_HOME% ^");
					
					print.println("-a " + jar1 + ";" + jar2 + " ^");
					print.println("-b " + botId1 + ";" + botId2 + " ^");
					
					print.println("-m %MATCH_MAP% ^");
					print.println("-r %MATCH_RESULT_DIR% ^");
					print.println("-n " + "Match" + strMatchNum + "-" + botId1 + "-vs-" + botId2 + " ^");
					print.println("-s " + "DMServer-" + strMatchNum + " ^");
					
					print.println("-f %MATCH_FRAG_LIMIT% ^");
					print.println("-t %MATCH_TIME_LIMIT%");
					
					print.close();
					writer.close();
					
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
				
			}
		}
		
		try {
			
			FileWriter writer = new FileWriter(new File(targetDirFile, "execute-all.bat"));					
			PrintWriter print = new PrintWriter(writer);
			
			matchNum = 0;
			
			for (int i = 0; i < jars.size(); ++i) {
				
				String botId1 = botIds.get(i);
				
				for (int j = i+1; j < jars.size(); ++j) {
					String botId2 = botIds.get(j);
					
					++matchNum;
					String strMatchNum = prefixNum(matchNum, maxMatchNum);
					
					print.println("call match-" + strMatchNum + "-" + botId1 + "-vs-" + botId2 + ".bat");
				}
			}
			
			print.close();
			writer.close();
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		System.out.println("MATCH BATCH FILES GENERATED!");
		
	}
	
	private String prefixNum(int num, int maxNum) {
		
		String strNum = String.valueOf(num);
		String strMaxNum = String.valueOf(maxNum);
		
		while (strNum.length() < strMaxNum.length()) {
			strNum = "0" + strNum;
		}
		
		return strNum;
	}
	
	private List<String> extractBotIds(List<File> jars) {
		List<String> result = new ArrayList<String>(jars.size());
		
		for (File jar : jars) {
			String file = jar.getAbsolutePath().substring(jar.getAbsolutePath().lastIndexOf(FILE_SEPARATOR)+1);
			String id = file.substring(0, file.lastIndexOf("."));			
			result.add(id);
		}
		
		return result;
	}

	private List<File> gatherJarFiles(File sourceDirFile) {
		List<File> result = new ArrayList<File>();
		for (File file : sourceDirFile.listFiles()) {
			if (file.getAbsolutePath().toLowerCase().endsWith(".jar")) {
				result.add(file);
			}
		}
		return result;
	}

	public static void main(String[] args) {
		UT2004DMTableGenerator generator = new UT2004DMTableGenerator();
		generator.generate();
	}

}
