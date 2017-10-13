package cz.cuni.amis.nb.pogamut.base.agent;

import cz.cuni.amis.nb.pogamut.base.logging.LogNode;
import cz.cuni.amis.nb.pogamut.base.NamedAction;
import cz.cuni.amis.nb.util.collections.ObservableCollectionNode;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.pogamut.base.utils.logging.NetworkLogClient;
import cz.cuni.amis.utils.collections.ObservableList;
import cz.cuni.amis.utils.exception.PogamutException;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.LinkedList;
import javax.swing.Action;
import org.openide.nodes.Node;
import org.openide.util.ImageUtilities;

/**
 * Node with all log categories of agent's logger.
 * @author ik
 */
public class LogsNode extends ObservableCollectionNode<Node> {

    IAgentLogger logger = null;

    public LogsNode(IAgentLogger logger) {
        super(new ObservableList<Node>(new LinkedList<Node>()));
        this.logger = logger;

        setName("Logs");

        refreshCategories();
    }

    @Override
    public Action[] getActions(boolean arg0) {
        return new Action[]{
                    new NamedAction("ACT_Refresh") {

                        @Override
                        protected void action(ActionEvent e) throws PogamutException {
                            refreshCategories();
                        }
                    }
                };
    }

    /**
     * Updates list of categories present on this logger.
     */
    protected void refreshCategories() {
        getChildrenCollection().clear();
        String[] categoryNames = logger.getCategories().keySet().toArray(new String[0]);
        Arrays.sort(categoryNames);
        for (String categoryName : categoryNames) {
            getChildrenCollection().add(new LogNode(logger.getCategory(categoryName)));
        }
    }

    @Override
    public Image getIcon(int type) {
        return ImageUtilities.loadImage("cz/cuni/amis/nb/pogamut/base/logging/LogsNodeIcon.png");
    }

    @Override
    public Image getOpenedIcon(int type) {
        return getIcon(type);
    }


    
}
