package cz.cuni.pogamut.posh.explorer;

import java.util.regex.Pattern;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

/**
 * Static class that provides some supporting functions (like dialogs) used in this module.
 * @author Honza
 */
public class PGSupport {
	/**
	 * Create a dialog asking for input of identifier and return identifier or null
	 *
	 * @param purposeTitle What is purpose of the indientifies
	 * @return identifier when user input is OK or null if it is not.
	 */
	public static String getIdentifierFromDialog(String purposeTitle) {
		NotifyDescriptor.InputLine desc = new NotifyDescriptor.InputLine("Please write new identifier. Name cannot have whitespaces in it.", purposeTitle);
		DialogDisplayer.getDefault().notify(desc);

		if (desc.getValue() != NotifyDescriptor.OK_OPTION) {
			return null;
		}
		if (!Pattern.compile("[a-zA-Z0-9_-]+").matcher(desc.getInputText().trim()).matches()) {
			DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message("Identifier wasn't valid."));
			return null;
		}
		return desc.getInputText().trim();
	}

	/**
	 * Display message box with message
	 * @param text message that will be displayed
	 */
	public static void message(String text) {
		NotifyDescriptor.Message desc = new NotifyDescriptor.Message(text);
		DialogDisplayer.getDefault().notify(desc);
	}

}
