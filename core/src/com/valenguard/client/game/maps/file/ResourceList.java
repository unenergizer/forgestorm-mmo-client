package com.valenguard.client.game.maps.file;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/**
 * list resources available from the classpath @ *
 */
public class ResourceList {

    public static Collection<String> getMapResources(String directory, String extension) {
        final ArrayList<String> returnValue = new ArrayList<String>();
        final String classPath = System.getProperty("java.class.path", "." + File.separator + directory);
        final String[] classPathElements = classPath.split(System.getProperty("path.separator"));
        for (final String element : classPathElements) {
            returnValue.addAll(getResources(element, extension));
        }
        return returnValue;
    }

    private static Collection<String> getResources(String element, String extension) {
        return new ArrayList<String>(getResourcesFromJarFile(new File(element), extension));
    }

    private static Collection<String> getResourcesFromJarFile(File file, String extension) {
        final ArrayList<String> returnValue = new ArrayList<String>();
        ZipFile zf;
        try {
            zf = new ZipFile(file);
        } catch (final ZipException e) {
            throw new Error(e);
        } catch (final IOException e) {
            throw new Error(e);
        }
        final Enumeration e = zf.entries();
        while (e.hasMoreElements()) {
            final ZipEntry ze = (ZipEntry) e.nextElement();
            final String fileName = ze.getName();
            if (fileName.endsWith(extension)) {
                returnValue.add(fileName);
            }
        }
        try {
            zf.close();
        } catch (final IOException e1) {
            throw new Error(e1);
        }
        return returnValue;
    }
}
