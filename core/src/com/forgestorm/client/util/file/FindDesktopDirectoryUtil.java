package com.forgestorm.client.util.file;

import java.io.File;


public class FindDesktopDirectoryUtil {

    private static final String MAIN_NAME = "ForgeStorm";
    //If (windows)
    //   System.getenv("APPDATA") + "\Company\Game"
    //Else if (macos)
    //   "~/Library/Preferences/Company/Game"
    //Else
    //   If System.getenv("XDG_CONFIG_HOME") exists
    //       System.getenv("XDG_CONFIG_HOME") + "/Company/Game"
    //   Else
    //       "~/.config/Company/Game"

    public static File getDirectory() {
        FindOSUtil.OperatingSystems operatingSystem = FindOSUtil.getOperatingSystem();

        String directory = null;
        switch (operatingSystem) {
            case WINDOWS:
                directory = System.getenv("APPDATA");
                break;
            case LINUX:
                directory = System.getenv("XDG_CONFIG_HOME");
                if (!new File(directory).exists()) {
                    directory = "~" + File.separator + ".config";
                }
                break;
            case MAC:
                directory = "~/Library/Application Support/";
                break;
            case SOLARIS:
                break;
        }

        return new File(directory + File.separator + MAIN_NAME);
    }
}
