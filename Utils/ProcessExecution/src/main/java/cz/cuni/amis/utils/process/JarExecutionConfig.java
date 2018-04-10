package cz.cuni.amis.utils.process;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import cz.cuni.amis.utils.Const;

/**
 * Win/Linux/Mac compatible way of executing a JAR file.
 * 
 * Uses JAVA_HOME property if available.
 * 
 * Invokes (concrete line is OS-dependent): JAVA_HOME/bin/java {@link ProcessExecutionConfig#args}s -jar {@link #jar} {@link #javaArgs} 
 * 
 * Wrt. {@link ProcessExecutionConfig}, it auto-fills {@link ProcessExecutionConfig#pathToProgram} with "JAVA_HOME/bin/java" (OS-dependent). 
 * 
 * @author Jimmy
 */
@XStreamAlias(value="jarProcess")
public class JarExecutionConfig extends ProcessExecutionConfig {
	
	@XStreamAlias("jar")
	private String jar;
	
	@XStreamImplicit(itemFieldName="javaArg")
	private List<String> javaArgs;
	
	public JarExecutionConfig() {
		this(System.getenv("JAVA_HOME"));
	}
	
	public JarExecutionConfig(String javaHome) {
		if (javaHome != null && javaHome.trim().length() == 0) javaHome = null;
		boolean linux = System.getProperty("os.name").toLowerCase().contains("linux");
		boolean mac   = System.getProperty("os.name").contains("Mac");
		
		String command = 
				(javaHome == null 
					? (linux || mac ? "java" : "java.exe")
					: javaHome + (linux || mac ? "/bin/java" : "\\bin\\java.exe"));
		
		setPathToProgram(command);		
	}
	
	public List<String> getJavaArgs() {
		return javaArgs;
	}
	
	public JarExecutionConfig addJavaArg(String arg) {
		if (this.javaArgs == null) this.javaArgs = new ArrayList<String>();
		this.javaArgs.add(arg);
		return this;
	}

	public JarExecutionConfig setJavaArgs(List<String> args) {
		this.javaArgs = args;
		return this;
	}
	
	public String getJar() {
		return jar;
	}

	public JarExecutionConfig setJar(String jar) {
		this.jar = jar;
		return this;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("JarExecutionConfig[");
		sb.append(Const.NEW_LINE + "  pathToProgram     = " + getPathToProgram()); 
		sb.append(Const.NEW_LINE + "  executionDir      = " + getExecutionDir());
		sb.append(Const.NEW_LINE + "  args              = ");
		if (getArgs() != null) {
			for (String arg : getArgs()) {
				sb.append(Const.NEW_LINE + "    " + arg);
			}
		}
		sb.append(Const.NEW_LINE + "  javaArgs          = ");
		if (getJavaArgs() != null) {
			for (String arg : getJavaArgs()) {
				sb.append(Const.NEW_LINE + "    " + arg);
			}
		}
		sb.append(Const.NEW_LINE + "  redirectStdOut   = " + isRedirectStdOut());
		sb.append(Const.NEW_LINE + "  redirectStdErr   = " + isRedirectStdErr());
		sb.append("]");
		return sb.toString();
	}
	
}
