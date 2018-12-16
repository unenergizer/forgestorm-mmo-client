package com.valenguard.client.game.inventory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ItemManager {

    private String[] itemNames;

    public void readItems() {

        List<String> itemNames = new ArrayList<String>();

        FileHandle fileHandle = Gdx.files.internal("items");

        BufferedReader bufferedReader = null;
        try {

            bufferedReader = new BufferedReader(new FileReader(fileHandle.file()));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                itemNames.add(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        this.itemNames = new String[itemNames.size()];
        for (int i = 0; i < itemNames.size(); i++)
            this.itemNames[i] = itemNames.get(i);

    }

    public String getItemName(int itemId) {
        return itemNames[itemId];
    }
}
