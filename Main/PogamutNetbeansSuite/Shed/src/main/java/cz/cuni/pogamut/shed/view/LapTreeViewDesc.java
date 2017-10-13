package cz.cuni.pogamut.shed.view;

import cz.cuni.pogamut.posh.PoshDataObject;
import java.awt.Image;
import org.netbeans.core.spi.multiview.MultiViewDescription;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.openide.util.HelpCtx;
import org.openide.windows.TopComponent;

/**
 * Basically a factory for creation of {@link LapTreeMVElement}.
 *
 * @author HonzaH
 */
public class LapTreeViewDesc implements MultiViewDescription {

    /**
     * Data object this the MVE should modify.
     */
    private final PoshDataObject dObj;

    /**
     * Create visual view of the yaposh plan from the data object.
     * @param dObj Data object with Yaposh plan inside.
     */
    public LapTreeViewDesc(PoshDataObject dObj) {
        this.dObj = dObj;
    }

    /**
     * Shed is never persistent.
     * @return {@link TopComponent#PERSISTENCE_NEVER}.
     */
    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_NEVER;
    }

    /**
     * @return "Visual"
     */
    @Override
    public String getDisplayName() {
        return "Visual";
    }

    /**
     * No icon
     */
    @Override
    public Image getIcon() {
        return null;
    }

    /**
     * No help ctx
     */
    @Override
    public HelpCtx getHelpCtx() {
        return null;
    }

    @Override
    public String preferredID() {
        return "lap_tree_view" + dObj.getPrimaryFile().getPath();
    }

    @Override
    public MultiViewElement createElement() {
        return new LapTreeMVElement(dObj);
    }
}
