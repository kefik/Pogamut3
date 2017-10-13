package cz.cuni.pogamut.posh.view;

import cz.cuni.amis.pogamut.sposh.elements.PoshElement;
import cz.cuni.pogamut.posh.widget.PoshWidget;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is an attempt to remember which widgets were collapse in the widget tree
 * so I can try to restore collapse state of tree after some modifications in source view.
 * <p>
 * Basically :
 * <ul>
 *  <li>Graph view is notified about view switch</li>
 *  <li>Graph view will create a TreeCollapseImprint to store which nodes in the tree were collapsed</li>
 *  <li>User will modify the source of posh plan</li>
 *  <li>User will switch to graph view</li>
 *  <li>Graph view will regerate the graph of posh plan or announce syntax error.</li>
 *  <li>Graph view will use TreeCollapseImprint to colapse the tree, but since tree has changed, there will be some changes</li>
 * </ul>
 * @author Honza
 */
public class TreeCollapseImprint {

    private CollapseStateNode root;

    /**
     * Create a TreeCollapseImprint from using passed posh plan tree
     * @param dcw DriveCollectionWidget, root widget of KidElementView
     */
    public TreeCollapseImprint(PoshWidget<? extends PoshElement> dcw) {
        root = new CollapseStateNode(dcw);
    }

    /**
     * Take passed widget tree and try to restore collapse state that is
     * stored in this memory.
     * @param dcw root of widget tree we want to collapse according to this imprint
     */
    public void restore(PoshWidget<? extends PoshElement> dcw) {
        root.restore(dcw);
    }

    private static class CollapseStateNode {

        private String nodeHeadline;
        private Class nodeClass;
        private boolean collapsed;
        private List<CollapseStateNode> children;

        private CollapseStateNode(PoshWidget<? extends PoshElement> widget) {
            this.nodeHeadline = widget.getHeadlineText();
            this.nodeClass = widget.getClass();
            this.collapsed = widget.isCollapsed();

            this.children = new ArrayList<CollapseStateNode>();
            for (PoshWidget<? extends PoshElement> child : widget.getChildNodes()) {
                children.add(new CollapseStateNode(child));
            }
        }

        /**
         * Take widget, compare it with info in this node and
         * if they are equal,
         * @param widget
         */
        public void restore(PoshWidget<? extends PoshElement> widget) {
            if (!sameInfo(widget)) {
                return;
            }
            
            for (CollapseStateNode childCollapseNode : children)  {
                for (PoshWidget<? extends PoshElement> childWidget : widget.getChildNodes()) {
                    if (childCollapseNode.sameInfo(childWidget)) {
                        childCollapseNode.restore(childWidget);
                    }
                }
            }

            if (collapsed) {
                widget.setCollapsed(collapsed);
            }
        }

        private boolean sameInfo(PoshWidget widget) {
            if (nodeHeadline != null && widget != null && nodeHeadline.equals(widget.getHeadlineText()) && nodeClass == widget.getClass()) {
                return true;
            }
            return false;
        }
    }
}
