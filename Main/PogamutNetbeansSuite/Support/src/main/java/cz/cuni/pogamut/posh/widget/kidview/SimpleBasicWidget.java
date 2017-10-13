package cz.cuni.pogamut.posh.widget.kidview;

import cz.cuni.amis.pogamut.sposh.PoshTreeEvent;
import cz.cuni.amis.pogamut.sposh.elements.ActionPattern;
import cz.cuni.amis.pogamut.sposh.elements.Competence;
import cz.cuni.amis.pogamut.sposh.elements.NamedLapElement;
import cz.cuni.amis.pogamut.sposh.elements.PoshElement;
import cz.cuni.amis.pogamut.sposh.elements.PoshPlan;
import cz.cuni.amis.pogamut.sposh.elements.TriggeredAction;
import cz.cuni.pogamut.posh.widget.PoshScene;
import cz.cuni.pogamut.posh.widget.PoshWidget;
import java.awt.Point;
import java.beans.PropertyChangeEvent;
import java.util.List;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import org.netbeans.api.visual.widget.Widget;
import org.openide.util.Exceptions;




abstract class NamedBasicWidget<T extends NamedLapElement> extends SimpleBasicWidget<T> {

    public NamedBasicWidget(PoshScene poshScene, T dataNode, PoshWidget<? extends PoshElement> parent) {
        super(poshScene, dataNode, parent, dataNode.getName());
    }
    
}
/**
 * Basic widget for KidView. 
 * 
 * Events are handled individually in methods 
 *   addChildWidget
 *   deleteWidgetFromScene
 *   moveChildWidget
 * 
 * @author HonzaH
 */
public abstract class SimpleBasicWidget<T extends PoshElement> extends PoshWidget<T> {

    protected SimpleBasicWidget(PoshScene poshScene, T dataNode, PoshWidget<? extends PoshElement> parent, String name) {
        super(poshScene, dataNode, parent, name);
    }

    @Override
    public final void nodeChanged(PoshTreeEvent event, PoshElement dataNode) {
        if (event == PoshTreeEvent.NEW_CHILD_NODE) {
            assert getPoshScene().isInMainLayer(this); // XXX: Not sure why is this here, shouldn't ever happen, but keep for now
            addChildWidget(dataNode);
        } else if (event == PoshTreeEvent.NODE_DELETED) {
            deleteWidgetFromScene(dataNode);
        } else if (event == PoshTreeEvent.CHILD_NODE_MOVED) {
            moveChildWidget(dataNode);
        } else {
            throw new IllegalArgumentException("Argument (" + event + ") shouldn't exist.");
        }
        // reposition tree so every widget is at its proper place
        getPoshScene().consolidate();
        doRepaint();
        updateSources();
    }
    
	/**
	 * This is called when node is added as child
	 * @param dataNode
	 */
	protected abstract void addChildWidget(PoshElement dataNode);

	/**
	 * Key functionm that is called when dataNode is moved
	 * Since KidView closely resembles PoshPlan structure, just one function
	 * for all should be enough.
	 * 
	 * We don't know what or where was it moved.
	 * @param movedChildDN data node that was moved in POSH plan.
	 */
	protected void moveChildWidget(PoshElement movedChildDN) {
		// what children do I have?
		List<PoshWidget<? extends PoshElement>> widgetChildren = getChildNodes();
		List<? extends PoshElement> nodeChildren = movedChildDN.getParent().getChildDataNodes();

		// fixme: border cases when movedChildDN is not in lists
		// find new position in node
		int movedDataNodeIndex =
			this.getIndexOfElement(nodeChildren, movedChildDN);

		// Get widget currently associated with passed dataNode
		PoshWidget dataNodeAssociatedWidget =
			this.getAssociatedWidget(widgetChildren, movedChildDN);

		// find position in widget
		int currentDataNodeWidgetIndex =
			this.getIndexOfElement(widgetChildren, dataNodeAssociatedWidget);

		// now move
		moveChildWidgetNode(currentDataNodeWidgetIndex, movedDataNodeIndex);
	}

