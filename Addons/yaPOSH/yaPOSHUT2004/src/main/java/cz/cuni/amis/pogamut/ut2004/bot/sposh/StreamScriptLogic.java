package cz.cuni.amis.pogamut.ut2004.bot.sposh;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotLogicController;

/**
 * This is class for the agents that are using the ScriptEngine. ScriptEngine is
 * a part of Java API that allows execution of scripting file using integrated engine.
 * <p>
 * This basically allows us to create a agent with logic implemented in some
 * scripting language (like JavaScript or Python). Beware, Java has currently
 * implemented  only one engine and that is JavaScript, for others it is
 * necessary to provide custom binding library for the language (like jython).
 * <p>
 * The script that will be executed shoudl contain all the normal functions
 * that are expected, i.e.:
 * <ul>
 *   <li>doLogic</li>
 *   <li>prePrepareAgent</li>
 *   <li>postPrepareAgent</li>
 *   <li>shutdownAgent</li>
 * </ul>
 * <p>
 * You have to specify the file to load and engine to bind before
 * starting the agent.
 *
 * Subclasses has to implement {@link StreamScriptedAgent.getScriptFile()} to
 * specify the script. This is done mostly because of google Guice.
 *
 * Also, if subclass is utilizing some script that is not available as default, 
 * it should override {@link StreamScriptedAgent.engineIsGoingToBeBinded()} to set it up.
 *
 * TODO: this class is rather ugly, it deserves some clean up.
 * <p>
 * It should go approx. like this
 *  * Create a new instance
 *  * Create ScriptEngineManager
 *  * call engineIsGoingToBeBinded()
 *  * get engine from manager based on extension or something
 *
 * How to get data:
 *
 * get file
 * get stream from file
 *
 * What can be done:
 * <ol>
 *   <li>Override getScriptFile, in such case default get stream from file will be used.
 *       Engine will be created based on extension of file.
 *   </li>
 *   <li>Override getScriptStream, in that case getScriptFile won't be used, but
 *       user has to specify the type of engine.and
 *
 *   </li>
 * </or>
 *
 * @author Honza
 */

public abstract class StreamScriptLogic extends UT2004BotLogicController {

    protected ScriptEngineManager scriptEngineManager;
    /**
     * ScriptEngine which contains the file with logic.
     */
    protected ScriptEngine engine = null;
    /**
     * Same engine as in {@link engine}, but recasted as Invocable, allowing us to call methods.
     */
    protected Invocable invocableEngine = null;
    
    /**
     * Constructor which is needed when you have to initialize the
     * environment of the scripting language.
     * <p>
     * Currently used by SPOSHBot.
     */
    @Override
    public void initializeController(UT2004Bot bot) {
    	super.initializeController(bot);
        // first create proper ScriptEngine
        scriptEngineManager = createScriptEngineManager();
        this.engine = createScriptEngine(scriptEngineManager);
        this.invocableEngine = (Invocable) this.engine;
        this.engineBinded();

        // and than load up the script.
        try {
            evalStream(this.getScriptStream());
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ScriptedAgentException("Unable to evaluate script", ex);
        }
        scriptBinded();
    }
    
    /**
     * Return correct ScriptEngine for script this bot is going to run.
     * <p>
     * Because this class is using stream instead of file we don't know what
     * is scripting language of processed script. That is what this function is for.
     *
     * @param manager {@link ScriptEngineManager} that is used to manage engines. 
     * @return correct {@link ScriptEngine} for this agent. 
     */
    protected abstract ScriptEngine createScriptEngine(ScriptEngineManager manager);

    /**
     * Return stream for script that this class should execute.
     * @return input stream containing script. Not null.
     */
    protected abstract Reader getScriptStream() throws IOException;

    /**
     * This method is called after the script is evaluated.
     * To be used by developer, for some adjustments or such.
     */
    protected abstract void scriptBinded();

    /**
     * Creates script engine manager that looks for engines in classpath and
     * also in $POGAMUT_PLATFORM/scriptEngines.
     */
    private static ScriptEngineManager createScriptEngineManager() {
        return new ScriptEngineManager();
    }

    /**
     * This method is called when the engine is binded.
     */
    protected abstract void engineBinded();

    /**
     * Sets attribute to the global scope of the engine.
     * TODO: same as engine.put(name, attribute) ? Learn more about scopes.
     * @param name
     * @param attribute
     */
    final protected void setAttribute(String name, Object attribute) {
        ScriptContext context = this.engine.getContext();
        List<Integer> scopes = context.getScopes();
        context.setAttribute(name, attribute, scopes.get(0));
    }

    /**
     * Evaluates the stream of the script.
     *
     * @param is
     * @return true if eval succeeded
     * @throws ScriptedAgentException
     */
    final protected boolean evalStream(Reader reader) throws ScriptedAgentException {
        try {
            engine.eval(reader);
            return true;
        } catch (ScriptException e) {
            getLog().severe("Script error -> " + e.getMessage());
            throw new ScriptedAgentException("Script error -> " + e.getMessage(), e);
        }
    }

    /**
     * Calls function without parameters from the ScriptEngine.
     *
     * @param name
     * @return true if everything is ok
     * @throws ScriptedAgentException
     */
    final protected boolean callFunction(String name) throws ScriptedAgentException {
        try {
            this.invocableEngine.invokeFunction(name, (Object[]) null);
        } catch (ScriptException e) {
            String msg = "Script exception -> " + e.getMessage();
            getLog().severe(msg);

            throw new ScriptedAgentException(msg, e);
        } catch (NoSuchMethodException e) {
            String msg = "Method '" + name + "' uninmplemented by scripts.";
            getLog().severe(msg);

            throw new ScriptedAgentException(msg, e);
        }
        return true;
    }

    public UT2004Bot getBot() {
        return bot;
    }
    
}
