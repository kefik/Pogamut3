package cz.cuni.amis.pogamut.sposh.ut2004;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Set;

import cz.cuni.amis.pogamut.base.utils.guice.AgentScoped;
import cz.cuni.amis.pogamut.sposh.context.Context;
import cz.cuni.amis.pogamut.sposh.context.IUT2004Context;
import cz.cuni.amis.pogamut.sposh.engine.PoshEngine;
import cz.cuni.amis.pogamut.sposh.exceptions.StateInstantiationException;
import cz.cuni.amis.pogamut.sposh.executor.IAction;
import cz.cuni.amis.pogamut.sposh.executor.ISense;
import cz.cuni.amis.pogamut.sposh.executor.IWorkExecutor;
import cz.cuni.amis.pogamut.sposh.executor.StateAction;
import cz.cuni.amis.pogamut.sposh.executor.StateSense;
import cz.cuni.amis.pogamut.sposh.executor.StateWorkExecutor;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import java.io.IOException;
import java.util.*;

/**
 * This class should be used as base for bot that utilizes sposh and state primitives.
 * It is failry simple, it creates {@link PoshEngine}, {@link IWorkExecutor} and {@link Context} during {@link StateSposhLogicController#initializeController(cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot) }.
 * The {@link IWorkExecutor} takes all primitives from the plan and automatically
 * tries to instantiate them.
 *
 * The automatic instantiation expects that names of the primitives are fully
 * qualified names of classes (e.g. cz.cuni.pogamut.senses.Fail) that implement 
 * {@link ISense} for senses and {@link IAction} for actions. The classes are 
 * expected to have public constructor with one parameter that is of type CONTEXT
 * (or its parent). It may be convinient to use classes {@link StateAction} and 
 * {@link StateSense} as basic classes for your own custom primitives.
 *
 * Custom instantiation can be utilized in method {@link StateSposhLogicController#customPrimitiveInstantiation(cz.cuni.amis.pogamut.sposh.executor.StateWorkExecutor, java.util.Set, java.util.Set)  },
 * simply insert your own primitives into the {@link StateWorkExecutor}. When automatic
 * instantiation detects, that primitive with some name (probaly name that is not
 * FQN of class) is already defined in the executor, the name is skipped (IOW: if
 * you add sense <b>hurt</b> in the custom instantion, it won't cause error later one,
 * although it is not defined by any class).
 * 
 * @author Honza
 */
@AgentScoped
public abstract class StateSposhLogicController<BOT extends UT2004Bot, CONTEXT extends IUT2004Context> extends SposhLogicController<BOT, StateWorkExecutor> {

    /** Context for states. */
    protected CONTEXT context;

    /**
     * Initialize logic controller=call super initialization and create context
     * and other stuff that is needed to have.
     * @param bot
     */
    @Override
    public void initializeController(BOT bot) {
        super.initializeController(bot);
        context = createContext();
        // This will make sure work executor is instantiated
        getWorkExecutor();
    }


    @Override
    public void finishControllerInitialization() {
    	super.finishControllerInitialization();
        context.finishInitialization();
    }

    /**
     * Get context.
     * @return get context, possibly create one
     */
    public final CONTEXT getContext() {
        return context;
    }

    /**
     * To be overriden in children, this method enables user to instantiate
     * primitives in any way it desires. If you keep it empty, logic will try
     * to instantiate primitivies by their names and annotations.
     * @param executor executor that will be used for primitives (empty, no primitive should be defined yet)
     * @param actions set of actions used in the plan (unmodifiable)
     * @param senses set of senses used in the plan (unmodifiable)
     */
    protected void customPrimitiveInstantiation(StateWorkExecutor executor, Set<String> actions, Set<String> senses) {
        // Nothing to do here, override in children.
    }

    /**
     * Find class that is a state primitivie corresponding to passed action name.
     * @param actionName name of action, should be FQN.
     * @return class of the action
     */
    private Class getActionClass(String actionName) {
        try {
            return Class.forName(actionName);
        } catch (ClassNotFoundException ex) {
            throw new StateInstantiationException("Unable to find state class for action \"" + actionName + "\"", ex);
        }
    }

    /**
     * Find class that is a state primitivie corresponding to passed sense name.
     * @param senseName name of action, should be FQN.
     * @return class of the sense
     */
    private Class getSenseClass(String senseName) {
        try {
            return Class.forName(senseName);
        } catch (ClassNotFoundException ex) {
            throw new StateInstantiationException("Unable to find state class for sense \"" + senseName + "\"", ex);
        }
    }

