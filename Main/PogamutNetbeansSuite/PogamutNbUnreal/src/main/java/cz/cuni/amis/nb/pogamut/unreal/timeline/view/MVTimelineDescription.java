package cz.cuni.amis.nb.pogamut.unreal.timeline.view;

import cz.cuni.amis.nb.pogamut.unreal.timeline.dataobject.TLDataObject;
import java.awt.Image;
import org.netbeans.core.spi.multiview.MultiViewDescription;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.openide.util.HelpCtx;
import org.openide.util.ImageUtilities;
import org.openide.windows.TopComponent;

/**
 * Description of timeline view of the experiment. 
 * 
 * Specifies displayName, icon, creates actual display area ect. Used during 
 * creation of CloneableTopComponent by MultiViewFactory.
 * 
 * @author Honza
 */
public class MVTimelineDescription implements MultiViewDescription {

    private static Image ICON = ImageUtilities.loadImage("cz/cuni/amis/nb/pogamut/ut2004/timeline/view/timeline_icon.png");
    private TLDataObject dataObject;

    public MVTimelineDescription(TLDataObject dataObject) {
        this.dataObject = dataObject;
    }

    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_NEVER;
    }

    @Override
    public String getDisplayName() {
        return "Timeline";
    }

    @Override
    public Image getIcon() {
        return ICON;
    }

    @Override
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    /**
     * Contribution to unique Id of enclosing TopComponent.
     * @return
     */
    @Override
    public String preferredID() {
        return "TimelineDescription";
    }

    @Override
    public MultiViewElement createElement() {
        return new MVTimelineElement(dataObject.getDatabase(), dataObject.getLookup());
    }
}