	/**
	 * Common function for deleting widget from scene
	 */
	protected void deleteWidgetFromScene(PoshElement dataNode) {
		// delete all children widgets
		deleteWidgetSubtree(this);
		// delete this widget

		this.getPoshScene().deletePoshWidget(this);
		this.getDataNode().removeElementListener(this);

		PoshPlan root = this.getDataNode().getRootNode();

		root.removeListenersFromTree(this);

		if (this.getParent() != null) {
			this.getParent().getChildNodes().remove(this);
		}
	}

	/**
	 * Delete all widgets from tree with root <code>widget</code> =
	 * remove all children from scene and children will remove itself from
	 * listeners of the POSh tree.
	 *
	 * @param widget
	 */
	private void deleteWidgetSubtree(PoshWidget<? extends PoshElement> widget) {
		for (PoshWidget<? extends PoshElement> child : widget.getChildNodes()) {
			deleteWidgetSubtree(child);
			this.getPoshScene().deletePoshWidget(child);
			this.getDataNode().removeElementListener(child);
		}
		widget.getChildNodes().clear();
	}

	/**
	 * Create actions for context menu.
	 * @return not null, list of actions for context menu
	 */
	abstract protected List<AbstractMenuAction> createMenuActions();

	@Override
	final public JPopupMenu getPopupMenu(Widget arg0, Point arg1) {
		JPopupMenu menu = new JPopupMenu("Popup menu");

		// create action list
		List<AbstractMenuAction> actionList = createMenuActions();

		for (AbstractMenuAction action : actionList) {
			JMenuItem item = new JMenuItem(action.getDescription());
			item.addActionListener(action);
			menu.add(item);
		}

		return menu;
	}

	/**
	 * Go through list of all Competences and check is Action name
	 * from Triggered action is Competence
	 * 
	 * @param ta
	 * @return null is no such competenceNode found, else found node
	 */
	private Competence isComp(TriggeredAction ta) {
		String name = ta.getName();
		PoshPlan rootNode = ta.getRootNode();

		for (Competence competence : rootNode.getCompetences()) {
			if (competence.getName().equals(name)) {
				return competence;
			}
		}
		return null;
	}

	/**
	 * Go through list of all APs and check is Action name
	 * from Triggered action is an AP.
	 * 
	 * @param ta
	 * @return null is not such ap found
	 */
	private ActionPattern isAP(TriggeredAction ta) {
		String name = ta.getName();
		PoshPlan rootNode = ta.getRootNode();

		for (ActionPattern ap : rootNode.getActionPatterns()) {
			if (ap.getName().equals(name)) {
				return ap;
			}
		}

		return null;
	}

	/**
	 * Add correct widget that is going to represent triggered action
	 * (competence, action pattern or action) to the place of the tree.
	 *
	 * This doesn't add one widget, but it determines what is dataNode
	 * representing (C, AP or action) and adds all widgets of that representaion
	 * to the tree. Action has only one widget,but other two can be intervened and
	 * can create a really big new subtree instead of just one widget.
	 *
	 * Add this new subtree to the last place of your children. *
	 *
	 * @param dataNode triggered widget
	 */
	protected void addTriggeredActionWidgets(TriggeredAction dataNode) {
		int pos = this.getChildNodes().size();
		addTriggeredActionWidgets(pos, dataNode);

	}

