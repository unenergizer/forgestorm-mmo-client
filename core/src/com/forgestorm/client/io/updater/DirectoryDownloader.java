package com.forgestorm.client.io.updater;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.forgestorm.client.util.Log.println;

public class DirectoryDownloader {

    private static final String filesDirectory = System.getProperty("user.home") + File.separator + "ForgeStorm";

    private static Set<String> existingFiles = new HashSet<String>();
    private static Set<String> existingFilesAndVersions = new HashSet<String>();
    private static Map<String, String> fileNameToVersion = new HashMap<String, String>();

    private String directoryName;
    private String fileType;

    private String directoryPath;

    public DirectoryDownloader(String directoryName, String fileType) {
        this.directoryName = directoryName;
        this.fileType = fileType;
        directoryPath = filesDirectory + File.separator + directoryName;
    }

    private static Elements getDocumentElements(String directory) {
        Document pageRoot = null;
        try {
            pageRoot = Jsoup.connect("https://forgestorm.com/game_files/" + directory).get(); // URL shortened!
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        if (pageRoot == null) {
            //TODO: handle issue
            return null;
        }

        return pageRoot.select("a[href]");
    }

    public void download() {

        makeDirectoryIfNotExist();

        readLocalFiles();

        downloadFiles();

    }

    private void makeDirectoryIfNotExist() {
        File filesDirectoryTest = new File(filesDirectory + File.separator + directoryName);

        println(getClass(), "" + filesDirectoryTest.getAbsoluteFile());

        if (!filesDirectoryTest.exists()) {
            if (filesDirectoryTest.mkdir()) {

                println(getClass(), "WHAHWHWA CANNOTMAKE");

                // todo: can they even play the game?
                System.exit(1);
            }
        }
    }

    private void readLocalFiles() {

        File mapsDirectory = new File(filesDirectory + File.separator + directoryName);
        File[] files = mapsDirectory.listFiles();

        if (files == null) return;

        for (File mapFile : files) {

            if (!mapFile.isFile()) continue;
            String fileName = mapFile.getName();
            if (!fileName.contains("." + fileType)) continue;

            String[] fileParts = fileName.split("\\.");

            existingFiles.add(fileParts[0]);
            existingFilesAndVersions.add(fileParts[0] + "." + fileParts[2]);

            fileNameToVersion.put(fileParts[0], fileParts[2]);

        }
    }

    private void downloadFiles() {

        Elements elements = getDocumentElements(directoryName);

        if (elements == null) {
            //TODO: Handle
            return;
        }

        for (Element element : elements) {

            String fileName = element.attributes().get("href");
            if (!fileName.contains("." + fileType)) continue;

            String[] worldNameParts = fileName.split("\\.");

            if (!existingFiles.contains(worldNameParts[0])) {

                try {
                    saveUrl(directoryPath + File.separator + fileName, "https://forgestorm.com/game_files/" + directoryName + "/" + fileName);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else if (!existingFilesAndVersions.contains(worldNameParts[0] + "." + worldNameParts[2])) {
                // Out of date version number. Delete old version and download new version

                String outOfDateVersion = fileNameToVersion.get(worldNameParts[0]);

                File deleteFile = new File(directoryPath + File.separator + worldNameParts[0] + "." + fileType + "." + outOfDateVersion);
                if (!deleteFile.delete()) {
                    // todo: handle however you want
                }

                try {
                    saveUrl(directoryPath + File.separator + fileName, "https://forgestorm.com/game_files/" + directoryName + "/" + fileName);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {

                println(getClass(), "File: " + fileName + " is up to date!");

            }
        }
    }

    private void saveUrl(final String filename, final String urlString) throws IOException {
        BufferedInputStream in = null;
        FileOutputStream fout = null;
        try {
            in = new BufferedInputStream(new URL(urlString).openStream());
            fout = new FileOutputStream(filename);

            final byte data[] = new byte[1024];
            int count;
            while ((count = in.read(data, 0, 1024)) != -1) {
                fout.write(data, 0, count);
            }
        } finally {
            if (in != null) {
                in.close();
            }
            if (fout != null) {
                fout.close();
            }
        }
    }
}
