package com.forgestorm.client.util;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.SequenceInputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import static com.forgestorm.client.util.Log.println;

public class HashingUtil {

    private static final String DEFAULT_PATH = System.getProperty("user.home") + File.separator + "ForgeStorm";

    public static void main(String[] args) throws IOException {
        String name = "island_cave.tmx.version-3";
        String file = hashFile(DEFAULT_PATH + File.separator + "maps", name);
        println(HashingUtil.class, "File: " + name + ", Hash: " + file);

        String directory = hashDirectory(DEFAULT_PATH, false);
        println(HashingUtil.class, "Directory: " + directory);
    }

    public static String hashFile(String directoryPath, String fileName) throws IOException {
        File file = new File(directoryPath + File.separator + fileName);

        if (file.isDirectory()) {
            throw new IllegalArgumentException("File is a directory. File: " + fileName);
        }

        FileInputStream fileInputStream = null;

        try {
            fileInputStream = new FileInputStream(file);
            return DigestUtils.md5Hex(new FileInputStream(file));
        } finally {
            if (fileInputStream != null) fileInputStream.close();
        }
    }

    public static String hashDirectory(String directoryPath, boolean includeHiddenFiles) throws IOException {
        File directory = new File(directoryPath);

        if (!directory.isDirectory()) {
            throw new IllegalArgumentException("Not a directory. Path: " + directoryPath);
        }

        Vector<FileInputStream> fileStreams = new Vector<FileInputStream>();
        collectFiles(directory, fileStreams, includeHiddenFiles);
        SequenceInputStream sequenceInputStream = null;
        try {
            sequenceInputStream = new SequenceInputStream(fileStreams.elements());
            return DigestUtils.md5Hex(sequenceInputStream);
        } finally {
            if (sequenceInputStream != null) sequenceInputStream.close();
        }
    }

    private static void collectFiles(File directory, List<FileInputStream> fileInputStreams, boolean includeHiddenFiles) {
        File[] files = directory.listFiles();

        if (files != null) {
            Arrays.sort(files, new Comparator<File>() {
                @Override
                public int compare(File rhs, File lhs) {
                    return rhs.getName().compareTo(lhs.getName());
                }
            });

            for (File f : files) {
                if (!includeHiddenFiles && f.getName().startsWith(".")) {
                    // Skip it
                } else if (f.isDirectory()) {
                    collectFiles(f, fileInputStreams, includeHiddenFiles);
                } else {
                    try {
                        println(HashingUtil.class,  "\t" + f.getAbsolutePath());
                        fileInputStreams.add(new FileInputStream(f));
                    } catch (FileNotFoundException e) {
                        throw new AssertionError(e.getMessage() + ": file should never not be found!");
                    }
                }
            }
        }
    }
}