	/**
	 * Add correct widget that is going to represent triggered action
	 * (competence, action pattern or action) to the place of the tree.
	 *
	 * This doesn't add one widget, but it determines what is dataNode
	 * representing (C, AP or action) and adds all widgets of that representaion
	 * to the tree. Action has only one widget,but other two can be intervened and
	 * can create a really big new subtree instead of just one widget.
	 *
	 * @param dataNode triggered widget
	 */
	protected void addTriggeredActionWidgets(int index, TriggeredAction dataNode) {
		ActionPattern apNode;
		Competence compNode;

		if ((apNode = isAP(dataNode)) != null) {
			// Add child widget that will represent AP (SimpleRoleAPW) 
			// to this drive.

			SimpleRoleActionPatternWidget actionRoleWidget = new SimpleRoleActionPatternWidget(getPoshScene(), dataNode, this, apNode);
			this.getChildNodes().add(index, actionRoleWidget);
			this.getPoshScene().addPoshWidget(actionRoleWidget, true);
			// this will create proper child widget nodes and all that other stuff.
			actionRoleWidget.regenerate();
		} else if ((compNode = isComp(dataNode)) != null) {

			SimpleRoleCompetenceWidget competenceWidget = new SimpleRoleCompetenceWidget(getPoshScene(), dataNode, this, compNode);

			this.getChildNodes().add(index, competenceWidget);
			this.getPoshScene().addPoshWidget(competenceWidget, true);
			// this will create proper child widget nodes and all that other stuff.
			competenceWidget.regenerate();

		} else {
			// pass an action
			SimpleRoleActionWidget actionRoleWidget = new SimpleRoleActionWidget(getPoshScene(), dataNode, this);
			this.getChildNodes().add(index, actionRoleWidget);
			this.getPoshScene().addPoshWidget(actionRoleWidget, true);
		}
	}

    /**
     * Take this widget, remove it and on the original place where this widget
     * was place proper representaion of trigggered action (by
     * <code>addTriggeredActionWidgets</code>)
     *
     * @param ta triggered action we are adding to place of this widget.
     */
    protected void changeTriggeredActionWidgets(TriggeredAction ta) {
        SimpleBasicWidget<? extends PoshElement> parent = (SimpleBasicWidget) this.getParent();

        // find position in parent
        int pos = 0;
        for (PoshWidget child : parent.getChildNodes()) {
            if (child == this) {
                // fixme: this is a workaround for double calling of propertyChanged
                parent.addTriggeredActionWidgets(pos, ta);
                deleteWidgetFromScene(getDataNode());

                getPoshScene().consolidate();
                getScene().validate();
                break;
            }
            pos++;
        }
    }

	/**
	 * Get indexof element in the list that has same reference as the passed object.
	 *
	 * Throw runtimeexception if object is not in the list.
	 * 
	 * @param list List in which we are looking.
	 * @param element object we are looking for in the list.
	 * @return index of object in the list
	 */
	final protected int getIndexOfElement(List list, Object element) {
		int position = 0;

		for (Object listElement : list) {
			if (listElement == element) {
				return position;
			}
			position++;
		}
		throw new RuntimeException("Object " + element + " not found in list.");
	}

	/**
	 * Get widget from the <tt>list</tt> that has <tt>dataNode</tt>as associated node.
	 *
	 * Throw runtime exception if no such widget found.
	 * 
	 * @param list
	 * @param dataNode
	 * @return widget that is in the list and that is associated with dataNode
	 */
	final protected PoshWidget getAssociatedWidget(List<PoshWidget<? extends PoshElement>> list, PoshElement dataNode) {
		for (PoshWidget w : list) {
			if (w.getDataNode() == dataNode) {
				return w;
			}
		}
		throw new RuntimeException("DataNode " + dataNode + " not found in list.");
	}

	/**
	 * Move element in getChildNodes() 
	 * @param from index in the list of children the child is moved from
	 * @param to index in the list of children the child is moved to
	 */
	final protected void moveChildWidgetNode(int from, int to) {
		PoshWidget<? extends PoshElement> movingWidget = getChildNodes().get(from);
		getChildNodes().add(to, movingWidget);

		if (from > to) {
			from++;
		}

		getChildNodes().remove(from);
	}
}
