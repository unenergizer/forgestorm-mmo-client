package com.forgestorm.client.game.rpg;

import com.forgestorm.client.io.FactionLoader;

import java.util.Map;

public class FactionManager {

    private final Map<Byte, FactionLoader.FactionData> factionDataMap = new FactionLoader().loadFactionInfo();

    public Byte getFactionByName(String factionName) {
        factionName = factionName.replace(" ", "_");
        for (Map.Entry<Byte, FactionLoader.FactionData> entry : factionDataMap.entrySet()) {
            if (factionName.equals(entry.getValue().getFactionName())) return entry.getKey();
        }
        return null;
    }

    public String getFactionFromByte(byte id) {
        if (factionDataMap.containsKey(id)) return factionDataMap.get(id).getFactionName();
        return null;
    }

    public byte[] getFactionEnemies(byte b) {
        return factionDataMap.get(b).getEnemyFactions();
    }

    int getNumberOfFactions() {
        return factionDataMap.size();
    }
}
