package com.forgestorm.client.game.world.entities;

import com.forgestorm.client.ClientMain;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NPC extends AiEntity {

    private byte faction;
    private int scriptId;

    public NPC(ClientMain clientMain) {
        super(clientMain);
    }

    /**
     * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
     * Script engine is NOT supported on Android!!!
     * Advice given is to use Mozilla Rhino
     * https://stackoverflow.com/questions/42128648/how-to-use-scriptenginemanager-in-android
     * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
     */

    public void chat() {
//        ClientMain.getInstance().getScriptProcessor().runScript(scriptId);
    }
}
