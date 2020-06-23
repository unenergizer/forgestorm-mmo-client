package com.valenguard.client.game.scripting;

public class ScriptProcessor {

//    private static final Context context = Context.enter();
//    private static final Scriptable scope = context.initStandardObjects();
//
//    private static final String WRAPPER_FUNCTIONS = "function say(npcName, formattedMsg) {" +
//            "NPCTextDialog.say(npcName, formattedMsg);" +
//            "}";
//
//    private int currentLine = 0;
//
//    private NPCTextDialog npcTextDialog;
//
//    public void setNPCTextDialog(NPCTextDialog npcTextDialog) {
//        this.npcTextDialog = npcTextDialog;
//    }
//
//    public void runScript(int scriptId) {
//
//        String script = ClientMain.getInstance().getScriptManager().getScript(scriptId);
//
//        StringBuilder upload = new StringBuilder(WRAPPER_FUNCTIONS);
//        upload.append(script);
//
//        ScriptableObject.putProperty(scope, "NPCTextDialog", Context.javaToJS(npcTextDialog, scope));
//        context.evaluateString(scope, upload.toString(), "<cmd>", 1, null);
//
//    }

}
