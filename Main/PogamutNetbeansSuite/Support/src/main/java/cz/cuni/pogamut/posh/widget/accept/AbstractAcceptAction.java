package cz.cuni.pogamut.posh.widget.accept;

import cz.cuni.amis.pogamut.sposh.elements.PoshElement;
import cz.cuni.pogamut.posh.widget.PoshWidget;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;
import org.netbeans.api.visual.action.AcceptProvider;
import org.netbeans.api.visual.action.ConnectorState;
import org.netbeans.api.visual.widget.Widget;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Exceptions;

/**
 * Ancestor for accept providers of widgets.
 * @author Honza
 */
public abstract class AbstractAcceptAction<DROPPED extends PoshElement, TARGET extends PoshElement> implements AcceptProvider {

    protected DataFlavor dataFlavor;
    protected TARGET dataNode;
    protected PoshWidget widget;

    protected static final Logger log = Logger.getLogger("AcceptAction");

    /**
     * Create an abstract action in the widget widget that will do something to
     * data node dataNode when transferable of same data flavor as dataFlavor
     * is dropped onto widget.
     *
     * @param dataFlavor data flavor we are accepting, no multiple flavors accepted
     * @param dataNode data node that will be used somehow when accept action happens.
     * @param widget widget transferable is dropped onto for this accept provider to work.
     */
    protected AbstractAcceptAction(DataFlavor dataFlavor, TARGET dataNode, PoshWidget widget) {
        this.dataFlavor = dataFlavor;
        this.dataNode = dataNode;
        this.widget = widget;
    }

    /**
     * same as other constructor, but we don't require widget.
     * @param dataFlavor
     * @param dataNode
     */
    protected AbstractAcceptAction(DataFlavor dataFlavor, TARGET dataNode) {
        this(dataFlavor, dataNode, null);
    }

    @Override
    public final ConnectorState isAcceptable(Widget arg0, Point arg1, Transferable transferable) {
        if (transferable.isDataFlavorSupported(dataFlavor)) {
            return ConnectorState.ACCEPT;
        }
        return ConnectorState.REJECT;
    }

    @Override
    public final void accept(Widget widget, Point point, Transferable transferable) {
        try {
            DROPPED droppedData = (DROPPED) transferable.getTransferData(dataFlavor);
            if (droppedData != null) {
                performAction(droppedData);
            }  else {
                StringBuilder sb = new StringBuilder();
                for (DataFlavor flavor : transferable.getTransferDataFlavors()) {
                    sb.append(' ');
                    sb.append(flavor);
                }
                log.warning("Recived transferable (flavor:" + sb + ") with an empty payload.");
            }
        } catch (UnsupportedFlavorException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    /**
     * Get data flavor this accept provider accepts.
     * @return data flavour passed in the constructor
     */
    public final DataFlavor getDataFlavor() {
        return dataFlavor;
    }

    /**
     * When deriving from this widget, implement what should actually be done
     * in this function.
     *
     * @param droppedData Object that was stored in the {@link Transferable}, basically object that was dropped.
     */
    abstract protected void performAction(DROPPED droppedData);

    /**
     * Get index of object in the list.
     *
     * @param list list of object where this function will look for object o.
     * @param o object whos index we are looking for
     * @return index of object o in the list or length of list if object wasn't found.
     * XXX: ??? Javadoc is not consistent with what method does? Replace wiht List.indexOf?
     */
    final protected int getIndexInList(List list, Object o) {
        int newlyAddedSenseIndex = 0;
        int index = 0;

        for (Object n : list) {
            if (n == o) {
                newlyAddedSenseIndex = index;
            }
            index++;
        }
        return newlyAddedSenseIndex;
    }

    /**
     * Display message using {@link NotifyDescriptor}.
     * @param messgae
     * @param messageType Type of message, from NotifyDescriptor.*_MESSAGE
     */
    protected final void displayMessage(String message, int messageType) {
        NotifyDescriptor.Message error = new NotifyDescriptor.Message(message, messageType);
        DialogDisplayer.getDefault().notify(error);
    }
}
