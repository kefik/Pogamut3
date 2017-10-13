package cz.cuni.amis.pogamut.ut2004.bot.sposh;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

/**
 * This class is base class for users who program their agents in some scripting
 * language and have thir file ready to use. Override {@link FileScriptedAgent.getScriptFile()}
 * to specify the script that will be loaded.
 * <p>
 * To subclass this class, override {@link getScriptFile()}, {@link doLogic()} and
 * other necessary methods. Use callFunction method to call scripted function 
 * from specified script.
 *
 * @author Honza
 */
@Deprecated
public abstract class FileScriptLogic extends StreamScriptLogic {

    /**
     * Create a {@link ScriptEngine} according to extension of script specified in
     * {@link getScriptFile()}
     * @param manager
     * @return Correct script engine for script file.
     */
    @Override
    protected ScriptEngine createScriptEngine(ScriptEngineManager manager) {
        String scriptExtension = getExt(getScriptFile());

        return manager.getEngineByExtension(scriptExtension);
    }

    /**
     * Get stream of file from script file specified in {@link FileScriptedAgent.getScriptFile()}.
     * @return
     * @throws IOException If some problem occurs with getting stream from script file.
     */
    @Override
    protected Reader getScriptStream() throws IOException {
        return new FileReader(getScriptFile());
    }

    /**
     * Return path to the script file.
     * Absolute path if possible, because I am not sure about relative path.
     * TODO: some reliable way to get root directroy of project
     * @return Return path
     */
    protected abstract String getScriptFile();

    /**
     * Get extension of
     * @param path
     * @return extension or "" if no extension found
     */
    protected String getExt(String path) {
        int separatorIndex = path.lastIndexOf(System.getProperty("file.separator"));
        String filename = (separatorIndex == -1 ? path : path.substring(separatorIndex + 1));

        System.out.println("filename is "+filename);
        return (filename.lastIndexOf('.') == -1 ? "" : filename.substring(filename.lastIndexOf('.') + 1));
    }
}
