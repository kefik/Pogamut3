package cz.cuni.amis.utils.rewrite.rewriter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

@XStreamAlias(value = "substitution")
public class Substitution implements ISubstitution {
	
	@XStreamAlias(value = "match")
	private String reMatchOrig;
	
	private String reMatch;
	
	@XStreamAlias(value = "replace")
	private String reSubstOrig;
		
	private String reSubst;
	
	@XStreamAlias(value = "multiLine")
	private Boolean multiLine;
	
	@XStreamAlias(value = "caseSensitive")
	private Boolean caseSensitive;
	
	@XStreamAlias(value = "replaceVariables")
	private Boolean replaceVariables;

	@XStreamOmitField
	private transient Pattern pattern;
	
	public Substitution() {		
	}
	
	public Substitution(String reMatch, String reSubst, boolean caseSensitive, boolean multiLine, boolean replaceVariables) {
		this.reMatchOrig = this.reMatch = reMatch;
		this.reSubstOrig = this.reSubst = reSubst;
		this.caseSensitive = caseSensitive;
		this.multiLine = multiLine;
		this.replaceVariables = replaceVariables;
		if (this.replaceVariables) replaceVariables();
	}
	
	private Substitution readResolve() {
		if (caseSensitive == null) caseSensitive = true;
		if (multiLine == null) multiLine = false;
		if (replaceVariables == null) replaceVariables = true;
		this.reSubst = this.reSubstOrig;
		if (this.replaceVariables) replaceVariables();
		return this;
	}

	public static final Pattern SIMPLE_PARAM = Pattern.compile("\\$([a-zA-Z0-9_]+)");
	    
	public static final Pattern FORMAL_PARAM = Pattern.compile("\\$\\{(.+)\\}");
	
	private static class Tuple3<FIRST, SECOND, THIRD> {
		
		private FIRST first;
		private SECOND second;
		private THIRD third;

		public Tuple3(FIRST first, SECOND second, THIRD third) {
			this.first = first;
			this.second = second;
			this.third = third;
		}

		public FIRST getFirst() {
			return first;
		}

		public void setFirst(FIRST first) {
			this.first = first;
		}

		public SECOND getSecond() {
			return second;
		}

		public void setSecond(SECOND second) {
			this.second = second;
		}

		public THIRD getThird() {
			return third;
		}

		public void setThird(THIRD third) {
			this.third = third;
		}

	}
	
	/**
     * Searches for $XXX or ${XXX} and substitute them with {@link System#getenv()} (prioritized) or {@link System#getProperty(String)}.
     * @param string
     * @return
     */
    public static String substituteParams(String string, boolean replacement) {
    	List<Tuple3<Integer, Integer, String>> params = new ArrayList<Tuple3<Integer, Integer, String>>();
    	Matcher m;
    	
    	for (int i = 0; i < 2; ++i) {
    		m = (i == 0 ? SIMPLE_PARAM.matcher(string) : FORMAL_PARAM.matcher(string));
    		while (m.find()) {
    			params.add(
    					new Tuple3<Integer, Integer, String>(
    						m.start(),          // where does the parameter start
    						m.group().length(), // how long the param is
    						m.group(1)          // name of the parameter matched
    					)
    			);
    		}
    	}
    	
    	if (params.size() == 0) return string;
    	
    	Collections.sort(
    		params, 
    		new Comparator<Tuple3<Integer, Integer, String>>() {
				@Override
				public int compare(Tuple3<Integer, Integer, String> o1,	Tuple3<Integer, Integer, String> o2) {
					return o2.getFirst() - o1.getFirst(); // descending order!
				}
    		}
    	);
    	
    	String result = string;
    	    	
    	for (Tuple3<Integer, Integer, String> param : params) {
    		String paramValue = System.getenv(param.getThird());
    		if (paramValue == null) {
    			paramValue = System.getProperty(param.getThird());
    		}
    		if (paramValue == null) {
    			if (replacement) {
    				try {
    					int i = Integer.parseInt(param.getThird());
    				} catch (Exception e) {
    					// NOT AN INT!
    					throw new RuntimeException("Parameter '" + param.getThird() + "' not found! Both System.getenv(\"" + param.getThird() + "\") and System.getProperty(\"" + param.getThird() + "\") evaluates to null!");
    				}
    				// IS INT & REPLACEMENT -> reference to captured group -> leave it as is
    				continue;
    			} else {
    				throw new RuntimeException("Parameter '" + param.getThird() + "' not found! Both System.getenv(\"" + param.getThird() + "\") and System.getProperty(\"" + param.getThird() + "\") evaluates to null!");
    			}    			    			
    		}
    		result = result.substring(0, param.getFirst()) + paramValue + result.substring(param.getFirst() + param.getSecond());
    	}
    	
    	return result;
    }
	
	private void replaceVariables() {
		this.reMatch = substituteParams(reMatchOrig, false);
		this.reSubst = substituteParams(reSubstOrig, true);
	}

	public String getReMatch() {
		return reMatch;
	}

	public void setReMatch(String reMatch) {		
		this.reMatch = reMatch;
		this.pattern = null;
	}
	
	public String getReSubstOrig() {
		return reSubstOrig;
	}

	public void setReSubstOrig(String reSubstOrig) {
		this.reSubstOrig = reSubstOrig;
	}

	public String getReSubst() {
		return reSubst;
	}

	public void setReSubst(String reSubst) {
		this.reSubst = reSubst;
	}
	
	public Pattern getPattern() {
		if (pattern == null) {
			pattern = Pattern.compile(reMatch, (caseSensitive ? 0 : Pattern.CASE_INSENSITIVE) | (multiLine ? 0 : Pattern.MULTILINE));
		}
		return pattern;
	}
	
	public Matcher getMatcher(String str) {
		return getPattern().matcher(str);
	}
	
	@Override
	public SubstitutionResult substitute(String str) {
		if (getMatcher(str).find()) {
			return new SubstitutionResult(getMatcher(str).replaceAll(reSubst), true);
		} else {
			return new SubstitutionResult(str, false);
		}
		
	}
	
	@Override
	public boolean isMultiLine() {
		return multiLine;
	}

	public void setMultiLine(boolean multiLine) {
		this.multiLine = multiLine;
	}

	public boolean isCaseSensitive() {
		return caseSensitive;
	}

	public void setCaseSensitive(boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}

	/**
	 * Warning! Once you set the patter manually, IT WON'T BE CONSTRUCTED AUTOMATICALLY FROM SUBSTITUTION SETTINGS! 
	 * See {@link Substitution#setPattern(Pattern)}.
	 * 
	 * @param pattern
	 */
	public void setPattern(Pattern pattern) {
		this.pattern = pattern;
	}

	@Override
	public String toString() {
		return "Substitution[" + (caseSensitive ? "case-sensitive" : "case-INsensitive") + ", " + (multiLine ? "multi-line pattern" : "single-line pattern") + " | " + reMatch + " --> " + reSubst + "]";
	}

}
