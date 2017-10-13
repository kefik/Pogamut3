package cz.cuni.amis.utils.process;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import cz.cuni.amis.utils.Const;

@XStreamAlias(value="process")
public class ProcessExecutionConfig {
	
	@XStreamAlias(value = "id")
	private String id;
	
	@XStreamAlias(value = "run")
	private String pathToProgram;
	
	@XStreamImplicit(itemFieldName="arg")
	private List<String> args;
	
	@XStreamAlias(value = "dir")
	private String executionDir;
	
	@XStreamAlias(value = "redirectStdErr")
	private Boolean redirectStdErr;
	
	@XStreamAlias(value = "redirectStdOut")
	private Boolean redirectStdOut;
	
	@XStreamAlias(value = "timeoutMillis")
	private Long timeout;

	public ProcessExecutionConfig() {
	}
	
	private ProcessExecutionConfig readResolve() {
		if (redirectStdErr == null) redirectStdErr = false;
		if (redirectStdOut == null) redirectStdOut = false;
		if (executionDir == null) {
			executionDir = ".";			
		}
		if (args == null) {
			args = new ArrayList<String>();
		}
		if (id == null) {
			if (pathToProgram != null) {
				id = pathToProgram;
			} else {
				id = "exec";
			}
		}
		return this;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Boolean getRedirectStdErr() {
		return redirectStdErr;
	}

	public void setRedirectStdErr(Boolean redirectStdErr) {
		this.redirectStdErr = redirectStdErr;
	}

	public Boolean getRedirectStdOut() {
		return redirectStdOut;
	}

	public void setRedirectStdOut(Boolean redirectStdOut) {
		this.redirectStdOut = redirectStdOut;
	}
	
	public boolean isRedirectStdErr() {
		if (redirectStdErr == null) return false;
		return redirectStdErr;
	}

	public boolean isRedirectStdOut() {
		if (redirectStdOut == null) return false;
		return redirectStdOut;
	}

	/**
	 * In millis...
	 * @return
	 */
	public Long getTimeout() {
		return timeout;
	}

	public void setTimeout(Long timeoutMillis) {
		this.timeout = timeoutMillis;
	}
	
	public void setTimeout(long timeoutMillis) {
		this.timeout = timeoutMillis;
	}

	public String getPathToProgram() {
		return pathToProgram;
	}

	public void setPathToProgram(String pathToProgram) {
		this.pathToProgram = pathToProgram;
	}

	public List<String> getArgs() {
		return args;
	}

	public void setArgs(List<String> args) {
		this.args = args;
	}

	public String getExecutionDir() {
		return executionDir;
	}

	public void setExecutionDir(String executionDir) {
		this.executionDir = executionDir;
	}

	public void setRedirectStdErr(boolean redirectStdErr) {
		this.redirectStdErr = redirectStdErr;
	}

	public void setRedirectStdOut(boolean redirectStdOut) {
		this.redirectStdOut = redirectStdOut;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("ProcessExecutionConfig[");
		sb.append(Const.NEW_LINE + "  pathToProgram     = " + pathToProgram); 
		sb.append(Const.NEW_LINE + "  executionDir      = " + executionDir);
		sb.append(Const.NEW_LINE + "  args = ");
		if (args != null) {
			for (String arg : args) {
				sb.append(Const.NEW_LINE + "    " + arg);
			}
		}
		sb.append(Const.NEW_LINE + "  redirectStdOut   = " + redirectStdOut);
		sb.append(Const.NEW_LINE + "  redirectStdErr   = " + redirectStdErr);
		sb.append("]");
		return sb.toString();
	}
	
}
