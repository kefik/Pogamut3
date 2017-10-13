package cz.cuni.amis.nb.pogamut.unreal.timeline.view;

import cz.cuni.amis.nb.pogamut.unreal.timeline.dataobject.TLDataObject;
import java.awt.Image;
import javax.swing.JOptionPane;
import org.netbeans.core.spi.multiview.MultiViewDescription;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.openide.util.HelpCtx;
import org.openide.util.ImageUtilities;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;

/**
 * Description of Map view of experiment. 
 * 
 * Specifies displayName, icon, creates actual display area ect. Used during 
 * creation of CloneableTopComponent by MultiViewFactory.
 * 
 * @author Honza
 */
public class MVMapDescription implements MultiViewDescription {
	private static Image ICON = ImageUtilities.loadImage("cz/cuni/amis/nb/pogamut/ut2004/timeline/view/map_icon.png");
	private TLDataObject dataObject;
	private MVMapElement mapElement = null;

	public MVMapDescription(TLDataObject dataObject) {
		this.dataObject = dataObject;
		createElement();
	}

    @Override
	public int getPersistenceType() {
		return TopComponent.PERSISTENCE_NEVER;
	}

    @Override
	public String getDisplayName() {
		return "Map";
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
		return "MapDescription";
	}

    @Override
	public MultiViewElement createElement() {
		if (mapElement == null) {
			mapElement = new MVMapElement(dataObject);
		}
		return mapElement;
	}

}