    /**
     * Instantiate primitive of passed class. If there will be an error, write it into log and
     * throw runtime exception.
     * @param <T> Returning class of the primitive
     * @param cls class type of primitive
     * @return created instance.
     */
    private <T> T instantiatePrimitive(Class<T> cls) {
        String name = cls.getName();
        try {
            Constructor primitiveConstructor = null;
            Constructor<?>[] constructors = cls.getConstructors();
            for (Constructor<?> constructor : constructors) {
                Class[] constructorParameters = constructor.getParameterTypes();
                if (constructorParameters.length != 1) {
                    continue;
                }
                if (constructorParameters[0].isAssignableFrom(this.getContext().getClass())) {
                    primitiveConstructor = constructor;
                }
            }
            if (primitiveConstructor == null) {
                throw new StateInstantiationException("Primitive \"" + name + "\" doesn't have a constructor that has exactly one parameter of class " + this.getContext().getClass().getName());
            }
            return (T) primitiveConstructor.newInstance(this.getContext());
        } catch (InstantiationException ex) {
            throw new StateInstantiationException("Unable to instantiate primitive \"" + name + "\" (" + ex.getMessage() + ")", ex);
        } catch (IllegalAccessException ex) {
            throw new StateInstantiationException("Illegal access protection for primitive \"" + name + "\"", ex);
        } catch (IllegalArgumentException ex) {
            throw new StateInstantiationException("Primitive \"" + name + "\" doesn't accept bot class (" + this.bot.getClass().getName() + ") in the constructor.", ex);
        } catch (InvocationTargetException ex) {
            throw new StateInstantiationException("Constructor of primitive \"" + name + "\" has thrown an exception (" + ex.getMessage() + ")", ex);
        }
    }

    private Set<String> getActionNames() {
        Set<String> actionNames = new HashSet<String>();
        for (PoshEngine engine : getEngines()) {
            Set<String> engineActions = engine.getPlan().getActionsNames();
            actionNames.addAll(engineActions);
        }
        return actionNames;
    }

    private Set<String> getSensesNames() {
        Set<String> senseNames = new HashSet<String>();
        for (PoshEngine engine : getEngines()) {
            Set<String> engineSenses = engine.getPlan().getSensesNames();
            senseNames.addAll(engineSenses);
        }
        return senseNames;
    }
    
    @Override
    protected StateWorkExecutor createWorkExecutor() {
        StateWorkExecutor executor = new StateWorkExecutor(bot.getLogger().getCategory(SPOSH_LOG_CATEGORY));

        // Get names of all primitives;
        Set<String> actions = getActionNames();
        Set<String> senses = getSensesNames();

        // Check that there isn't a sense with same name as action
        for (String senseName : senses) {
            if (actions.contains(senseName))
                throw new StateInstantiationException("List of senses and " + senseName);
        }


        // Use custom initialization
        customPrimitiveInstantiation(executor, Collections.unmodifiableSet(actions), Collections.unmodifiableSet(senses));

        // Get classses associated with the names
        for (String name : actions) {
            if (executor.isNameUsed(name)) {
                throw new StateInstantiationException("Action instantiation: Primitive with name \"" + name + "\" is already in has already used in the executor.");
            }
            Class<IAction> cls = getActionClass(name);
            IAction action = instantiatePrimitive(cls);
            executor.addAction(name, action);
        }
        for (String name : senses) {
            if (executor.isNameUsed(name)) {
                throw new StateInstantiationException("Sense instantiation: Primitive with name \"" + name + "\" is already in has already used in the executor.");
            }
            Class<ISense> cls = getSenseClass(name);
            ISense sense = instantiatePrimitive(cls);
            executor.addSense(name, sense);
        }

        return executor;
    }

    /**
     * Take the plan from {@link #getPlan() } and return it as the single plan
     * of the bot.
     *
     * @see SposhLogicController#getPlans()
     * @return One plan from from {@link #getPlan() }.
     */
    @Override
    protected List<String> getPlans() throws IOException {
        return Arrays.asList(getPlan());
    }
    
    /**
     * Get Yaposh plan this bot is supposed to execute. Easiest way is to use
     * {@link #getPlanFromResource(java.lang.String) }, {@link #getPlanFromFile(java.lang.String)
     * } or {@link #getPlanFromStream(java.io.InputStream)
     * }.
     *
     * @return Sources of the plan
     */
    protected abstract String getPlan() throws IOException;
    
    /**
     * Create context for this logic controller.
     * @return new logic controller.
     */
    protected abstract CONTEXT createContext();
}
