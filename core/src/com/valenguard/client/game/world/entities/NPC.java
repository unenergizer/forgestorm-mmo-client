package com.valenguard.client.game.world.entities;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NPC extends AiEntity {
    private byte faction;

    /**
     * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
     * Script engine is NOT supported on Android!!!
     * Advice given is to use Mozilla Rhino
     * https://stackoverflow.com/questions/42128648/how-to-use-scriptenginemanager-in-android
     * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
     */
//    private static final ScriptEngineManager manager = new ScriptEngineManager();
//    private static final ScriptEngine engine = manager.getEngineByName("JavaScript");

    public void chat() {

        // 1. Read from file for NCPs
        // 2. call say over and over


//
//        // read script file
//        engine.eval(Files.newBufferedReader(Paths.get("C:/Scripts/Jsfunctions.js"), StandardCharsets.UTF_8));
//
//        Invocable inv = (Invocable) engine;
//        // call function from script file
//        inv.invokeFunction("yourFunction", "param");

        //String loadNpcScript = "" +
        //       "importPackage(" + getClass().getPackage().getName()  + ");\n";
//        Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
//        bindings.put("npc", this);
//
//        String scriptVar = "This is a variable nigga";
//
//        try {
//
//            String scriptString = "npc.test(\"" + scriptVar + "\")";
//
//            System.out.println(scriptString);
//
//            engine.eval(scriptString);
//        } catch (ScriptException e) {
//            e.printStackTrace();
//        }

    }

//    public void test(String var) {
//        System.out.println(var);
//    }
}
