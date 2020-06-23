package com.forgestorm.client.io.updater;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static com.forgestorm.client.util.Log.println;

public class ClientUpdater {

    private static final String LOCAL_FILES = System.getProperty("user.home") + File.separator + "ForgeStorm";
    private static final String URL = "https://forgestorm.com/game_files/";
    private static final String LIST = "list.txt";

    public static void main(String[] args) {

        if (false) {
            // 1. Connect to url and download contents file.
            println(ClientUpdater.class, "Downloading file from internet.");
            try {
                saveUrl(LOCAL_FILES + File.separator + LIST, URL + "/" + LIST);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // 2. Deserialize List
            println(ClientUpdater.class, "Deserialize file.");
            Map<String, String> map = null;
            try {
                map = deserializeMap(new File(LOCAL_FILES + File.separator + LIST));
            } catch (IOException e) {
                e.printStackTrace();
            }

            println(ClientUpdater.class, "File Contents:");
            println(ClientUpdater.class, map.toString());
        } else {
            Map<String, String> map = new HashMap<String, String>();
            map.put("Andrew", "Brown");
            map.put("Justin", "Sory");
            map.put("Karen", "Gary");

            println(ClientUpdater.class, "Serialize file.");
            File file = new File(LIST);
            println(ClientUpdater.class, file.getAbsolutePath());
            try {
                serializeMap(map, new File(LIST));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveUrl(final String filename, final String urlString) throws IOException {
        BufferedInputStream bufferedInputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            bufferedInputStream = new BufferedInputStream(new URL(urlString).openStream());
            fileOutputStream = new FileOutputStream(filename);

            final byte data[] = new byte[1024];
            int count;
            while ((count = bufferedInputStream.read(data, 0, 1024)) != -1) {
                fileOutputStream.write(data, 0, count);
            }
        } finally {
            if (bufferedInputStream != null) bufferedInputStream.close();
            if (fileOutputStream != null) fileOutputStream.close();
        }
    }

    private static Map<String, String> deserializeMap(File file) throws IOException {
        FileInputStream fileInputStream = null;
        ObjectInputStream objectInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            objectInputStream = new ObjectInputStream(fileInputStream);
            try {
                return (Map<String, String>) objectInputStream.readObject();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } finally {
            if (objectInputStream != null) objectInputStream.close();
            if (fileInputStream != null) fileInputStream.close();
        }
        return null;
    }

    private static void serializeMap(Map<String, String> stringMap, File file) throws IOException {
        FileOutputStream fileOutputStream = null;
        ObjectOutputStream objectOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(stringMap);
        } finally {
            if (fileOutputStream != null) fileOutputStream.close();
            if (objectOutputStream != null) objectOutputStream.close();
        }
    }
}
