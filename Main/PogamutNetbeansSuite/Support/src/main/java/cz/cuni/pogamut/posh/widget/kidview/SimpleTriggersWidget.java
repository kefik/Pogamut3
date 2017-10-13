package cz.cuni.pogamut.posh.widget.kidview;

import cz.cuni.amis.pogamut.sposh.elements.PoshElement;
import cz.cuni.amis.pogamut.sposh.elements.Sense;
import cz.cuni.amis.pogamut.sposh.elements.Triggers;
import cz.cuni.pogamut.posh.widget.accept.AbstractAcceptAction;
import cz.cuni.pogamut.posh.widget.PoshNodeType;
import cz.cuni.pogamut.posh.widget.PoshScene;
import cz.cuni.pogamut.posh.widget.PoshWidget;
import cz.cuni.pogamut.posh.widget.accept.AcceptSense2Triggers;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.util.LinkedList;
import java.util.List;

/**
 * Widget representation of Triggers in the KidView.
 * 
 * @author Honza
 */
class SimpleTriggersWidget extends SimpleBasicWidget<Triggers> {

	SimpleTriggersWidget(PoshScene poshScene, Triggers dataNode, PoshWidget<? extends PoshElement> parentWidget) {
		super(poshScene, dataNode, parentWidget, "triggers");
	}

	@Override
	protected void addChildWidget(PoshElement dataNode) {
		if (dataNode instanceof Sense) {
			SimpleSenseWidget senseWidget =
				new SimpleSenseWidget(getPoshScene(), (Sense)dataNode, this);

			this.getChildNodes().add(senseWidget);
			this.getPoshScene().addPoshWidget(senseWidget, true);
		} else {
			throw new RuntimeException("Only PoshSense accepted, not " + dataNode.getClass().getName());
		}
	}

	@Override
	protected List<AbstractMenuAction> createMenuActions() {
		List<AbstractMenuAction> list = new LinkedList<AbstractMenuAction>();

		list.add(new AbstractMenuAction<Triggers>("Add sense", getDataNode()) {

			public void actionPerformed(ActionEvent e) {
				String newSense = getIdentifierFromDialog("Name of new sense");
				if (newSense != null) {
					this.dataNode.addTrigger(new Sense(newSense));
				}
			}
		});

		return list;
	}

	@Override
	protected PoshNodeType getType() {
		return PoshNodeType.TRIGGER;
	}

	@Override
	public void elementPropertyChange(PropertyChangeEvent evt) {
		throw new RuntimeException("No property change expected in Triggers.");
	}

	@Override
	protected List<AbstractAcceptAction> getAcceptProviders() {
		List<AbstractAcceptAction> list = new LinkedList<AbstractAcceptAction>();

		list.add(new AcceptSense2Triggers(getDataNode()));

		return list;
	}
}
