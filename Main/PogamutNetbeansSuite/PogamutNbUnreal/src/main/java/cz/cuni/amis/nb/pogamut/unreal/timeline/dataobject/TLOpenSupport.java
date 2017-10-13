package cz.cuni.amis.nb.pogamut.unreal.timeline.dataobject;

import cz.cuni.amis.nb.pogamut.unreal.timeline.UTTimelinesRootNode;
import cz.cuni.amis.nb.pogamut.unreal.timeline.view.MVMapDescription;
import cz.cuni.amis.nb.pogamut.unreal.timeline.view.MVTimelineDescription;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import javax.swing.JOptionPane;
import org.netbeans.core.spi.multiview.MultiViewDescription;
import org.netbeans.core.spi.multiview.MultiViewFactory;
import org.openide.cookies.CloseCookie;
import org.openide.cookies.OpenCookie;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.OpenSupport;
import org.openide.nodes.Node;
import org.openide.windows.CloneableTopComponent;

/**
 * Support for timeline file. 
 * 
 * Can be used as OpenCookie, ViewCookie, or CloseCookie, depending on which 
 * cookies the subclass implements. 
 * 
 * @author Honza
 */
public class TLOpenSupport extends OpenSupport implements OpenCookie, CloseCookie {

	public TLOpenSupport(MultiDataObject.Entry entry) {
		super(entry);
	}

	@Override
	protected CloneableTopComponent createCloneableTopComponent() {
		TLDataObject dataObject = (TLDataObject) entry.getDataObject();
		
		MVTimelineDescription timelineDescription = new MVTimelineDescription(dataObject);
		MVMapDescription mapDescription = new MVMapDescription(dataObject);


		MultiViewDescription[] tcViewDescriptions = new MultiViewDescription[]{
			timelineDescription,
			mapDescription
		};

		
		// Create component representing experiment
		CloneableTopComponent tc = MultiViewFactory.createCloneableMultiView(
			tcViewDescriptions,
			timelineDescription);

		tc.setDisplayName(dataObject.getPrimaryFile().getNameExt());

		return tc;
	}

    @Override
    public void open() {
        super.open();

        // add it to the timeline
        UTTimelinesRootNode.getInstance().addTimeline((TLDataObject) entry.getDataObject());
    }
}
