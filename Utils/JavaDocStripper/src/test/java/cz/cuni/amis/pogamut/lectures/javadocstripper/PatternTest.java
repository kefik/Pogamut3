package cz.cuni.amis.pogamut.lectures.javadocstripper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import cz.cuni.amis.tests.BaseTest;

public class PatternTest extends BaseTest {
	
	public Pattern getPattern(String regex) {
		return Pattern.compile(regex);
	}
	
	public Pattern getPattern(String regex, boolean multiLine) {
		return Pattern.compile(regex, multiLine ? 0 : Pattern.MULTILINE);
	}
	
	public String getMatch(Pattern p, String msg) {
		
		Matcher m = p.matcher(msg);
		if (!m.find()) throw new RuntimeException("Regexp '" + p.pattern() + "' does not match onto '" + msg + "'.");
		return m.group(0);
	}
	
	public void shouldMatch(Pattern p, String msg, String expectedMatch) {
		log.info("------------");
		log.info("SHOULD MATCH");
		log.info("Pattern:  " + p.pattern());
		log.info("Message:  " + msg);
		log.info("Expected: " + expectedMatch);
		Matcher m = p.matcher(msg);
		if (!m.find()) {
			testFailed("Regexp '" + p.pattern() + "' does not match onto '" + msg + "'.");
		}		
		if (!m.group(0).equals(expectedMatch)) {
			testFailed("Regexp '" + p.pattern() + "' does matched onto '" + msg + "', but it does not match it correctly. Expected match: '" + expectedMatch + "', matched: '" + expectedMatch + "'.");
		}
		log.info("Matched - ok!");
	}
	
	public void shouldNotMatch(Pattern p, String msg) {
		log.info("----------------");
		log.info("SHOULD NOT MATCH");
		log.info("Pattern: " + p.pattern());
		log.info("Message: " + msg);
		Matcher m = p.matcher(msg);
		if (m.find()) {
			testFailed("Regexp '" + p.pattern() + "' SHOULD NOT match onto '" + msg + "' but it DOES! Matched: '" + m.group(0) + "'.");
		}
		log.info("No match - ok!");
	}
	
	@Test
	public void testPattern1() {
		String pattern       = JavaDocStripper.singleLineCommentPattern;
		int patternFlag      = 0;
		String message       = "alhashaslh // ashaslkaskljf";
		boolean shouldMatch  = true;
		String expectedMatch = "// ashaslkaskljf";
		
		Pattern p = Pattern.compile(pattern, patternFlag);
		
		if (shouldMatch) {
			shouldMatch(p, message, expectedMatch);
		} else {
			shouldNotMatch(p, message);
		}		
	}
	
	@Test
	public void testPattern2() {
		String pattern       = JavaDocStripper.singleLineCommentPattern;
		int patternFlag      = 0;
		String message       = "alhashaslh / / ashaslkaskljf";
		boolean shouldMatch  = false;
		String expectedMatch = "// ashaslkaskljf";
		
		Pattern p = Pattern.compile(pattern, patternFlag);
		
		if (shouldMatch) {
			shouldMatch(p, message, expectedMatch);
		} else {
			shouldNotMatch(p, message);
		}		
	}
	
	@Test
	public void testPattern3() {
		// MULTI LINE COMMENT		
		String pattern       = JavaDocStripper.multiLineCommentPattern;
		int patternFlag      = 0;
		String message       = "alhashaslh /*  / ashaslkaskljf    */ asfasf";
		boolean shouldMatch  = true;
		String expectedMatch = "/*  / ashaslkaskljf    */";
		
		Pattern p = Pattern.compile(pattern, patternFlag);
		
		if (shouldMatch) {
			shouldMatch(p, message, expectedMatch);
		} else {
			shouldNotMatch(p, message);
		}		
	}
	
	@Test
	public void testPattern4() {
		// MULTI LINE COMMENT		
		String pattern       = JavaDocStripper.multiLineCommentPattern;
		int patternFlag      = 0;
		String message       = "alhashaslh /*  / ashaslkaskljf    */ */ asfasf";
		boolean shouldMatch  = true;
		String expectedMatch = "/*  / ashaslkaskljf    */";
		
		Pattern p = Pattern.compile(pattern, patternFlag);
		
		if (shouldMatch) {
			shouldMatch(p, message, expectedMatch);
		} else {
			shouldNotMatch(p, message);
		}		
	}
	
	@Test
	public void testPattern5() {
		// MULTI LINE COMMENT		
		String pattern       = JavaDocStripper.multiLineCommentPattern;
		int patternFlag      = 0;
		String message       = "alhashaslh /*  / ashaslkaskljf \r\n asdfasdf   */ */ asfasf";
		boolean shouldMatch  = true;
		String expectedMatch = "/*  / ashaslkaskljf \r\n asdfasdf   */";
		
		Pattern p = Pattern.compile(pattern, patternFlag);
		
		if (shouldMatch) {
			shouldMatch(p, message, expectedMatch);
		} else {
			shouldNotMatch(p, message);
		}		
	}
	
	
	
}
