package cz.cuni.pogamut.posh.widget.kidview;

import cz.cuni.amis.pogamut.sposh.elements.PoshElement;
import cz.cuni.amis.pogamut.sposh.elements.PoshParser;
import cz.cuni.amis.pogamut.sposh.elements.Sense;
import cz.cuni.amis.pogamut.sposh.elements.Sense.Predicate;
import cz.cuni.amis.pogamut.sposh.elements.Token;
import cz.cuni.pogamut.posh.widget.accept.AbstractAcceptAction;
import cz.cuni.pogamut.posh.widget.PoshNodeType;
import cz.cuni.pogamut.posh.widget.PoshScene;
import cz.cuni.pogamut.posh.widget.PoshWidget;
import cz.cuni.pogamut.posh.widget.accept.AcceptSense2Sense;
import cz.cuni.pogamut.posh.widget.menuactions.DeleteNodeAction;
import java.beans.PropertyChangeEvent;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.TextFieldInplaceEditor;
import org.netbeans.api.visual.widget.Widget;
import org.openide.nodes.Node;
import org.openide.nodes.Node.Property;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;

/**
 * Representation of Sense in KidView.
 * @author Honza
 */
public class SimpleSenseWidget extends NamedBasicWidget<Sense> {

    public static final String DEFAULT_FAIL_SENSE = "cz.cuni.amis.pogamut.sposh.ut2004.senses.Fail";
    public static final String DEFAULT_SUCCEED_SENSE = "cz.cuni.amis.pogamut.sposh.ut2004.senses.Succeed";
    
    SimpleSenseWidget(PoshScene scene, Sense dataNode, PoshWidget<? extends PoshElement> parent) {
        super(scene, dataNode, parent);

        // Add inplace editor for the sensecall
        getActions().addAction(ActionFactory.createInplaceEditorAction(new SenseInplaceEditor(dataNode)));
    }

    /**
     * Class for inplace editing the sense call.
     * 
     */
    private static class SenseInplaceEditor implements TextFieldInplaceEditor {

        final private Sense sense;

        /**
         * Create an inplace editor that modified the 
         * @param senseCall
         */
        SenseInplaceEditor(Sense sense) {
            this.sense = sense;
        }

        @Override
        public boolean isEnabled(Widget widget) {
            return true;
        }

        @Override
        public String getText(Widget widget) {
            return sense.getRepresentation();
        }

        @Override
        public void setText(Widget widget, String string) {
            try {
                PoshParser pp = new PoshParser(new StringReader(string));
                Token nameToken = null;
                Token secondToken = null;
                Token thirdToken = null;

                nameToken = pp.getNextToken();
                if (nameToken.kind != PoshParser.EOF) {
                    secondToken = pp.getNextToken();
                    if (secondToken.kind != PoshParser.EOF) {
                        thirdToken = pp.getNextToken();
                        if (thirdToken.kind != PoshParser.EOF) {
                            // name, predicate and value specified
                            // only name and value specified
                            sense.setValueString(thirdToken.image.trim());
                            sense.setPredicate(Predicate.getPredicate(secondToken.image.trim()));
                            sense.setSenseName(nameToken.image.trim());
                        } else {
                            // only name and value specified
                            sense.setValueString(secondToken.image.trim());
                            sense.setPredicate(Predicate.DEFAULT);
                            sense.setSenseName(nameToken.image.trim());
                        }
                    } else {
                        // only name specified
                        sense.setSenseName(nameToken.image.trim());
                        sense.setPredicate(Predicate.DEFAULT);
                        sense.setOperand(Boolean.TRUE);
                    }
                } else {
                    // not even name specified
                    return;
                }
            } catch (Throwable ex) {
            }
        }
    }

    @Override
    protected void addChildWidget(PoshElement dataNode) {
        throw new RuntimeException("This should never be called. No children expected");
    }

    @Override
    protected List<AbstractMenuAction> createMenuActions() {
        List<AbstractMenuAction> list = new LinkedList<AbstractMenuAction>();

        int numSenses = getDataNode().getParent().getNumberOfChildInstances(Sense.class);
        if (numSenses > 1) {
            list.add(new DeleteNodeAction<Sense>("Delete sense", getDataNode()));
        }

        return list;
    }
    
    @Override
    public String getHeadlineText() {
        String name = getPoshScene().getSensesFQNMapping().get(getDataNode().getName());
        if (name == null) name = getDataNode().getName();
        if (getDataNode().getPredicate() != null) {
            if (getDataNode().getPredicate() == Predicate.DEFAULT && 
                (getDataNode().getOperand() != null && getDataNode().getOperand() == Boolean.TRUE)) return name;
            name += " " + getDataNode().getPredicate().toString() + " " + getDataNode().getValueString();
        }
        return name;
    }

    @Override
    protected PoshNodeType getType() {
        return PoshNodeType.SENSE;
    }

    @Override
    public void elementPropertyChange(PropertyChangeEvent evt) {
        Sense sense = (Sense) evt.getSource();
        this.setHeadlineText(sense.getRepresentation());
        this.doRepaint();
    }

    @Override
    protected List<AbstractAcceptAction> getAcceptProviders() {
        List<AbstractAcceptAction> list = new LinkedList<AbstractAcceptAction>();

        list.add(new AcceptSense2Sense(getDataNode()));

        return list;
    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set set = Sheet.createPropertiesSet();
        sheet.put(set);

        try {
            Node.Property senseNameProp = new PropertySupport.Reflection<String>(getDataNode(), String.class, "getSenseName", "setSenseName");
            senseNameProp.setName(Sense.psSenseName);
            senseNameProp.setDisplayName("Name of the sense");
            senseNameProp.setShortDescription("In this field can be act, sense or competence node");

            Node.Property predicateProp = new PropertySupport.Reflection<Integer>(getDataNode(), Integer.class, "getPredicateIndex", "setPredicateIndex");
            predicateProp.setName(Sense.psPredicateIndex);
            predicateProp.setDisplayName("Predicate(<, =, !=...)");
            predicateProp.setShortDescription("One od the following: == | = | != | < | > | <= | >= \nBoth '==' and '=' test for equality");

            int[] intValues = new int[Predicate.values().length];
            String[] stringKeys = new String[Predicate.values().length];
            for (int i = 0; i < Predicate.values().length; i++) {
                intValues[i] = i;
                stringKeys[i] = Predicate.values()[i].toString();
            }
            predicateProp.setValue("intValues", intValues);
            predicateProp.setValue("stringKeys", stringKeys);

            Node.Property valueProp = new PropertySupport.Reflection<String>(getDataNode(), String.class, "getValueString", "setValueString");
            valueProp.setName(Sense.psValue);
            valueProp.setDisplayName("Value");
            valueProp.setShortDescription("Value is used together with predicate, it is compared to the value returned from the act using predicate and that is the result of the act.");

            set.put(new Property[]{senseNameProp, predicateProp, valueProp});
        } catch (NoSuchMethodException ex) {
            Exceptions.printStackTrace(ex);
        }
        return sheet;
    }
}
