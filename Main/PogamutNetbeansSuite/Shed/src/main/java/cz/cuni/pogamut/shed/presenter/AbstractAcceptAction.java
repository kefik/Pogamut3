package cz.cuni.pogamut.shed.presenter;

import cz.cuni.amis.pogamut.sposh.elements.PoshElement;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import org.netbeans.api.visual.action.AcceptProvider;
import org.netbeans.api.visual.action.ConnectorState;
import org.netbeans.api.visual.widget.Widget;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Exceptions;

/**
 * Ancestor for accept providers of widgets. This action takes an element of
 * type TARGET and performs some sort of accept action (accepting drag and drop)
 * when user drops an element of type DROPPED on it.
 *
 * The {@link #performAction(cz.cuni.amis.pogamut.sposh.elements.PoshElement) }
 * will perform action when user drops transferable with correct data flavor.
 * The action will take {@link #dataNode} which is accepting the dropped element
 * and does something.
 *
 * @author Honza Havlicek
 * @param <DROPPED> We are dragging {@link PoshElement} of DROPPED type. This is
 * the source.
 * @param <TARGET> Type of {@link PoshElement} this action drops to, it is
 * destination element.
 */
public abstract class AbstractAcceptAction<DROPPED extends PoshElement, TARGET extends PoshElement> implements AcceptProvider {

    /**
     * Data flavor this action desires in order to accept the dropped {@link Transferable}.
     */
    protected DataFlavor dataFlavor;
    /**
     * Node that is accepting dropped node.
     */
    protected TARGET dataNode;

    /**
     * Create an abstract action in the widget widget that will do something to
     * data node dataNode when transferable of same data flavor as dataFlavor is
     * dropped onto widget.
     *
     * @param dataFlavor data flavor we are accepting, no multiple flavors
     * accepted
     * @param dataNode data node that will be used somehow when accept action
     * happens.
     * @param widget widget transferable is dropped onto for this accept
     * provider to work.
     */
    protected AbstractAcceptAction(DataFlavor dataFlavor, TARGET dataNode) {
        this.dataFlavor = dataFlavor;
        this.dataNode = dataNode;
    }

    /**
     * Does @transferable support {@link #dataFlavor}?
     *
     * @param transferable Transferable that is being tested if it supports the
     * data flavor that is accepted by this action.
     * @return True if transferable can be dropped on this.
     */
    @Override
    public final ConnectorState isAcceptable(Widget arg0, Point arg1, Transferable transferable) {
        if (transferable.isDataFlavorSupported(dataFlavor)) {
            return ConnectorState.ACCEPT;
        }
        return ConnectorState.REJECT;
    }

    /**
     * Basically take the data from @transferable (use our {@link #getDataFlavor()
     * }) and perform the action ({@link #performAction(cz.cuni.amis.pogamut.sposh.elements.PoshElement)
     * }). If @transferable is empty (its dataflavored data is null), don't
     * perform the action, only log it.
     *
     * @param widget Not used, widget at which was the transferable dropped
     * @param point Not used, location at which was the transferable dropped,
     * local coordinatin system of the @widget.
     * @param transferable Transferable used to get the data.
     */
    @Override
    public final void accept(Widget widget, Point point, Transferable transferable) {
        try {
            DROPPED droppedData = (DROPPED) transferable.getTransferData(dataFlavor);
            if (droppedData != null) {
                performAction(droppedData);
            }
        } catch (UnsupportedFlavorException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    /**
     * Get data flavor this accept provider accepts.
     *
     * @return data flavour passed in the constructor
     */
    public final DataFlavor getDataFlavor() {
        return dataFlavor;
    }

    /**
     * When deriving from this widget, implement what should actually be done in
     * this function. Basically the accepting element is supposed to accept the
     * @droppedData.
     *
     * CAREFUL: @droppedData can still be referenced somewhere (e.g. when sense
     * is moved, dropped data is the reference to the original sense), newly
     * created and other stuff. Be sure to take it into account.
     *
     * @param droppedData Object that was dropped to the accepting element (in
     * case of drag and drop, data that was stored in the {@link Transferable}).
     */
    public abstract void performAction(DROPPED droppedData);

    /**
     * Display message using {@link NotifyDescriptor}.
     *
     * @param message Message to display
     * @param messageType Type of message, from NotifyDescriptor.*_MESSAGE
     */
    protected final void displayMessage(String message, int messageType) {
        NotifyDescriptor.Message error = new NotifyDescriptor.Message(message, messageType);
        DialogDisplayer.getDefault().notify(error);
    }
}
