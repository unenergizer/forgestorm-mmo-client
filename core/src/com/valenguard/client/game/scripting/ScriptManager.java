package com.valenguard.client.game.scripting;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class ScriptManager {

    private static final ScriptEngineManager manager = new ScriptEngineManager();
    private static final ScriptEngine engine = manager.getEngineByName("JavaScript");

    private int currentLine = 0;

    public void runScript(String scriptFile) {
        
    }

}
