package cz.cuni.pogamut.posh.widget.accept;

import cz.cuni.amis.pogamut.sposh.elements.PoshElement;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import org.openide.util.datatransfer.ExTransferable;

/**
 * Simple transferable (used for DnD), that will take PoshElement and
 * encapsulate it.
 * 
 * @author Honza
 */
public class DataNodeExTransferable extends ExTransferable.Single {

	PoshElement dataNode;

	/**
	 * Data flavor of transferable will be taken from dataNode.
	 * @param dataNode
	 */
	public DataNodeExTransferable(PoshElement dataNode) {
		super(dataNode.getDataFlavor());

		this.dataNode = dataNode;
	}

	/**
	 * Create a transferable with passed flavor and encapuslated dateNode.
	 * @param flavor Supported flavour
	 * @param dataNode encapsulated PoshElement
	 */
	DataNodeExTransferable(DataFlavor flavor, PoshElement dataNode) {
		super(flavor);

		this.dataNode = dataNode;
	}

	@Override
	protected Object getData() throws IOException, UnsupportedFlavorException {
		return dataNode;
	}
}
