package com.forgestorm.client.io.updater;

public class DirectoryScrapping {

    private static DirectoryDownloader mapsdDownloader = new DirectoryDownloader("maps", "tmx");
    private static DirectoryDownloader audioDownloader = new DirectoryDownloader("audio", "WAV");
    private static DirectoryDownloader abilitiesDownloader = new DirectoryDownloader("data/abilities", "yaml");

    public static void main(String[] args) {

        mapsdDownloader.download();

        audioDownloader.download();

        abilitiesDownloader.download();

        //TODO: still should have something for creating the roo directory
//        makeDirectoryIfNotExist("");

    }

}
