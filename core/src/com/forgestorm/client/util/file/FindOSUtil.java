package com.forgestorm.client.util.file;

public class FindOSUtil {

    public enum OperatingSystems {
        WINDOWS,
        LINUX,
        MAC,
        SOLARIS
    }

    private static OperatingSystems operatingSystems = null;

    public static OperatingSystems getOperatingSystem() {
        if (operatingSystems == null) {
            String operatingSystem = System.getProperty("os.name").toLowerCase();
            if (operatingSystem.contains("win")) {
                operatingSystems = OperatingSystems.WINDOWS;
            } else if (operatingSystem.contains("nix") || operatingSystem.contains("nux")
                    || operatingSystem.contains("aix")) {
                operatingSystems = OperatingSystems.LINUX;
            } else if (operatingSystem.contains("mac")) {
                operatingSystems = OperatingSystems.MAC;
            } else if (operatingSystem.contains("sunos")) {
                operatingSystems = OperatingSystems.SOLARIS;
            }
        }
        return operatingSystems;
    }
}
