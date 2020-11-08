package com.forgestorm.client.game.world.maps;

public class BullshitChunkGen {

    public static void main(String[] args) {
        String s = "0,";
        String retarted = "";
        for (int i = 0; i < 16 * 16; i++) {
            retarted += s;
        }
        System.out.println(retarted);
        System.out.println("check: " + (retarted.length() / 2));
    }
}
