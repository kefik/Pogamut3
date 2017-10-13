/*
 * ExceptionToString.java
 *
 * Created on 26. cerven 2007, 17:32
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package cz.cuni.amis.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Simple class that serialize (format) exception to the String allowing you to
 * specify a message as a prefix of the whole string.
 * 
 * @author Jimmy
 */
public class ExceptionToString {

	public static String process(String message, Throwable e) {
		StringBuffer sb = new StringBuffer();
		if (message != null) {
			sb.append(message);
			sb.append(Const.NEW_LINE);
		}
		Throwable cur = e;
		if (cur != null) {
			sb.append(cur.getClass().getName() + ": " + cur.getMessage() +
					cur.getStackTrace() == null || cur.getStackTrace().length == 0 ? 
							" (at UNAVAILABLE)"
						:	" (at " + cur.getStackTrace()[0].toString() + ")"
			);
			cur = cur.getCause();
			while (cur != null) {
				sb.append(Const.NEW_LINE);
				sb.append("caused by: ");
				sb.append(cur.getClass().getName() + ": " + cur.getMessage() + 
						cur.getStackTrace() == null || cur.getStackTrace().length == 0 ? 
								" (at UNAVAILABLE)"
							:	" (at " + cur.getStackTrace()[0].toString() + ")"
				);
				cur = cur.getCause();
			}
			sb.append(Const.NEW_LINE);
			sb.append("Stack trace:");
			sb.append(Const.NEW_LINE);
			StringWriter stringError = new StringWriter();
			PrintWriter printError = new PrintWriter(stringError);
			e.printStackTrace(printError);
			sb.append(stringError.toString());
		}
		return sb.toString();
	}

	public static String process(Throwable e) {
		return process(null, e);
	}
	
	public static String getCurrentStackTrace() {
		Exception e = new Exception();
		StringWriter stringError = new StringWriter();
		PrintWriter printError = new PrintWriter(stringError);
		e.printStackTrace(printError);
		return stringError.toString();
	}

}
