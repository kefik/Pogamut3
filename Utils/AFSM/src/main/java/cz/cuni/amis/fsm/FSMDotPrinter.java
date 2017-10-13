/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.amis.fsm;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Map;
import java.util.logging.Logger;

import cz.cuni.amis.fsm.FSM.StateWrapper;
import cz.cuni.amis.fsm.FSM.TransitionWrapper;

/**
 * Printer creating a Graphviz .dot file from the instantiated FSM.
 * @author ik
 */
public class FSMDotPrinter {

    /**
     * Print the FSM in a Graphviz .dot format.
     * @param fsm FSM to print
     * @param out output stream where the dot will be printed
     * @param graphName name of the FSM graph
     */
    public static void print(FSM fsm, OutputStream out, String graphName) {
        PrintStream pout = new PrintStream(out);
        pout.println("digraph " + graphName + " {");
        pout.println(" node [shape=Mrecord fillcolor=lightblue style=filled]");
        // print all states
        for (Object state : fsm.getStates()) {
            StateWrapper wrapper = (StateWrapper) state;
            String fromStr = wrapper.getWrappedState().getClass().getSimpleName();
            // and print all reachable states together with symbols triggering this transition
            for (Object transEntryObj : wrapper.getTransitions().entrySet()) {
                Map.Entry<Class, TransitionWrapper> transEntry = (Map.Entry<Class, TransitionWrapper>) transEntryObj;
                String symbolStr = transEntry.getKey().getSimpleName();
                String toStr;
                StateWrapper target = transEntry.getValue().getTarget();
                if (target != null) {
                    toStr = target.getWrappedState().getClass().getSimpleName();
                } else {
                    // back link
                    toStr = fromStr;
                }
                pout.println(fromStr + " -> " + toStr + " [label=\"" + symbolStr + "\"];");
            }
        }
        pout.println("}");
    }

    public static void print(Class<IFSMState> initialStateClass, OutputStream out, String graphName) {
        // instantiate the FSM
        Logger log = Logger.getAnonymousLogger();
        FSM fsm = new FSM(null, initialStateClass, log);
        print(fsm, out, graphName);
    }

    protected static void printUsage() {
        System.out.println("java cz.cuni.amis.fsm.FSMDotPrinter INIT_STATE [GRAPH_NAME] [FILE]");
        System.out.println();
        System.out.println("Prints a Graphviz dot file showing the FSM.");
        System.out.println();
        System.out.println("INIT_STATE   fully qualified name of the initial state of the FSM");
        System.out.println("GRAPH_NAME   name used for the graphviz graph");
        System.out.println("FILE         output file");
        System.out.println();
    }

    public static void main(String[] args) throws FileNotFoundException, IOException {
        if (args.length == 0) {
            printUsage();
            System.exit(-3);
        }

        String className = args[0];
        String graphName;
        if (args.length == 1) {
            graphName = "DefaultGraph";
        } else {
            graphName = args[1];
        }

        Class stateClass;
        try {
            // load the initial state class
            stateClass = Class.forName(className);
            // check the state format
            if (IFSMState.class.isAssignableFrom(stateClass)) {
                OutputStream out;
                if (args.length >= 2) {
                    out = new FileOutputStream(args[2]);
                } else {
                    out = System.out;
                }
                print((Class<IFSMState>)stateClass, out, graphName);
                out.close();
                out.close();
                System.exit(0);
            } else {
                // error
                System.err.println("The specified class doesn't implement the IFSMState interface, hence it couldn't be used as initial state of the FSM.");
                System.exit(-1);
            }
        } catch (ClassNotFoundException ex) {
            System.err.println("Class " + className + " not found. It probably isn't on the classpath.");
            ex.printStackTrace();
            System.exit(-2);
        }
    }
}
