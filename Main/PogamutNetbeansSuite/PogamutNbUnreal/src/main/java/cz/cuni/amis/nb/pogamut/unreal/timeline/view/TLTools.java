/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.amis.nb.pogamut.unreal.timeline.view;

import javax.swing.SwingUtilities;
import org.openide.util.Exceptions;

/**
 *
 * @author Honza
 */
public class TLTools {

    public static void runAndWaitInAWTThread(Runnable runnable) {
        if (SwingUtilities.isEventDispatchThread()) {
            runnable.run();
        } else {
            try {
                SwingUtilities.invokeAndWait(runnable);
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }
}
