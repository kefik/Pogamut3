package cz.cuni.pogamut.posh.widget.kidview;

import cz.cuni.amis.pogamut.sposh.elements.Goal;
import cz.cuni.amis.pogamut.sposh.elements.PoshElement;
import cz.cuni.amis.pogamut.sposh.elements.Sense;
import cz.cuni.pogamut.posh.widget.accept.AbstractAcceptAction;
import cz.cuni.pogamut.posh.widget.PoshNodeType;
import cz.cuni.pogamut.posh.widget.PoshScene;
import cz.cuni.pogamut.posh.widget.PoshWidget;
import cz.cuni.pogamut.posh.widget.accept.AcceptSense2Goal;
import cz.cuni.pogamut.posh.widget.menuactions.AddSense2Goal;
import java.beans.PropertyChangeEvent;
import java.util.LinkedList;
import java.util.List;

/**
 * Representation of Goal in the KidView.
 * @author Honza
 */
class SimpleGoalWidget extends SimpleBasicWidget<Goal> {

	SimpleGoalWidget(PoshScene poshScene, Goal dataNode, PoshWidget<? extends PoshElement> parent) {
		super(poshScene, dataNode, parent, "Goal");
	}

	@Override
	protected PoshNodeType getType() {
		return PoshNodeType.GOAL;
	}

	@Override
	protected List<AbstractMenuAction> createMenuActions() {
		List<AbstractMenuAction> list = new LinkedList<AbstractMenuAction>();

		list.add(new AddSense2Goal(getDataNode()));

		return list;
	}

	/**
	 * Create proper widget from child data node.
	 * 
	 * @param dataNode DataNode that was added as child 
	 *                 to associated data node
	 */
	@Override
	protected void addChildWidget(PoshElement dataNode) {
		if (dataNode instanceof Sense) {
			SimpleSenseWidget sense = new SimpleSenseWidget(getPoshScene(), (Sense)dataNode, this);

			this.getChildNodes().add(sense);
			this.getPoshScene().addPoshWidget(sense, true);
		} else {
			throw new RuntimeException("Only poshSense accepted, not " + dataNode.getClass().getName());
		}
	}

	@Override
	public void elementPropertyChange(PropertyChangeEvent evt) {
		throw new RuntimeException("No property change expected in goal.");
	}

	@Override
	protected List<AbstractAcceptAction> getAcceptProviders() {
		List<AbstractAcceptAction> list = new LinkedList<AbstractAcceptAction>();

		list.add(new AcceptSense2Goal(getDataNode()));

		return list;
	}
}
