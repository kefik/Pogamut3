package cz.cuni.amis.nb.pogamut.base.agent;

import cz.cuni.amis.nb.util.PeriodicalyUpdatedProperty;
import cz.cuni.amis.pogamut.base3d.agent.IAgent3D;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.base3d.worldview.object.Rotation;
import cz.cuni.amis.pogamut.base3d.worldview.object.Velocity;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;

/**
 * Adds location, rotation and velocity to node properties.
 * @author ik
 */
public abstract class Agent3DNode<T extends IAgent3D> extends ControllableAgentNode<T> {

    protected Collection<Node.Property> updatableProps = new LinkedList<Node.Property>();

    public Agent3DNode(T agent) {
        super(agent);
                addUpdateTask(new Runnable() {

            @Override
            public void run() {
                for (Property prop : updatableProps) {
                    try {
                        firePropertyChange(prop.getName(), null, prop.getValue());
                    } catch (IllegalAccessException ex) {
                        ex.printStackTrace();
                    } catch (InvocationTargetException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet = super.createSheet();
        Sheet.Set props = new Sheet.Set();
        props.setDisplayName("Agent3D");
        props.setName("Agent3D");
        sheet.put(props);

        try {
            Property[] propArr = new Property[]{
                new PeriodicalyUpdatedProperty(agent, "Location", Location.class, "Location", "Location of the agent."),
                new PeriodicalyUpdatedProperty(agent, "Rotation", Rotation.class, "Rotation", "Rotation of the agent."),
                new PeriodicalyUpdatedProperty(agent, "Velocity", Velocity.class, "Velocity", "Velocity of the agent."),};

            updatableProps.addAll(Arrays.asList(propArr));
            props.put(propArr);
        } catch (NoSuchMethodException ex) {
            Exceptions.printStackTrace(ex);
        }

       return sheet;
    }
}
